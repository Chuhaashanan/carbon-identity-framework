/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.identity.gateway.handler.authentication;


import org.wso2.carbon.identity.common.base.exception.IdentityRuntimeException;
import org.wso2.carbon.identity.common.base.message.MessageContext;
import org.wso2.carbon.identity.gateway.api.handler.AbstractGatewayHandler;
import org.wso2.carbon.identity.gateway.authentication.*;
import org.wso2.carbon.identity.gateway.context.AuthenticationContext;
import org.wso2.carbon.identity.gateway.exception.AuthenticationHandlerException;
import org.wso2.carbon.identity.gateway.handler.GatewayHandlerResponse;

public class AuthenticationHandler extends AbstractGatewayHandler {
    @Override
    public boolean canHandle(MessageContext messageContext) throws IdentityRuntimeException {
        return true;
    }

    public GatewayHandlerResponse doAuthenticate(AuthenticationContext authenticationContext) throws
            AuthenticationHandlerException {
        HandlerManager handlerManager = HandlerManager.getInstance();
        AbstractSequenceBuildFactory abstractSequenceBuildFactory =
                handlerManager.getSequenceBuildFactory(authenticationContext);
        AbstractSequence abstractSequence = abstractSequenceBuildFactory.buildSequence(authenticationContext);
        authenticationContext.setSequence(abstractSequence);
        SequenceManager sequenceManager =
                handlerManager.getSequenceManager(authenticationContext);
        AuthenticationResponse authenticationResponse =
                sequenceManager.handleSequence(authenticationContext);
        return buildFrameworkHandlerResponse(authenticationResponse);
    }

    @Override
    public String getName() {
        return null;
    }

    private GatewayHandlerResponse buildFrameworkHandlerResponse(AuthenticationResponse handlerResponse) {
        GatewayHandlerResponse gatewayHandlerResponse = null;
        if (AuthenticationResponse.Status.AUTHENTICATED.equals(handlerResponse.status)) {
            gatewayHandlerResponse = new GatewayHandlerResponse();
        } else if (AuthenticationResponse.Status.INCOMPLETE.equals(handlerResponse.status)) {
            gatewayHandlerResponse = new GatewayHandlerResponse(GatewayHandlerResponse.Status.REDIRECT, handlerResponse.getGatewayResponseBuilder());
        }
        return gatewayHandlerResponse;
    }
}