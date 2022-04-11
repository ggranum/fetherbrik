#!/bin/sh

__dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source ${__dir}/configure.sh

if [ -f ./$CA_NAME.pem ]; then
    echo "It looks like you have certs already present in this directory. Exiting to avoid overwriting existing data"
    exit 1
fi


# Thanks to the kind post here: https://stackoverflow.com/a/60516812/1867101 for the concise descriptoin of
# creating a certificate authority (CA) (to get Chrome to shut up and let us by)

# Specify a Hostname. Technically you should on the domain, because ICANN didn't reserve any TLDs for internal use.
# But, not to put too fine a point on it, fuck the sellouts at ICANN.
# probably don't use .local, MacOS uses it.
# Some good suggestions here if you do not own your own domain name:
#         https://serverfault.com/questions/17255/top-level-domain-domain-suffix-for-private-network
### Lightly modified from https://stackoverflow.com/a/60516812/1867101 ###

# Generate private key for root cert
echo Generate private key for root cert to $CA_NAME.key
openssl genrsa -des3  -passout pass:"$PASSWORD" -out $CA_NAME.key 2048
# Generate root certificate
echo "Generate root certificate to $CA_NAME.pem ( /C=$COUNTRY_CODE/ST=$STATE_NAME/L=$CITY/O=$ORGANIZATION/CN=$HOSTNAME )"
openssl req -x509 -new -passin pass:"$PASSWORD"\
    -subj "/C=$COUNTRY_CODE/ST=$STATE_NAME/L=$CITY/O=$ORGANIZATION/CN=$HOSTNAME" \
    -nodes -key $CA_NAME.key -sha256 -days 825 -out $CA_NAME.pem
######################
# Create CA-signed certs
######################


# Generate a private key for cert
echo Generate a private key for cert
openssl genrsa -out $HOSTNAME.key 2048 -passout pass:"$PASSWORD"
# Create a certificate-signing request ('CSR')
echo "Create a certificate-signing request ( /C=$COUNTRY_CODE/ST=$STATE_NAME/L=$CITY/O=$ORGANIZATION/CN=$HOSTNAME )"
openssl req -new -key $HOSTNAME.key -out $HOSTNAME.csr\
     -subj "/C=$COUNTRY_CODE/ST=$STATE_NAME/L=$CITY/O=$ORGANIZATION/CN=$HOSTNAME" \
# Create a config file for the extensions
echo Create a config file for the extensions
>$HOSTNAME.ext cat <<-EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
extendedKeyUsage=serverAuth,clientAuth
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names
[alt_names]
DNS.1 = $HOSTNAME # Be sure to include the domain name here because Common Name is not so commonly honoured by itself
DNS.2 = bar.$HOSTNAME # Optionally, add additional domains (I've added a subdomain here)
IP.1 = 192.168.0.13 # Optionally, add an IP address (if the connection which you have planned requires it)
EOF
# Create the signed certificate
echo Create the signed certificate
openssl x509 -req -in $HOSTNAME.csr \
     -passin pass:"$PASSWORD" \
     -CA $CA_NAME.pem -CAkey $CA_NAME.key -CAcreateserial \
     -out $HOSTNAME.crt -days 825 -sha256 -extfile $HOSTNAME.ext


 # Add the cert and key into a pkcs12 file because that's what Java's Keytool supports.
  echo "Adding key and cert to pkcs12 file"
  openssl pkcs12 -export \
          -in $HOSTNAME.crt \
          -inkey $HOSTNAME.key \
          -passin pass:$PASSWORD \
          -out $HOSTNAME.pkcs12 -passout pass:$PASSWORD

  # You now have a pkcs12 file that can be imported into a Java keystore, and a cert that can be loaded into your
  # operating systems keychain/trust store. E.g. for MacOS, you'll need to drag the jetty.crt file into
  # the Certificates tab of the Keychain Access app, and set it to trusted.

    echo "Creating keystore and keystore.properties using a random password."
    STORE_PASS=$(openssl rand -base64 64 | head -c 32;echo;)
    echo "keystore_password=$STORE_PASS
  keystore_manager_password=$STORE_PASS
  truststore_password=$STORE_PASS" > keystore.properties
  #https://docs.oracle.com/en/java/javase/12/tools/keytool. (see -importkeystore section)
    echo "Attempting to import jetty cert into a new keystore"
    keytool -importkeystore -noprompt \
     -alias 1\
     -srckeystore $HOSTNAME.pkcs12\
     -srcstoretype PKCS12\
     -srcstorepass "$PASSWORD"\
     -destkeystore keystore\
     -deststorepass "$STORE_PASS"\
     -destalias $HOSTNAME

  cp keystore ../../etc
  cp keystore.properties ../../etc

rm $HOSTNAME.ext
rm $HOSTNAME.key
rm $HOSTNAME.local.pwd
rm $HOSTNAME.csr
rm $HOSTNAME.pkcs12
rm $CA_NAME.srl
rm $CA_NAME.key
