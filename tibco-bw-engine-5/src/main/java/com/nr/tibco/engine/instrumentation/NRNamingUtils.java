package com.nr.tibco.engine.instrumentation;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;

public class NRNamingUtils {

	private static String applicationName = null;
	
	public static String getApplicationName() {
		return applicationName;
	}
	
	public static void setApplicationName() {
		String tmp = System.getProperty("newrelic.config.app_name");
		if(tmp != null && !tmp.isEmpty()) {
			applicationName = tmp;
			return;
		}
		Config config = NewRelic.getAgent().getConfig();
		if(AgentConfig.class.isInstance(config)) {
			AgentConfig agentConfig = (AgentConfig)config;
			applicationName = agentConfig.getApplicationName();
			if(applicationName != null && !applicationName.isEmpty()) {
				return;
			}
		} else {
			
		}
		
	}
		
	
}
