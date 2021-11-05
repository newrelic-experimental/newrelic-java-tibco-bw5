package com.nr.instrumentation.tibco.soap;

import java.util.Map;

import com.tibco.spin.config.ISoapServiceConfig;
import com.tibco.spin.config.address.IServiceAddress;
import com.tibco.spin.soap.processors.context.MessageContext;
import com.tibco.spin.soap.processors.context.OperationContext;
import com.tibco.spin.soap.processors.context.ServiceContext;

public class Utils {

	public static void addAttribute(Map<String, Object> attributes, String key, Object value) {
		if(key != null && !key.isEmpty() && value != null) {
			attributes.put(key, value);
		}
	}
	
	public static void addMessageContext(Map<String,Object> attributes, MessageContext context) {
		if(context != null) {
			addAttribute(attributes, "MessageContext-MessageID", context.getMessageId());
			addAttribute(attributes, "MessageContext-MessageName", context.getMessageName());
			addAttribute(attributes, "MessageContext-ScopeName", context.getScopeName());
			addAttribute(attributes, "MessageContext-SoapAction", context.getSoapAction());
			addAttribute(attributes, "MessageContext-ID", context.getMessageId());
			addServiceContext(attributes, context.getServiceContext());
			addOperationContext(attributes, context.getOperationContext());
			addAttribute(attributes, "MessageContext-isInbound", context.isInbound());
			addAttribute(attributes, "MessageContext-isInput", context.isInput());
			addAttribute(attributes, "MessageContext-isService", context.isService());
		}
	}
	
	public static void addOperationContext(Map<String,Object> attributes, OperationContext context) {
		if(context != null) {
			addAttribute(attributes, "OperationContext-ScopeName", context.getScopeName());
			addAttribute(attributes, "OperationContext-OperationName", context.getOperationName());
			addAttribute(attributes, "OperationContext-OperationInstanceId", context.getOperationInstanceId());
		}
	}
	
	public static void addServiceAddress(Map<String,Object> attributes, IServiceAddress address) {
		if(address != null) {
			addAttribute(attributes, "ServiceAddress-Name", address.getName());
			addAttribute(attributes, "ServiceAddress-ServiceType", address.getServiceType());
			addAttribute(attributes, "ServiceAddress-Location", address.getLocation());
		}
		
	}
	
	public static void addISoapServiceConfig(Map<String,Object> attributes, ISoapServiceConfig config) {
		if(config != null) {
			addAttribute(attributes, "ISoapServiceConfig-Name", config.getName().getExpandedForm());
			addAttribute(attributes, "ISoapServiceConfig-TargetService", config.getTargetService());
			addServiceAddress(attributes, config.getAddress());
		}
		
	}
	
	public static void addServiceContext(Map<String,Object> attributes, ServiceContext context) {
		if(context != null) {
			addAttribute(attributes, "ServiceContext-ScopeName", context.getScopeName());
			addISoapServiceConfig(attributes, context.getServiceConfig());			
		}
	}
}
