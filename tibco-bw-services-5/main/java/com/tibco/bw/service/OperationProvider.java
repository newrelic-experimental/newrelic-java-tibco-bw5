package com.tibco.bw.service;

import java.util.logging.Level;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.Interface)
public abstract class OperationProvider {
	@Trace(dispatcher=true)
	public void onEvent(XiNode xiNode, ExchangeContext exchangeCtx) {
		if (exchangeCtx != null) {
			ServiceContext serviceCtx = exchangeCtx.getServiceContext();
			if (serviceCtx != null) {
				String serviceURI = serviceCtx.getServiceUri();
				if(serviceURI == null || serviceURI.isEmpty()) {
					serviceURI = "UnknownServiceURI";
				}
				NewRelic.addCustomParameter("ServiceURI", serviceURI);
				String serviceName = serviceCtx.getName();
				if(serviceName == null || serviceName.isEmpty()) {
					serviceName = "UnknownService";
				}
				NewRelic.addCustomParameter("Service Name", serviceName);
				NewRelic.getAgent().getLogger().log(Level.FINER, "Added serviceURI: {0} and Name: {1}", serviceURI, serviceName);
				NewRelic.getAgent().getTracedMethod().setMetricName(new String[] { "Custom", "OperationProvider", serviceName });
			}
		}
		Weaver.callOriginal();
	}

}
