package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
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
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","SignalInActivity",getName()});
		NewRelic.addCustomParameter("SignalInActivity "+getName()+ " URI", getURI());
		NewRelic.addCustomParameter("SignalInActivity "+getName()+ " class name", getClassName());
		if(Job.class.isInstance(processContext)) {
			Job job = (Job)processContext;
			if(job.token != null) {
				job.token.link();
			}
		}

		return Weaver.callOriginal();
	}

}
