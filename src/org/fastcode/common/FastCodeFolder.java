package org.fastcode.common;

import org.eclipse.core.resources.IFolder;

public class FastCodeFolder extends FastCodeEntity {

	private String	fullPath;
	private IFolder	folder;
	private String	projectRelativePath;

	public FastCodeFolder(final IFolder iFolder) {
		super();
		if (iFolder == null || !iFolder.exists()) {
			this.isEmpty = true;
			return;
		}
		this.folder = iFolder;
		this.name = iFolder.getName();
		this.fullPath = iFolder.getFullPath().toString();
		this.projectRelativePath = iFolder.getProjectRelativePath().toString();
	}

	public String getFullPath() {
		return this.fullPath;
	}

	public IFolder getFolder() {
		return this.folder;
	}

	public String getFirstSegment() {
		return this.folder.getFullPath().segments()[0];
	}

	public String getLastSegment() {
		return this.folder.getFullPath().lastSegment();
	}

	public String getSegment(final int from) {
		final int segmentCount = getSegmentCount();
		return this.folder.getFullPath().removeFirstSegments(from - 1).uptoSegment(segmentCount).toString();
		//return this.iFolder.getFullPath().uptoSegment(from).toString();
	}

	public String getSegment(final int from, final int to) {
		final int segmentCount = getSegmentCount();
		return this.folder.getFullPath().removeFirstSegments(from).uptoSegment(to - from).toString();
		// return this.iFolder.getFullPath().uptoSegment(to).toString();
	}

	public int getSegmentCount() {
		return this.folder.getFullPath().segmentCount();
	}

	public String getProjectRelativePath() {
		return projectRelativePath;
	}

	public void setProjectRelativePath(String projectRelativePath) {
		this.projectRelativePath = projectRelativePath;
	}

}
