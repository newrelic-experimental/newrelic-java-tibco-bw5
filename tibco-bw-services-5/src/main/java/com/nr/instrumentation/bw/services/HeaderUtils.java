package com.nr.instrumentation.bw.services;

import com.newrelic.agent.Transaction;
import com.newrelic.agent.tracing.DistributedTracePayloadImpl;
import com.newrelic.agent.tracing.SpanProxy;

public class HeaderUtils {
	
	public static boolean canCallAccept() {
		Transaction tx = Transaction.getTransaction(false);
		if(tx != null) {
			SpanProxy spanProxy = tx.getSpanProxy();
			DistributedTracePayloadImpl outbound = spanProxy.getOutboundDistributedTracePayload();
			return outbound == null;
		}
		
		return false;
	}

}
