package com.tibco.plugin.share.http.wssdk;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.BWResponseWrapper;
import com.tibco.bw.service.binding.bwhttp.BwHttpResponse;
import com.tibco.plugin.share.http.wssdk.ServletContext;
import com.tibco.xml.soap.api.transport.TransportMessage;

@Weave(type=MatchType.BaseClass)
public abstract class ServletTransportDriver {

	@Trace
	public void sendMessage(TransportMessage transportMessage, boolean paramBoolean) {
		
		ServletContext localServletContext = (ServletContext)transportMessage.getTransportContext();
		BwHttpResponse localBwHttpResponse = localServletContext.getResponseMessage();
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new BWResponseWrapper(localBwHttpResponse));
		Weaver.callOriginal();
	}
	
	@Trace
	public void sendLastMessage(TransportMessage transportMessage, boolean paramBoolean) {
		ServletContext localServletContext = (ServletContext)transportMessage.getTransportContext();
		BwHttpResponse localBwHttpResponse = localServletContext.getResponseMessage();
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new BWResponseWrapper(localBwHttpResponse));
		Weaver.callOriginal();
	}
	
	@Trace
	public void sendPartialMessage(TransportMessage transportMessage, boolean paramBoolean1, boolean paramBoolean2) {
		ServletContext localServletContext = (ServletContext)transportMessage.getTransportContext();
		BwHttpResponse localBwHttpResponse = localServletContext.getResponseMessage();
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(new BWResponseWrapper(localBwHttpResponse));
		Weaver.callOriginal();
	}
}
