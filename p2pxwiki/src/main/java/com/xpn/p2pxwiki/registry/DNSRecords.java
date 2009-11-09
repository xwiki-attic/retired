package com.xpn.p2pxwiki.registry;

/**
 * Google SoC Project: Created by Bikash Agarwalla (bikash.agarwalla@gmail.com)
 * User: bikash Date: Aug 19, 2005 Time: 4:13:50 AM
 */
public class DNSRecords {
	private int id = 0;

	private Integer domainId;

	private String name;

	private String type;

	private String content;

	private Integer ttl;

	private Integer prio;

	private Integer changeDate;

	public DNSRecords() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Integer changeDate) {
		this.changeDate = changeDate;
	}

	public Integer getDomainId() {
		return domainId;
	}

	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}

	public Integer getPrio() {
		return prio;
	}

	public void setPrio(Integer prio) {
		this.prio = prio;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

}
