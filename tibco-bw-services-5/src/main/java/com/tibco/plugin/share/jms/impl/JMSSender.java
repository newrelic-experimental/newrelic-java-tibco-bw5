package com.tibco.plugin.share.jms.impl;

import javax.jms.Destination;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
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
		SenderResponseMessage respMsg = Weaver.callOriginal();
		String destName = paramSenderRequestMessage.getDestinationName();
		
		if(destName != null && !destName.isEmpty()) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","JMSSender","send",destName});
		}
		return respMsg;
	}

}
