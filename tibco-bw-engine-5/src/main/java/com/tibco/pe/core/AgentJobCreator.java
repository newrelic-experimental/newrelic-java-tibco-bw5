package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.pe.plugin.AgentEventContext;
import com.tibco.pe.plugin.transaction.BWTransaction;
import com.tibco.xml.datamodel.XiNode;

@Weave
public abstract class AgentJobCreator {

	@Trace(dispatcher=true)
	public long startProcess(XiNode xiNode, AgentEventContext agentEventCtx, Workflow workflow, JobListener jobListener, boolean paramBoolean, BWTransaction bwTransaction) {
		
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "Process", new String[] {workflow.getName()});
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","AgentJobCreator",workflow.getName()});
		return Weaver.callOriginal();
	}
	
	@Trace(dispatcher=true)
	public void continueProcess(JobData jobData, AgentEventContext paramAgentEventContext, Workflow workflow, JobListener paramJobListener, boolean paramBoolean) {
		Weaver.callOriginal();
	}
	
}
