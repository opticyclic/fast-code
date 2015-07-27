/**
 * @author : Gautam

 * Created : 12/26/2010

 */

package org.fastcode.common;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;

import org.eclipse.core.resources.IFile;

public class FastCodeFile extends FastCodeEntity {

	private String			path;
	private IFile			file;
	private FastCodeProject	project;
	private String			fullPath;

	/**
	 * @param name
	 * @param path
	 */
	public FastCodeFile(final String name, final String path) {
		this.name = name;
		this.path = path;

	}

	/**
	 *
	 * @param file
	 */

	public FastCodeFile(final IFile file) {
		if (file == null || !file.exists()) {
			this.isEmpty = true;
			return;
		}
		this.file = file;
		this.name = file.getName();
		this.path = file.getProjectRelativePath().toString();
		this.project = new FastCodeProject(file.getProject());
		this.fullPath = file.getFullPath().toString();
	}

	/**
	 *
	 * getter method for name
	 *
	 * @return
	 *
	 */
	public String getNameWithoutExtension() {
		final int index = this.name.indexOf(DOT);
		if (index < 0) {
			return this.name;
		}
		return this.name.substring(0, index);
	}	
	/**
	 *
	 * getter method for full name
	 *
	 * @return
	 *
	 */
	public String getFullName() {
		return this.path; // + "/" + this.name;
	}

	/**
	 *
	 * getter method for path
	 *
	 * @return
	 *
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 *
	 * getter method for extention
	 *
	 * @return
	 *
	 */
	public String getExtension() {
		final int index = this.name.indexOf(DOT);
		return index < 0 ? EMPTY_STR : this.name.substring(index + 1);
	}

	public String getFirstSegment() {
		return this.path.substring(0, this.path.indexOf(FORWARD_SLASH));
	}

	public String getLastSegment() {
		return this.path.substring(this.path.lastIndexOf(FORWARD_SLASH) + 1);
	}

	public String segment(final int from) {
		final String[] tmpStrArr = this.path.split(FORWARD_SLASH);
		String toReturn = tmpStrArr[from - 1];
		for (int i = from; i < tmpStrArr.length; i++) {
			toReturn = toReturn + FORWARD_SLASH + tmpStrArr[i];
		}
		return toReturn;

	}

	public String segment(final int from, final int to) {
		final String[] tmpStrArr = this.path.split(FORWARD_SLASH);
		String toReturn = tmpStrArr[from - 1];
		for (int i = from; i < to && i <= tmpStrArr.length; i++) {
			toReturn = toReturn + FORWARD_SLASH + tmpStrArr[i];
		}
		return toReturn;

	}

	public int getSegmentCount() {
		return this.path.split(FORWARD_SLASH).length;
	}

	public IFile getFile() {
		return this.file;
	}

	public void setFile(final IFile file) {
		this.file = file;
	}

	public FastCodeProject getProject() {
		return this.project;
	}

	public void setProject(final FastCodeProject fastCodeProject) {
		this.project = fastCodeProject;
	}

	public String getFullPath() {
		return this.fullPath;
	}

	public void setFullPath(final String fullPath) {
		this.fullPath = fullPath;
	}

}
