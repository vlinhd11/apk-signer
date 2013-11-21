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

        **NOTE:** Currently this function doesn't work because of issue #2103:
        http://bugs.jython.org/issue2103 -- You should use the Java wrapper
        instead: ``open_java_url(url)``.

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
        if settings.get_proxy_username() and settings.get_proxy_password():
            pwd_mgr = urllib2.HTTPPasswordMgrWithDefaultRealm()
            pwd_mgr.add_password(None, settings.get_proxy_host(),
                                 settings.get_proxy_username(),
                                 settings.get_proxy_password())
            proxy_auth_handler = urllib2.ProxyBasicAuthHandler(pwd_mgr)

        opener = urllib2.build_opener(proxy_handler, proxy_auth_handler)
        try:
            return opener.open(url, timeout=NETWORK_TIMEOUT)
        except:
            # Perhaps there is no available Internet connections.
            pass
        #.if

    try:
        return urllib2.urlopen(url, timeout=NETWORK_TIMEOUT)
    except:
        # Perhaps there is no available Internet connections.
        pass

    #.open_url()

def open_java_url(url):
    ''' Opens new connection to ``url`` with default settings.

        Parameters:

        :url (String):
            the URL.

        Returns:
            the ``java.net.HttpURLConnection``, or ``None`` if an error
            occurred.
    '''

    PROPERTY_SYS_HTTP_PROXY_HOST = 'http.proxyHost'
    PROPERTY_SYS_HTTP_PROXY_PORT = 'http.proxyPort'

    PROPERTY_SYS_HTTPS_PROXY_HOST = 'https.proxyHost'
    PROPERTY_SYS_HTTPS_PROXY_PORT = 'https.proxyPort'

    if not url: return

    # Proxies

    from java.lang import System
    if settings.is_using_proxy():
        System.setProperty(PROPERTY_SYS_HTTP_PROXY_HOST,
                           settings.get_proxy_host())
        System.setProperty(PROPERTY_SYS_HTTP_PROXY_PORT,
                           str(settings.get_proxy_port()))

        System.setProperty(PROPERTY_SYS_HTTPS_PROXY_HOST,
                           settings.get_proxy_host())
        System.setProperty(PROPERTY_SYS_HTTPS_PROXY_PORT,
                           str(settings.get_proxy_port()))
    else:
        for s in [ PROPERTY_SYS_HTTP_PROXY_HOST, PROPERTY_SYS_HTTP_PROXY_PORT,
                   PROPERTY_SYS_HTTPS_PROXY_HOST, PROPERTY_SYS_HTTPS_PROXY_PORT ]:
            System.clearProperty(s)

    # Now create connection

    try:
        from java.net import URL
        conn = URL(url).openConnection()

        if settings.is_using_proxy():
            if settings.get_proxy_username() and settings.get_proxy_password():
                import base64
                proxy_auth = base64.urlsafe_b64encode('{}:{}'.format(
                    settings.get_proxy_username(),
                    settings.get_proxy_password()))
                # https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
                conn.setRequestProperty('Proxy-Authorization',
                                        'Basic ' + proxy_auth)

        conn.setConnectTimeout(NETWORK_TIMEOUT * 1000)
        conn.setReadTimeout(NETWORK_TIMEOUT * 1000)
        return conn
    except:
        # Perhaps there is no available Internet connections.
        pass
    #.open_java_url()