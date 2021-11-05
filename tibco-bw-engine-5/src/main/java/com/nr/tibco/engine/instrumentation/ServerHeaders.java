package com.nr.tibco.engine.instrumentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;
import com.tibco.im.jrmi.Request;

public class ServerHeaders implements Headers {
	
	private Request request = null;

	public ServerHeaders(Request req) {
		request = req;
	}
	
	@Override
	public HeaderType getHeaderType() {
		return HeaderType.MESSAGE;
	}

	@Override
	public String getHeader(String name) {
		Object value = request.get(name);
		
		return value != null ? value.toString() : null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		List<String> list = new ArrayList<String>();
		String value = getHeader(name);
		if(value != null && !value.isEmpty()) {
			list.add(value);
		}
		return list;
	}

	@Override
	public void setHeader(String name, String value) {
		request.put(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		request.put(name, value);
	}

	@Override
	public Collection<String> getHeaderNames() {
		Set<?> keys = request.keySet();
		List<String> list = new ArrayList<String>();
		
		for(Object key : keys) {
			list.add(key.toString());
		}
		return list;
	}

	@Override
	public boolean containsHeader(String name) {
		return getHeaderNames().contains(name);
	}

}
