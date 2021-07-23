package com.tibco.pe;

import java.lang.management.ManagementFactory;
import java.util.Properties;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.NRNamingUtils;

@Weave
public class PEMain {

	public static void main(String[] paramArrayOfString) {
		String name = findName(paramArrayOfString);
		if(name != null) {
			NRNamingUtils.setApplicationName(name);
		}
		Weaver.callOriginal();
	}
	
	protected void updateProps(Properties paramProperties) {
		String engineName = paramProperties.getProperty("name");
		if(engineName != null && !engineName.isEmpty()) {
			NRNamingUtils.setApplicationName(engineName);
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

	private static String findName(String[] args) {
		int length = args.length;
		int i = 0;
		String name = null;
		name = System.getProperty("name");
		
		while(i < length && name == null) {
			String tmp = args[i];
			if(tmp.equals("-name")) {
				if(i < length -2) {
					name = args[i+1];
				}
			}
			i++;
		}
		
		return name;
	}
}
