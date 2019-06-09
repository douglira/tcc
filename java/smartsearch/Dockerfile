FROM tomcat:9.0-jre8-alpine

COPY ./target/smartsearch.war $CATALINA_HOME/webapps/ROOT.war

RUN rm -fr $CATALINA_HOME/webapps/ROOT