package com.tibco.plugin.share.jms;

import java.util.HashMap;
import java.util.logging.Level;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;

import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.MessageConsumeParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.bw.services.HeaderUtils;
import com.nr.instrumentation.bw.services.JMSHeaders;
import com.nr.instrumentation.bw.services.TibcoUtils;
import com.tibco.plugin.share.jms.impl.JMSEventContext;
import com.tibco.plugin.share.jms.impl.JMSReceiver;

@Weave(type=MatchType.Interface)
public abstract class JMSMessageProcessor {

	@Trace(dispatcher=true)
	public void processMessage(JMSEventContext jmsEventContext) {
		NewRelic.getAgent().getLogger().log(Level.FINE, "Call to {0}.processMessage", getClass());
		JMSReceiver receiver = jmsEventContext.evs;
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		if(jmsEventContext != null) {
			TibcoUtils.addAttribute(attributes, "Correlation-ID", jmsEventContext.correlationId);
			TibcoUtils.addAttribute(attributes, "TypeHeader", jmsEventContext.typeHeader);
			TibcoUtils.addAttribute(attributes, "Correlation-ID", jmsEventContext.correlationId);
			if(jmsEventContext.isQueue()) {
				TibcoUtils.addAttribute(attributes, "Type", "Queue");
			} else {
				TibcoUtils.addAttribute(attributes, "Type", "Topic");
			}
			if(jmsEventContext.replyToDest != null) {
				try {
					if(jmsEventContext.replyToDest instanceof Queue) {
						Queue queue = (Queue)jmsEventContext.replyToDest;
						TibcoUtils.addAttribute(attributes, "ReplyToDestinatino", queue.getQueueName());
					} else if(jmsEventContext.replyToDest instanceof Topic) {
						Topic topic = (Topic)jmsEventContext.replyToDest;
						TibcoUtils.addAttribute(attributes, "ReplyToDestinatino", topic.getTopicName());
					}
				} catch (JMSException e) {
				}
			}
		}
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		String recDest = "Unknown";
		if(receiver != null) {
			ReceiverConfiguration config = receiver.getConfiguration();
			if(config != null) {
				recDest = jmsEventContext.evs.getConfiguration().getDestinationName();
				TibcoUtils.addReceiverConfiguration(attributes, config);
			}
		}
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "MessageProcessor", new String[] {"JMSMessageProcessor",recDest});
		Message msg = jmsEventContext.getMessage();
		if(HeaderUtils.canCallAccept()) {
			JMSHeaders headers = new JMSHeaders(msg);
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.JMS, headers);
		}
		try {
			DestinationType destinationType= TibcoUtils.getDestinationType(msg.getJMSDestination());
			MessageConsumeParameters params = MessageConsumeParameters.library("JMSMessageProcessor").destinationType(destinationType).destinationName(recDest).inboundHeaders(null).build();
			traced.reportAsExternal(params);
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINEST, e, "Error getting destination type");
		}
		TibcoUtils.saveMessageParameters(msg,attributes);
		traced.addCustomAttributes(attributes);
		Weaver.callOriginal();
	}
}