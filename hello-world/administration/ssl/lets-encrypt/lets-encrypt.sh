#!/bin/sh


printHelp() {
  echo "Install utility for LetsEncrypt certs. Assumes you've already created your certs"
  echo " using the Certbot. E.g. something like"
  echo "> sudo certbot certonly --manual --preferred-challenges=dns -d example.com"
  echo " which will output certs to: "
  echo "/etc/letsencrypt/live/example.com/"
  echo ""
  echo "This script will prompt for your sudo password. It should not be run as sudo user, as that will cause"
  echo "the generated keystore file to be be owned by said sudo user. Better to run this as the server user."
  echo "If you run this script as any other user, chown the keystore and properties files to the server user and group."
  echo ""
  echo "Usage: "
  echo "  lets-encrypt [--os mac] -p path-to-lets-encrypt-pem-files"
  echo ""
  echo "Options: "
  echo "    --os  mac                     Currently only 'mac' supported. Thus, optional"
  echo "    -p | --path                   Cert-Tool will have provided the path it wrote"
  echo "                                  your new cert files to.  "
  echo "    -d | --outDir                 File output directory."
  echo ""
  exit 0
}

# $1 - $letsEncryptPemDir: Path to PEM file provided by Lets Encrypt (CertTool).
#                           Probably starts with /etc/ssl/letsencrypt
# $2 - $outDir: Output directory for the new pkcs12 and Keystore files that will be generated
# $3 - pkcs12Password: Password to use to encrypt the PKCS12 file.
importPemToPkcs12() {
  local letsEncryptPemDir=$1; shift
  local outDir=$1; shift

echo "Sudo password required to read PEM key and cert to pkcs12 file:"

# Add the cert and key into a pkcs12 file because that's what Java's Keytool supports.
sudo openssl pkcs12 -export \
    -in "$letsEncryptPemDir/fullchain.pem" \
    -inkey   "$letsEncryptPemDir/privkey.pem" \
    -out "$outDir/cert.pkcs12" \
    -passout pass:"$pkcs12Password"

    # You now have a pkcs12 file that can be imported into a Java keystore, and a cert that can be loaded into your
    # operating systems keychain/trust store. E.g. for MacOS, you'll need to drag the jetty.crt file into
    # the Certificates tab of the Keychain Access app, and set it to trusted.
}


# $1 - $outDir: Output directory for the new pkcs12 and Keystore files that will be generated
# $2 - pkcs12Password: Password used to encrypt the PKCS12 file.
# $3 - keystorePassword: Password used to encrypt the PKCS12 file.
importPkcs12Keystore() {
  local outDir=$1; shift
  local pkcs12Password=$1; shift
  local keystorePassword=$1; shift

  echo "keystore_password=$keystorePassword
  keystore_manager_password=$keystorePassword
  truststore_password=$keystorePassword" > "$outDir/keystore.properties"

  #https://docs.oracle.com/en/java/javase/12/tools/keytool. (see -importkeystore section)
  echo "Attempting to import cert into a new keystore"
  keytool -importkeystore -noprompt \
     -alias 1\
     -srckeystore "$outDir/cert.pkcs12" \
     -destkeystore "$outDir/keystore" \
     -srcstorepass "$pkcs12Password" \
     -deststorepass "$keystorePassword"

}

_main() {
  local os=mac
  local outDir=.
  local keystoreDir=.
  local letsEncryptPemPath=.


  while (( "$#" )); do
      case $1 in
          -o |--outDir)
          shift; outDir="$1"; shift;
          ;;
          -k |--keystoredir)
          shift; keystoreDir="$1"; shift;
          ;;
          --os)
          shift; os="$1"; shift;
          ;;
          -p |--path)
          shift; letsEncryptPemPath="$1"; shift;
          ;;
          -h |--help)
          printHelp 0
          ;;
          -*|--*=)
          echo "Error: Unsupported argument $1" >&2
          printHelp 1
          ;;
          *)
          echo "Error unknown argument: $1"
          printHelp 2
          ;;
      esac
  done


  if [ -z "$keystoreDir" ]
    then
      keystoreDir=$outDir
  fi

  mkdir -p "$outDir"

  local pkcs12Password=$(openssl rand -base64 64 | head -c 32;echo;)
  local keystorePassword=$(openssl rand -base64 64 | head -c 32;echo;)

  importPemToPkcs12 "$letsEncryptPemPath" "$outDir" "$pkcs12Password"
  importPkcs12Keystore "$outDir" "$pkcs12Password" "$keystorePassword"

  sudo rm "$outDir/cert.pkcs12"

}

_main "$@"

