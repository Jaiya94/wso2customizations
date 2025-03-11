package com.wso2customizations.CustomSynapseHandler;

import org.apache.synapse.AbstractSynapseHandler;
import org.apache.synapse.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomizedGlobalSynapseHandler extends AbstractSynapseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizedGlobalSynapseHandler.class);

    @Override
    public boolean handleRequestInFlow(MessageContext synCtx) {
        String correlationId = (String) synCtx.getProperty("correlation_id");
        String requestPath = (String) synCtx.getProperty("REST_FULL_REQUEST_PATH");
        LOGGER.info("Request in flow for the request path " + requestPath + " with correlation id " + correlationId);
        return true;
    }

    @Override
    public boolean handleRequestOutFlow(MessageContext synCtx) {
        String correlationId = (String) synCtx.getProperty("correlation_id");
        String requestPath = (String) synCtx.getProperty("REST_FULL_REQUEST_PATH");
        LOGGER.info("Request out flow for the request path " + requestPath + " with correlation id " + correlationId);
        return true;
    }

    @Override
    public boolean handleResponseInFlow(MessageContext synCtx) {
        String correlationId = (String) synCtx.getProperty("correlation_id");
        String requestPath = (String) synCtx.getProperty("REST_FULL_REQUEST_PATH");
        LOGGER.info("Response in flow for the request path " + requestPath + " with correlation id " + correlationId);
        return true;
    }

    @Override
    public boolean handleResponseOutFlow(MessageContext synCtx) {
        String correlationId = (String) synCtx.getProperty("correlation_id");
        String requestPath = (String) synCtx.getProperty("REST_FULL_REQUEST_PATH");
        LOGGER.info("Response out flow for the request path " + requestPath + " with correlation id " + correlationId);
        return true;
    }
}