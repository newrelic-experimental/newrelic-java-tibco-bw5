package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.BWHeaders;
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
	public BWHeaders headers = null;
	
	public Job(String var1, Workflow var2) {
	}

	public Job(String var1, Workflow var2, VariableList var3) {
	}

	public Job(String var1, Workflow var2, VariableList var3, Object var4) {
		
	}
	
	long h = ((Long)Weaver.callOriginal()).longValue();

	public abstract Workflow getActualWorkflow();

	abstract Track getCurrentTrack();

	public abstract Workflow getWorkflow();

	public abstract long getId();

	public abstract void addJobListener(JobListener paramJobListener);

	@Trace(dispatcher=true)
	public int callProcess(String workflow, XiNode paramXiNode, boolean paramBoolean) throws Exception { 
		if(headers == null) {
			headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else if(headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addAttribute(attributes, "WorkFlow", workflow);
		NRTibcoUtils.addProcessContext(attributes, this);
		traced.addCustomAttributes(attributes);
		return ((Integer)Weaver.callOriginal()).intValue(); 
	}

	@Trace
	long a(long paramLong) {
		String invocationName = "Unknown";
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		if (getCurrentTrack() != null) {
			NRTibcoUtils.addTrack(attributes, getCurrentTrack());
			invocationName = getName();
			// avoid metric explosion related to invocation name being a incremented number
			if(invocationName.matches(JobUtils.ALLDIGITS) || invocationName.matches(JobUtils.JOBANDDIGITS)) {
				invocationName = JobUtils.JOBMASK;
			}
		}
		NRTibcoUtils.addProcessContext(attributes, this);
		
		Workflow aWorkflow = getActualWorkflow();
		if(aWorkflow != null) {
			NRTibcoUtils.addAttribute(attributes, "Job-ActualWorkflow", aWorkflow.getName());
		}
		Workflow workflow = getWorkflow();
		if(workflow != null) {
			NRTibcoUtils.addAttribute(attributes, "Job-Workflow", workflow.getName());
		}
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		String tmp = "Custom/Job/"+traced.getMetricName();
		if(!tmp.equalsIgnoreCase(invocationName)) {
			traced.setMetricName(new String[] { "Custom", "Job", invocationName });
		}
		return ((Long)Weaver.callOriginal()).longValue();
	}
	
	@Trace(dispatcher=true)
	long k() {
		if(headers == null) {
			headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else if(headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addProcessContext(attributes, this);
		traced.addCustomAttributes(attributes);
		String workflowName = "UnknownWorkflow";
		if (getActualWorkflow() != null) {
			Workflow aWorkflow = getActualWorkflow();
			if ((aWorkflow != null) && 
					(aWorkflow.getName() != null)) {
				workflowName = aWorkflow.getName();
			}
		}

		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Job", new String[] { JobUtils.maskJobNumber(workflowName) });
		return ((Long)Weaver.callOriginal()).longValue();
	}
	
	@Trace(dispatcher=true)
	public Job spawnJob(String paramString1, String paramString2, XiNode paramXiNode, String paramString3, String paramString4) {
		if(headers == null) {
			headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else if(headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}

		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addJobWithPrefix(attributes, "Input", this);
		Job job = (Job)Weaver.callOriginal();
		if(job.headers == null) {
			job.headers = headers;
		}
		NRTibcoUtils.addJobWithPrefix(attributes, "Outout", job);
		traced.addCustomAttributes(attributes);
		return job;
	}
	
	@Trace(dispatcher=true)
	public void moveOn(int paramInt) {
		if(headers == null) {
			headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else if(headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher = true)
	public void resume() {
		if(headers == null) {
			headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else if(headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		Weaver.callOriginal();
	}

	@Trace(dispatcher = true)
	boolean a(long var1, String processName, String calledProcessName, VariableList var5, XiNode var6, boolean var7, XiNode var8, Track var9) {
		if(headers == null) {
			headers = new BWHeaders();
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else if(headers.isEmpty()) {
			NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		} else {
			NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, headers);
		}
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addTrack(attributes, var9);
		NRTibcoUtils.addAttribute(attributes, "ProcessName", processName);		
		NRTibcoUtils.addAttribute(attributes, "CalledProcessName", calledProcessName);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		
		return Weaver.callOriginal();
	}

	boolean a(long paramLong1, String paramString1, String paramString2, String paramString3, VariableList paramVariableList, long paramLong2, long paramLong3) {
		String processName = paramString1;
		String called = paramString2;
		String activityName = paramString3;
		NRActivityStats.reportJobStats(activityName, processName, called, paramLong2, paramLong3);
		return ((Boolean)Weaver.callOriginal()).booleanValue();
	}
}