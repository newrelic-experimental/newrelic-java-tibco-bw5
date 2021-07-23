package com.nr.tibco.engine.instrumentation;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import com.tibco.im.jrmi.Request;

public class OutboundWrapper implements OutboundHeaders {
	
	private Request request;
	
	public OutboundWrapper(Request req) {
		request = req;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		request.put(name, value);
	}

}
