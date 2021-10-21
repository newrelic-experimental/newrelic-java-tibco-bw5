package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class JobPool {
	
	@Trace
	public void addJob(Job job, String paramString, boolean paramBoolean) {
		if(job.token == null) {
			job.token = NewRelic.getAgent().getTransaction().getToken();
		} else {
			job.token.link();
		}
		Weaver.callOriginal();
	}
	
	@Trace
	public boolean checkpointConfirmJob(Job job, String paramString1, String paramString2) {
		if(job.token == null) {
			job.token = NewRelic.getAgent().getTransaction().getToken();
		} else {
			job.token.link();
		}
		return Weaver.callOriginal();
	}
}
