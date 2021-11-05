package com.tibco.plugin.share.jms.impl;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.TibcoUtils;
import com.tibco.pe.plugin.Tracer;
import com.tibco.plugin.share.jms.SenderRequestMessage;
import com.tibco.plugin.share.jms.SenderResponseMessage;
import com.tibco.plugin.share.jms.SenderResponseMessageFactory;

@Weave
public abstract class JMSReplySender {

	@Trace
	public SenderResponseMessage send(JMSEventContext var1, SenderRequestMessage paramSenderRequestMessage, Tracer var3, SenderResponseMessageFactory var4, String var5) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		TibcoUtils.addSenderRequestMessage(attributes, paramSenderRequestMessage);
		
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		SenderResponseMessage respMsg = Weaver.callOriginal();
		String destName = paramSenderRequestMessage.getDestinationName();
		
		if(destName != null && !destName.isEmpty()) {
			traced.setMetricName(new String[] {"Custom","JMSSender","send",destName});
		}
		
		return respMsg;
	}
}
