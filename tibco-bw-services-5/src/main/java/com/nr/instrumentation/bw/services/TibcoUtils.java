package com.nr.instrumentation.bw.services;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.DestinationType;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.tibco.pe.plugin.ActivityContext;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.plugin.share.jms.ReceiverConfiguration;
import com.tibco.plugin.share.jms.SenderRequestMessage;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportUri;

public class TibcoUtils implements AgentConfigListener {

	private static final String IGNORESKEY = "TIBCO.jms.ignores";
	public static List<String> destinationIgnores;
	private static boolean initialized = false;
	private static final String ATTRIBUTE_PREFIX = "Tibco";
	
	static {
		initializeIgnores();
	}
	
	private static void initializeIgnores() {
		destinationIgnores = new ArrayList<String>();
		Config config = NewRelic.getAgent().getConfig();
		String ignoresStr = (String)config.getValue(IGNORESKEY);
		if(ignoresStr != null && !ignoresStr.isEmpty()) {
			Logger logger = NewRelic.getAgent().getLogger();
			StringTokenizer st = new StringTokenizer(ignoresStr, ",");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				logger.log(Level.INFO, "Will ignore JMS destinations matching {0}", token);
				destinationIgnores.add(token);
			}
		}

		ServiceFactory.getConfigService().addIAgentConfigListener(new TibcoUtils());
		initialized = true;
	}
	
	public static boolean ignore(Destination dest) {
		if(!initialized) {
			initializeIgnores();
		}
		if(dest instanceof Queue) {
			Queue queue = (Queue)dest;
			try {
				String destName = queue.getQueueName();
				for(String dName : destinationIgnores) {
					if(destName.equalsIgnoreCase(dName)) return true;
					if(destName.matches(dName)) return true;
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else if(dest instanceof Topic) {
			Topic topic = (Topic)dest;
			try {
				String destName = topic.getTopicName();
				for(String dName : destinationIgnores) {
					if(destName.equalsIgnoreCase(dName)) return true;
					if(destName.matches(dName)) return true;
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} 
		
		return false;
	}

	public static String nameConsumerMetric(Destination dest) {
		return nameMetric(dest, "Consume");
	}

	public static String nameProducerMetric(Destination dest) {
		return nameMetric(dest, "Produce");
	}

	public static void saveMessageParameters(Message msg, Map<String, Object> attributes) {
		if (msg != null) {
			if(attributes != null) {
				Map<String,String> params = getMessageParameters(msg);
				Set<String> keys = params.keySet();
				
				for(String key : keys) {
					String value = params.get(key);
					if(value != null) {
						attributes.put("MessageProperty-"+key, value);
					}
				}
			}
		}
	}

	public static Map<String, String> getMessageParameters(Message msg)
	{
		Map<String,String> result = new LinkedHashMap<String, String>(1);
		try
		{
			Enumeration<?> parameterEnum = msg.getPropertyNames();
			if ((parameterEnum == null) || (!parameterEnum.hasMoreElements())) {
				return Collections.emptyMap();
			}

			while (parameterEnum.hasMoreElements()) {
				String key = (String)parameterEnum.nextElement();
				Object val = msg.getObjectProperty(key);

				result.put(key, val == null ? null : val.toString());
			}
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Unable to capture JMS message property", new Object[0]);
		}

		return result;
	}

	public static void nameTransaction(Destination dest) {
		try {
			if ((dest instanceof Queue)) {
				Queue queue = (Queue)dest;
				if ((queue instanceof TemporaryQueue)) {
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Message", new String[] { "JMS/Queue/Temp" });
				}
				else {
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "Message", new String[] { "JMS/Queue/Named", queue.getQueueName() });
				}
			}
			else if ((dest instanceof Topic)) {
				Topic topic = (Topic)dest;
				if ((topic instanceof TemporaryTopic)) {
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Message", new String[] { "JMS/Topic/Temp" });
				}
				else
					NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "Message", new String[] { "JMS/Topic/Named", topic.getTopicName() });
			}
			else
			{
				NewRelic.getAgent().getLogger().log(Level.FINE, "Error naming JMS transaction: Invalid Message Type.", new Object[0]);
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "Message", new String[] { "JMS", "Unknown Destination" });
				
			}
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error naming JMS transaction", new Object[0]);
		}

	}

	public static void nameTransaction(Destination dest,Transaction transaction) {
		try {
			if ((dest instanceof Queue)) {
				Queue queue = (Queue)dest;
				if ((queue instanceof TemporaryQueue)) {
					transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Message", new String[] { "JMS/Queue/Temp" });
				}
				else {
					transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "Message", new String[] { "JMS/Queue/Named", queue.getQueueName() });
				}
			}
			else if ((dest instanceof Topic)) {
				Topic topic = (Topic)dest;
				if ((topic instanceof TemporaryTopic)) {
					transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Message", new String[] { "JMS/Topic/Temp" });
				}
				else
					transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, false, "Message", new String[] { "JMS/Topic/Named", topic.getTopicName() });
			}
			else
			{
				NewRelic.getAgent().getLogger().log(Level.FINE, "Error naming JMS transaction: Invalid Message Type.", new Object[0]);
			}
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Error naming JMS transaction", new Object[0]);
		}

	}

	static String nameMetric(Destination dest, String operation) {
		String metricName = null;

		if(dest instanceof Queue) {
			Queue queue = (Queue)dest;
			try {
				if(queue instanceof TemporaryQueue) {
					metricName = MessageFormat.format("TIBCO/JMS/{0}/{1}/Temp", new Object[] {"Queue", operation});
				} else {
					metricName = MessageFormat.format("TIBCO/JMS/{0}/{1}/Named/{2}", new Object[] { "Queue", operation, queue.getQueueName() });
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else if(dest instanceof Topic) {
			Topic topic = (Topic)dest;
			try {
				if ((topic instanceof TemporaryTopic)) {
					metricName = MessageFormat.format("TIBCO/JMS/{0}/{1}/Temp", new Object[] { "Topic", operation });
				} else {
					metricName = MessageFormat.format("TIBCO/JMS/{0}/{1}/Named/{2}", new Object[] { "Topic", operation, topic.getTopicName() });
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} 
		return metricName;
	}

	public static void processSendMessage(Message message, Destination dest, TracedMethod tracer) {
		if (message == null) {
			NewRelic.getAgent().getLogger().log(Level.FINER, "processSendMessage(): message is null", new Object[0]);
		} else if (tracer == null)
		{
			NewRelic.getAgent().getLogger().log(Level.FINER, "processSendMessage(): no tracer", new Object[0]);
		}
		else {
			tracer.addRollupMetricName(new String[] { "External/all" });
			if (NewRelic.getAgent().getTransaction().isWebTransaction()) {
				tracer.addRollupMetricName(new String[] { "External", "allWeb" });
			} else {
				tracer.addRollupMetricName(new String[] { "External", "allOther" });
			}
			JMSHeaders headers = new JMSHeaders(message);
			
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		}
	}
	
	public static String getDestinationName(Destination dest) {
		String destName = "Unknown JMS Destination";
		try {
			if(Queue.class.isInstance(dest)) {
				destName = ((Queue)dest).getQueueName();
			} else if(Topic.class.isInstance(dest)) {
				destName = ((Topic)dest).getTopicName();
			}
		} catch (JMSException e) {
			NewRelic.getAgent().getLogger().log(Level.FINE, e, "Unable to get the JMS message destination name. ({0})", new Object[]{dest});
		}
		return destName;
	}
	
	@Override
	public void configChanged(String category, AgentConfig config) {
		String ignoresStr = (String)config.getValue(IGNORESKEY);
		if(ignoresStr != null && !ignoresStr.isEmpty()) {
			Logger logger = NewRelic.getAgent().getLogger();
			StringTokenizer st = new StringTokenizer(ignoresStr, ",");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				logger.log(Level.INFO, "Will ignore JMS destinations matching {0}", token);
				destinationIgnores.add(token);
			}
		}

	}
	
	public static DestinationType getDestinationType(Destination destination) {
        if (destination instanceof TemporaryQueue) {
            return DestinationType.TEMP_QUEUE;
        } else if (destination instanceof TemporaryTopic) {
            return DestinationType.TEMP_TOPIC;
        } else if (destination instanceof Queue) {
            return DestinationType.NAMED_QUEUE;
        } else {
            return DestinationType.NAMED_TOPIC;
        }
    }
	
	public static void addSenderRequestMessage(Map<String, Object> attributes, SenderRequestMessage message) {
		if(message != null) {
			String temp = null;
			try {
				temp = message.getCorelationId();
			} catch (JMSException e) {
			}
			addAttribute(attributes, "SenderRequestMessage-CorelationId", temp);
			addAttribute(attributes, "SenderRequestMessage-ReplyToName", message.getReplyToName());
			addAttribute(attributes, "SenderRequestMessage-Type", message.getType());
			addAttribute(attributes, "SenderRequestMessage-DestinationName", message.getDestinationName());			
		}
	}
	
	public static void addProcessContext(Map<String, Object> attributes, ProcessContext context) {
		if(context != null) {
			try {
				addAttribute(attributes, "ProcessContext-FullCallName", context.getFullCallName());
			} catch (Exception e) {
			}
			addAttribute(attributes, "ProcessContext-ID", context.getId());
			try {
				addAttribute(attributes, "ProcessContext-InvocationName", context.getInvocationName());
			} catch (Exception e) {
			}
			addAttribute(attributes, "ProcessContext-Name", context.getName());
			addAttribute(attributes, "ProcessContext-Service", context.getService());
		}
		
	}
	
	public static void addActivityContext(Map<String,Object> attributes, ActivityContext context) {
		if(context != null) {
			addAttribute(attributes, "ActivityContext-Name", context.getName());
			addAttribute(attributes, "ActivityContext-ProcessModelName", context.getProcessModelName());
			addAttribute(attributes, "ActivityContext-TraceSource", context.getTraceSource());
		}
	}
	
	public static void addReceiverConfiguration(Map<String,Object> attributes, ReceiverConfiguration config) {
		if(config != null) {
			addAttribute(attributes, "ReceiverConfiguration-Destination", config.getDestinationName());
			addAttribute(attributes, "ReceiverConfiguration-MessageType", config.getMessageType());
			addAttribute(attributes, "ReceiverConfiguration-Selector", config.getSelector());
			addAttribute(attributes, "ReceiverConfiguration-SubName", config.getSubName());
		}
	}
	
	public static void addTransportContext(Map<String,Object> attributes, TransportContext context) {
		if(context != null) {
			addAttribute(attributes, "TransportContext-SoapAction", context.getSoapAction());
			TransportUri tUri = context.getTransportUri();
			if(tUri != null) {
				addAttribute(attributes, "TransportContext-TransportUri", tUri.toString());				
			}
			//addAttribute(attributes, "TransportContext-SoapAction", context.get);
		}
	}
	
	public static void addAttribute(Map<String, Object> attributes, String key, Object value) {
		if(attributes != null && key != null && !key.isEmpty() && value != null) {
			if(!key.startsWith(ATTRIBUTE_PREFIX)) key = ATTRIBUTE_PREFIX + "-" + key;
			attributes.put(key, value);
		}
	}
}
