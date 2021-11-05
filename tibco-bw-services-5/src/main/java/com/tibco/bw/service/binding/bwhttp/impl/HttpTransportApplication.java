package com.tibco.bw.service.binding.bwhttp.impl;

import java.net.URI;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.BWHeaders;
import com.tibco.bw.service.Endpoint;
import com.tibco.plugin.share.http.wssdk.ServletContext;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportMessage;
import com.tibco.xml.soap.api.transport.TransportUri;


@Weave(type=MatchType.ExactClass)
public abstract class HttpTransportApplication {
	
	@Trace(dispatcher=true)
	public void processMessage(TransportMessage transportMsg) {
		TransportContext transportCtx = transportMsg.getTransportContext();
		Transaction transaction = NewRelic.getAgent().getTransaction();
		URI theURI = null;
		if(transportCtx != null) {
			TransportUri transportUri = transportCtx.getTransportUri();
			if(transportUri != null) {
				String uri = transportUri.toExternalForm();
				theURI = URI.create(transportUri.toExternalForm());
				transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "HTTPTransport", new String[] {uri});
			}
		}
		if(!transaction.isTransactionNameSet()) {
			transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "HTTPTransport", new String[] {"/UnknownTransportURI"});
		}
		ServletContext servletContext = (ServletContext)transportCtx;
		BWHeaders headers = new BWHeaders(servletContext.getRequestMessage());
		NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.HTTP, headers);
		if(theURI == null) {
			theURI = URI.create("http://UnknownHost");
		}
		
		HttpParameters params = HttpParameters.library("HttpTransportApplication").uri(theURI).procedure("processMessage").inboundHeaders(null).build();
		NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		transaction.convertToWebTransaction();
		Weaver.callOriginal();
	}
	
	protected void invokeServiceEndpoint(Endpoint paramEndpoint, TransportMessage paramTransportMessage) {
		NewRelic.addCustomParameter("Endpoint", paramEndpoint.getName());
		Weaver.callOriginal();
	}
	
	@Weave(type=MatchType.Interface)
	static class Strategy
	  {
		@Trace(dispatcher=true)
	    public void execute(DefaultBwHttpReplyHandler paramDefaultBwHttpReplyHandler) {
    		Weaver.callOriginal();
	    }
	  }
}
