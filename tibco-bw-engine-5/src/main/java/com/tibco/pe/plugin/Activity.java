package com.tibco.pe.plugin;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.Transaction;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.pe.core.NRTibcoUtils;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.BaseClass)
public abstract class Activity {

	@Trace(dispatcher = true)
	public XiNode eval(ProcessContext paramProcessContext, XiNode paramXiNode) {
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		Transaction transaction = NewRelic.getAgent().getTransaction();
		NRTibcoUtils.checkHeaders(paramProcessContext, transaction);
		
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		NRTibcoUtils.addProcessContext(attributes, paramProcessContext);
		NRTibcoUtils.addXiNode(attributes, paramXiNode);
		traced.addCustomAttributes(attributes);
		traced.setMetricName(new String[] {"Custom","Activity",getClass().getSimpleName(),"eval"});
		return Weaver.callOriginal();
	}
}
