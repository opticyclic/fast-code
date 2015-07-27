/**
 *
 */
package org.fastcode.popup.actions.snippet;

import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IEditorInput;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.setting.TemplateSettings;

/**
 * @author Gautam
 *
 */
public class TemplateCache {

	TemplateSettings				lastTemplateSettings;
	String							templateType;
	IEditorInput					lastEditorInput;
	//	GETTER_SETTER getterSetterEXIST;
	IType							lastType;
	IType							lastToType;
	CreateSnippetData							createSnippetData;

	private static TemplateCache	templateCache	= new TemplateCache();

	private TemplateCache() {

	}

	/**
	 *
	 * @return
	 */
	public static TemplateCache getInstance() {
		return templateCache;
	}
}
