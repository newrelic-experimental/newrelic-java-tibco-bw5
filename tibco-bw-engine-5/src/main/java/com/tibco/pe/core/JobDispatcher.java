package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.BWHeaders;
import com.nr.tibco.engine.instrumentation.JobUtils;
import com.tibco.pe.util.Reminder;

@Weave
public abstract class JobDispatcher {

	@Trace
	public void itsTime(Reminder paramReminder, long paramLong) {
		Job localJob = (Job)paramReminder.getClosure();
		Transaction transaction = NewRelic.getAgent().getTransaction();
		if(localJob != null) {
			if(localJob.headers == null) {
				localJob.headers = new BWHeaders();
				transaction.insertDistributedTraceHeaders(localJob.headers);
			} else if(localJob.headers.isEmpty()) {
				transaction.insertDistributedTraceHeaders(localJob.headers);
			} else {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, localJob.headers);
			}
			
		}
		Weaver.callOriginal();
	}
	
	@Weave
	static class JobCourier {
		
		@Trace(dispatcher=true)
		private boolean a(Job job) {
			TracedMethod traced = NewRelic.getAgent().getTracedMethod();
			
			HashMap<String, Object> attributes = new HashMap<String, Object>();
			NRTibcoUtils.addProcessContext(attributes, job);
			
			String fullCallName = job.getName();
			String tmp = traced.getMetricName();
			if(!tmp.equalsIgnoreCase(fullCallName)) {
				NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","JobCourier",JobUtils.maskJobNumber(fullCallName)});
			}
			Transaction transaction = NewRelic.getAgent().getTransaction();
			if(!transaction.isTransactionNameSet()) {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "Job", new String[] {JobUtils.maskJobNumber(fullCallName)});
			}
			if(job.headers != null && !job.headers.isEmpty()) {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, job.headers);
			}
			return Weaver.callOriginal();
		}
	}
}
