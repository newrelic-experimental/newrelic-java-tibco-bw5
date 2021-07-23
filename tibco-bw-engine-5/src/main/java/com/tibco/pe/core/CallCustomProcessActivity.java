package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.xml.datamodel.XiNode;

@Weave
public abstract class CallCustomProcessActivity {

	public abstract String getClassName();

	protected abstract String getURI();

	public abstract String getName();

	@Trace
	public XiNode eval(ProcessContext processContext, XiNode xiNode) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","CalledCustomProcessActivity",getName()});
		NewRelic.addCustomParameter("CalledCustomProcessActivity "+getName()+ " URI", getURI());
		NewRelic.addCustomParameter("CalledCustomProcessActivity "+getName()+ " class name", getClassName());
		return Weaver.callOriginal();
	}
}
