package com.tibco.spin.soap.processors;

import java.util.HashMap;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Token;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TracedMethod;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.nr.instrumentation.tibco.soap.Utils;
import com.tibco.spin.soap.processors.context.MessageContext;
import com.tibco.xml.data.primitive.ExpandedName;

@Weave(type = MatchType.Interface)
public abstract class ISoapProcessor {
	
	@NewField
	private Token token = null;

	@Trace
	public boolean process(MessageContext var1) {
		if(token == null) {
			Token t = NewRelic.getAgent().getTransaction().getToken();
			if(t != null && t.isActive()) {
				token = t;
			} else if(t != null) {
				t.expire();
				t = null;
			}
		}
		TracedMethod traced = NewRelic.getAgent().getTracedMethod();
		ExpandedName name = getName();
		String temp = name != null ? name.toString() : null;
		if(temp != null) {
			traced.setMetricName("Custom","TibcoSOAP","ISoapProcessor",getClass().getSimpleName(),"process",temp);
		} else {
			traced.setMetricName("Custom","TibcoSOAP","ISoapProcessor",getClass().getSimpleName(),"process");
		}
		HashMap<String, Object> attributes = new HashMap<String, Object>();
		Utils.addMessageContext(attributes, var1);
		traced.addCustomAttributes(attributes);
		return Weaver.callOriginal();
	}

	@Trace(async = true)
	public void finish() {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}
	
	public abstract ExpandedName getName();

}
