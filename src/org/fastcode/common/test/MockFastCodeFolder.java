package org.fastcode.common.test;

import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;

public class MockFastCodeFolder {

	private final String	name;
	private final String	fullPath;



	public MockFastCodeFolder(final String name, final String fullPath) {
		super();
		this.name = name;
		this.fullPath = fullPath;
	}

	public String getName() {
		return this.name;
	}

	public String getFullPath() {
		return this.fullPath;
	}


	public String getFirstSegment() {
		return this.fullPath.substring(0, this.fullPath.indexOf(FORWARD_SLASH));
	}

	public String getLastSegment() {
		return this.fullPath.substring(this.fullPath.lastIndexOf(FORWARD_SLASH) + 1);
	}

	public String getSegment(final int from) {
		final String[] tmpStrArr = this.fullPath.split(FORWARD_SLASH);
		String toReturn = tmpStrArr[from - 1];
		for (int i = from; i < tmpStrArr.length; i++) {
			toReturn = toReturn + FORWARD_SLASH + tmpStrArr[i];
		}
		return toReturn;
	}

	public String getSegment(final int from, final int to) {
		final String[] tmpStrArr = this.fullPath.split(FORWARD_SLASH);
		String toReturn = tmpStrArr[from - 1];
		for (int i = from; i < to && i <= tmpStrArr.length; i++) {
			toReturn = toReturn + FORWARD_SLASH + tmpStrArr[i];
		}
		return toReturn;
	}

	public int getSegmentCount() {
		return this.fullPath.split(FORWARD_SLASH).length;
	}

}
