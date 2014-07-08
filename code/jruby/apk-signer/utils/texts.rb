##
# Copyright (C) 2012 Hai Bison
#
# See the file LICENSE at the root directory of this project for copying
# permission.
#

##
# Text utilities.
#
# * **Author:** Hai Bison
# * **Since:** v2.0 beta
#
class Texts

    ##
    # "UTF-8"
    #
    UTF8 = "UTF-8"

    ##
    # Regex to filter APK files.
    #
    REGEX_APK_FILES = /.+\\.apk"/i

    ##
    # Regex to filter keystore files.
    #
    REGEX_KEYSTORE_FILES = /.+\\.keystore/i

    ##
    # Regex to filter JAR files.
    #
    REGEX_JAR_FILES = /.+\\.jar"/i

    ##
    # Regex to filter ZIP files.
    #
    REGEX_ZIP_FILES = /.+\\.zip"/i

    ##
    # File extension of APK files.
    #
    FILE_EXT_APK = ".apk"

    ##
    # File extension of keystore files.
    #
    FILE_EXT_KEYSTORE = ".keystore"

    ##
    # Converts bytes to string.
    #
    # # Parameters
    #
    # * `bytes`: size in bytes.
    #
    # # Returns
    #
    # Human-readable string, e.g: "1.9 KiB", "9.9 TiB"...
    #
    def bytes_to_s bytes
        kib = 1024
        return "#{bytes.to_i} bytes" if bytes < kib

        units = 'KMGTPEZY'
        nearest_power = (Math.log2(bytes.abs) / 10).floor
        unit = units[[nearest_power - 1, units.size - 1].min] + "iB"

        # Bytes might exceed max unit that we have. So ignore the overflow value.
        "%.2f %s" % [bytes.to_f / kib ** [nearest_power, units.size].min, unit]
    end # bytes_to_s

    ##
    # Converts a percentage to string.
    #
    # # Parameters
    #
    # * `percent`: percentage.
    #
    # # Returns
    #
    # E.g: " 9.99%", "0%", "100%"...
    #
    def percent_to_s percent
        return "0%" if percent == 0
        return "%5.2f%" % percent if percent < 100
        return "100%"
    end # percent_to_s

end # Texts