#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.


'''
Test utilities.
'''

UTF8 = 'UTF-8'
EMPTY = ''
''' Regex to filter APK files.
'''
REGEX_APK_FILES = r'(?si).+\.apk'
''' Regex to filter keystore files.
'''
REGEX_KEYSTORE_FILES = r'(?si).+\.keystore'
''' Regex to filter JAR files.
'''
REGEX_JAR_FILES = r'(?si).+\.jar'
''' Regex to filter ZIP files.
'''
REGEX_ZIP_FILES = r'(?si).+\.zip'
''' File extension of APK files.
'''
FILE_EXT_APK = '.apk'
''' File extension of keystore files.
'''
FILE_EXT_KEYSTORE = '.keystore'

def size_to_str(size):
    ''' Converts {@code size} (in bytes) to string. This tip is from:

            http://stackoverflow.com/a/5599842/942821

        Parameters:

        :size (double):
            the size in bytes.

        Returns:

            E.g: ``128 B``, ``1.5 KiB``, ``10 MiB``...
    '''

    import math

    if size <= 0: return '0 B'

    UNITS = [ '', 'Ki', 'Mi', 'Gi', 'Ti', 'Pi', 'Ei', 'Zi', 'Yi' ]
    BLOCK_SIZE = 1024

    digit_groups = int(math.log10(size) / math.log10(BLOCK_SIZE))
    if (digit_groups >= len(UNITS)):
        digit_groups = len(UNITS) - 1
    size = size / math.pow(BLOCK_SIZE, digit_groups)

    return '{} {{}}B' \
               .format('{:,.0f}' if digit_groups == 0 else '{:,.2f}') \
               .format(size, UNITS[digit_groups])
    #.size_to_str()

def percent_to_str(percent):
    ''' Converts a percentage to string.
    '''
    if not percent:
        return '0%'
    elif percent < 100:
        return '{:.02f}%'.format(percent)
    else:
        return '100%'
    #.percent_to_str()

def format_date(date):
    ''' Formats an instance of ``java.util.Date``.
    '''
    import java
    return java.text.DateFormat.getInstance().format(date)
    #.format_date()

def hex_to_fingerprint(s):
    ''' Formats a hex string ``s`` as a digital fingerprint.
    '''
    return ':'.join([s[i:i+2] for i in range(0,len(s),2)])
    #.hex_to_fingerprint()