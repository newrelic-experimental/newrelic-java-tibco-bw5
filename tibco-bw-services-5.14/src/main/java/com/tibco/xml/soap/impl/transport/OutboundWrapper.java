package com.tibco.xml.soap.impl.transport;

import java.util.HashMap;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.OutboundHeaders;

public class OutboundWrapper implements OutboundHeaders {
	
	private DefaultTransportEntity entity;
	
	public OutboundWrapper(DefaultTransportEntity e) {
		entity = e;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setHeader(String name, String value) {
		if(entity.m_additionalHeaderMap != null) {
			entity.m_additionalHeaderMap.put(name, value);
		} else {
			entity.m_additionalHeaderMap = new HashMap();
			entity.m_additionalHeaderMap.put(name, value);
		}
	}

}
