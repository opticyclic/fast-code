/**
 *
 */
package org.fastcode.popup.actions.snippet;

import java.util.Map;

import org.eclipse.ui.IEditorPart;

/**
 * @author Gautam
 *
 */
public interface SnippetCreator {

	/**
	 * @param editorPart
	 * @param template
	 * @param placeHolders
	 * @param memberSelection
	 * @param spacesBeforeCursor
	 * @return
	 * @throws Exception
	 */
	public Object createSnippet(final IEditorPart editorPart, final String template, final Map<String, Object> placeHolders,
			final Map<String, Object> memberSelection, final String spacesBeforeCursor) throws Exception;

}