/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner.utils

import group.pals.desktop.app.apksigner.i18n.Messages
import group.pals.desktop.app.apksigner.i18n.R
import group.pals.desktop.app.apksigner.services.BaseThread

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FilterOutputStream
import java.io.IOException
import java.io.OutputStream
import java.io.RandomAccessFile
import java.util.ArrayList
import java.util.Arrays
import java.util.Calendar
import java.util.Date
import java.util.Enumeration
import java.util.GregorianCalendar
import java.util.List
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * ZipAlign.
 * <p>
 * This file is ported from <a href=
 * "https://android.googlesource.com/platform/build/+/master/tools/zipalign/"
 * >AOSP's ZipAlign</a> tool.
 * </p>
 * <p>
 * <h1>Quote from original README</h1>
 * </p>
 * <p>
 *
 * <pre>
 * The purpose of zipalign is to ensure that all uncompressed data starts
 * with a particular alignment relative to the start of the file.  This
 * allows those portions to be accessed directly with mmap() even if they
 * contain binary data with alignment restrictions.
 *
 * Some data needs to be word-aligned for easy access, others might benefit
 * from being page-aligned.  The adjustment is made by altering the size of
 * the "extra" field in the zip Local File Header sections.  Existing data
 * in the "extra" fields may be altered by this process.
 *
 * Compressed data isn't very useful until it's uncompressed, so there's no
 * need to adjust its alignment.
 *
 * Alterations to the archive, such as renaming or deleting entries, will
 * potentially disrupt the alignment of the modified entry and all later
 * entries.  Files added to an "aligned" archive will not be aligned.
 * </pre>
 *
 * </p>
 * <p>
 * <h1>Notes</h1>
 * </p>
 * <p>
 * <ul>
 * <li>The tool modifies the "extra" field of all entries which are not
 * compressed ({@link ZipEntry#STORED}).</li>
 *
 * <li>Only the "extra" fields in local file headers are modified. The ones in
 * central directory are not touched.</li>
 * </ul>
 * </p>
 * <p>
 * See <a href="http://en.wikipedia.org/wiki/Zip_(file_format)">Zip (file
 * format) - Wikipedia</a> for further information..
 * </p>
 *
 * @author Hai Bison
 * @since v1.6.9 beta
 */

object ZipAlign {

    /**
     * The minimum size of a ZIP entry's header.
     */
    lazy final val ZIP_ENTRY_HEADER_LEN = 30

    /**
     * Default version to work with ZIP files.
     */
    lazy final val ZIP_ENTRY_VERSION = 20

    /**
     * The offset of extra field length in a ZIP entry's header.
     */
    lazy final val ZIP_ENTRY_OFFSET_EXTRA_LEN = 28

    /**
     * The size of field extra length, in a ZIP entry's header.
     */
    lazy final val ZIP_ENTRY_FIELD_EXTRA_LEN_SIZE = 2

    /**
     * @see <a
     *      href="https://android.googlesource.com/platform/build/+/master/tools/zipalign/ZipEntry.h">ZipEntry.h</a>
     */
    lazy final val ZIP_ENTRY_USES_DATA_DESCR = 0x0008

    /**
     * @see <a
     *      href="https://android.googlesource.com/platform/build/+/master/tools/zipalign/ZipEntry.h">ZipEntry.h</a>
     */
    lazy final val ZIP_ENTRY_DATA_DESCRIPTOR_LEN = 16

    /**
     * Default alignment value.
     * <p>
     * See <a
     * href="http://developer.android.com/tools/help/zipalign.html">zipalign
     * </a>.
     * </p>
     */
    lazy final val DEFAULT_ALIGNMENT = 4

    /**
     * Used to append to newly aligned APK's file name.
     */
    lazy final val ALIGNED = "ALIGNED"

}

class ZipAlign {

    import ZipAlign._

    /**
     * Private helper class.
     *
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    private class XEntry(val entry: ZipEntry, val headerOffset: Long,
            val flags: Int, val padding: Int) {}

    /**
     * Extended class of {@link FilterOutputStream}, which has some helper
     * methods for writing data to ZIP stream.
     *
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    private class FilterOutputStreamEx(out: OutputStream) extends
            FilterOutputStream(out) {

        private var _totalWritten = 0l
        def totalWritten = _totalWritten
        private totalWritten_= (v: Long) = _totalWritten = v

        override def write(b: Array[Byte]) = {
            out.write(b)
            totalWritten += b.length
        }// write()

        override def write(b: Array[Byte], off: Int, len: Int) = {
            out.write(b, off, len)
            totalWritten += len
        }// write()

        override def write(b: Int) = {
            out.write(b)
            totalWritten += 1
        }// write()

        override def close() = {
            // l("\t\tclose() >> totalWritten = %,d", totalWritten)
            super.close()
        }// close()

        /**
         * Writes a 32-bit int to the output stream in little-endian byte order.
         *
         * @param v
         *            the data to write.
         * @throws IOException
         */
        def writeInt(v: Long) = {
            write(((v >>> 0) & 0xff).asInstanceOf[Int])
            write(((v >>> 8) & 0xff).asInstanceOf[Int])
            write(((v >>> 16) & 0xff).asInstanceOf[Int])
            write(((v >>> 24) & 0xff).asInstanceOf[Int])
        }// writeInt()

        /**
         * Writes a 16-bit short to the output stream in little-endian byte
         * order.
         *
         * @param v
         *            the data to write.
         * @throws IOException
         */
        def writeShort(v: Int) = {
            write((v >>> 0) & 0xff)
            write((v >>> 8) & 0xff)
        }// writeShort()

    }// FilterOutputStreamEx

    /**
     * To align ZIP files :-)
     *
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    class ZipAligner(inputFile: File, outputFile: File,
            alignment: Int = DEFAULT_ALIGNMENT) extends BaseThread {

        setName(Messages.getString(R.string.apk_aligner_thread))

        private var mZipFile: ZipFile = null
        private var mRafInput: RandomAccessFile = null
        private var mOutputStream: FilterOutputStreamEx = null
        private var mXEntries = List[XEntry]()
        private var mInputOffset = 0l
        private var mTotalPadding = 0l

        /**
         * 0 >> 100
         */
        private var mProgress = 0d

        override def run() = {
            L.d("%s >> starting", classOf[ZipAligner].getSimpleName())

            try {
                openFiles()
                if (!isInterrupted()) copyAllEntries()
                if (!isInterrupted()) buildCentralDirectory()
            } catch {
                case e: Exception =>
                    outputFile.delete()
                    sendNotification(
                            MSG_ERROR,
                            Texts.NULL,
                            Messages.getString(R.string.pmsg_error_details,
                                    e.getMessage(), L.printStackTrace(e)))
            } finally {
                try {
                    closeFiles()
                } catch {
                    case e: Exception =>
                        outputFile.delete()
                        sendNotification(
                                MSG_ERROR,
                                Texts.NULL,
                                Messages.getString(R.string.pmsg_error_details,
                                        e.getMessage(), L.printStackTrace(e)))
                }
            }

            if (isInterrupted())
                sendNotification(MSG_ERROR, Texts.NULL,
                        Messages.getString(R.string.cancelled))

            sendNotification(MSG_DONE)

            L.d("%s >> finishing", classOf[ZipAligner].getSimpleName())
        }// run()

        /**
         * Opens files.
         * <p>
         * This takes 5% of total.
         * </p>
         *
         * @throws IOException
         */
        def openFiles() = {
            sendNotification(MSG_INFO, Texts.NULL, String.format(
                    "%s\n\n",
                    Messages.getString(R.string.pmsg_aligning_apk,
                            inputFile.getName(), alignment)))

            mZipFile = new ZipFile(inputFile)
            mRafInput = new RandomAccessFile(inputFile, "r")
            mOutputStream = new FilterOutputStreamEx(new BufferedOutputStream(
                    new FileOutputStream(outputFile), Files.FILE_BUFFER))

            sendNotification(MSG_INFO, mProgress = 5)
        }// openFiles()

        /**
         * Copies all entries, aligning them if needed.
         * <p>
         * This takes 80% of total.
         * </p>
         *
         * @throws IOException
         */
        def copyAllEntries() = {
            val entryCount = mZipFile.size()
            if (entryCount == 0) {
                sendNotification(MSG_INFO, mProgress += 80)
                return
            }

            val progress = 80f / entryCount

            val entries = mZipFile.entries()
            while (entries.hasMoreElements() && !isInterrupted()) {
                val entry = entries.nextElement()

                var flags = entry.getMethod() == ZipEntry.STORED ? 0 : 1 << 3
                flags |= 1 << 11

                val outputEntryHeaderOffset = mOutputStream.totalWritten
                if (Sys.DEBUG)
                    L.d("\t\toutputEntryHeaderOffset = %,d",
                            outputEntryHeaderOffset)

                val inputEntryHeaderSize = ZIP_ENTRY_HEADER_LEN
                        + (if (entry.getExtra() != null) entry.getExtra().length
                                else 0)
                        + entry.getName().getBytes(Texts.UTF8).length
                val inputEntryDataOffset = mInputOffset + inputEntryHeaderSize

                sendNotification(
                        MSG_INFO,
                        Texts.NULL,
                        "%,15d  %s".format(inputEntryDataOffset, entry.getName()))

                var padding

                if (entry.getMethod() != ZipEntry.STORED) {
                    /*
                     * The entry is compressed, copy it without padding.
                     */
                    padding = 0
                } else {
                    /*
                     * Copy the entry, adjusting as required. We assume that the
                     * file position in the new file will be equal to the file
                     * position in the original.
                     */
                    var newOffset = inputEntryDataOffset + mTotalPadding
                    if (Sys.DEBUG)
                        L.d("\t\t\tnewOffset = %,d", newOffset)
                    padding =
                        ((alignment - (newOffset % alignment)) % alignment)
                        .asInstanceOf[Int]
                    mTotalPadding += padding
                }

                val xentry = new XEntry(entry, outputEntryHeaderOffset, flags, padding)
                mXEntries :+= xentry

                if (Sys.DEBUG)
                    L.d("\t'%s' >> header = %,d, padding = %,d",
                            entry.getName(), inputEntryHeaderSize, padding)

                /*
                 * Modify the original header, add padding to `extra` field and
                 * copy it to output.
                 */
                var extra = entry.getExtra()
                if (extra == null) extra = new Array[Byte](0)
                extra ++= Array.fill(padding){0.asInstanceOf[Byte]}
                entry.setExtra(extra)

                /*
                 * Now write the header to output.
                 */

                mOutputStream.writeInt(ZipOutputStream.LOCSIG)
                mOutputStream.writeShort(ZIP_ENTRY_VERSION)
                mOutputStream.writeShort(flags)
                mOutputStream.writeShort(entry.getMethod())

                var modDate: Int = 0
                var time: Int = 0
                var cal = new GregorianCalendar()
                cal.setTime(new Date(entry.getTime()))
                var year = cal.get(Calendar.YEAR)
                if (year < 1980) {
                    modDate = 0x21
                    time = 0
                } else {
                    modDate = cal.get(Calendar.DATE)
                    modDate = (cal.get(Calendar.MONTH) + 1 << 5) | modDate
                    modDate = ((cal.get(Calendar.YEAR) - 1980) << 9) | modDate
                    time = cal.get(Calendar.SECOND) >> 1
                    time = (cal.get(Calendar.MINUTE) << 5) | time
                    time = (cal.get(Calendar.HOUR_OF_DAY) << 11) | time
                }

                mOutputStream.writeShort(time)
                mOutputStream.writeShort(modDate)

                mOutputStream.writeInt(entry.getCrc())
                mOutputStream.writeInt(entry.getCompressedSize())
                mOutputStream.writeInt(entry.getSize())

                mOutputStream
                        .writeShort(entry.getName().getBytes(Texts.UTF8).length)
                mOutputStream.writeShort(entry.getExtra().length)
                mOutputStream.write(entry.getName().getBytes(Texts.UTF8))
                mOutputStream.write(entry.getExtra(), 0,
                        entry.getExtra().length)

                /*
                 * Copy raw data.
                 */

                mInputOffset += inputEntryHeaderSize

                val sizeToCopy =
                    if ((flags & ZIP_ENTRY_USES_DATA_DESCR) != 0) {
                        if (entry.isDirectory()) 0
                        else
                            entry.getCompressedSize()) + ZIP_ENTRY_DATA_DESCRIPTOR_LEN
                    } else {
                        if (entry.isDirectory()) 0
                        else entry.getCompressedSize()
                    }

                if (sizeToCopy > 0) {
                    mRafInput.seek(mInputOffset)

                    def read() = {
                        var totalSizeCopied = 0l
                        val buf = new Array[Byte](Files.FILE_BUFFER)
                        while (totalSizeCopied < sizeToCopy) {
                            var read = mRafInput.read(
                                    buf,
                                    0,
                                    Math.min(Files.FILE_BUFFER, sizeToCopy
                                            - totalSizeCopied).asInstanceOf[Int])
                            if (read <= 0)
                                return

                            mOutputStream.write(buf, 0, read)
                            totalSizeCopied += read
                        }// while
                    }// read()

                    read()
                }// if

                mInputOffset += sizeToCopy

                if (padding == 0)
                    sendNotification(MSG_INFO, mProgress += progress,
                            Texts.NULL, "  (%s, %s)\n".format(
                                    Messages.getString(R.string.compressed),
                                    Messages.getString(R.string.passed)))
                else
                    sendNotification(
                            MSG_INFO,
                            mProgress += progress,
                            Texts.NULL,
                            "  (%s, %s)\n".format(
                                    Messages.getString(R.string.aligned),
                                    Texts.sizeToStr(padding)))
            }// while
        }// copyAllEntries()

        /**
         * Builds central directory.
         * <p>
         * This takes 10% of total.
         * </p>
         *
         * @throws IOException
         */
        private def buildCentralDirectory() = {
            val centralDirOffset = mOutputStream.totalWritten

            L.d("\tWriting Central Directory at %,d", centralDirOffset)

            for (xentry <- mXEntries) {
                if (isInterrupted()) return

                /*
                 * Write entry.
                 */
                val entry = xentry.entry

                var modDate: Int = 0
                var time: Int = 0
                var cal = new GregorianCalendar()
                cal.setTime(new Date(entry.getTime()))
                var year = cal.get(Calendar.YEAR)
                if (year < 1980) {
                    modDate = 0x21
                    time = 0
                } else {
                    modDate = cal.get(Calendar.DATE)
                    modDate = (cal.get(Calendar.MONTH) + 1 << 5) | modDate
                    modDate = ((cal.get(Calendar.YEAR) - 1980) << 9) | modDate
                    time = cal.get(Calendar.SECOND) >> 1
                    time = (cal.get(Calendar.MINUTE) << 5) | time
                    time = (cal.get(Calendar.HOUR_OF_DAY) << 11) | time
                }

                mOutputStream.writeInt(ZipFile.CENSIG) // CEN header signature
                mOutputStream.writeShort(ZIP_ENTRY_VERSION) // version made by
                mOutputStream.writeShort(ZIP_ENTRY_VERSION) // version needed
                                                             // to
                // extract
                mOutputStream.writeShort(xentry.flags) // general purpose bit
                                                        // flag
                mOutputStream.writeShort(entry.getMethod()) // compression
                                                             // method
                mOutputStream.writeShort(time)
                mOutputStream.writeShort(modDate)
                mOutputStream.writeInt(entry.getCrc()) // crc-32
                mOutputStream.writeInt(entry.getCompressedSize()) // compressed
                                                                   // size
                mOutputStream.writeInt(entry.getSize()) // uncompressed size
                val nameBytes = entry.getName().getBytes(Texts.UTF8)
                mOutputStream.writeShort(nameBytes.length)
                mOutputStream.writeShort(
                    if (entry.getExtra() != null)
                        entry.getExtra().length - xentry.padding
                    else 0)

                val commentBytes =
                    if (entry.getComment() != null)
                        entry.getComment().getBytes(Texts.UTF8)
                    else null
                if (commentBytes != null)
                    mOutputStream.writeShort(Math.min(commentBytes.length, 0xffff))
                else
                    mOutputStream.writeShort(0)

                mOutputStream.writeShort(0) // starting disk number
                mOutputStream.writeShort(0) // internal file attributes
                                             // (unused)
                mOutputStream.writeInt(0) // external file attributes (unused)
                mOutputStream.writeInt(xentry.headerOffset) // relative offset
                                                             // of
                // local
                // header
                mOutputStream.write(nameBytes)
                if (entry.getExtra() != null)
                    mOutputStream.write(entry.getExtra(), 0,
                            entry.getExtra().length - xentry.padding)
                if (commentBytes != null)
                    mOutputStream.write(commentBytes, 0,
                            Math.min(commentBytes.length, 0xffff))
            }// for xentry

            if (isInterrupted()) return

            sendNotification(MSG_INFO, mProgress += 5)

            /*
             * Write the end of central directory.
             */
            val centralDirSize = mOutputStream.totalWritten - centralDirOffset
            L.d("\tWriting End of Central Directory, its size = %,d",
                    centralDirSize)

            val entryCount = mXEntries.size()

            mOutputStream.writeInt(ZipFile.ENDSIG) // END record signature
            mOutputStream.writeShort(0) // number of this disk
            mOutputStream.writeShort(0) // central directory start disk
            mOutputStream.writeShort(entryCount) // number of directory entries
                                                  // on
            // disk
            mOutputStream.writeShort(entryCount) // total number of directory
                                                  // entries
            mOutputStream.writeInt(centralDirSize) // length of central
                                                    // directory
            mOutputStream.writeInt(centralDirOffset) // offset of central
            // directory
            if (mZipFile.getComment() != null) { // zip file comment
                val bytes = mZipFile.getComment().getBytes(Texts.UTF8)
                mOutputStream.writeShort(bytes.length)
                mOutputStream.write(bytes)
            } else {
                mOutputStream.writeShort(0)
            }

            mOutputStream.flush()

            sendNotification(MSG_INFO, mProgress += 5)
        }// buildCentralDirectory()

        /**
         * Closes all files.
         * <p>
         * This takes 5% of total.
         * </p>
         *
         * @throws IOException
         */
        private def closeFiles() = {
            try mZipFile.close()
            finally {
                try mRafInput.close()
                finally { mOutputStream.close() }
            }

            sendNotification(MSG_INFO, mProgress = 100, Texts.NULL,
                    "\n%s".format(
                            Messages.getString(R.string.pmsg_alignment_done,
                                    outputFile.getName())))
        }// closeFiles()
    }// ZipAligner

    /**
     * The ZIP alignment verifier.
     *
     * @author Hai Bison
     * @since v1.6.9 beta
     */
    class ZipAlignmentVerifier(inputFile: File,
            alignment: Int = DEFAULT_ALIGNMENT) extends BaseThread {

        private var mZipFile: ZipFile = null
        private var mRafInput: RandomAccessFile = null

        /**
         * 0 >> 100
         */
        private var mProgress = 0d
        private var mFoundBad = false

        setName(Messages.getString(R.string.apk_alignment_verifier_thread))

        override def run() = {
            L.d("%s >> starting", classOf[ZipAlignmentVerifier].getSimpleName())

            try {
                openFiles()
                if (!isInterrupted()) verify()
            } catch {
                case e: Exception =>
                    mFoundBad = true
                    sendNotification(
                            MSG_ERROR,
                            Texts.NULL,
                            Messages.getString(R.string.pmsg_error_details,
                                    e.getMessage(), L.printStackTrace(e)))
            } finally {
                try closeFiles()
                catch {
                    case e: Exception =>
                        mFoundBad = true
                        sendNotification(
                                MSG_ERROR,
                                Texts.NULL,
                                Messages.getString(R.string.pmsg_error_details,
                                        e.getMessage(), L.printStackTrace(e)))
                }
            }

            if (isInterrupted())
                sendNotification(MSG_ERROR, Texts.NULL,
                        Messages.getString(R.string.cancelled))

            sendNotification(MSG_DONE)

            L.d("%s >> finishing", classOf[ZipAlignmentVerifier].getSimpleName())
        }// run()

        /**
         * Opens files.
         * <p>
         * This takes 5% of total.
         * </p>
         *
         * @throws IOException
         */
        private def openFiles() = {
            sendNotification(MSG_INFO, Texts.NULL, "%s\n\n".format(
                    Messages.getString(
                            R.string.pmsg_verifying_alignment_of_apk,
                            inputFile.getName(), alignment)))

            mZipFile = new ZipFile(inputFile)
            mRafInput = new RandomAccessFile(inputFile, "r")

            sendNotification(MSG_INFO, mProgress = 5)
        }// openFiles()

        /**
         * Verifies input file.
         * <p>
         * This takes 90% of total.
         * </p>
         *
         * @throws IOException
         */
        private def verify() = {
            val entryCount = mZipFile.size()
            if (entryCount == 0) {
                sendNotification(MSG_INFO, mProgress += 90)
                return
            }

            val entries = mZipFile.entries()
            val progress = 90f / entryCount
            var dataOffset = 0l

            while (entries.hasMoreElements() && !isInterrupted()) {
                val entry = entries.nextElement()

                mRafInput.seek(dataOffset + ZIP_ENTRY_OFFSET_EXTRA_LEN)
                val buf = new byte[ZIP_ENTRY_FIELD_EXTRA_LEN_SIZE]
                if (mRafInput.read(buf) != buf.length) {
                    mFoundBad = true
                    throw new IOException("Reading extra field length failed")
                }
                /*
                 * Fetches unsigned 16-bit value from byte array at specified
                 * offset. The bytes are assumed to be in Intel (little-endian)
                 * byte order.
                 */
                val extraLen = (buf[0] & 0xff) | ((buf[1] & 0xff) << 8)

                val headerSize = ZIP_ENTRY_HEADER_LEN + extraLen
                        + entry.getName().getBytes(Texts.UTF8).length

                if (entry.getMethod() != ZipEntry.STORED) {
                    /*
                     * The entry is compressed.
                     */
                    sendNotification(
                            MSG_INFO,
                            mProgress += progress,
                            Texts.NULL,
                            "%,15d  %s  (%s - %s)\n".format(dataOffset
                                    + headerSize, entry.getName(),
                                    Messages.getString(R.string.ok),
                                    Messages.getString(R.string.compressed)))
                } else {
                    /*
                     * The entry is not compressed.
                     */
                    if ((dataOffset + headerSize) % alignment != 0) {
                        sendNotification(
                                MSG_INFO,
                                mProgress += progress,
                                Texts.NULL,
                                "%,15d  %s  (%s - %s)\n".format(
                                        dataOffset + headerSize,
                                        entry.getName(),
                                        Messages.getString(R.string.BAD),
                                        Texts.sizeToStr((dataOffset + headerSize)
                                                % alignment)))
                        mFoundBad = true
                    } else {
                        sendNotification(
                                MSG_INFO,
                                mProgress += progress,
                                Texts.NULL,
                                "%,15d  %s  (%s)\n".format(dataOffset
                                        + headerSize, entry.getName(),
                                        Messages.getString(R.string.ok)))
                    }
                }

                var flags =
                    if (entry.getMethod() == ZipEntry.STORED) 0
                    else 1 << 3
                flags |= 1 << 11
                val dataSize =
                    if ((flags & ZIP_ENTRY_USES_DATA_DESCR) != 0) {
                        if (entry.isDirectory()) 0
                        else entry.getCompressedSize() + ZIP_ENTRY_DATA_DESCRIPTOR_LEN
                    } else {
                        if (entry.isDirectory()) 0
                        else entry.getCompressedSize()
                    }

                if (Sys.DEBUG)
                    L.d("size = %,8d, compressed = %,8d, crc32 = %08x, data mHeaderOffset = %,8d >> %,8d"
                            + " >> Entry '%s'", entry.getSize(),
                            entry.getCompressedSize(), entry.getCrc(),
                            dataOffset, dataOffset + headerSize,
                            entry.getName())

                dataOffset += headerSize + dataSize
            }// while
        }// verify()

        /**
         * Closes source files.
         * <p>
         * This takes 5% of total.
         * </p>
         *
         * @throws IOException
         */
        private def closeFiles() = {
            mZipFile.close()
            mRafInput.close()

            sendNotification(
                    MSG_INFO,
                    mProgress = 100,
                    Texts.NULL,
                    "\n%s".format(
                            if (mFoundBad)
                                Messages.getString(R.string.verification_failed)
                            else
                                Messages.getString(R.string.verification_succesful)))
        }// closeFiles()

    }// ZipAlignmentVerifier

}
