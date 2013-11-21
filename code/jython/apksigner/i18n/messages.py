# -*- coding: utf-8 -*-
#  Copyright (C) 2012 Hai Bison
#
#  See the file LICENSE at the root directory of this project for copying
#  permission.

DEFAULT_LOCALE = 'en_US'

'''
Map of available locale tags to their human readable names.
'''
AVAILABLE_LOCALES = {
    DEFAULT_LOCALE: "English (Default)",
    "vi_VN": "Vietnamese (Tiếng Việt)"
}

'''
Map of locale tags to real data.
'''
LOCALES = {}

def get_locale_filename(locale):
    ''' Gets the locale filename.
    '''
    return 'messages_{}.json'.format(locale)
    #.get_locale_filename()

def get_string(key):
    ''' Gets a string for current locale by its key. If it's not available for
        current locale, the default string in built-in locale (English) will
        return.

        Parameters:

        :key (string):
            the string's key.

        Returns:

            The string, or ``None`` if not found.
    '''

    from apksigner.utils import settings
    LOCALE = settings.get_locale()

    def load_data(filename):
        ''' Loads data.
        '''

        import json, sys, system, zipfile
        with zipfile.ZipFile(sys.argv[0], 'r') as z:
            info = z.getinfo(system.APP_NAME + '/i18n/' + filename)
            return json.loads(z.read(info))
        #.load_data()

    if not LOCALES.get(LOCALE):
        LOCALES[LOCALE] = load_data(get_locale_filename(LOCALE))
        #.if

    s = LOCALES[LOCALE][key]
    if not s and LOCALE != DEFAULT_LOCALE:
        if not LOCALES.get(DEFAULT_LOCALE):
            LOCALES[DEFAULT_LOCALE] = load_data(get_locale_filename(DEFAULT_LOCALE))
        s = LOCALES[DEFAULT_LOCALE][key]
    return s
    #.get_string()

'''
Map of resources IDs to their name.
'''
MAP_IDS = {}

def get_string(res_id, *args):
    ''' Gets a string by its resource ID. If it's not available for current
        locale, the default string in built-in locale (English) will return.

        Parameters:

            ``res_id``
                the resource ID.
            ``*args``
                The format arguments, if you want to format the output string.

        Returns:
            The string, or ``None`` if not found.
    '''

    if MAP_IDS.has_key(res_id):
        s = get_string(MAP_IDS[res_id])
        return s.format(*args) if args else s

    import string
    for name in dir(string):
        attr = getattr(string, name)
        if attr == res_id:
            MAP_IDS[res_id] = name
            s = get_string(name)
            return s.format(*args) if args else s

    #.get_string()
