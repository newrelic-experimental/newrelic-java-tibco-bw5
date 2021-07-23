package com.nr.instrumentation.bw.services;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import com.tibco.bw.service.binding.bwhttp.BwHttpResponse;

public class BWResponseWrapper implements OutboundHeaders {
	
	private BwHttpResponse response;
	
	public BWResponseWrapper(BwHttpResponse resp) {
		response = resp;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public void setHeader(String name, String value) {
		response.setHeader(name, value);
	}

}
