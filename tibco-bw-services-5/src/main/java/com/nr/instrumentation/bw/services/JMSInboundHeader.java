package com.nr.instrumentation.bw.services;

import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.newrelic.api.agent.NewRelic;

public class JMSInboundHeader implements InboundHeaders {
	
	private Message message;
	
	public JMSInboundHeader(Message msg) {
		message = msg;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		if(message != null) {
			String value = null;
			try {
				value = message.getStringProperty(name);
			} catch (JMSException e) {
				NewRelic.getAgent().getLogger().log(Level.FINEST, e, "Error getting string message property: {0}", name);
			}
			return value;
		}
		return null;
	}

}
