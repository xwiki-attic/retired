package com.xpn.p2pxwiki.synchronization;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SynchronizationRequest {
	private List list;
	private boolean async = false;

	public SynchronizationRequest(SynchronizationElement[] arr) {
		this(Arrays.asList(arr), false);
	}

	public SynchronizationRequest(SynchronizationElement[] arr, boolean async) {
		this(Arrays.asList(arr), async);
	}

	public SynchronizationRequest(List list) {
		this(list, false);
	}
	public SynchronizationRequest(List list, boolean async) {
		this.list = new LinkedList(list);
		Collections.sort(this.list);
		Collections.reverse(this.list);
		this.async = async;
	}

	public List getElements() {
		return list;
	}

	public boolean isAsync() {
		return async;
	}

	public void setAsync(boolean async) {
		this.async = async;
	}
	
	public int size(){
		return this.list.size();
	}
	
	public SynchronizationElement get(int i){
		return (SynchronizationElement)list.get(i);
	}
}
