package com.tibco.xml.soap.impl.transport;

import java.net.URI;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.soap.api.transport.TransportApplication;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportEntity;
import com.tibco.xml.soap.api.transport.TransportMessage;
import com.tibco.xml.soap.api.transport.TransportUri;

@Weave
public class HttpTransportDriver {

	@Trace
	public void sendMessage(TransportMessage transportMessage, TransportApplication paramTransportApplication, int paramInt) {
		TransportEntity transportEntity = transportMessage.getBody();
		if(transportEntity != null && DefaultTransportEntity.class.isInstance(transportEntity)) {
			OutboundWrapper wrapper = new OutboundWrapper((DefaultTransportEntity) transportEntity);
			NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		}
		TransportContext context = transportMessage.getTransportContext();
		if(context != null) {
			TransportUri transportURI = context.getTransportUri();
			URI uri = URI.create(transportURI.toExternalForm());
			HttpParameters params = HttpParameters.library("Transport").uri(uri).procedure("sendMessage").noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		
		Weaver.callOriginal();
	}
}
