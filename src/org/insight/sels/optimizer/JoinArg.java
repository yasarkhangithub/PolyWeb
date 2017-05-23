package org.insight.sels.optimizer;

import org.insight.sels.query.SubQuery;
import org.insight.sels.query.TPGroup;

/**
 * 
 * @author Yasar Khan
 *
 * @param <T>
 */
public class JoinArg<T> {

	public static final String QUERY_NODE = "QUERY_NODE";
	public static final String JOIN_NODE = "JOIN_NODE";
	
	private T arg;
	private String argType;

	public T getArg() {
		return arg;
	}

	public void setArg(T arg) {
		this.arg = arg;
	}

	public String getArgType() {
		return argType;
	}

	public void setArgType(String argType) {
		this.argType = argType;
	}
	
	@Override
	public String toString() {
		String argString = "";
		
		if(argType.equals(QUERY_NODE)) {
			TPGroup tpGroup = (TPGroup) arg;
			argString += "Arg-" + tpGroup.getId();
		} else {
			FederatedJoin<T> fedJoin = (FederatedJoin<T>) arg;
			argString += "Arg-" + fedJoin.getId();
		}
		
		argString += "<"+ argType +"> ";
		
		return argString;
	}
	
}
