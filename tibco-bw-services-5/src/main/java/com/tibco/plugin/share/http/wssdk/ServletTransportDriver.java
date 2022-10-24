package com.tibco.plugin.share.http.wssdk;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.BWHeaders;
import com.nr.instrumentation.bw.services.HeaderUtils;
import com.tibco.bw.service.binding.bwhttp.BwHttpResponse;
import com.tibco.xml.soap.api.transport.TransportMessage;

@Weave(type=MatchType.BaseClass)
public abstract class ServletTransportDriver {

	@Trace
	public void sendMessage(TransportMessage transportMessage, boolean paramBoolean) {

		ServletContext localServletContext = (ServletContext)transportMessage.getTransportContext();
		BwHttpResponse localBwHttpResponse = localServletContext.getResponseMessage();
		if(HeaderUtils.canCallAccept()) {
			BWHeaders headers = new BWHeaders(localBwHttpResponse);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		}
		Weaver.callOriginal();
	}

	@Trace
	public void sendLastMessage(TransportMessage transportMessage, boolean paramBoolean) {
		ServletContext localServletContext = (ServletContext)transportMessage.getTransportContext();
		BwHttpResponse localBwHttpResponse = localServletContext.getResponseMessage();
		if(HeaderUtils.canCallAccept()) {
			BWHeaders headers = new BWHeaders(localBwHttpResponse);
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		}
		Weaver.callOriginal();
	}

	@Trace
	public void sendPartialMessage(TransportMessage transportMessage, boolean paramBoolean1, boolean paramBoolean2) {
		ServletContext localServletContext = (ServletContext)transportMessage.getTransportContext();
		BwHttpResponse localBwHttpResponse = localServletContext.getResponseMessage();
		BWHeaders headers = new BWHeaders(localBwHttpResponse);
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		Weaver.callOriginal();
	}
}
