/**
 * @author : Gautam

 * Created : 09/09/2010

 */

package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE_TYPE_COPY_CLASS_SINGLE;
import static org.fastcode.util.SourceUtil.getFieldsOfType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.FastCodeField;

public class CreateNewCopyBeanSingleViewAction extends AbstractCreateNewSnippetAction implements IEditorActionDelegate, IActionDelegate,
		IWorkbenchWindowActionDelegate {

	/**
	 *
	 */
	public CreateNewCopyBeanSingleViewAction() {
		this.templateType = TEMPLATE_TYPE_COPY_CLASS_SINGLE;
		this.templatePrefix = TEMPLATE;
	}

	/**
	 * @param type
	 * @param toType
	 * @param fields
	 * @param fieldType
	 *
	 */
	@Override
	protected Map<String, List<FastCodeField>> getFieldSelection(final IType type, final IType toType, final FastCodeField[] fields,
			final Map<String, Object> placeHolders, final String fieldType) throws Exception {

		final Map<String, List<FastCodeField>> allFields = new HashMap<String, List<FastCodeField>>();

		final Map<String, List<FastCodeField>> frmSelectedFields = super.getFieldSelection(type, null, fields, placeHolders, "from field");
		if (frmSelectedFields == null || frmSelectedFields.get("from field") == null || frmSelectedFields.get("from field").size() == 0) {
			return null;
		}
		allFields.put("from_fields", frmSelectedFields.get("from field"));
		if (frmSelectedFields.get("from field").size() == 1) {
			allFields.put("from_field", frmSelectedFields.get("from field"));
		}

		final IField[] toFields = getFieldsOfType(toType);
		final List<FastCodeField> fcFieldList = new ArrayList<FastCodeField>();
		for (final IField toField : toFields) {
			fcFieldList.add(new FastCodeField(toField));
		}
		final Map<String, List<FastCodeField>> toSelectedFields = super.getFieldSelection(toType, null,
				fcFieldList.toArray(new FastCodeField[0]), placeHolders, "to field");
		if (toSelectedFields == null || toSelectedFields.get("to field") == null || toSelectedFields.get("to field").size() == 0) {
			return null;
		}
		allFields.put("to_fields", toSelectedFields.get("to field"));

		return allFields;
	}

	@Override
	protected boolean doContinueWithNoFields() {
		return false;
	}

	@Override
	protected boolean isSingleSelection() {
		return true;
	}
}