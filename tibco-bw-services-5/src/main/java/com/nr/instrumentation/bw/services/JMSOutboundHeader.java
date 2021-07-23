package com.nr.instrumentation.bw.services;

import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.OutboundHeaders;

public class JMSOutboundHeader implements OutboundHeaders {
	
	private Message message;
	
	public JMSOutboundHeader(Message msg) {
		message = msg;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String name, String value) {
		if(message != null) {
			try {
				message.setStringProperty(name, value);
			} catch (JMSException e) {
				NewRelic.getAgent().getLogger().log(Level.FINEST, e, "Error getting string message property: {0}", name);
			}
		}
	}

}
