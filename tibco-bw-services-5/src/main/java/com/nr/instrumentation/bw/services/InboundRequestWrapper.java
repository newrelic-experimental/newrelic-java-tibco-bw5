package com.nr.instrumentation.bw.services;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.tibco.bw.service.ExchangeContext;

public class InboundRequestWrapper implements InboundHeaders {

	private ExchangeContext exchange;
	
	public InboundRequestWrapper(ExchangeContext exCtx) {
		exchange = exCtx;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		return (String) exchange.getContextObject(name);
	}

}
