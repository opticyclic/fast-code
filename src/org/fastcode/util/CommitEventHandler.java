package org.fastcode.util;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

public class CommitEventHandler implements ISVNEventHandler {

	@Override
	public void handleEvent(final SVNEvent event, final double progress) {
		final SVNEventAction action = event.getAction();
		if (action == SVNEventAction.COMMIT_MODIFIED) {
			System.out.println("Sending   " + event.getURL().getPath());
		} else if (action == SVNEventAction.COMMIT_DELETED) {
			System.out.println("Deleting   " + event.getURL().getPath());
		} else if (action == SVNEventAction.COMMIT_REPLACED) {
			System.out.println("Replacing   " + event.getURL().getPath());
		} else if (action == SVNEventAction.COMMIT_DELTA_SENT) {
			System.out.println("Transmitting file data....");
		} else if (action == SVNEventAction.COMMIT_ADDED) {
			/*
			 * Gets the MIME-type of the item.
			 */
			final String mimeType = event.getMimeType();
			if (SVNProperty.isBinaryMimeType(mimeType)) {
				/*
				 * If the item is a binary file
				 */
				System.out.println("Adding  (bin)  " + event.getURL().getPath());
			} else {
				System.out.println("Adding         " + event.getURL().getPath());
			}
		} else if (action == SVNEventAction.COMMIT_COMPLETED) {
			System.out.println("commit completed ");// + event.getInfo().toString());
		}

	}

	@Override
	public void checkCancelled() throws SVNCancelException {
	}

}
