package com.xpn.p2pxwiki.services;

import com.xpn.p2pxwiki.P2PXWikiException;
import com.xpn.p2pxwiki.services.file.FileP2PServer;
import com.xpn.p2pxwiki.services.file.FileWikiGroupServices;

public class FileP2PServerTest extends P2PServerAbstractTest {
	private WikiGroupServices services = FileWikiGroupServices.getInstance(); 
	private static int count = 0;

	public FileP2PServerTest(String arg0) {
		super(arg0);
	}

	public Class getP2PServerClass() {
		return FileP2PServer.class;
	}
	
	public WikiGroup getNewWikiGroup()  throws P2PXWikiException {
	    return services.createWikiGroup("wiki"+(++count));
	}
}
