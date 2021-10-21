package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
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
		JobUtils.addAttribute("Job-Name", jobData.name,attributes);
		JobUtils.addAttribute("Job-Group", jobData.group, attributes);
		JobUtils.addAttribute("Job-Service", jobData.service, attributes);
		JobUtils.addAttribute("Job-WF", jobData.wf, attributes);
		JobUtils.addAttribute("Workflow-Name", workflow.getName(), attributes);
		if (workflow.getStarter() !=  null) {
			JobUtils.addAttribute("Workflow-Starter-Name", workflow.getStarter().getName(), attributes);
			JobUtils.addAttribute("Workflow-Starter-StarterActivity", workflow.getStarter().getStarterActivityName(),
					attributes);
			JobUtils.addAttribute("Workflow-Starter-ProcessName", workflow.getStarter().processName, attributes);
		}
		traced.addCustomAttributes(attributes);
		String workflowName = workflow != null ? workflow.getName() : "UnknownWorkflow";
		String jobName = jobData != null ? jobData.name : "UnknownJob";
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "Job", new String[] {JobUtils.maskJobNumber(workflowName),JobUtils.maskJobNumber(jobName)});
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","JobCreator","createJob",workflowName,jobName});
		Job job = Weaver.callOriginal();
		if (job != null) {
			if (job.token == null) {
				job.token = NewRelic.getAgent().getTransaction().getToken();
			} else {
				job.token.link();
			}
		}
		return job;
	}
	
	@Trace(dispatcher=true)
	protected Job createJob(Workflow workflow, XiNode xiNode, EventContext eventContext, boolean paramBoolean) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		JobUtils.addAttribute("Workflow-Name", workflow.getName(), attributes);
		if (workflow.getStarter() !=  null) {
			JobUtils.addAttribute("Workflow-Starter-Name", workflow.getStarter().getName(), attributes);
			JobUtils.addAttribute("Workflow-Starter-StarterActivity", workflow.getStarter().getStarterActivityName(),
					attributes);
			JobUtils.addAttribute("Workflow-Starter-ProcessName", workflow.getStarter().processName, attributes);
		}
		if(xiNode != null) {
			JobUtils.addAttribute("XiNode-Name", xiNode.getName(), attributes);
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
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "Job", new String[] {JobUtils.maskJobNumber(workflowName),JobUtils.maskJobNumber(name)});
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","JobCreator","createJob",workflowName,name});
		Job job = Weaver.callOriginal();
		if (job != null) {
			if (job.token == null) {
				job.token = NewRelic.getAgent().getTransaction().getToken();
			} else {
				job.token.link();
			}
		}
		return job;
	}
}
