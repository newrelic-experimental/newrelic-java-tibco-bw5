package com.tibco.im.jrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.OutboundWrapper;

@Weave(type=MatchType.Interface)
public abstract class IntegrationManager {

	@Trace(dispatcher=true)
	public void send(Request paramRequest, Remote paramRemote) throws RemoteException {
		OutboundWrapper wrapper = new OutboundWrapper(paramRequest);
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public void send(Request paramRequest) throws RemoteException {
		OutboundWrapper wrapper = new OutboundWrapper(paramRequest);
		NewRelic.getAgent().getTracedMethod().addOutboundRequestHeaders(wrapper);
		Weaver.callOriginal();

	}

	@Trace(dispatcher=true)
	public abstract void send(Remote paramRemote) throws RemoteException;

}
