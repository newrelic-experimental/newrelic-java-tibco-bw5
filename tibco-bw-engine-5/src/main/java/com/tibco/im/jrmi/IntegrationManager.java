package com.tibco.im.jrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.ServerHeaders;

@Weave(type=MatchType.Interface)
public abstract class IntegrationManager {

	@Trace(dispatcher=true)
	public void send(Request paramRequest, Remote paramRemote) throws RemoteException {
		ServerHeaders headers = new ServerHeaders(paramRequest);
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public void send(Request paramRequest) throws RemoteException {
		ServerHeaders headers = new ServerHeaders(paramRequest);
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		Weaver.callOriginal();

	}

	@Trace(dispatcher=true)
	public abstract void send(Remote paramRemote) throws RemoteException;

}
