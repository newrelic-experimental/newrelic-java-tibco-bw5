package com.tibco.xml.soap.impl.transport;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.InboundHeaders;

public class InboundWrapper implements InboundHeaders {

	private DefaultTransportEntity entity;
	
	public InboundWrapper(DefaultTransportEntity e) {
		entity = e;
	}

	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		return (String) entity.m_additionalHeaderMap.get(name);
	}

}
