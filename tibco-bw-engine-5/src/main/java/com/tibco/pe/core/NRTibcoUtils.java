package com.tibco.pe.core;

import java.util.Map;

import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.TransportType;
import com.nr.tibco.engine.instrumentation.BWHeaders;
import com.nr.tibco.engine.instrumentation.HeaderUtils;
import com.tibco.pe.plugin.ActivityContext;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.xml.datamodel.XiNode;

public class NRTibcoUtils {

	public static void addAttribute(Map<String, Object> map, String key, Object value) {
		if(key != null && !key.isEmpty() && value != null) {
			if(!key.toLowerCase().startsWith("tibcobw-")) {
				key = "TibcoBW-"+key;
			}
			map.put(key, value);
		}
	}
	
	public static void addProcessContext(Map<String, Object> attributes, ProcessContext context) {
		if(context != null) {
			// added try catch to avoid null point errors
			try {
				addAttribute(attributes, "ProcessContext-FullCallName", context.getFullCallName());
			} catch (NullPointerException e) {
			}
			addAttribute(attributes, "ProcessContext-ID", context.getId());
			try {
				addAttribute(attributes, "ProcessContext-InvocationName", context.getInvocationName());
			} catch (NullPointerException e) {
			}
			addAttribute(attributes, "ProcessContext-Name", context.getName());
			addAttribute(attributes, "ProcessContext-Service", context.getService());
		}
		
	}
	
	public static void addActivityContext(Map<String,Object> attributes, ActivityContext context) {
		if(context != null) {
			addAttribute(attributes, "ActivityContext-Name", context.getName());
			addAttribute(attributes, "ActivityContext-ProcessModelName", context.getProcessModelName());
			addAttribute(attributes, "ActivityContext-TraceSource", context.getTraceSource());
		}
	}

	public static void addTrack(Map<String,Object> attributes, Track track) {
		if(track != null) {
			addAttribute(attributes, "Track-FullTaskName", track.getFullTaskName());
			addAttribute(attributes, "Track-Name", track.getName());
		}
	}
	
	public static void addJobData(Map<String,Object> attributes, JobData data) {
		if(data != null) {
			addAttribute(attributes, "JobData-CustomID", data.customId);
			addAttribute(attributes, "JobData-Group", data.group);
			addAttribute(attributes, "JobData-Name", data.name);
			addAttribute(attributes, "JobData-Service", data.service);
			addAttribute(attributes, "JobData-StarterName", data.starterName);
			addAttribute(attributes, "JobData-Task", data.task);
			addAttribute(attributes, "JobData-Workflow", data.wf);
		}
	}
	
	public static void addJobWithPrefix(Map<String, Object> attributes, String prefix, Job context) {
		if(context != null) {
			addAttribute(attributes, prefix+"-Job-FullCallName", context.getFullCallName());
			addAttribute(attributes, prefix+"-Job-ID", context.getId());
			addAttribute(attributes, prefix+"-Job-InvocationName", context.getInvocationName());
			addAttribute(attributes, prefix+"-Job-Name", context.getName());
			addAttribute(attributes, prefix+"-Job-Service", context.getService());
		}
		
	}
	
	
	public static void addWorkflow(Map<String,Object> attributes, Workflow workflow) {
		if(workflow != null) {
			addAttribute(attributes, "WorkFlow-Name", workflow.getName());
			addProcessStarter(attributes, "Workflow", workflow.getStarter());
			
		}
	}
	
	public static void addProcessStarter(Map<String,Object> attributes, String prefix, ProcessStarter starter) {
		if(starter != null) {
			String key = prefix != null ? prefix + "-Starter-Name" : "Starter-Name";
			addAttribute(attributes, key, starter.getName());
			key = prefix != null ? prefix + "-Starter-StarterActivityName" : "Starter-StarterActivityName";
			addAttribute(attributes, key, starter.getStarterActivityName());
			key = prefix != null ? prefix + "-Starter-ProcessName" : "Starter-ProcessName";
			addAttribute(attributes, key, starter.processName);
		}
	}
	
	public static void addXiNode(Map<String,Object> attributes, XiNode node) {
		if(node != null) {
			addAttribute(attributes, "XiNode-Name", node.getName());
			addAttribute(attributes, "XiNode-BaseURI", node.getBaseURI());
			addAttribute(attributes, "XiNode-ItemKind", node.getItemKind());
			addAttribute(attributes, "XiNode-Type", node.getType());

		}
	}
	
	public static void checkHeaders(ProcessContext context, Transaction transaction) {
		if(context instanceof Job) {
			Job job = (Job)context;
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
	}
}
