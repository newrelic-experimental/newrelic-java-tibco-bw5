package com.tibco.pe.core;

import java.util.HashMap;
import java.util.Map;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.ExactClass)
public abstract class StatsCollector {

	public void jobCompleted(Job job, long endTime, boolean success) {
		String result;
		if(success) {
			result = "success";
		} else {
			result = "error";
		}
		if(job.headers != null) {
			job.headers = null;
		}
		long startTime = job.h;
		String jobName = job.getName();
		String service = job.getService();
		String metricName = "Jobs/"+jobName+"/"+service;
		long executeTime = endTime - startTime;
		NewRelic.recordResponseTimeMetric(metricName, executeTime);
		NewRelic.incrementCounter(metricName+"/"+result);
		
		Map<String, Object> eventMap = new HashMap<String, Object>();
		eventMap.put("Result", result);
		eventMap.put("Service", service);
		eventMap.put("Job Name", jobName);
		eventMap.put("StartTime", job.h);
		eventMap.put("EndTime", endTime);
		eventMap.put("Execute Time", executeTime);
		
		NewRelic.getAgent().getInsights().recordCustomEvent("Jobs", eventMap);
		
		Weaver.callOriginal();
	}
}
