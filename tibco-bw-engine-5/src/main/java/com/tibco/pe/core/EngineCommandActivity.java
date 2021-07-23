package com.tibco.pe.core;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.pe.plugin.ProcessContext;
import com.tibco.xml.datamodel.XiNode;

@Weave
public abstract class EngineCommandActivity {

	public abstract String getClassName();

	protected abstract String getURI();

	public abstract String getName();

	@Trace(dispatcher=true)
	public XiNode eval(ProcessContext processContext, XiNode paramXiNode) {
		String processCtxName = processContext.getName();
		String service = processContext.getService();
		String invocationName = processContext.getInvocationName();
		NewRelic.addCustomParameter("Process Context Name", processCtxName);
		NewRelic.addCustomParameter("Service", service);
		NewRelic.addCustomParameter("Invocation Name", invocationName);
		NewRelic.addCustomParameter("EngineCommandActivity "+getName()+ " URI", getURI());
		NewRelic.addCustomParameter("EngineCommandActivity "+getName()+ " Class", getClassName());
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","EngineCommandActivity",getName()});
		return (XiNode)Weaver.callOriginal();
	}

}
