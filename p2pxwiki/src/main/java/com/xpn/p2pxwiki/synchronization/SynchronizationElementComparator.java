package com.xpn.p2pxwiki.synchronization;

import java.util.Comparator;

public class SynchronizationElementComparator implements Comparator {
	private static final int PRIORITY_BOOST = 10;
	
	public int compare(Object arg0, Object arg1) {
		try{
			WrappedSynchronizationElement el1 = (WrappedSynchronizationElement)arg0;
			WrappedSynchronizationElement el2 = (WrappedSynchronizationElement)arg1;
			long priority1 = getAgingPriority(el1.getElement().getPriority(), el1.getTime());
			long priority2 = getAgingPriority(el2.getElement().getPriority(), el2.getTime());
			long diff = priority2 - priority1;
			if (diff < 0) {
				return -1;
			} else if (diff > 0) {
				return 1;
			}
			return 0;
		}
		catch(ClassCastException ex){}

		return 0;
	}

	private long getAgingPriority(int priority, long time){
		return priority * PRIORITY_BOOST - time;
	}
}
