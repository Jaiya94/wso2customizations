package com.wso2customizations.BackendJWTGenerator;

import org.osgi.service.component.annotations.Component;
import org.wso2.carbon.apimgt.common.gateway.dto.JWTInfoDto;
import org.wso2.carbon.apimgt.common.gateway.jwtgenerator.APIMgtGatewayJWTGeneratorImpl;
import org.wso2.carbon.apimgt.common.gateway.jwtgenerator.AbstractAPIMgtGatewayJWTGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Component(
        enabled = true,
        service = AbstractAPIMgtGatewayJWTGenerator.class,
        name = "CustomizedBackendJWTGenerator"
)


public class CustomizedBackendJWTGenerator extends APIMgtGatewayJWTGeneratorImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizedBackendJWTGenerator.class);

    private static final String ROLE_CLAIM = "http://wso2.org/claims/role";
    private static final String COMMA_SEPARATOR = ",";

    @Override
    public Map<String, Object> populateCustomClaims(JWTInfoDto jwtInfoDto) {
        LOGGER.debug("Handing Custom Claims");

        Map<String, Object> claims = super.populateCustomClaims(jwtInfoDto);
        if (claims.containsKey(ROLE_CLAIM)) {
            LOGGER.debug("JWT contains the role claim");
            Object value = claims.get(ROLE_CLAIM);
            if (value instanceof String) {
                LOGGER.debug("JWT contains the role claim as a String");
                // Split the comma-separated string into an array
                String[] roleArray = ((String) value).split(COMMA_SEPARATOR);
                claims.put(ROLE_CLAIM,roleArray);
            }
        }
        return claims;
    }
}



