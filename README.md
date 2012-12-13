This hook adds the new authentication module for Liferay /api services.

User can specify in his/her details custom application token. 3rd party applications can use the token to authenticate themselves.

3rd party applications can specify the authentication token using:
1. applicationToken URL parameter
2. x-application-token HTTP header

Example
-------

1. Go to Control Panel -> My Account -> Custom Fields
2. Enter random token into Application Tokens field - *be sure to specify long random token that can't be easily guessed*, e.g. b0da5ad6-04d5-462d-9e7c-b197d62413ec
3. You can specify more tokens, delimited by a new line or by comma
4. Save
5. To test the URL parameter use: http://localhost:8080/api/secure/jsonws/company/get-company-by-web-id/web-id/liferay.com?applicationToken=b0da5ad6-04d5-462d-9e7c-b197d62413ec
6. To test HTTP header use cURL from linux/cygwin command line: curl 'http://localhost:8080/api/secure/jsonws/company/get-company-by-web-id/web-id/liferay.com' --header 'x-application-token: b0da5ad6-04d5-462d-9e7c-b197d62413ec'

Installation
------------
Download from https://github.com/topolik/service-token-authenticator/downloads and copy into deploy directory

Local Build & Deploy
--------------------
Edit pom.xml and define correct location for: <liferay.auto.deploy.dir>

Call `mvn clean package liferay:deploy'

Licence
--------------------
Licenced under LGPL.
