package com.nr.instrumentation.bw.services;

import java.util.Map;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;
import com.tibco.xml.soap.api.transport.TransportContext;

public class OutboundTransportWrapper implements OutboundHeaders {

	private TransportContext transportCtx;
	
	public  OutboundTransportWrapper(TransportContext ctx) {
		transportCtx = ctx;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public void setHeader(String name, String value) {
		Map map = transportCtx.getFieldMap();
		if(map != null) {
			map.put(name, value);
		}
	}

}
