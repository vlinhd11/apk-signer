#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
This is the **simple-and-weak** encryption utilities.
'''

import array
import base64
import sys

from java.security.spec import KeySpec

from javax.crypto import Cipher, SecretKey, SecretKeyFactory
from javax.crypto.spec import IvParameterSpec, PBEKeySpec, SecretKeySpec

_TRANSFORMATION = "AES/CBC/PKCS5Padding"
_SECRET_KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1"
_SECRET_KEY_SPEC_ALGORITHM = "AES"

'''
Only ``128`` bits. If this is ``256``, some JVMs must need extra tools to be
installed. Poor Java :|
'''
_KEY_LEN = 128
_ITERATION_COUNT = 2**16
_SEPARATOR = '\t'

_UTF8 = 'UTF-8'

def encrypt_to_base64(password, data):
    ''' Encrypts ``data`` (String) by ``password`` (char[]).
    '''

    cipher = Cipher.getInstance(_TRANSFORMATION)
    cipher.init(Cipher.ENCRYPT_MODE, gen_key(password))

    data = cipher.doFinal(data.encode(_UTF8))

    return '{}{}{}'.format(base64.b64encode(cipher.getIV().tostring()),
                           _SEPARATOR, base64.b64encode(data.tostring()))
    #.encrypt_to_base64()

def decrypt_from_base64(password, data):
    ''' Decrypts an encrypted string (``data`` -- String) by ``password``
        (char[]).

        Returns:
            the decrypted string, or ``None``.
    '''

    cipher = Cipher.getInstance(_TRANSFORMATION)
    idx = data.find(_SEPARATOR)
    if idx <= 0: return

    try:
        cipher.init(
            Cipher.DECRYPT_MODE,
            gen_key(password),
            IvParameterSpec(array.array('b', base64.b64decode(data[:idx]))))
    except:
        print(' ! Error: {}'.format(sys.exc_info()[1]))
        # Ignore it
        return

    try:
        data = array.array('b', base64.b64decode(data[idx+1:]))
        return cipher.doFinal(data).tostring()
    except:
        print(' ! Error: {}'.format(sys.exc_info()[1]))
        # Ignore it
    #.decrypt_from_base64()

def gen_key(password):
    ''' Generates secret key.

        Parameters:

        :password (char[]):
            the password.
    '''

    factory = SecretKeyFactory.getInstance(_SECRET_KEY_FACTORY_ALGORITHM)
    spec = PBEKeySpec(password, str(password).encode(_UTF8),
                      _ITERATION_COUNT, _KEY_LEN)
    tmp = factory.generateSecret(spec)

    return SecretKeySpec(tmp.getEncoded(), _SECRET_KEY_SPEC_ALGORITHM)
    #.gen_key()