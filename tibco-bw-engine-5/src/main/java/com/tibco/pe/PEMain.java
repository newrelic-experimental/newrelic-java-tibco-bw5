package com.tibco.pe;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public class PEMain {

	public static void main(String[] paramArrayOfString) {
		Weaver.callOriginal();
	}
	
	protected void updateProps(Properties paramProperties) {
		String engineName = paramProperties.getProperty("name");
		if(engineName != null && !engineName.isEmpty()) {
			NewRelic.setInstanceName(engineName);
		}
		NewRelic.setProductName("Tibco");
		NewRelic.setAppServerPort(getPidNumber());

		Weaver.callOriginal();
	}

	private int getPidNumber() {
		String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
		String[] split = runtimeName.split("@");
		if (split.length > 1) {
			return Integer.parseInt(split[0]);
		}
		return 80;	
	}

}
