package com.nr.instrumentation.bw.services;

import java.io.IOException;
import java.util.logging.Level;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.OutboundHeaders;
import com.tibco.bw.service.binding.bwhttp.BwHttpResponseMessage;

public class BWResponseMessageWrapper implements OutboundHeaders {

	private BwHttpResponseMessage response;
	
	public BWResponseMessageWrapper(BwHttpResponseMessage resp) {
		response = resp;
	}
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}
	@Override
	public void setHeader(String name, String value) {
		try {
			response.setHeaderValue(name, value);
		} catch (IOException e) {
			NewRelic.getAgent().getLogger().log(Level.FINER,e, "Unable to populate outbound header");
		}
	}


}
