#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
Helper class to generate keystore file.
'''

import os, subprocess

def gen_key(jdk_path, target, storepass, alias, keypass, alias_years, co_name,
            ou_name, o_name, city, state, country):
    ''' Generates new keystore file.

        Parameters:

        :jdk_path:
                the JDK path, can be ``None`` on Unix system.
        :target:
                the target file which will be generated to.
        :storepass:
                the keystore's password.
        :alias:
                the keystore's alias name.
        :keypass:
                the keystore's alias password.
        :alias_years:
                the validity, in years.
        :co_name:
                the company name.
        :ou_name:
                the organization unit name.
        :o_name:
                the organization name.
        :city:
                the city name.
        :state:
                the state name.
        :country:
                the country ISO code.

        Returns:
            ``None`` if OK, or some error message.
    '''

    if os.path.isfile(target):
        try: os.remove(target)
        except:
            print(' ! Can\'t delete "{}"'.format(os.path.basename(target)))
            return

    # keytool -genkey -sigalg MD5withRSA -digestalg SHA1 -alias ALIAS_NAME
    # -keypass KEY_PASS -validity YEARS -keystore TARGET_FILE -storepass
    # STORE_PASS -genkeypair -dname
    # "CN=Mark Jones, OU=JavaSoft, O=Sun, L=city, S=state C=US"

    certs = { 'CN': co_name, 'OU': ou_name, 'O': o_name, 'L': city, 'S': state,
              'C': country }
    dname = []
    for key in certs:
        if certs[key]:
            dname += ['{}={}'.format(key, certs[key])]
    dname = ' '.join(dname)

    # JDK for Linux does not need to specify full path
    keytool = 'keytool'
    if jdk_path and os.path.isdir(jdk_path):
        keytool = os.path.join(jdk_path, keytool + '.exe')

    cmd = [ keytool, '-genkey', '-keyalg', 'RSA', '-alias', alias, '-keypass',
            keypass, '-validity', str(alias_years), '-keystore', target,
            '-storepass', storepass, '-genkeypair', '-dname', dname ]

    try:
        subprocess.check_output(cmd)
    except Exception as e:
        return str(e.output)

    if not os.path.isfile(target):
        return ' ! Can\'t generate target file "{}"'\
               .format(os.path.basename(target))
    #.gen_key()