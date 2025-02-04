### Steps to Deploy
1. Build the sample

- Build the maven project `mvn clean install`
- Copy the jar from target folder into  ${PRODUCT_HOME}/repository/components/lib location

2. Add Configuration in deployment.toml

Add the below configuration to deployment.toml file which resides inside ${PRODUCT_HOME}/repository/conf directory

```
[oauth.extensions.token_types.token_type]
name="JWT"
issuer="com.wso2customizations.JWTTokenIssuer.ExtendedJWTTokenIssuer"
```

3. Restart the WSO2 Server.
