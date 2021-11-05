package com.tibco.plugin.share.http.client;

import java.net.URI;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;

import com.newrelic.api.agent.HttpParameters;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.soap.api.transport.TransportApplication;
import com.tibco.xml.soap.api.transport.TransportContext;
import com.tibco.xml.soap.api.transport.TransportEntity;
import com.tibco.xml.soap.api.transport.TransportMessage;
import com.tibco.xml.soap.api.transport.TransportUri;
import com.tibco.xml.soap.impl.transport.DefaultTransportEntity;
import com.tibco.xml.soap.impl.transport.JakartaTransportContext;
import com.tibco.xml.soap.impl.transport.OutboundWrapper;

@Weave
public abstract class JakartaHttpTransportDriver {
	
	@Trace
	public void sendMessage(TransportMessage transportMessage, TransportApplication paramTransportApplication, int paramInt) {
		TransportEntity transportEntity = transportMessage.getBody();
		if(transportEntity != null && DefaultTransportEntity.class.isInstance(transportEntity)) {
			OutboundWrapper wrapper = new OutboundWrapper((DefaultTransportEntity) transportEntity);
			
			NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		}
		TransportContext context = transportMessage.getTransportContext();
		if(context != null) {
			TransportUri transportURI = context.getTransportUri();
			URI uri = URI.create(transportURI.toExternalForm());
			HttpParameters params = HttpParameters.library("JakartaHttpTransportDriver").uri(uri).procedure("sendMessage").noInboundHeaders().build();
			NewRelic.getAgent().getTracedMethod().reportAsExternal(params);
		}
		
		Weaver.callOriginal();
	}

	@Weave
	private static class RequestExecutor {
		
		@NewField
		protected Token token;
		
		 @SuppressWarnings("unused")
		RequestExecutor(HttpClient paramHttpClient, HttpMethodBase paramHttpMethodBase, JakartaTransportContext paramJakartaTransportContext, TransportApplication paramTransportApplication) {
			 token = NewRelic.getAgent().getTransaction().getToken();
		 }
		
		@Trace(async=true)
		public void run() {
			if(token != null) {
				token.linkAndExpire();
				token = null;
			}
			Weaver.callOriginal();
		}
	}
}
