package com.tibco.pe.core;

import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class WorkflowImpl {
	
	public abstract String getName();

	public ProcessStats getProcessStats() {
		ProcessStats stats = Weaver.callOriginal();
		if(stats.name == null) {
			String processName = getName();
			if(processName != null && !processName.isEmpty()) {
				stats.name = processName;
			}
		}
		return stats;
		
	}
}
