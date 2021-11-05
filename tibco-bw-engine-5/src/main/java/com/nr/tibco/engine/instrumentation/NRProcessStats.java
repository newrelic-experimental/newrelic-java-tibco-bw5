package com.nr.tibco.engine.instrumentation;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.AgentConfigListener;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;

public class NRProcessStats  implements AgentConfigListener
{
	public static final int JOBCOMPLETION = 1;
	public static final int INVOCATIONCOMPELETION = 2;
	private String processName = null;
	Long invokeTime = null;
	Long elapsedTime = null;
	boolean aborted = true;
	private static boolean sendEvents = true;
	private static boolean disableInvocations = false;
	private static boolean disableJobs = false;
	
	private static final String SENDEVENTKEY = "Processes.sendEvents";
	private static final String DISABLEINVOCATIONSKEY = "Processes.disableInvocations";
	private static final String DISABLEJOBSKEY = "Processes.disableJobs";
	private boolean isListener = false;
	
	static {
		Config config = NewRelic.getAgent().getConfig();
		Boolean b = config.getValue(SENDEVENTKEY);
		if(b != null) {
			sendEvents = b;
			NewRelic.getAgent().getLogger().log(Level.INFO, "Set flag for sending Process Events to Insights to {0}", sendEvents);
		}
		b = config.getValue(DISABLEINVOCATIONSKEY);
		if(b != null) {
			disableInvocations = b;
			NewRelic.getAgent().getLogger().log(Level.INFO, "Set flag for disabling Invocation Process Events to Insights to {0}", disableInvocations);
		}
		b = config.getValue(DISABLEJOBSKEY);
		if(b != null) {
			disableJobs = b;
			NewRelic.getAgent().getLogger().log(Level.INFO, "Set flag for disabling Job Process Events to Insights to {0}", disableJobs);
		}
		NRProcessStats listener = new NRProcessStats();
		ServiceFactory.getConfigService().addIAgentConfigListener(listener);
	}
	
	private NRProcessStats() {
		isListener = true;
	}

	protected NRProcessStats(String process_name, Long execute, Long elapsed, boolean abt)
	{
		this.processName = process_name;
		this.invokeTime = execute;
		this.elapsedTime = elapsed;
		this.aborted = abt;
	}

	public static void addStats(String process, Long invoke, Long elapsed, boolean completed, int type)
	{
		if(!sendEvents) return;
		String countMetricName = "Custom/Process/Finished";
		NewRelic.getAgent().getMetricAggregator().incrementCounter(countMetricName);
		NRProcessStats stats = new NRProcessStats(process, invoke, elapsed, completed);
		if(type == 1 && disableJobs) return;
		if(type == 2 && disableInvocations) return;
		stats.postStats(type);
	}

	private void postStats(int type)
	{
		if(isListener) return;
		String metricName = "Custom/BW-Process/" + this.processName;

		Map<String,Object> event = new HashMap<String,Object>();

		if ((this.processName != null) && (!this.processName.isEmpty())) {
			event.put("ProcessName", this.processName);
		}

		Logger logger = NewRelic.getAgent().getLogger();
		if (!event.isEmpty()) {
			if (this.aborted)
				event.put("EndState", "Aborted");
			else {
				event.put("EndState", "Completed");
			}
			String eventType = type == 1 ? "Job" : "Invocation";
			event.put("EndType", eventType);
			event.put("ExecutionTime", this.invokeTime);
			event.put("Elapsed Time", this.elapsedTime);
			NewRelic.getAgent().getInsights().recordCustomEvent("ProcessEvent", event);

			NewRelic.getAgent().getMetricAggregator().recordResponseTimeMetric(metricName + "/ExecutionTime", this.invokeTime.longValue());
			NewRelic.getAgent().getMetricAggregator().recordResponseTimeMetric(metricName + "/ElapsedTime", this.elapsedTime.longValue());
		}
		else {
			logger.log(Level.FINER, "Did not get any names from job: {0}", new Object[] { this.processName });
		}
	}

	public static void reportProcessStart()
	{
		String metricName = "Custom/Process/Started";
		NewRelic.getAgent().getMetricAggregator().incrementCounter(metricName);
	}

	public void configChanged(String paramString, AgentConfig config) {
		Boolean b = config.getValue(SENDEVENTKEY);
		if(b != null) {
			sendEvents = b;
			NewRelic.getAgent().getLogger().log(Level.INFO, "Set flag for sending Process Events to Insights to {0}", sendEvents);
		}	  
		b = config.getValue(DISABLEINVOCATIONSKEY);
		if(b != null) {
			disableInvocations = b;
		}
		b = config.getValue(DISABLEJOBSKEY);
		if(b != null) {
			disableJobs = b;
		}
	}
}