package com.xpn.p2pxwiki.synchronization;

public class WrappedSynchronizationElement {
	private SynchronizationElement element = null;
	private long time;
	public WrappedSynchronizationElement(SynchronizationElement element, long time){
		this.element = element;
		this.time = time;
	}

	public SynchronizationElement getElement() {
		return element;
	}
	public long getTime() {
		return time;
	}

	public String toString(){
		return "(element: [" + element + "]; time: " + time + ")";
	}
}
