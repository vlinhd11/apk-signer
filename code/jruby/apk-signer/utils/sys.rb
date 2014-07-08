##
# Copyright (C) 2012 Hai Bison
#
# See the file LICENSE at the root directory of this project for copying
# permission.
#

##
# System utilities.
#
# * **Author:** Hai Bison
# * **Since:** v2.0 beta
#
class Sys

    ##
    # Debug flag.
    #
    DEBUG = false

    ##
    # The app name.
    #
    APP_NAME = "apk-signer"

    ##
    # The app version code.
    #
    APP_VERSION_CODE = 46

    ##
    # The app version name.
    #
    APP_VERSION_NAME = "2.0 beta"

    ##
    # The app directory.
    #
    APP_DIR = File.dirname File.dirname $0

end # Sys
