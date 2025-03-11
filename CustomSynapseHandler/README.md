### Steps to Deploy
1. Build the sample

- Build the maven project `mvn clean install`
- Copy the jar from target folder into  ${APIM_HOME}/repository/components/lib location

2. Add Configuration in deployment.toml

Add the below configuration to deployment.toml file which resides inside ${APIM_HOME}/repository/conf directory

```
[synapse_handlers.custom_handler_name]
enabled=true
class="com.wso2customizations.CustomSynapseHandler.CustomizedGlobalSynapseHandler"
```

3. Restart the WSO2 Server.

4. Add logging related configurations in log4j2.properties file

Add the below configuration to log4j2.properties file which resides inside ${APIM_HOME}/repository/conf directory

```
logger.CustomizedGlobalSynapseHandler.name = com.wso2customizations.CustomSynapseHandler.CustomizedGlobalSynapseHandler
logger.CustomizedGlobalSynapseHandler.level = INFO
```

and add below in the loggers section.

```
loggers = CustomizedGlobalSynapseHandler, <existing loggers>
```

Please note that this sample is built and tested for WSO2 APIM 4.4.0.
You will have to change the dependencies based on the WSO2 product version that you are using.

