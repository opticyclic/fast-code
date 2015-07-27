package org.fastcode.util;

import java.io.File;
import org.eclipse.core.resources.IProject;

public class FastCodeFileForCheckin {

	private String	status;
	private String	cachedTime;
	private File	file;
	private String	comments;
	private String	fileFullName;
	private IProject project;

	public FastCodeFileForCheckin(final File file, final String fileFullName, final String comments, final String status, final String cachedTime, final IProject project) {
		super();
		this.status = status;
		this.cachedTime = cachedTime;
		this.file = file;
		this.comments = comments;
		this.fileFullName = fileFullName;
		this.project = project;
	}

	public FastCodeFileForCheckin(final String status, final String fileFullName) {
		super();
		this.status = status;
		this.fileFullName = fileFullName;
	}

	/**
	 *
	 * getter method for status
	 * @return
	 *
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 *
	 * setter method for status
	 * @param status
	 *
	 */
	public void setStatus(final String status) {
		this.status = status;
	}

	/**
	 *
	 * getter method for cachedTime
	 * @return
	 *
	 */
	public String getCachedTime() {
		return this.cachedTime;
	}

	/**
	 *
	 * setter method for cachedTime
	 * @param cachedTime
	 *
	 */
	public void setCachedTime(final String cachedTime) {
		this.cachedTime = cachedTime;
	}

	/**
	 *
	 * getter method for file
	 * @return
	 *
	 */
	public File getFile() {
		return this.file;
	}

	/**
	 *
	 * setter method for file
	 * @param file
	 *
	 */
	public void setFile(final File file) {
		this.file = file;
	}

	/**
	 *
	 * getter method for comments
	 * @return
	 *
	 */
	public String getComments() {
		return this.comments;
	}

	/**
	 *
	 * setter method for comments
	 * @param comments
	 *
	 */
	public void setComments(final String comments) {
		this.comments = comments;
	}

	/**
	 *
	 * getter method for fileFullName
	 * @return
	 *
	 */
	public String getFileFullName() {
		return this.fileFullName;
	}

	/**
	 *
	 * setter method for fileFullName
	 * @param fileFullName
	 *
	 */
	public void setFileFullName(final String fileFullName) {
		this.fileFullName = fileFullName;
	}

	/**
	 *
	 * getter method for project
	 * @return
	 *
	 */
	public IProject getProject() {
		return this.project;
	}

	/**
	 *
	 * setter method for project
	 * @param project
	 *
	 */
	public void setProject(final IProject project) {
		this.project = project;
	}
}
