package com.xpn.p2pxwiki;

import com.xpn.xwiki.plugin.PluginException;

public class P2PXWikiException extends PluginException {
    private static final long serialVersionUID = 1106138323477622364L;
    
	public static final int COMMUNICATION = 30999;
	public static final int REMOTE_CALL = 30998;
	public static final int NO_SUCH_GROUP = 30997;
	public static final int ACCESS_DENIED = 30996;
	public static final int GROUP_NOT_ALLOWED = 30995;
	public static final int INSTANCE = 30994;
	public static final int SINGLETON_INSTANCE = 30993;
	public static final int INIT_RPC_NETWORK = 30992;
	public static final int INIT_JXTA_NETWORK = 30991;
	
	static final String MODULE = "P2PXWiki";
	
	public P2PXWikiException(int code, String message, Throwable e,
			Object[] args) {
		super(MODULE, code, message, e, args);
	}

	public P2PXWikiException(int code, String message, Throwable e) {
		super(MODULE, code, message, e, null);
	}

	public P2PXWikiException(int code, String message) {
		super(MODULE, code, message, null, null);
	}

	public P2PXWikiException(int code, Throwable e) {
		super(MODULE, code, "Wrapping exception", e, null);
	}

	public P2PXWikiException() {
		super();
	}
}
