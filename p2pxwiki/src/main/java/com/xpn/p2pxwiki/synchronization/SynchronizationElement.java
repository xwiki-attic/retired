package com.xpn.p2pxwiki.synchronization;

public class SynchronizationElement implements Comparable {
	public static final int MIN_PRIORITY = 0;
	public static final int MAX_PRIORITY = 10;
	public static final int DEFAULT_PRIORITY = 5;
	private String documentName;
	private String documentLanguage;
	private String serverName;
	private int priority; // Higher is more important
	private boolean updated;

	public SynchronizationElement(String documentName, String serverName, String language) {
		this(documentName, serverName, language, DEFAULT_PRIORITY);
	}

	public SynchronizationElement(String documentName, String serverName, String language, int priority) {
		this.documentName = documentName;
		this.serverName = serverName;
		this.documentLanguage = language;
		this.priority = priority;
		this.updated = false;
	}

	/**
	 * @return the documentName
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * @param documentName
	 *            the documentName to set
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	/**
	 * @return the documentLanguage
	 */
	public String getDocumentLanguage() {
		return documentLanguage;
	}

	/**
	 * @param documentLanguage the documentLanguage to set
	 */
	public void setDocumentLanguage(String documentLanguage) {
		this.documentLanguage = documentLanguage;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority
	 *            the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the updated
	 */
	public boolean isUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		try{
			SynchronizationElement other = (SynchronizationElement)obj;
			if(this.documentName.equals(other.documentName) && this.serverName.equals(other.serverName) && this.documentLanguage.equals(other.documentLanguage)){
				return true;
			}
			return false;
		}
		catch(ClassCastException ex){
			return false;
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.documentName.hashCode() + this.serverName.hashCode() + this.documentLanguage.hashCode();
	}
	
	public int compareTo(Object o){
		SynchronizationElement o2 = (SynchronizationElement)o; 
		long diff = priority - o2.priority;
		if (diff < 0) {
			return -1;
		} else if (diff > 0) {
			return 1;
		}
		return 0;
	}
}
