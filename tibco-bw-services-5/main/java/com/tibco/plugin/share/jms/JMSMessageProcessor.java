package com.tibco.plugin.share.jms;

import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;

import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageConsumeParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.JMSInboundHeader;
import com.nr.instrumentation.bw.services.TibcoUtils;
import com.tibco.plugin.share.jms.impl.JMSEventContext;
import com.tibco.plugin.share.jms.impl.JMSReceiver;

@Weave(type=MatchType.Interface)
public abstract class JMSMessageProcessor {

	@Trace(dispatcher=true)
	public void processMessage(JMSEventContext jmsEventContext) {
		
		JMSReceiver receiver = jmsEventContext.evs;
		String recDest = "Unknown";
		if(receiver != null) {
			ReceiverConfiguration config = receiver.getConfiguration();
			if(config != null) {
				recDest = jmsEventContext.evs.getConfiguration().getDestinationName();
			}
		}
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "MessageProcessor", new String[] {"JMSMessageProcessor",recDest});
		Message msg = jmsEventContext.getMessage();
		JMSInboundHeader wrapper = new JMSInboundHeader(msg);
		try {
			DestinationType destinationType= TibcoUtils.getDestinationType(msg.getJMSDestination());
			MessageConsumeParameters params = MessageConsumeParameters.library("JMSMessageProcessor").destinationType(destinationType).destinationName(recDest).inboundHeaders(wrapper).build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINEST, e, "Error getting destination type");
		}
		TibcoUtils.saveMessageParameters(msg);
		Weaver.callOriginal();
	}
}
