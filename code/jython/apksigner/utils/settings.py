#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
Convenient module for storing / loading settings.

@author Hai Bison
'''

import json, os, sys

import system

from apksigner.i18n import messages
from simple_weak_encryption import *

SETTINGS_FILENAME = system.APP_NAME + '-settings.json'

_settings = {}
_transaction = None

def begin_transaction():
    global _transaction
    if _transaction == None: _transaction = {}
    #.begin_transaction()

def end_transaction():
    if not _transaction: return

    _settings.update(_transaction)
    global _transaction
    _transaction = None
    #.end_transaction()

def destroy_transaction():
    global _transaction
    if _transaction: _transaction = None
    #.destroy_transaction()

def cancel_transaction():
    destroy_transaction()
    #.cancel_transaction()

def load():
    ''' Loads settings from file.
    '''

    global _settings

    try:
        with open(os.path.join(os.path.dirname(sys.argv[0]), SETTINGS_FILENAME),
                  'r') as f:
            _settings = json.loads(f.read())
    except Exception as e:
        _settings = {}
        print(' ! Error reading settings: {}'.format(e))
    #.load()

def store():
    ''' Stores all settings to file.
    '''

    try:
        with open(os.path.join(os.path.dirname(sys.argv[0]), SETTINGS_FILENAME),
                  'w') as f:
            f.write(json.dumps(_settings))
    except Exception as e:
        print(' ! Error storing settings: {}'.format(e))
    #.store()

def settings():
    ''' Gets settings, loads them from file if not yet.
    '''

    if _transaction: return _transaction
    if not _settings: load()
    return _settings
    #.settings()

### PREFERENCES

KEY_JDK_PATH = 'JDK_PATH'
KEY_LOCALE = 'locale'
KEY_UID = 'uid'
KEY_NETWORK_USE_PROXY = 'network.use_proxy'
KEY_NETWORK_PROXY_HOST = 'network.proxy.host'
KEY_NETWORK_PROXY_PORT = 'network.proxy.port'
KEY_NETWORK_PROXY_USERNAME = 'network.proxy.username'
KEY_NETWORK_PROXY_PASSWORD = 'network.proxy.password'

def uid():
    ''' Gets global unique ID.
    '''

    # DON'T use settings() to avoid of transaction.

    _ = _settings.get(KEY_UID)
    if not _:
        import uuid
        _ = uuid.uuid4()
        _settings[KEY_UID] = _
    return _
    #.uid()

def x_set(key, value):
    ''' Encrypt ``value`` before putting it to settings.
    '''
    if value:
        settings()[key] = encrypt_to_base64(uid(), str(value))
    elif key in settings():
        del settings()[key]
    #.x_set()

def x_get(key, default=None):
    value = settings().get(key)
    return decrypt_from_base64(udi(), value) if value else default
    #.x_get()

def get_jdk_path():
    ''' Gets JDK path (a string), can be ``None``.
    '''

    return settings().get(KEY_JDK_PATH)
    #.get_jdk_path()

def set_jdk_path(path):
    ''' Sets JDK path.
    '''

    if path: settings()[KEY_JDK_PATH] = path
    elif KEY_JDK_PATH in settings(): del settings()[KEY_JDK_PATH]
    #.set_jdk_path()

def get_locale():
    ''' Gets locale, default is ``messages.DEFAULT_LOCALE``.
    '''

    return settings().get(KEY_LOCALE, messages.DEFAULT_LOCALE)
    #.get_locale()

def set_locale(locale):
    ''' Sets locale.
    '''

    settings()[KEY_LOCALE] = locale
    #.set_locale()

def is_using_proxy():
    ''' Checks if we're using a proxy.
    '''

    return settings().get[KEY_NETWORK_USE_PROXY]
    #.is_using_proxy()

def set_using_proxy(using_proxy):
    ''' Sets using proxy.
    '''

    settings()[KEY_NETWORK_USE_PROXY] = using_proxy
    #.set_using_proxy()

def get_proxy_host():
    ''' Gets proxy host.
    '''

    return settings().get(KEY_NETWORK_PROXY_HOST)
    #.get_proxy_host()

def set_proxy_host(proxy_host):
    ''' Sets proxy host.
    '''

    settings()[KEY_NETWORK_PROXY_HOST] = proxy_host
    #.set_proxy_host()

def get_proxy_port():
    ''' Gets proxy port.
    '''

    return settings().get(KEY_NETWORK_PROXY_PORT)
    #.get_proxy_port()

def set_proxy_port(proxy_port):
    ''' Sets proxy port.
    '''

    settings()[KEY_NETWORK_PROXY_PORT] = proxy_port
    #.set_proxy_port()

def get_proxy_username():
    ''' Gets proxy username.
    '''

    return x_get(KEY_NETWORK_PROXY_USERNAME)
    #.get_proxy_username()

def set_proxy_username(proxy_username):
    ''' Sets proxy username.
    '''

    x_set(KEY_NETWORK_PROXY_USERNAME, proxy_username)
    #.set_proxy_username()

def get_proxy_password():
    ''' Gets proxy password.
    '''

    return x_get(KEY_NETWORK_PROXY_PASSWORD)
    #.get_proxy_password()

def set_proxy_password(proxy_password):
    ''' Sets proxy password.
    '''

    x_set(KEY_NETWORK_PROXY_PASSWORD, proxy_password)
    #.set_proxy_password()