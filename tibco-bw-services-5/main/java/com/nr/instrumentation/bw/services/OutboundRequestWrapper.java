package com.nr.instrumentation.bw.services;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import com.tibco.bw.service.ExchangeContext;

public class OutboundRequestWrapper implements OutboundHeaders {
	
	private ExchangeContext exchange;
	
	public OutboundRequestWrapper(ExchangeContext exCtx) {
		exchange = exCtx;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String name, String value) {
		exchange.setContextObject(name, value);
	}

}
