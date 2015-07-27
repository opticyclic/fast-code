package org.fastcode.common;

import org.eclipse.jdt.core.IPackageFragment;

public class FastCodePackage extends FastCodeEntity {

	private String				fullName;
	private String				javaProject;
	private IPackageFragment	packageFragment;

	public FastCodePackage(final IPackageFragment packageFragment) {
		super();
		if (packageFragment == null || !packageFragment.exists()) {
			this.isEmpty = true;
			return;
		}
		this.packageFragment = packageFragment;
		this.name = packageFragment.getElementName();
		this.javaProject = packageFragment.getJavaProject().getElementName();
	}

	public FastCodePackage(final String packageName, final String javaProject) {
		super();
		this.name = packageName;
		this.javaProject = javaProject;
	}

	public String getFullName() {
		return this.fullName;
	}

	public void setFullName(final String fullName) {
		this.fullName = fullName;
	}

	public String getJavaProject() {
		return this.javaProject;
	}

	public void setJavaProject(final String javaProject) {
		this.javaProject = javaProject;
	}

	public IPackageFragment getPackageFragment() {
		return this.packageFragment;
	}

	public void setPackageFragment(final IPackageFragment packageFragment) {
		this.packageFragment = packageFragment;
	}

}
