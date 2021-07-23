package com.tibco.bw.rmi;

import java.io.Serializable;
import java.rmi.RemoteException;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;

@Weave(type=MatchType.Interface)
public abstract class SyncRemote {

	@Trace(dispatcher=true)
	public abstract void respond(Serializable paramSerializable) throws RemoteException;
}
