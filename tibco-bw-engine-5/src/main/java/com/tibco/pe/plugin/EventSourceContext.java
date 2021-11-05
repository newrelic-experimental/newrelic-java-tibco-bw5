package com.tibco.pe.plugin;

import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TransactionNamePriority;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;
import com.tibco.xml.datamodel.XiNode;

@Weave(type=MatchType.Interface)
public abstract class EventSourceContext {
	public abstract String getProcessModelName();
	
	@Trace
	public void newEvent(XiNode paramXiNode, EventContext paramEventContext) {
		String processName = getProcessModelName() != null ? getProcessModelName() : "UnknownProcess";
		NewRelic.getAgent().getTracedMethod().addCustomAttribute("ProcessName", processName);
		NewRelic.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "Event", new String[] {processName});
		Weaver.callOriginal();
	}
}
