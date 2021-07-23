package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave
public abstract class JobPool {
	
	@Trace
	public void addJob(Job job, String paramString, boolean paramBoolean) {
//		Logger logger = NewRelic.getAgent().getLogger();
		if(job.token == null) {
			job.token = NewRelic.getAgent().getTransaction().getToken();
//			logger.log(Level.FINE, "Got token from transaction {0} in {1}.addJob", job.token,getClass().getName());
		} else {
			job.token.link();
//			logger.log(Level.FINE, "Linked to token {0} in {1}.addJob", job.token,getClass().getName());
		}
		Weaver.callOriginal();
	}
	
	@Trace
	public boolean checkpointConfirmJob(Job job, String paramString1, String paramString2) {
//		Logger logger = NewRelic.getAgent().getLogger();
		if(job.token == null) {
			job.token = NewRelic.getAgent().getTransaction().getToken();
//			logger.log(Level.FINE, "Got token from transaction {0} in {1}.checkpointConfirmJob", job.token,getClass().getName());
		} else {
			job.token.link();
//			logger.log(Level.FINE, "Linked to token {0} in {1}.checkpointConfirmJob", job.token,getClass().getName());
		}
		return Weaver.callOriginal();
	}
}
