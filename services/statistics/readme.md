-----------------------------------------------
-- Stats API ----------------------------------
-----------------------------------------------
This module operates as an end point for the Stats UI interface (https://europass.cedefop.europa.eu/resources/statistics/custom-reports).
A Stats API guide can be found at the Europass Interoperability site (https://interop.europass.cedefop.europa.eu/web-services/statistics-api/).

Prerequisites

java 1.7
Tomcat 7

Deployment

Add jtds-1.3.1.jar to Tomcat lib folder.
Configure database resource in application's database.properties and in Tomcat's server.xml and context.xml file (Stats API connects to europass_statistics).

    server.xml:
    <Resource name="jdbc/EWA_SERV_STATISTICS"
    type="javax.sql.DataSource"
    auth="Container" driverClassName="net.sourceforge.jtds.jdbc.Driver"
    url="jdbc:jtds:sqlserver://<server_ip>:<port>;instance=MSSQLINSTANCE1;DatabaseName=europass_statistics"
    username="*****"
    password="*****"
    maxActive="300" maxIdle="2" maxWait="5000"        
    removeAbandoned="true"
    removeAbandonedTimeout="60"
    validationQuery="SELECT 1"/>

    context.xml:
    <ResourceLink name="jdbc/EWA_SERV_STATISTICS" global="jdbc/EWA_SERV_STATISTICS" auth="Container" type="javax.sql.DataSource"/>
    
Build application & add to tomcat webapps.
Edit config.properties file to specify logback.xml location