rm -force *.jks
rm -force *.cert

function createcert($name, $file, $passwd, $cert) {
"$passwd
$passwd
$name
TP2
SD2223
LX
LX
PT
yes" | keytool -genkey -alias $name -keyalg RSA -validity 365 -keystore $file -ext SAN=dns:$name -storetype pkcs12

"$passwd" | keytool -exportcert -alias $name -keystore $file -file $cert
}

function importcert($cert, $name, $keystore) {
    "yes" | keytool -importcert -file $cert -alias $name -keystore $keystore
}

$list = ("users0-ourorg0",
"feeds0-ourorg0",
"feeds1-ourorg0",
"feeds2-ourorg0",
"users0-ourorg1",
"feeds0-ourorg1",
"feeds1-ourorg1",
"feeds2-ourorg1",
"users0-ourorg2",
"feeds0-ourorg2",
"feeds1-ourorg2",
"feeds2-ourorg2")

cp cacerts client-ts.jks

foreach ($name in $list) {
    createcert $name "$name.jks" "$name.pwd" "$name.cert"
    importcert "$name.cert" $name "client-ts.jks"
}
