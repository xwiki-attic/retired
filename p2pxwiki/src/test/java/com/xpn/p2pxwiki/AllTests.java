package com.xpn.p2pxwiki;

import junit.framework.TestSuite;
import junit.textui.TestRunner;

import com.xpn.p2pxwiki.communication.ConnectorPluginTest;
import com.xpn.p2pxwiki.communication.JxtaRpcLocalTest;
import com.xpn.p2pxwiki.communication.XmlRpcLocalTest;
import com.xpn.p2pxwiki.communication.jxtarpc.JxtaRpcNetworkTest;
import com.xpn.p2pxwiki.communication.xmlrpc.XmlRpcNetworkTest;

/**
 * 
 */

public class AllTests extends TestSuite {
	public AllTests() {
		addTest(new TestSuite(ConnectorPluginTest.class));
		addTest(new TestSuite(XmlRpcNetworkTest.class));
		addTest(new TestSuite(XmlRpcLocalTest.class));
		addTest(new TestSuite(JxtaRpcNetworkTest.class));
		addTest(new TestSuite(JxtaRpcLocalTest.class));
//		addTest(new TestSuite(FileP2PServerTest.class));
	}

	public static void main(String[] args) {
		TestRunner.run(new AllTests());
	}
}
