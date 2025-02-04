package com.wso2customizations.JWTTokenIssuer;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.common.model.ClaimConfig;
import org.wso2.carbon.identity.application.common.model.ServiceProvider;
import org.wso2.carbon.identity.oauth.config.OAuthServerConfiguration;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.authz.OAuthAuthzReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.JWTTokenIssuer;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.util.OAuth2Util;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.UserCoreConstants;
import org.wso2.carbon.user.core.util.UserCoreUtil;
import org.wso2.carbon.identity.application.common.model.ClaimMapping;

import java.util.*;

/**
 * This class extends JWTTokenIssuer to add the user claims requested in the service provider level to JWT access token.
 */

public class ExtendedJWTTokenIssuer extends JWTTokenIssuer  {
    private static final Log log = LogFactory.getLog(ExtendedJWTTokenIssuer.class);
    private Algorithm signatureAlgorithm = null;

    public ExtendedJWTTokenIssuer() throws IdentityOAuth2Exception {

        OAuthServerConfiguration config = OAuthServerConfiguration.getInstance();
        this.signatureAlgorithm = this.mapSignatureAlgorithm(config.getSignatureAlgorithm());
    }

    @Override
    public String accessToken(OAuthTokenReqMessageContext oAuthTokenReqMessageContext) throws OAuthSystemException {

        try {
            log.debug("Engaging ExtendedJWTTokenIssuer");
            return this.buildJWTToken(oAuthTokenReqMessageContext);
        } catch (IdentityOAuth2Exception var3) {
            throw new OAuthSystemException(var3);
        }
    }

    protected String buildJWTToken(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        JWTClaimsSet jwtClaimsSet = null;
        JWTClaimsSet newJWTClaimsSet = null;
        try {
            Map<String, Object> claimsMappedAtSP = getUserClaims(tokReqMsgCtx);
            jwtClaimsSet = this.createJWTClaimSet((OAuthAuthzReqMessageContext) null, tokReqMsgCtx, tokReqMsgCtx.getOauth2AccessTokenReqDTO().getClientId());
            Map<String, Object> claims = new HashMap<>(jwtClaimsSet.getClaims());
            for (String key : claimsMappedAtSP.keySet()) {
                // Check if the requested claim is not present in current claims with same key
                if (!claims.containsKey(key)) {
                    // Add the key-value pair from claimsMappedAtSP to claims
                    claims.put(key, claimsMappedAtSP.get(key));
                }
            }

            JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

            for (Map.Entry<String, Object> claim : claims.entrySet()) {
                builder.claim(claim.getKey(), claim.getValue());
            }

            newJWTClaimsSet = builder.build();

            JWTClaimsSet.Builder jwtClaimsSetBuilder = new JWTClaimsSet.Builder(newJWTClaimsSet);
            if (tokReqMsgCtx.getScope() != null && Arrays.asList(tokReqMsgCtx.getScope()).contains("aud")) {
                jwtClaimsSetBuilder.audience(Arrays.asList(tokReqMsgCtx.getScope()));
            }

            jwtClaimsSet = jwtClaimsSetBuilder.build();
            return JWSAlgorithm.NONE.getName().equals(this.signatureAlgorithm.getName()) ? (new PlainJWT(jwtClaimsSet)).serialize() : this.signJWT(jwtClaimsSet, tokReqMsgCtx, (OAuthAuthzReqMessageContext) null);

        } catch (UserStoreException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> getUserClaims(OAuthTokenReqMessageContext tokReqMsgCtx)
            throws IdentityOAuth2Exception, UserStoreException {

        String authorizedUser = tokReqMsgCtx.getAuthorizedUser().getUserName();
        log.debug("authorizedUser who is requesting the token: " + authorizedUser);
        String clientId = tokReqMsgCtx.getOauth2AccessTokenReqDTO().getClientId();
        log.debug("clientId of the application: " + clientId);
        ServiceProvider serviceProvider = OAuth2Util.getServiceProvider(clientId);
        ClaimConfig claimConfig = serviceProvider.getClaimConfig();
        UserStoreManager userStoreManager
                = CarbonContext.getThreadLocalCarbonContext().getUserRealm().getUserStoreManager();
        Map<String, Object> claimList = new HashMap<>();
        if (authorizedUser != null) {
            try {
                if (claimConfig.getClaimMappings() != null && claimConfig.getClaimMappings().length > 0) {
                    final String userStoreDomain = tokReqMsgCtx.getAuthorizedUser().getUserStoreDomain();
                    log.debug("userStoreDomain of the user: " + userStoreDomain);
                    final String UsernameWithDomain = UserCoreUtil.addDomainToName(authorizedUser, userStoreDomain);
                    log.debug("endUsernameWithDomain of the user: " + UsernameWithDomain);
                    ClaimMapping[] claimMappings = claimConfig.getClaimMappings();
                    for (ClaimMapping claimMapping : claimMappings) {
                        Map<String, String> localClaim = userStoreManager.getUserClaimValues(UsernameWithDomain, new String[]{claimMapping.getLocalClaim().getClaimUri()}, UserCoreConstants.DEFAULT_PROFILE);
                        log.debug("mapped claim key: " + claimMapping.getLocalClaim().getClaimUri());
                        log.debug("mapped claim value: " + localClaim.get(claimMapping.getLocalClaim().getClaimUri()));
                        claimList.put(claimMapping.getLocalClaim().getClaimUri(), localClaim.get(claimMapping.getLocalClaim().getClaimUri()));

                    }
                }
                return claimList;
            } catch (Exception e) {
                throw new IdentityOAuth2Exception(e.getMessage(), e);
            }
        }
        return claimList;
    }
}
