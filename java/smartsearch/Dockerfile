FROM tomcat:9.0-jre8-alpine

RUN rm -fr $CATALINA_HOME/webapps/ROOT

COPY target/smartsearch.war $CATALINA_HOME/webapps/ROOT.war