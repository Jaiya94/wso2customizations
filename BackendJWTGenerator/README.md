### Steps to Deploy
1. Build the sample

- Build the maven project `mvn clean install`
- Copy the jar from target folder into  ${APIM_HOME}/repository/components/dropins location

2. Add Configuration in deployment.toml

Add the below configuration to deployment.toml file which resides inside ${APIM_HOME}/repository/conf directory

```
[apim.jwt.gateway_generator]
impl = "com.wso2customizations.BackendJWTGenerator.CustomizedBackendJWTGenerator"
```

3. Restart the WSO2 Server.

Please note that this sample is built and tested for WSO2 APIM 4.0.0.
You will have to change the dependencies based on the WSO2 product version that you are using.

