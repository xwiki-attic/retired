package com.xpn.p2pxwiki.examples;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.xpn.p2pxwiki.rpc.RpcClient;

public class RpcClientExamples extends ClientBaseExamples {
	private static Log log = LogFactory.getFactory().getInstance(
			RpcClientExamples.class);
	
	public void test() { 
		RpcClient rpcClient = new RpcClient(context);
		log.error("getting all wikipage names");
		
		String token = rpcClient.login("bikash", "bikash", "registry");
		log.error("got token: " + token
				+ " after login in to RPCHandler class at the registry");
		
		Vector res = rpcClient.getAllPages("registry");
		log.error("got " + res.size() + " entries for all the pages");
		for (int i = 0; i < res.size(); i++) {
			log.error(((String) res.get(i))+"");
		}
	}
	
	public void run() { 
		test() ; 
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RpcClientExamples examples = new RpcClientExamples(); 
		examples.initJXTA(); 
		examples.run(); 
	}

}
