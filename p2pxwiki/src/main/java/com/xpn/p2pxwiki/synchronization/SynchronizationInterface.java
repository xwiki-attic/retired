package com.xpn.p2pxwiki.synchronization;

public interface SynchronizationInterface {

	public Object[] sync(String docName, String language, String version, String versionHash);

	public Object[] merge(String docName, String language,
	        Object[] branch);
}