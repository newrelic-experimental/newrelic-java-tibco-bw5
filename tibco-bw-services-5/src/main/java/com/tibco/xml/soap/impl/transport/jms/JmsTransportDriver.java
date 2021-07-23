package com.tibco.xml.soap.impl.transport.jms;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.soap.api.transport.TransportApplication;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportMessage;

@Weave
public abstract class JmsTransportDriver {

	@Trace
	public void sendMessage(TransportMessage paramTransportMessage, TransportApplication paramTransportApplication, int paramInt) {
		Weaver.callOriginal();
	}
	
	@Trace
	public void noResponse(TransportContext paramTransportContext) {
		Weaver.callOriginal();
	}
}
