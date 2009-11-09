
package com.xpn.xwiki.wiked.repository;

import java.util.EventObject;

/**
 * The event object used to inform about repository changes.
 * 
 * @see com.xpn.xwiki.wiked.repository.IRepositoryListener
 * @author psenicka_ja
 */
public class RepositoryObjectChangeEvent extends EventObject {

	/** The event type, see conatsnts for allowed values */
	private int type;
	/** Modified property name */ 
	private String propertyName;

	/** 
	 * Error type, see source object for the error source 
	 */
	public static final int ERROR = -1;
	/** 
	 * Create type, see source object for newly created object 
	 */
	public final static int CREATE = 1;
	/** 
	 * Remove type, see source object for the removed object 
	 */
	public final static int REMOVE = 2;
	/** 
	 * Change type, see source object for modified object and propertyName 
	 * for the property modified 
	 */
	public final static int MODIFY = 3;

	public RepositoryObjectChangeEvent(Object object, int type) {
		super(object);
		switch (type) {
			case ERROR:
			case CREATE:
			case REMOVE:
			case MODIFY:
				this.type = type;
				break;
			default:
				throw new IllegalArgumentException("unknown type "+type);
		}
	}
	
	public RepositoryObjectChangeEvent(Object object, int type, 
		String propertyName) {
		this(object, type);
		this.propertyName = propertyName;
	}

	/**
	 * Provides access to parent event (source) object
	 * @return ISpace for IPage and IRepository for ISpace. Otherwise
	 * returns <code>null</code>.
	 */
	public IRepositoryObject getParentObject() {
		Object object = getSource();
		if (object instanceof IPage) {
			return ((IPage)object).getSpace();
		} else if (object instanceof ISpace) {
			return ((ISpace)object).getRepository();
		}
		return null;
	}

	/**
	 * @return source object downcasted to <code>IRepositoryObject</code>.
	 */
	public IRepositoryObject getRepositoryObject() {
		return (IRepositoryObject)getSource();
	}

	/**
	 * @return the event type
	 */
	public int getEventType() {
		return type;
	}

	/**
	 * @return the changed property name (for MODIFY only).
	 */
	public String getPropertyName() {
		return this.propertyName;
	}


}