package com.tibco.bw.service.binding.soap;

import java.io.IOException;
import java.net.URI;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.soap.api.SoapMessage;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportUri;

@Weave(type=MatchType.Interface)
public abstract class SoapDriver {

	
	@Trace(dispatcher=true)
	public void sendMessage(SoapMessage soapMsg) throws IOException {
		TransportContext transportCtx = soapMsg.getTransportContext();
		if (transportCtx != null) {
			String soapAction = transportCtx.getSoapAction();
			if (soapAction != null && !soapAction.isEmpty()) {
				NewRelic.addCustomParameter("SoapAction", soapAction);
			}
			TransportUri transportUri = transportCtx.getTransportUri();
			if(transportUri != null) {
				URI uri = URI.create(transportUri.toExternalForm());
				
				HttpParameters params = HttpParameters.library("SoapDriver").uri(uri).procedure("sendMessage").noInboundHeaders().build();
				TracedMethod traced = NewRelic.getAgent().getTracedMethod();
				traced.reportAsExternal(params);
			}
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public void noResponse(TransportContext transportCtx) throws IOException {
		Weaver.callOriginal();
	}

}
