package com.nr.tibco.engine.instrumentation;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.tibco.im.jrmi.Request;

public class InboundWrapper implements InboundHeaders {
	
	private Request request;

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		return (String) request.get(name);
	}

}
