package com.tibco.plugin.share.jms.wssdk;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.soap.api.transport.TransportMessage;

@Weave
public abstract class JmsReplyTransportDriver {
	
	@Trace
	public void sendMessage(TransportMessage paramTransportMessage) {
		Weaver.callOriginal();
	}
}
