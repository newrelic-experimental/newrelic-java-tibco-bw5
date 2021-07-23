package com.tibco.xml.soap.api.transport;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(type=MatchType.Interface)
public abstract class TransportApplication {

	@Trace(dispatcher=true)
	public void processMessage(TransportMessage transportMsg) {
		TransportContext transportCtx = transportMsg.getTransportContext();
		if (transportCtx != null) {
			String URI = transportCtx.getTransportUri() != null ? transportCtx.getTransportUri().toString() : null;
			if(URI == null || URI.isEmpty()) {
				URI = "Unknown";
			}
			String soapAction = transportCtx.getSoapAction();
			if(soapAction != null) {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH,true,"TransportApp",new String[] { getClass().getSimpleName(), URI,soapAction });
			} else {
				NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH,true,"TransportApp",new String[] { getClass().getSimpleName(), URI});
			}
		}
		Weaver.callOriginal();
	}

	public void processReplyException(TransportContext paramTransportContext, Exception e) {
		NewRelic.noticeError(e);
		Weaver.callOriginal();
	}

}
