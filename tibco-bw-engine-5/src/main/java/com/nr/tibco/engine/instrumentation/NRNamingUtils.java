package com.nr.tibco.engine.instrumentation;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.ConfigService;
import com.newrelic.agent.samplers.SamplerService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.service.ServiceManager;
import com.newrelic.agent.util.DefaultThreadFactory;
import com.newrelic.api.agent.Config;
import com.newrelic.api.agent.NewRelic;

public class NRNamingUtils {

	private static String applicationName = null;
	private static final String CONFIG_APPNAME = "newrelic.config.app_name";
	private static final String USENGINENAME = "TIBCO.UseEngineName.enabled";
	private static boolean engineNameEnabled = true;
	private static boolean applicationNameSet = false;
	
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
	
	private static boolean systemAppNameSet() {
		String tmp = System.getProperty("newrelic.config.app_name");
		
		return tmp != null && !tmp.isEmpty();
	}
	
	public static void setApplicationName(String name) {
		if(applicationNameSet) {
			return;
		}
		NewRelic.setInstanceName(name);

		Config config = NewRelic.getAgent().getConfig();
		Boolean b = config.getValue(USENGINENAME);
		NewRelic.getAgent().getLogger().log(Level.INFO, "Value of TIBCO.UseEngineName.enabled: {0}", b);
		
		if(b != null && b != engineNameEnabled) {
			engineNameEnabled = b;
			NewRelic.getAgent().getLogger().log(Level.INFO, "Tibco Engine Naming is set to {0}", engineNameEnabled ? "enabled" : "disabled");
		}
		if (engineNameEnabled) {
			String tmp = System.getProperty(CONFIG_APPNAME);
			if(systemAppNameSet()) {
				if(!name.equalsIgnoreCase(tmp)) {
					NewRelic.getAgent().getLogger().log(Level.FINE, "Did not set application set because System Config name was set");
					return;
				}
			}
			
			if (tmp == null || tmp.isEmpty()) {
				System.setProperty(CONFIG_APPNAME, name);
				ConfigService configService = ServiceFactory.getConfigService();
				try {
					configService.stop();
					configService.start();
				} catch (Exception e) {
					NewRelic.getAgent().getLogger().log(Level.FINE, e,"Error restarting Service Manager");
				}
//				ServiceManager serviceManager = ServiceFactory.getServiceManager();
//				try {
//					serviceManager.stop();
//					
//					serviceManager.start();
//				} catch (Exception e) {
//					NewRelic.getAgent().getLogger().log(Level.FINE, e,"Error restarting Service Manager");
//				}
//				SamplerService samplerService = ServiceFactory.getSamplerService();
//				try {
//					samplerService.stop();
//				} catch (Exception e) {
//					NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to stop SamplerService");
//				}
//				NewRelic.getAgent().getLogger().log(Level.INFO, "In Tibco Engine, Using {0} as the New Relic application name", name);
//				System.setProperty(CONFIG_APPNAME, name);
//				DelayedStart delayedStart = new DelayedStart(samplerService);
//				ScheduledExecutorService scheduledExecutor;
//				ThreadFactory threadFactory = new DefaultThreadFactory("New Relic Temp Service", true);
//				scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
//				scheduledExecutor.schedule(delayedStart, 1, TimeUnit.MINUTES);
				applicationNameSet = true;
			} else {
				NewRelic.getAgent().getLogger().log(Level.INFO, "Not using {0} as the New Relic application name, since property already set to {1}", name, tmp);
			}
		}
	}
	
	private static class DelayedStart implements Runnable {

		private SamplerService samplerService;
		
		public DelayedStart(SamplerService s) {
			samplerService = s;
		}
		
		@Override
		public void run() {
			if(samplerService.isStopped()) {
				try {
					samplerService.start();
				} catch (Exception e) {
					NewRelic.getAgent().getLogger().log(Level.FINE, e, "Failed to start SamplerService");
				}
			}
		}
		
	}
}
