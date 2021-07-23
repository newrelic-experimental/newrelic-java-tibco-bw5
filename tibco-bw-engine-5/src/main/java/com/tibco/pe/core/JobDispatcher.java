package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.pe.util.Reminder;

@Weave
public abstract class JobDispatcher {

	@Trace
	public void itsTime(Reminder paramReminder, long paramLong) {
		Job localJob = (Job)paramReminder.getClosure();
		if(localJob != null) {
			if(localJob.token == null) {
				localJob.token = NewRelic.getAgent().getTransaction().getToken();
			} else {
				localJob.token.link();
			}
			
		}
		Weaver.callOriginal();
	}
	
	@Weave
	static class JobCourier {
		
		@Trace(dispatcher=true)
		private boolean a(Job job) {
			String fullCallName = job.getName();
			String tmp = NewRelic.getAgent().getTracedMethod().getMetricName();
			if(!tmp.equalsIgnoreCase(fullCallName)) {
				NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","JobCourier",fullCallName});
			}
			Transaction transaction = NewRelic.getAgent().getTransaction();
			if(!transaction.isTransactionNameSet()) {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, true, "Job", new String[] {fullCallName});
			}
			if(job.token != null) {
				job.token.link();
			}
			return Weaver.callOriginal();
		}
	}
}
