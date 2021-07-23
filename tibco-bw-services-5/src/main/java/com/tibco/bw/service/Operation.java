package com.tibco.bw.service;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.Interface)
public abstract class Operation {

	@Trace(dispatcher=true)
	public void onMessage(ExchangeContext exchangeContext, XiNode paramXiNode) {
		if (exchangeContext != null) {
			ServiceContext serviceCtx = exchangeContext.getServiceContext();
			if (serviceCtx != null) {
				String service = serviceCtx.getName();
				String serviceURI = serviceCtx.getServiceUri();
				if (service != null) {
					NewRelic.addCustomParameter("Service Name", service);
					NewRelic.getAgent().getTracedMethod().setMetricName(new String[] { "Custom", "Operation", service });
				}
				if (serviceURI != null) {
					NewRelic.addCustomParameter("Service URI", serviceURI);
				}
			}
		}
		Weaver.callOriginal();
	}
	
}
