package com.tibco.bw.service.binding.bwhttp.impl;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.bw.service.Operation;
import com.tibco.bw.service.config.ec.OperationConfiguration;
import com.tibco.xml.datamodel.XiNode;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportDriver;

@Weave
public abstract class DefaultBwHttpReplyHandler {
	
	@NewField
	public Token token;
	
	@Trace(dispatcher=true)
	public void partialReply(XiNode paramXiNode, boolean paramBoolean) {
//		Logger logger = NewRelic.getAgent().getLogger();
		if(token != null) {
			token.link();
//			logger.log(Level.FINE, "Linked to token {0} in {1}.partialReply", token,getClass().getName());
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public void reply(XiNode paramXiNode, boolean paramBoolean) throws Exception {
//		Logger logger = NewRelic.getAgent().getLogger();
		if(token != null) {
			token.linkAndExpire();
//			logger.log(Level.FINE, "Linked and expired token {0} in {1}.partialReply", token,getClass().getName());
			token = null;
		}
		Weaver.callOriginal();
	}
	
	public DefaultBwHttpReplyHandler(TransportDriver paramTransportDriver, TransportContext paramTransportContext, Operation paramOperation, OperationConfiguration paramOperationConfiguration) {
		token = NewRelic.getAgent().getTransaction().getToken();
	}

}
