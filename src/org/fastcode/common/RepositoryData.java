package org.fastcode.common;

import java.util.Map;
import java.util.HashMap;

public class RepositoryData {

	private String				repName;
	private String				repBaseLocation;
	private String				repUrl;
	private String				associatedProject;
	private String				userName;
	private String				password;
	private Map<String, String>	prjNamesPair	= new HashMap<String, String>();
	private Map<String, String>	prjUrlMap	= new HashMap<String, String>();
	private String source;

	/**
	 *
	 * getter method for repName
	 * @return
	 *
	 */
	public String getRepName() {
		return this.repName;
	}

	/**
	 *
	 * setter method for repName
	 * @param repName
	 *
	 */
	public void setRepName(final String repName) {
		this.repName = repName;
	}

	/**
	 *
	 * getter method for repLocation
	 * @return
	 *
	 */
	public String getRepBaseLocation() {
		return this.repBaseLocation;
	}

	/**
	 *
	 * setter method for repLocation
	 * @param repLocation
	 *
	 */
	public void setRepBaseLocation(final String repLocation) {
		this.repBaseLocation = repLocation;
	}

	/**
	 *
	 * getter method for repUrl
	 * @return
	 *
	 */
	public String getRepUrl() {
		return this.repUrl;
	}

	/**
	 *
	 * setter method for repUrl
	 * @param repUrl
	 *
	 */
	public void setRepUrl(final String repUrl) {
		this.repUrl = repUrl;
	}

	/**
	 *
	 * getter method for associatedProject
	 * @return
	 *
	 */
	public String getAssociatedProject() {
		return this.associatedProject;
	}

	/**
	 *
	 * setter method for associatedProject
	 * @param associatedProject
	 *
	 */
	public void setAssociatedProject(final String associatedProject) {
		this.associatedProject = associatedProject;
	}

	/**
	 *
	 * getter method for userName
	 * @return
	 *
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 *
	 * setter method for userName
	 * @param userName
	 *
	 */
	public void setUserName(final String userName) {
		this.userName = userName;
	}

	/**
	 *
	 * getter method for password
	 * @return
	 *
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 *
	 * setter method for password
	 * @param password
	 *
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	public Map<String, String> getPrjNamesPair() {
		return this.prjNamesPair;
	}

	public void setPrjNamesPair(final Map<String, String> prjNamesPair) {
		this.prjNamesPair = prjNamesPair;
	}

	/**
	 *
	 * getter method for source
	 * @return
	 *
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 *
	 * setter method for source
	 * @param source
	 *
	 */
	public void setSource(final String source) {
		this.source = source;
	}

	public Map<String, String> getPrjUrlMap() {
		return this.prjUrlMap;
	}

	public void setPrjUrlMap(final Map<String, String> prjUrlMap) {
		this.prjUrlMap = prjUrlMap;
	}
}
