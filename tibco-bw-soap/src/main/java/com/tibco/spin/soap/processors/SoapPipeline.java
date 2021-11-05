package com.tibco.spin.soap.processors;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.tibco.soap.Utils;
import com.tibco.spin.soap.processors.context.MessageContext;
import com.tibco.spin.soap.processors.context.OperationContext;
import com.tibco.xml.soap.api.SoapMessage;

@Weave
public abstract class SoapPipeline {

	@Trace(dispatcher = true)
	public MessageContext process(SoapMessage var1) {
		
		return Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	public void process(SoapMessage var1, OperationContext var2) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		Utils.addOperationContext(attributes, var2);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		Weaver.callOriginal();
	}
	
	@Trace(dispatcher = true)
	protected void process(MessageContext var1) {
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		Utils.addMessageContext(attributes, var1);
		NewRelic.getAgent().getTracedMethod().addCustomAttributes(attributes);
		Weaver.callOriginal();
	}
	

}
