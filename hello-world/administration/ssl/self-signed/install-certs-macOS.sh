#!/bin/sh
# Read from config:
__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source ${__dir}/configure.sh

echo "Script requires sudo access to install root cert"
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain $CA_NAME.pem
sudo bash -c "echo ::1 $HOSTNAME >> /etc/hosts"


