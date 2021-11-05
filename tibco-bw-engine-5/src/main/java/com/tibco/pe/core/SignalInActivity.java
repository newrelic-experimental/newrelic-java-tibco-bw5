package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.TransportType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.xml.datamodel.XiNode;

@Weave
public abstract class SignalInActivity {


	public abstract String getClassName();

	protected abstract String getURI();

	public abstract String getName();

	@Trace
	public XiNode eval(ProcessContext processContext, XiNode xiNode) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addProcessContext(attributes, processContext);
		NRTibcoUtils.addXiNode(attributes, xiNode);
		NRTibcoUtils.addAttribute(attributes, "SignalInActivity-Classname", getClassName());
		NRTibcoUtils.addAttribute(attributes, "SignalInActivity-URI", getURI());
		NRTibcoUtils.addAttribute(attributes, "SignalInActivity-Name", getName());
		
		traced.setMetricName(new String[] {"Custom","SignalInActivity",getName()});
		if(Job.class.isInstance(processContext)) {
			Job job = (Job)processContext;
			if(job.headers != null) {
				NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.Other, job.headers);
			}
		}

		return Weaver.callOriginal();
	}

}
