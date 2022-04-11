#!/bin/sh

########################################################
# Define your information here. This script is loaded by other scripts in this directory.
########################################################
HOSTNAME=dev.internal
CA_NAME=development_ca
# Two character country code only.
COUNTRY_CODE=DE
STATE_NAME=Brandonburg
CITY=Berlin
ORGANIZATION=Personal

########################################################
#    Code follows.
########################################################


# Any file is fine, we use '.local.pwd' because we have ignored *.local in our gitignore file.
PASS_FILE=./$HOSTNAME.local.pwd

if [ -f $PASS_FILE ]; then
    echo "Reading password from $PASS_FILE file"
    PASSWORD=$(cat "$PASS_FILE")
else
  # Create pseudo random password and save it to the pass file for later - e.g. to load the cert into the keystore
  # If .gitIgnore is configured properly the password file WILL NOT be committed.
  PASSWORD="$(openssl rand -base64 64 | head -c 32;echo;)"
  echo "PASSWORD=$PASSWORD" > "$PASS_FILE"
  echo "Wrote password to new $PASS_FILE file. Do not commit this file to source control!"
  # Read it from the file to get the correct padding. I'm sure there's a better way...
  PASSWORD=$(cat "$PASS_FILE")
fi

