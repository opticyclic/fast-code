package org.fastcode.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.fastcode.util.SourceUtil;


public class FastCodeProject extends FastCodeEntity {

	private IProject		project;
	private IJavaProject	javaProject;
	private String			path;

	public FastCodeProject(final IProject project) {
		super();
		if (project == null || !project.exists()) {
			this.isEmpty = true;
			return;
		}
		this.project = project;
		this.name = project.getName();
		this.path = project.getFullPath().toString();
		this.javaProject = SourceUtil.getJavaProject(project);
	}

	public FastCodeProject(final IJavaProject project) {
		super();
		if (project == null || !project.exists()) {
			this.isEmpty = true;
			return;
		}
		this.javaProject = project;
		this.project = project.getProject();
		this.name = project.getElementName();
		this.path = project.getPath().toString();
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

	public IJavaProject getJavaProject() {
		return this.javaProject;
	}

	public void setJavaProject(final IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	/**
	 *
	 * getter method for path
	 * @return
	 *
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 *
	 * setter method for path
	 * @param path
	 *
	 */
	public void setPath(final String path) {
		this.path = path;
	}
}
