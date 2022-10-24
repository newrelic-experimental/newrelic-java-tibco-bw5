package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.BWHeaders;
import com.nr.tibco.engine.instrumentation.HeaderUtils;
import com.nr.tibco.engine.instrumentation.JobUtils;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.Interface)
public abstract class Task {

	public abstract Workflow getWorkflow();
	public abstract String getName();

	@Trace(dispatcher=true)
	public String eval(ProcessContext processContext) {
		
		Transaction transaction = NewRelic.getAgent().getTransaction();
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addProcessContext(attributes, processContext);
		NRTibcoUtils.addWorkflow(attributes, getWorkflow());
		NRTibcoUtils.addAttribute(attributes, "Name", getName());
		traced.addCustomAttributes(attributes);
		
		if(processContext instanceof Job) {
			Job job = (Job)processContext;
			if(job.headers == null) {
				job.headers = new BWHeaders();
				transaction.insertDistributedTraceHeaders(job.headers);
			} else if(job.headers.isEmpty()) {
				transaction.insertDistributedTraceHeaders(job.headers);
			} else {
				if(HeaderUtils.canCallAccept()) {
					transaction.acceptDistributedTraceHeaders(TransportType.Other, job.headers);
				}
			}
 		}
		
		String workflowName = "UnknownWorkflow";
		Workflow workflow = getWorkflow();
		if(workflow != null) {
			workflowName = workflow.getName() != null ? JobUtils.maskJobNumber(workflow.getName()) : "UnknownWorkflow";
		}
		traced.setMetricName(new String[] {"Custom","Task",workflowName,getName()});
		transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_LOW, false, "Task", new String[] {"Task",workflowName,getName()});

		return Weaver.callOriginal();
	}

	public String handleError(ProcessContext processContext, String paramString, Throwable paramThrowable, byte paramByte, XiNode paramXiNode) {
		NewRelic.noticeError(paramThrowable);
		return Weaver.callOriginal();
	}

}
