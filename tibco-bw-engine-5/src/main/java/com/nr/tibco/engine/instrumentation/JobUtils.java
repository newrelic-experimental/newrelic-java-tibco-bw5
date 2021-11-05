package com.nr.tibco.engine.instrumentation;

import java.util.Map;

import com.tibco.pe.plugin.ActivityContext;
import com.tibco.pe.plugin.ProcessContext;

public class JobUtils {

	public static String ALLDIGITS = "\\d+";
	public static String JOBANDDIGITS = "Job-\\d+";
	
	public static String JOBMASK = "JobNumberXXXXX";
	
	public static String maskJobNumber(String job) {
		
		if(job.matches(ALLDIGITS) || job.matches(JOBANDDIGITS)) {
			return JOBMASK;
		}
		
		return job;
	}
	
	public static void addAttribute(Map<String, Object> map, String key, Object value) {
		if(key != null && !key.isEmpty() && value != null) {
			map.put(key, value);
		}
	}
	
	public static void addProcessContext(Map<String, Object> attributes, ProcessContext context) {
		if(context != null) {
			addAttribute(attributes, "ProcessContext-FullCallName", context.getFullCallName());
			addAttribute(attributes, "ProcessContext-ID", context.getId());
			addAttribute(attributes, "ProcessContext-InvocationName", context.getInvocationName());
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
	
	
}
