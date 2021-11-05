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
import com.nr.tibco.engine.instrumentation.JobUtils;
import com.tibco.pe.plugin.EventContext;
import com.tibco.xml.data.primitive.ExpandedName;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.BaseClass)
public abstract class JobCreator {

	public abstract String getName();
	
	@Trace(dispatcher=true)
	protected Job createJob(JobData jobData, Workflow workflow, EventContext paramEventContext, boolean paramBoolean) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addJobData(attributes, jobData);
		NRTibcoUtils.addWorkflow(attributes, workflow);
		traced.addCustomAttributes(attributes);
		String workflowName = workflow != null ? workflow.getName() : "UnknownWorkflow";
		String jobName = jobData != null ? jobData.name : "UnknownJob";
		Transaction transaction = NewRelic.getAgent().getTransaction();
		transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "Job", new String[] {JobUtils.maskJobNumber(workflowName),JobUtils.maskJobNumber(jobName)});
		traced.setMetricName(new String[] {"Custom","JobCreator","createJob",workflowName,jobName});
		Job job = Weaver.callOriginal();
		if (job != null) {
			if (job.headers == null) {
				job.headers = new BWHeaders();
				transaction.insertDistributedTraceHeaders(job.headers);
			} else if(job.headers.isEmpty()) {
				transaction.insertDistributedTraceHeaders(job.headers);
			} else {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, job.headers);
			}
		}
		return job;
	}
	
	@Trace(dispatcher=true)
	protected Job createJob(Workflow workflow, XiNode xiNode, EventContext eventContext, boolean paramBoolean) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addWorkflow(attributes, workflow);
		if(xiNode != null) {
			JobUtils.addAttribute(attributes, "XiNode-Name", xiNode.getName());
		}
		traced.addCustomAttributes(attributes);
		String workflowName = workflow != null ? workflow.getName() : "UnknownWorkflow";
		ExpandedName expandedName = xiNode != null ? xiNode.getName() : null;
		
		String name = "XiNode";
		StringBuffer sb = new StringBuffer();
		if(expandedName != null) {
			if(expandedName.getNamespaceURI() != null) {
				sb.append(expandedName.getNamespaceURI());
			}
			if(expandedName.getLocalName() != null) {
				if(!sb.toString().isEmpty()) {
					sb.append(':');
				}
				sb.append(expandedName.getLocalName());
			}
		}
		if(!sb.toString().isEmpty()) {
			name = sb.toString();
		}
		Transaction transaction = NewRelic.getAgent().getTransaction();
		transaction.setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "Job", new String[] {JobUtils.maskJobNumber(workflowName),JobUtils.maskJobNumber(name)});
		traced.setMetricName(new String[] {"Custom","JobCreator","createJob",workflowName,name});
		Job job = Weaver.callOriginal();
		if (job != null) {
			if (job.headers == null) {
				job.headers = new BWHeaders();
				transaction.insertDistributedTraceHeaders(job.headers);
			} else if(job.headers.isEmpty()) {
				transaction.insertDistributedTraceHeaders(job.headers);
			} else {
				transaction.acceptDistributedTraceHeaders(TransportType.Other, job.headers);
			}
		}
		return job;
	}
}
