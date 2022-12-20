package com.tibco.plugin.share.jms.wssdk;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.TibcoUtils;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportMessage;

@Weave
public abstract class JmsReplyTransportDriver {
	
	@Trace
	public void sendMessage(TransportMessage paramTransportMessage) {
		TransportContext context = paramTransportMessage.getTransportContext();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		TibcoUtils.addTransportContext(attributes, context);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		Weaver.callOriginal();
	}
}
