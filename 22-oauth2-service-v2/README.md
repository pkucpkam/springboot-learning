## Theory 32 - OAuth2 Service V2

## Create RSA key pair for JWT app
### private key (PKCS#8)
openssl genrsa -out private_key.pem 2048
openssl pkcs8 -topk8 -inform PEM -in private_key.pem -out private_key_pkcs8.pem -nocrypt
mv private_key_pkcs8.pem private_key.pem

### public key
openssl rsa -in private_key.pem -pubout -out public_key.pem

### put key pair to: src/main/resources/keys/
