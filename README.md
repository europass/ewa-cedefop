# EWA (Europass Web Apps - Cedefop)

**EWA (Europass Web Apps - Cedefop)** is a Java-based web system helping EU Citizens create their CV in the standardised Europass format.

It consists of the following main modules:

1. **Editors**: This is the main online CV Editor application which is called first when a user requests
the home page (URL active on 1/6/2020,  https://europass.cedefop.europa.eu/editors). It’s responsible for 
assembling the front-end part of EWA (a JavaScript-based Single Page Application) by detecting the User Agent,
Locale, etc. and creating the initial HTML page that the user sees with suitable variables.

2. **API**: This is the heart of EWA where most of the processing and business logic takes place:
data model, marshalling/unmarshalling of XML/JSON to Java objects and vice versa, file type
detection, image processing, error handling and messages, session management, population
of ODT template with user-entered data, downloads, emails, export to cloud services,
database interactions, etc.

3. **Office**: The module responsible for converting ODT templates to PDF and DOC as well as for
managing the lifecycle of the underlying LibreOffice binaries.

4. **REST API**: This is also a Java application which runs in isolation from the other modules, but nevertheless shares some common
libraries (JARs) with the API module. The Europass REST API mostly provides a set of conversion services which allow external systems 
to send a Europass XML or JSON document and get back a Europass document of a different file format such as PDF+XML.

## Prerequisites
```
JDK 1.7
Maven 3
Tomcat 7
SQL Server
LibreOffice 4.0
```
## Database

Europass editor is currently using a Microsoft SQL Server database.
Create a new database called `ewa_prod` and a database user and then run the script that can be found in the statistics module to create the database structure  
(path to script: europass-editors/statistics/src/main/resources/sql/ewa_prod_creation.sql).

## Configuration

1. Add the following application data source in Tomcat's server.xml under the GlobalNamingResources element (jtds library should be added to Tomcat's lib folder).
    ```
    <Resource name="jdbc/EWA_STATISTICS"  
        type="javax.sql.DataSource"  
        auth="Container"  
        driverClassName="net.sourceforge.jtds.jdbc.Driver"  
        url="jdbc:jtds:sqlserver://<SERVER_NAME>:1433;instance=MSSQLSERVER;DatabaseName=ewa_prod"  
        username="<USERNAME>"  
        password="<PASSWORD>"  
        maxActive="300" maxIdle="2" maxWait="5000"  
        removeAbandoned="true" removeAbandonedTimeout="60" />  
    ```
2. Add the following context initialization parameters to Tomcat's context.xml to set the properties paths for all modules and make them visible to the web application:
    ```
    <Parameter name="europass-ewa-editors.external.config.properties"                         override="false" value="<EUROPASS_ROOT_FOLDER>/editors/src/main/resources/ewa-editors-config.properties" />  
    <Parameter name="europass-ewa-services-remote-upload-postback.external.config.properties" override="false" value="<EUROPASS_ROOT_FOLDER>/editors/src/main/resources/ewa-remote-upload-partners.properties" />  
    <Parameter name="europass-ewa-services-editors.external.config.properties"                override="false" value="<EUROPASS_ROOT_FOLDER>/services/editors/src/main/resources/config.properties" />  
    <Parameter name="database-api.external.config.properties"                                 override="false" value="<EUROPASS_ROOT_FOLDER>/services/editors/src/main/resources/database.properties" />     
    <Parameter name="europass-ewa-services-rest.external.config.properties"                   override="false" value="<EUROPASS_ROOT_FOLDER>/services/rest/src/main/resources/config.properties" />  
    <Parameter name="database-rest.external.config.properties"                                override="false" value="<EUROPASS_ROOT_FOLDER>/services/rest/src/main/resources/database.properties" />     
    <Parameter name="europass-ewa-oo-server.external.config.properties"                       override="false" value="<EUROPASS_ROOT_FOLDER>/office/server/src/main/resources/config.properties"/>  
    ```
3. Create the following directories under Tomcat:
    ```
    <TOMCAT_INSTALLATION_DIR>/ewa-conf/webapp-editors-logback-config  
    <TOMCAT_INSTALLATION_DIR>/ewa-conf/webapp-api-logback-config  
    <TOMCAT_INSTALLATION_DIR>/ewa-conf/webapp-office-logback-config  
    <TOMCAT_INSTALLATION_DIR>/ewa-conf/webapp-rest-logback-config  
    ```

    and copy the logback.xml files from respective modules.
	
    Then modify the logback.xml files for each one of the modules so that they point to the right directory in Tomcat where the respective log file will be stored.

4. Adjust properties files to use the correct configurations.
   - In the Editors module resources folder (editors/src/main/resources) rename the `ewa-editors-config-default.properties` file to `ewa-editors-config.properties`
     and the `ewa-remote-upload-partners-default.properties` file to `ewa-remote-upload-partners.properties` and fill/edit the respective property values according 
     to the comments in the file.
   - In the Editors module under the stats folder (editors/src/main/webapp/stats) rename the `stats-config-default.properties` file to `stats-config.properties` and fill/edit the respective property values according 
     to the comments in the file.
   - In the cloud-share-config folder under the editors module resources folder fill the google service authentication properties. Rename the `google-service-account-key-development-${ENVIRONMENT}.json` file according to the 
     value of the `context.project.current.environment` in `ewa-editors-config.properties` e.g if context.project.current.environment=production then the json file name should be
     google-service-account-key-development-production.json
   - In the Internationalisation module under the extraction resources folder (internationalisation/extraction/src/main/resources) rename the `config-default.properties` file to `config.properties`
     and fill/edit the respective property values according to the comments in the file.
   - In the Internationalisation module under the templates resources folder (internationalisation/templates/src/main/resources) rename the `config-default.properties` file to `config.properties`
     and fill/edit the respective property values according to the comments in the file.
   - In the office module under the server resources folder (office/server/src/main/resources) rename the `config-default.properties` file to `config.properties`
     and fill/edit the respective property values according to the comments in the file.
   - In the API module under the editors resources folder (services/editors/src/main/resources) rename the `config-default.properties` file to `config.properties`
     and fill/edit the respective property values according to the comments in the file.
   - In the REST API module under the rest resources folder (services/rest/src/main/resources) rename the `config-default.properties` file to `config.properties`
     and fill/edit the respective property values according to the comments in the file.
   - In the API module under the statistics resources folder (services/statistics/src/main/resources) rename the `config-default.properties` file to `config.properties`
     and fill/edit the respective property values according to the comments in the file.
   - In the tools module under the ganalytics test resources folder (tools/ganalytics/src/test/resources) rename the `analytics-default.properties` file to `analytics.properties`
     and fill/edit the respective property values according to the comments in the file. 
   - In the tools module under the zanataLiteralsUpdate resources folder (tools/zanataLiteralsUpdate/src/main/resources) rename the `config-default.properties` file to `config.properties`
     and fill/edit the respective property values according to the comments in the file.

5. Create `tmp-office-files` directory under Tomcat directory.

## Build

To build the project from command line run `mvn clean compile` from the project's root folder.

## Deploy
The four applications that need to be deployed under Tomcat are: editors, api, rest, office.
Run `mvn package` from the project's root folder to produce the war files.

Rename the war produced under editors/target to `editors.war`  
Rename the war produced under services/editors/target to `api.war`  
Rename the war produced under services/rest/target to `rest#v1.war`  
Rename the war produced under office/server/target to `office.war`  

and copy all four to Tomcat webapps folder.

Enable https and start Tomcat.