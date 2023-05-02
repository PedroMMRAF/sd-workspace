rm -force *.jks

"123users
123users
Users.Users
TP2
SD2223
LX
LX
PT
yes" | keytool -genkey -alias users -keyalg RSA -validity 365 -keystore ./users.jks -storetype pkcs12

echo "

Exporting Certificates

"

"123users" | keytool -exportcert -alias users -keystore users.jks -file users.cert

echo "

Creating Client Truststore

"

cp cacerts client-ts.jks
"yes" | keytool -importcert -file users.cert -alias users -keystore client-ts.jks
