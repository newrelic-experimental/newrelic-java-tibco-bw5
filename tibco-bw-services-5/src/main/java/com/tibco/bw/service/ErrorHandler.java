package com.tibco.bw.service;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class ErrorHandler {

	@Trace
	public void onError(ExchangeContext exchangeContext, Exception e) {
		Weaver.callOriginal();
	}
	
}
