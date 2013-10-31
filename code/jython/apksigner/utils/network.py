#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

'''
Network utilities.
'''

import urllib2

from apksigner.utils import settings

'''
The network timeout, in seconds.
'''
NETWORK_TIMEOUT = 15

'''
Max redirection allowed.
'''
MAX_REDIRECTION_ALLOWED = 9

'''
Header field "Location".
'''
HEADER_LOCATION = 'Location'

'''
The date format of header fields.
'''
HEADER_DATE_FORMAT = 'EEE, dd MMM yyyy HH:mm:ss z'

'''
Header field 'Expires'.
'''
HEADER_EXPIRES = 'Expires'

def open_url(url):
    ''' Opens new connection to ``url`` with default settings.

        Parameters:

        :url (String):
            the URL.

        Returns:
            the connection, or ``None`` if an error occurred.
    '''

    if not url: return

    if settings.is_using_proxy():
        _ = '{}:{}'.format(settings.get_proxy_host(), settings.get_proxy_port())
        proxy_handler = urllib2.ProxyHandler({ 'http': _, 'https': _ })

        proxy_auth_handler = None
        if settings.get_proxy_username and settings.get_proxy_password:
            pwd_mgr = urllib2.HTTPPasswordMgrWithDefaultRealm()
            pwd_mgr.add_password(None, settings.get_proxy_host(),
                                 settings.get_proxy_username(),
                                 settings.get_proxy_password())
            proxy_auth_handler = urllib2.ProxyBasicAuthHandler(pwd_mgr)

        opener = urllib2.build_opener(proxy_handler, proxy_auth_handler)
        return opener.open(url, timeout=NETWORK_TIMEOUT)

    return urllib2.urlopen(url, timeout=NETWORK_TIMEOUT)

    #.open_url()
