package com.tibco.bw.service.binding.bwhttp.impl;

import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.bw.service.binding.soap.SoapDriver;
import com.tibco.plugin.share.security.context.SecurityContext;
import com.tibco.xml.soap.api.SoapMessage;
import com.tibco.xml.soap.api.SoapletFinisher;

@Weave
public class DefaultSoapApplication {

	@Trace(dispatcher=true)
	public void processMessage(SoapDriver paramSoapDriver, SoapMessage paramSoapMessage1, SoapMessage paramSoapMessage2, SoapletFinisher paramSoapletFinisher, SecurityContext paramSecurityContext) {
		Weaver.callOriginal();
	}
}
