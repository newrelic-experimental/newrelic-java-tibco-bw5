package com.tibco.pe.plugin;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.BaseClass)
public abstract class Activity {

	@Trace
	public XiNode eval(ProcessContext paramProcessContext, XiNode paramXiNode) {
		NewRelic.getAgent().getTracedMethod().setMetricName(new String[] {"Custom","Activity",getClass().getSimpleName(),"eval"});
		return Weaver.callOriginal();
	}
}
