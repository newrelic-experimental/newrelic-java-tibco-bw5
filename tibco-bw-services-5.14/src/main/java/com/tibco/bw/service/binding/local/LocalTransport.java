package com.tibco.bw.service.binding.local;

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
			Transaction transaction = NewRelic.getAgent().getTransaction();
			if(transaction != null) {
				token = transaction.getToken();
			}
		}
		
		@Trace(async=true)
		public void run() {
			if(token != null) {
				token.linkAndExpire();
				token = null;
			}
			Weaver.callOriginal();
		}
	}
}
