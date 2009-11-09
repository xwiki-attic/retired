package com.xpn.p2pxwiki.communication;

import java.net.URL;

import org.apache.xmlrpc.client.P2PXmlRpcLiteHttpTransportFactory;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.webserver.P2PServletWebServer;
import org.apache.xmlrpc.webserver.WebServer;
import org.p2psockets.P2PNetwork;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiConfig;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.xmlrpc.XWikiXmlRpcServlet;

// TODO: this code has a bug somewhere
public class Try {	
    public static void main(String[] args) throws Exception {
// not relevant - only for logging
    	XWikiConfig xconfig = new XWikiConfig();

		XWikiContext xcontext = new XWikiContext();
		XWiki xwiki = new XWiki(xconfig, xcontext);
		xcontext.setWiki(xwiki);
// end not relevant

		P2PNetwork.autoSignin("localPeer", "JXTA:P2PXWikiNetwork");
// works
//      WebServer webServer = new P2PWebServer(9090);
//      PropertyHandlerMapping phm = new PropertyHandlerMapping();
//      phm.addHandler("hello_handler", HelloHandler.class);
//      
//      XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();
//      xmlRpcServer.setHandlerMapping(phm);

		
// does not work
		WebServer webServer = new P2PServletWebServer(new XWikiXmlRpcServlet(), 9090);
        
        webServer.start();
        System.out.println("Server Started");
        
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL("http://www.localPeer.peer:9090"));
        XmlRpcClient client = new XmlRpcClient();
        client.setTransportFactory(new P2PXmlRpcLiteHttpTransportFactory(client)); // new XmlRpcCommonsTransportFactory(client)
        client.setConfig(config);
        Object[] params = new Object[]{"world"};
        System.out.println("Client Executing");
        System.out.println(client.execute("hello_handler.hello", params));
    }
}
