package com.tibco.bw.service.binding.local;

import java.util.logging.Level;

import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.bw.service.ReplyHandler;
import com.tibco.bw.service.config.common.EndpointOperationReference;
import com.tibco.bw.service.config.common.EndpointReference;
import com.tibco.xml.datamodel.XiNode;

@Weave
public class LocalTransport {

	@Trace(dispatcher=true)
	public void execute(EndpointOperationReference paramEndpointOperationReference, XiNode paramXiNode, ReplyHandler paramReplyHandler) {
		String opName = paramEndpointOperationReference.getOperationName();
		EndpointReference endptRef = paramEndpointOperationReference.getEndpointReference();
		String serviceName = "Unknown Service";
		if(endptRef != null) {
			serviceName = endptRef.getServiceName().toString();
		}
		
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","LocalTransport",serviceName,opName});
		Weaver.callOriginal();
	}
	
	@Weave
	static class RequestExecutor {
		
		@NewField 
		private Token token;
		
		public RequestExecutor(EndpointOperationReference paramEndpointOperationReference, XiNode paramXiNode, ReplyHandler paramReplyHandler) {
//			Logger logger = NewRelic.getAgent().getLogger();
			Transaction transaction = NewRelic.getAgent().getTransaction();
			if(transaction != null) {
				token = transaction.getToken();
//				logger.log(Level.FINE, "Got token from transaction {0} in {1}.<init>", token,getClass().getName());
			}
		}
		
		@Trace
		public void run() {
//			Logger logger = NewRelic.getAgent().getLogger();
			if(token != null) {
				token.linkAndExpire();
//				logger.log(Level.FINE, "Linked and expired token {0} in {1}.run", token,getClass().getName());
				token = null;
			}
			Weaver.callOriginal();
		}
	}
}
