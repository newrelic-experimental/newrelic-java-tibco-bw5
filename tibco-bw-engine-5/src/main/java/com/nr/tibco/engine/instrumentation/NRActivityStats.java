package com.nr.tibco.engine.instrumentation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;

public class NRActivityStats implements AgentConfigListener {

	private static final String metricPrefix = "Custom/BW-Job/";
	private static boolean sendEvents = false;
	private static final String SENDEVENTKEY = "Activities.sendEvents";
	
	static {
		Config config = NewRelic.getAgent().getConfig();
		Boolean b = config.getValue(SENDEVENTKEY);
		if(b != null) {
			sendEvents = b;
			NewRelic.getAgent().getLogger().log(Level.INFO, "Set flag for sending Job Events to Insights to {0}", sendEvents);
		}
		NRActivityStats listener = new NRActivityStats();
		ServiceFactory.getConfigService().addIAgentConfigListener(listener);
	}
	
	private NRActivityStats() {
		
	}
	
	public static void reportJobStats(String activityName, String processName, String calledProcess, long executionTime, long elapsedTime) {

		String metricName = getMetricName(activityName, processName, calledProcess);
		
		NewRelic.getAgent().getMetricAggregator().recordResponseTimeMetric(metricName+"/ExecutionTime", executionTime);
		NewRelic.getAgent().getMetricAggregator().recordResponseTimeMetric(metricName+"/ElapsedTime", elapsedTime);
		NewRelic.getAgent().getMetricAggregator().incrementCounter(metricName+"/Invocations");
		if(activityName.equals("ERROR")) {
			NewRelic.getAgent().getMetricAggregator().incrementCounter(metricName+"/Errors");
		}

		if (sendEvents) {
			Map<String, Object> event = new HashMap<String, Object>();
			if (activityName != null && !activityName.isEmpty()) {
				event.put("ActivityName", activityName);
			}
			if (processName != null && !processName.isEmpty()) {
				event.put("ActualProcessName", processName);
			}
			if (calledProcess != null && !calledProcess.isEmpty()) {
				event.put("CalledProcessName", calledProcess);
			}
			if (!event.isEmpty()) {
				event.put("ExecutionTime", executionTime);
				event.put("Elapsed Time", elapsedTime);
				NewRelic.getAgent().getInsights().recordCustomEvent("JobEvent", event);
			}
		}
	
	}
	
	private static String getMetricName(String activityName, String processName, String calledProcess) {
		StringBuffer sb = new StringBuffer();
		if(processName != null && !processName.isEmpty()) {
			sb.append(processName);
		}

		if (calledProcess != null && !calledProcess.isEmpty()) {
			if (!sb.toString().isEmpty()) {
				sb.append('/');
			}
			sb.append(calledProcess);
		}

		if(activityName != null && !activityName.isEmpty()) {
			if (!sb.toString().isEmpty()) {
				sb.append('/');
			}
			sb.append(activityName);
		}
		String temp = sb.toString();
		if(temp.isEmpty()) {
			temp = "UnknownJob";
		}
		return metricPrefix + temp;
	}

	public void configChanged(String paramString, AgentConfig paramAgentConfig) {
		Boolean b = paramAgentConfig.getValue(SENDEVENTKEY);
		if(b != null) {
			sendEvents = b;
			NewRelic.getAgent().getLogger().log(Level.INFO, "Set flag for sending Job Events to Insights to {0}", sendEvents);
		}
		
	}

}
