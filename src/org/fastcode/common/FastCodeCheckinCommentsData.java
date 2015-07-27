package org.fastcode.common;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;

import java.util.List;
import java.util.ArrayList;

public class FastCodeCheckinCommentsData {

	private List<String> comntsFromRepo = new ArrayList<String>();
	private String finalComment;
	private List<String> comntsFromCache = new ArrayList<String>();
	private String title = EMPTY_STR;
	private boolean addPrefixFooter;

	/**
	 *
	 * getter method for previousComments
	 * @return
	 *
	 */
	public List<String> getComntsFromRepo() {
		return this.comntsFromRepo;
	}

	/**
	 *
	 * setter method for previousComments
	 * @param previousComments
	 *
	 */
	public void setComntsFromRepo(final List<String> comntsFromRepo) {
		this.comntsFromRepo = comntsFromRepo;
	}

	/**
	 *
	 * getter method for finalComment
	 * @return
	 *
	 */
	public String getFinalComment() {
		return this.finalComment;
	}

	/**
	 *
	 * setter method for finalComment
	 * @param finalComment
	 *
	 */
	public void setFinalComment(final String finalComment) {
		this.finalComment = finalComment;
	}

	public List<String> getComntsFromCache() {
		return this.comntsFromCache;
	}

	public void setComntsFromCache(final List<String> comntsFromCache) {
		this.comntsFromCache = comntsFromCache;
	}

	/**
	 *
	 * getter method for title
	 * @return
	 *
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 *
	 * setter method for title
	 * @param title
	 *
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	public boolean isAddPrefixFooter() {
		return this.addPrefixFooter;
	}

	public void setAddPrefixFooter(final boolean addPrefixFooter) {
		this.addPrefixFooter = addPrefixFooter;
	}
}
