package com.tibco.pe.core;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
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
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addProcessContext(attributes, processContext);
		NRTibcoUtils.addAttribute(attributes,"ActivityClassName", getClassName());
		NRTibcoUtils.addAttribute(attributes,"ActivityName", getName());
		NRTibcoUtils.addAttribute(attributes,"ActivityURI", getURI());
		NRTibcoUtils.addXiNode(attributes, paramXiNode);
		traced.addCustomAttributes(attributes);
		traced.setMetricName(new String[] {"Custom","EngineCommandActivity",getName()});
		return (XiNode)Weaver.callOriginal();
	}

}
