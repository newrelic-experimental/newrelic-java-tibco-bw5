package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.Interface)
public abstract class Task {

	public abstract Workflow getWorkflow();
	public abstract String getName();

	@Trace(dispatcher=true)
	public String eval(ProcessContext processContext) {
		
		String workflowName = "UnknownWorkflow";
		Workflow workflow = getWorkflow();
		if(workflow != null) {
			workflowName = workflow.getName() != null ? workflow.getName() : "UnknownWorkflow";
		}
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Task",workflowName,getName()});
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Task", new String[] {workflowName,getName()});

		return Weaver.callOriginal();
	}

	public String handleError(ProcessContext processContext, String paramString, Throwable paramThrowable, byte paramByte, XiNode paramXiNode) {
		NewRelic.noticeError(paramThrowable);
		return Weaver.callOriginal();
	}

}
