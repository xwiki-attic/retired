package com.xpn.p2pxwiki.synchronization;

import edu.emory.mathcs.backport.java.util.concurrent.PriorityBlockingQueue;

public class SynchronizationElementQueue {
	private static final long serialVersionUID = -6741052850343536509L;
	private static final int DEFAULT_INITIAL_CAPACITY = 11;
	private long time;
	private PriorityBlockingQueue pqueue;

	public SynchronizationElementQueue() {
		this.time = 0;
		this.pqueue = new PriorityBlockingQueue(DEFAULT_INITIAL_CAPACITY, new SynchronizationElementComparator());
	}

	/**
	 * Unblocks blocked poolers.
	 * @param mo
	 * @return
	 */
	public WrappedSynchronizationElement add(SynchronizationElement mo) {
		age();
		WrappedSynchronizationElement wo = new WrappedSynchronizationElement(mo, time);
		pqueue.add(wo);
		return wo;
	}

	/**
	 * Blocks on empty queue
	 * @return
	 */
	public SynchronizationElement poll() {
		WrappedSynchronizationElement wo = null;
		do {
			try {
				wo = (WrappedSynchronizationElement) pqueue.take();
			} catch (InterruptedException ignore) { }
		} while (wo == null);
		return wo.getElement();
	}

	private void age() {
		time++;
	}

	public long getTime() {
		return time;
	}

	public boolean isEmpty() {
		return pqueue.isEmpty();
	}

	public String toString() {
		return pqueue.toString();
	}
}
