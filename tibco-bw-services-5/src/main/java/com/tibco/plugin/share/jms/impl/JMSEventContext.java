package com.tibco.plugin.share.jms.impl;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class JMSEventContext {
	
	public JMSReceiver evs = Weaver.callOriginal();

	
	public abstract Message getMessage();
	
	@Trace
	public void reply(Message var1, int var2, int var3, long var4, String var6) {
		Weaver.callOriginal();
	}
	
	@Trace
	public void reply(Session var1, MessageProducer var2, Message var3, int var4, int var5, long var6, String var8) {
		Weaver.callOriginal();
	}
}
