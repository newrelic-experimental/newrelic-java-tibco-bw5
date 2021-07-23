package com.nr.instrumentation.bw.services;

import java.util.Map;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;
import com.tibco.xml.soap.api.transport.TransportContext;

public class InboundTranportWrapper implements InboundHeaders {

	private TransportContext transportCtx;

	public InboundTranportWrapper(TransportContext ctx) {
		transportCtx = ctx;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		if (transportCtx != null) {
			Map map = transportCtx.getFieldMap();
			if (map != null) {
				return (String) map.get(name);
			}
		}
		return null;
	}


}
