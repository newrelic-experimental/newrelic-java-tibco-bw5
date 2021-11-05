package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.JobUtils;
import com.tibco.pe.plugin.AgentEventContext;
import com.tibco.pe.plugin.transaction.BWTransaction;
import com.tibco.xml.datamodel.XiNode;

@Weave
public abstract class AgentJobCreator extends JobCreator {
	
	String uri = Weaver.callOriginal();

	@Trace(dispatcher=true)
	public long startProcess(XiNode xiNode, AgentEventContext agentEventCtx, Workflow workflow, JobListener jobListener, boolean paramBoolean, BWTransaction bwTransaction) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addXiNode(attributes, xiNode);
		NRTibcoUtils.addWorkflow(attributes, workflow);
		NRTibcoUtils.addAttribute(attributes, "AgentJobCreator-URI", uri);
		NRTibcoUtils.addAttribute(attributes, "AgentJobCreator-Name", getName());

		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		
		String workflowName = JobUtils.maskJobNumber(workflow.getName());
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "Process", new String[] {workflowName});
		traced.setMetricName(new String[] {"Custom","AgentJobCreator",workflowName});
		return Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	public void continueProcess(JobData jobData, AgentEventContext paramAgentEventContext, Workflow workflow, JobListener paramJobListener, boolean paramBoolean) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addWorkflow(attributes, workflow);
		NRTibcoUtils.addJobData(attributes, jobData);
		
		NRTibcoUtils.addAttribute(attributes, "AgentJobCreator-URI", uri);
		NRTibcoUtils.addAttribute(attributes, "AgentJobCreator-Name", getName());

		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		traced.addCustomAttributes(attributes);
		Weaver.callOriginal();
	}
	
}
