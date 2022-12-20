package com.nr.instrumentation.bw.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;
import com.tibco.bw.service.binding.bwhttp.BwHttpRequest;
import com.tibco.bw.service.binding.bwhttp.BwHttpResponse;

public class BWHeaders implements Headers {
	
	private BwHttpRequest request = null;
	private BwHttpResponse response = null;

	public BWHeaders(BwHttpRequest req) {
		request = req;
	}
	
	public BWHeaders(BwHttpResponse resp) {
		response = resp;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.HTTP;
	}

	@Override
	public String getHeader(String name) {
		if(request != null) {
			return request.getHeader(name);
		}
		return null;
	}

	@Override
	public Collection<String> getHeaders(String name) {
		List<String> list = new ArrayList<String>();
		String header = getHeader(name);
		if(header != null && !header.isEmpty()) {
			list.add(header);
		}
		return list;
	}

	@Override
	public void setHeader(String name, String value) {
		if(response != null) {
			response.setHeader(name, value);
		}
	}

	@Override
	public void addHeader(String name, String value) {
		setHeader(name, value);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection<String> getHeaderNames() {
		if(request != null) {
			Iterator iterator = request.getHeaderNames();
			List<String> names = new ArrayList<String>();
			while(iterator.hasNext()) {
				names.add(iterator.next().toString());
			}
			return names;
		}
		return new ArrayList<String>();
	}

	@Override
	public boolean containsHeader(String name) {
		return getHeaderNames().contains(name);
	}

}
