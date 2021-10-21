package com.nr.tibco.engine.instrumentation;

import java.util.HashMap;

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
	
	public static void addAttribute(String key, Object value, HashMap<String, Object> map) {
		if(key != null && !key.isEmpty() && value != null) {
			map.put(key, value);
		}
	}
}
