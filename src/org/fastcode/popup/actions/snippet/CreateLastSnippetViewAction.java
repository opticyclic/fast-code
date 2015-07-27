/**
 * @author : Gautam

 * Created : 05/10/2010

 */

package org.fastcode.popup.actions.snippet;

import org.fastcode.common.CreateSnippetData;

public class CreateLastSnippetViewAction extends CreateNewSnippetAction {

	/**
	 *
	 */
	public CreateLastSnippetViewAction() {
		this.useLast = true;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.CreateNewSnippetAction#getCreateSnippetData(java.lang.String)
	 */
	@Override
	public CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
		return TemplateCache.getInstance().createSnippetData;
	}

}