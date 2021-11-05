package com.tibco.spin.soap.processors;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.spin.soap.processors.context.MessageContext;

@Weave(type=MatchType.BaseClass)
public abstract class SoapProcessorBase {

	@Trace
	protected boolean processRequest(MessageContext var1)  {
		return Weaver.callOriginal();
	}
	
	@Trace
	protected boolean processResponse(MessageContext var1) {
		return Weaver.callOriginal();
	}
	
	@Trace
	protected boolean processFault(MessageContext var1) {
		return Weaver.callOriginal();
	}
}
