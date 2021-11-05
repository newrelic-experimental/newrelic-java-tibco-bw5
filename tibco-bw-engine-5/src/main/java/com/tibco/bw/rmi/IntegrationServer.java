package com.tibco.bw.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.tibco.engine.instrumentation.ServerHeaders;

@Weave(type=MatchType.Interface)
public abstract class IntegrationServer {

	@Trace(dispatcher=true)
	public void send(ServerRequest request, Remote paramRemote) throws RemoteException {
		ServerHeaders headers = new ServerHeaders(request);
		
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		Weaver.callOriginal();
	}

	@Trace(dispatcher=true)
	public void send(ServerRequest request) throws RemoteException {
		ServerHeaders headers = new ServerHeaders(request);
		
		NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers);
		Weaver.callOriginal();
	}

}
