package com.tibco.plugin.share.jms.impl;

import java.util.HashMap;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.TibcoUtils;
import com.tibco.pe.plugin.ActivityContext;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.plugin.share.jms.SenderRequestMessage;
import com.tibco.plugin.share.jms.SenderResponseMessage;
import com.tibco.plugin.share.jms.SenderResponseMessageFactory;

@Weave(type=MatchType.BaseClass)
public class JMSSender {

	protected Destination configuredDest = Weaver.callOriginal();
	protected String savedDestinationName = Weaver.callOriginal();
	protected Destination savedDest = Weaver.callOriginal();
	
	@Trace
	public SenderResponseMessage send(ProcessContext paramProcessContext, SenderRequestMessage paramSenderRequestMessage, ActivityContext paramActivityContext, SenderResponseMessageFactory paramSenderResponseMessageFactory, String paramString) {
		
		Map<String, Object> attributes = new HashMap<String, Object>();
		TibcoUtils.addProcessContext(attributes, paramProcessContext);
		TibcoUtils.addSenderRequestMessage(attributes, paramSenderRequestMessage);
		TibcoUtils.addActivityContext(attributes, paramActivityContext);
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		SenderResponseMessage respMsg = Weaver.callOriginal();
		String destName = paramSenderRequestMessage.getDestinationName();
		
		if(destName != null && !destName.isEmpty()) {
			traced.setMetricName(new String[] {"Custom","JMSSender","send",destName});
		}
		return respMsg;
	}
	
	public void onException(JMSException var1) {
		NewRelic.noticeError(var1);
		Weaver.callOriginal();
	}

}
