package com.xpn.p2pxwiki.services;

import com.xpn.p2pxwiki.P2PXWikiException;

public interface WikiGroupServices {
	String[] getAllGroupsNames() throws P2PXWikiException;
	WikiGroup[] getAllGroups() throws P2PXWikiException;
	WikiGroup getWikiGroup(String groupName) throws P2PXWikiException;
	WikiGroup createWikiGroup(String groupName) throws P2PXWikiException;
}
