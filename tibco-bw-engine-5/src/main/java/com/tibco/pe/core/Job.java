package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.JobUtils;
import com.nr.tibco.engine.instrumentation.NRActivityStats;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.xml.datamodel.XiNode;
import com.tibco.xml.xdata.xpath.VariableList;

@Weave
abstract class Job
implements ProcessContext
{

	@NewField
	public Token token;
	long h = ((Long)Weaver.callOriginal()).longValue();

	public abstract Workflow getActualWorkflow();

	abstract Track getCurrentTrack();

	public abstract Workflow getWorkflow();

	public abstract long getId();

	public abstract void addJobListener(JobListener paramJobListener);

	@Trace(dispatcher=true)
	public int callProcess(String workflow, XiNode paramXiNode, boolean paramBoolean) throws Exception { 
		return ((Integer)Weaver.callOriginal()).intValue(); 
	}

	@Trace
	long a(long paramLong) {
		String invocationName = "Unknown";
		if (getCurrentTrack() != null) {
			invocationName = getName();
			// avoid metric explosion related to invocation name being a incremented number
			if(invocationName.matches(JobUtils.ALLDIGITS) || invocationName.matches(JobUtils.JOBANDDIGITS)) {
				invocationName = JobUtils.JOBMASK;
			}
		}
		String tmp = "Custom/Job/"+NewRelic.getAgent().getTracedMethod().getMetricName();
		if(!tmp.equalsIgnoreCase(invocationName)) {
			NewRelic.getAgent().getTracedMethod().setMetricName(new String[] { "Custom", "Job", invocationName });
		}
		return ((Long)Weaver.callOriginal()).longValue();
	}
	@Trace(dispatcher=true)
	long k() {
		NewRelic.addCustomParameter("Job Name", getName());
		if (getService() != null) {
			NewRelic.addCustomParameter("Job Service", getService());
		}
		String workflowName = "UnknownWorkflow";
		if (getActualWorkflow() != null) {
			Workflow aWorkflow = getActualWorkflow();
			if ((aWorkflow != null) && 
					(aWorkflow.getName() != null)) {
				workflowName = aWorkflow.getName();
			}
		}

		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Job", new String[] { workflowName });
		return ((Long)Weaver.callOriginal()).longValue();
	}
	
	@Trace(dispatcher=true)
	public Job spawnJob(String paramString1, String paramString2, XiNode paramXiNode, String paramString3, String paramString4) {
//		Logger logger = NewRelic.getAgent().getLogger();
		Job job = (Job)Weaver.callOriginal();
		if (job.token == null){
			job.token = NewRelic.getAgent().getTransaction().getToken();
//			logger.log(Level.FINE, "Got token from transaction {0} in {1}.spawnJob", job.token,getClass().getName());
		}
		return job;
	}
	
	@Trace(dispatcher=true)
	public void moveOn(int paramInt) {
		if (token != null) {
			token.link();
		}
		Weaver.callOriginal();
	}

	public void resume() {
		if (token != null) {
			token.link();
		}
		Weaver.callOriginal();
	}

	boolean a(long paramLong, String paramString1, String paramString2, VariableList paramVariableList, XiNode paramXiNode1, boolean paramBoolean, XiNode paramXiNode2, Track paramTrack) {
		return ((Boolean)Weaver.callOriginal()).booleanValue();
	}

	boolean a(long paramLong1, String paramString1, String paramString2, String paramString3, VariableList paramVariableList, long paramLong2, long paramLong3) {
		String processName = paramString1;
		String called = paramString2;
		String activityName = paramString3;
		NRActivityStats.reportJobStats(activityName, processName, called, paramLong2, paramLong3);
		return ((Boolean)Weaver.callOriginal()).booleanValue();
	}
}