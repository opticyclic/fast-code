/**
 * @author : Gautam

 * Created : 12/26/2010

 */

package org.fastcode.common.test;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;

import org.eclipse.core.resources.IFile;
import org.fastcode.common.FastCodeProject;

public class MockFastCodeFile {

	private final String	name;
	private final String	path;
	private IFile			file;
	private FastCodeProject project;
	private String			fullPath;

	/**
	 * @param name
	 * @param path
	 */
	public MockFastCodeFile(final String name, final String path) {
		this.name = name;
		this.path = path;
		this.fullPath = EMPTY_STR;
	}


	/**
	 *
	 * getter method for name
	 *
	 * @return
	 *
	 */
	public String getName() {
		return this.name;
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
	 * getter method for extension
	 *
	 * @return
	 *
	 */
	public String getExtension() {
		return this.name.substring(this.name.indexOf(DOT) + 1);
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
