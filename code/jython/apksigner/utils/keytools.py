#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
Utilities for keystore files.
'''

import hashlib

import files
import texts

from apksigner.i18n import messages, string

from java.io import BufferedInputStream, FileInputStream, InputStream
from java.security import KeyStore
from java.security.cert import Certificate, X509Certificate


KEYSTORE_TYPE_JKS = 'JKS'
KEYSTORE_TYPE_JCEKS = 'JCEKS'
KEYSTORE_TYPE_PKCS12 = 'PKCS12'
DEFAULT_KEYSTORE_TYPE = KEYSTORE_TYPE_JKS

def list_entries(key_file, keystore_type, storepass):
    ''' Lists entries in a keystore file.

        Parameters:

        :key_file:
            the keystore file.
        :keystore_type:
            the keystore type.
        :storepass:
            the keystore password.

        Returns:
            the entries inside given keystore file.
    '''

    result = ''

    try:
        input_stream = BufferedInputStream(FileInputStream(key_file),
                                           files.FILE_BUFFER)
        try:
            key_store = KeyStore.getInstance(keystore_type)
            key_store.load(input_stream, storepass)

            # HEADER

            result += '{}: {}\n'.format(
                messages.get_string(string.keystore_type), key_store.getType())
            result += '{}: {}\n'.format(
                messages.get_string(string.keystore_provider),
                key_store.getProvider())
            result += '\n'

            entry_count = key_store.size()
            if entry_count <= 1:
                result += messages.get_string(
                    string.pmsg_your_keystore_contains_x_entry, entry_count)
            else:
                result += messages.get_string(
                    string.pmsg_your_keystore_contains_x_entries, entry_count)
            result += '\n\n'

            # ENTRIES

            aliases = key_store.aliases()
            while aliases.hasMoreElements():
                alias = aliases.nextElement()
                cert = key_store.getCertificate(alias)

                result += '{}: {}\n'.format(
                    messages.get_string(string.alias_name), alias)
                result += '{}: {}\n'.format(
                    messages.get_string(string.creation_date),
                    key_store.getCreationDate(alias))
                result += '{}: {}\n'.format(
                    messages.get_string(string.entry_type), cert.getType())

                certChain = key_store.getCertificateChain(alias)
                if certChain:
                    result += '{}: {:,}\n'.format(
                        messages.get_string(string.certificate_chain_length),
                        len(certChain))
                    for i in range(len(certChain)):
                        result += '\t{}[{:,}]:\n'.format(
                            messages.get_string(string.certificate), i + 1)

                        if type(certChain[i]) == X509Certificate:
                            x509Cert = X509Certificate(certChain[i])

                            result += '\t\t{}: {}\n'.format(
                                messages.get_string(string.owner),
                                x509Cert.getIssuerX500Principal().getName())
                            result += '\t\t{}: {}\n'.format(
                                messages.get_string(string.issuer),
                                x509Cert.getIssuerX500Principal().getName())
                            result += '\t\t{}: %x\n'.format(
                                messages.get_string(string.serial_number),
                                x509Cert.getSerialNumber())
                            result += '\t\t'
                            result += messages.get_string(
                                string.pmsg_valid_from_until,
                                x509Cert.getNotBefore(),
                                x509Cert.getNotAfter())
                            result += '\n'
                            #.if

                        result += '\t\t{}:\n'.format(
                            messages.get_string(string.certificate_fingerprints))
                        for algorithm in ['MD5','SHA-1', 'SHA-256']:
                            h = hashlib.new(algorithm)
                            h.update(certChain[i].getEncoded())
                            result += '\t\t\t{}: {}\n'.format(
                                algorithm,
                                texts.hex_to_fingerprint(h.hexdigest().upper()))
                            #.for
                        #.for
                    #.if
                #.while
        finally: input_stream.close()
    except Exception as e:
        result += str(e)

    return result
    #.list_entries()

def get_aliases(key_file, keystore_type, storepass):
    ''' Gets all alias names from ``key_file``.

        Parameters:

        :key_file:
            the keyfile.
        :keystore_type:
            the keystore type.
        :storepass:
            the password.

        Returns:
            list of alias names, can be empty.
    '''

    result = []

    try:
        input_stream = BufferedInputStream(FileInputStream(key_file),
                                           files.FILE_BUFFER)
        try:
            key_store = KeyStore.getInstance(keystore_type)
            key_store.load(input_stream, storepass)

            aliases = key_store.aliases()
            while aliases.hasMoreElements():
                result += [aliases.nextElement()]
        finally: input_stream.close()
    except Exception as e:
        # Ignore it
        pass

    return result
    #.get_aliases()
