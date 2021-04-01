REM Import the root certificate
keytool -v -keystore "C:\Program Files\Java\jdk1.8.0_201\jre\lib\security\cacerts" -storepass changeit -import -file caadmin-netskope.cer