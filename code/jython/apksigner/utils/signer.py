#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
Tool to sign APK files.
'''

import os, re, subprocess

import files, texts

from apksigner.i18n import messages, string

'''
Used to append to newly signed target's file name.
'''
SIGNED = 'SIGNED'

'''
Used to append to newly signed target's file name.
'''
SIGNED_UNALIGNED = 'SIGNED_UNALIGNED'

def sign(jdk_path, target_file, key_file, storepass, alias, keypass):
    ''' Signs an APK file.

        Parameters:

        :jdk_path:
            the path to JDK's `bin` directory, can be ``None`` on Unix system.
        :target_file:
            the target file, can be an APK, JAR or ZIP.
        :key_file:
            the keystore file.
        :storepass:
            the keystore's password.
        :alias:
            the keystore alias.
        :keypass:
            the keystore's alias password.

        Returns:
            ``None`` if everything is OK, or an error message if it occurred.
    '''

    # JDK for Linux does not need to specify full path
    jarsigner = 'jarsigner'
    if jdk_path and os.path.isdir(jdk_path):
        jarsigner = os.path.join(jdk_path, jarsigner + '.exe')

    # jarsigner -keystore KEY_FILE -sigalg MD5withRSA -digestalg SHA1
    # -storepass STORE_PASS -keypass KEY_PASS APK_FILE ALIAS_NAME
    cmd = [ jarsigner, '-keystore', key_file, '-sigalg', 'MD5withRSA',
            '-digestalg', 'SHA1', '-storepass', storepass, '-keypass', keypass,
            target_file, alias ]
    try:
        output = subprocess.check_output(cmd)
    except Exception as e:
        return e.output

    if not output: return output

    ### RENAMES NEWLY SIGNED FILE...

    OLD_NAME = os.path.basename(target_file)
    if re.match(r'(?si).*?unsigned.+', OLD_NAME):
        if re.match(texts.REGEX_APK_FILES, OLD_NAME):
            new_name = re.sub(r'(?si)unsigned', SIGNED_UNALIGNED, OLD_NAME, 1)
        else:
            new_name = re.sub(r'(?si)unsigned', SIGNED, OLD_NAME, 1)
    elif re.match(texts.REGEX_APK_FILES, OLD_NAME):
        new_name = files.append_filename(OLD_NAME, '_' + SIGNED_UNALIGNED)
    elif re.match(texts.REGEX_JAR_FILES, OLD_NAME) or \
        re.match(texts.REGEX_ZIP_FILES, OLD_NAME):
        new_name = files.append_filename(OLD_NAME, '_' + SIGNED)
    else:
        new_name = '{}_{}'.format(OLD_NAME, SIGNED)

    new_name = os.path.join(os.path.dirname(target_file), new_name)
    try:
        os.rename(target_file, new_name)
    except:
        return messages.get_string(
            string.pmsg_file_is_signed_but_cannot_be_renamed_to_new_one, new_name)
    #.sign()