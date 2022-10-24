package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.BWHeaders;
import com.nr.tibco.engine.instrumentation.HeaderUtils;
import com.tibco.pe.plugin.transaction.BWTransaction;

@Weave
public abstract class JobPool {
	
	@Trace(dispatcher = true)
	public void addJob(Job job, String paramString, boolean paramBoolean) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addProcessContext(attributes, job);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		if(job.headers == null) {
			job.headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(job.headers);
		} else if(job.headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(job.headers);
		} else {
			if(HeaderUtils.canCallAccept()) {
				NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, job.headers);
			}
		}
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	public void checkpointJob(Job job, String var2, Object var3, JDBCConnectionEntry var4, BWTransaction var5, boolean var6, String var7) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addProcessContext(attributes, job);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		if(job.headers == null) {
			job.headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(job.headers);
		} else if(job.headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(job.headers);
		} else {
			if(HeaderUtils.canCallAccept()) {
				NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, job.headers);
			}
		}
		Weaver.callOriginal();
	}
}
