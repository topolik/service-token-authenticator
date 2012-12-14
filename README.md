This hook adds a new authentication module for Liferay 6.1 /api/* services.

User can specify in his/her account details custom application token. 3rd party applications can use the token to authenticate themselves.

3rd party applications can specify the authentication token using:

1. applicationToken URL parameter
2. x-application-token HTTP header

Example
-------

1. Go to Control Panel -> My Account -> Custom Fields
2. Enter random token into Application Tokens field. **Be sure to specify a long random token that can't be easily guessed**, you can use one of online GUID generators: https://www.google.com/search?q=online+guid+generator (e.g. b0da5ad6-04d5-462d-9e7c-b197d62413ec)
3. You can specify more tokens, delimited by a new line or by comma
4. Save
5. To test the URL parameter use: http://localhost:8080/api/secure/jsonws/company/get-company-by-web-id/web-id/liferay.com?applicationToken=b0da5ad6-04d5-462d-9e7c-b197d62413ec
6. To test HTTP header use cURL from linux/cygwin command line: curl 'http://localhost:8080/api/secure/jsonws/company/get-company-by-web-id/web-id/liferay.com' --header 'x-application-token: b0da5ad6-04d5-462d-9e7c-b197d62413ec'

Build & Deploy
--------------------
Download sources using the GitHub "ZIP" button

Call `mvn clean package' and copy the built WAR file into deploy directory.

Or edit pom.xml, define correct location for: &lt;liferay.auto.deploy.dir&gt; and call `mvn clean package liferay:deploy'


Licence
--------------------
Licenced under LGPL.
