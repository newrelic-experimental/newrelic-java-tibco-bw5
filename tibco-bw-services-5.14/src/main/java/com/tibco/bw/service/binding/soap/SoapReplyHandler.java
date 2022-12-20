package com.tibco.bw.service.binding.soap;

import com.newrelic.agent.bridge.Token;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.datamodel.XiNode;

@Weave
public abstract class SoapReplyHandler {

	@NewField
	public Token token;
	
	public void reply(XiNode paramXiNode) {
		if(token != null) {
			token.linkAndExpire();
			token = null;
		}
		Weaver.callOriginal();
	}
}
