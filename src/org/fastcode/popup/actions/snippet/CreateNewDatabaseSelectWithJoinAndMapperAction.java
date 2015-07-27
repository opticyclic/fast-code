package org.fastcode.popup.actions.snippet;

import static org.fastcode.preferences.PreferenceConstants.DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.util.StringUtil.createDefaultInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.FastCodeDataBaseFieldDecorator;
import org.fastcode.common.FastCodeType;
import org.fastcode.setting.TemplateSettings;

public class CreateNewDatabaseSelectWithJoinAndMapperAction extends CreateNewDatabaseSelectWithJoinAction implements IEditorActionDelegate,
		IActionDelegate, IWorkbenchWindowActionDelegate {

	public CreateNewDatabaseSelectWithJoinAndMapperAction() {

		this.templatePrefix = P_DATABASE_TEMPLATE_PREFIX;
		this.templateType = DATABASE_TEMPLATE_SELECT_WITH_JOIN_AND_MAPPER;

	}
	@Override
	protected void initializePlaceHolders(final TemplateSettings templateSettings, final Map<String, Object> placeHolders) throws Exception {
		super.initializePlaceHolders(templateSettings, placeHolders);
		Map<String, String> fieldNameMethodMap = new HashMap<String, String>();
		final FastCodeType pojoClassType=(FastCodeType)placeHolders.get("class");
		if ( pojoClassType!= null) {

			Map<String, List<FastCodeDataBaseFieldDecorator>> dbFieldSelection = new HashMap<String, List<FastCodeDataBaseFieldDecorator>>();
			dbFieldSelection = getFieldsForJoin(placeHolders);
			for (final Entry<String, List<FastCodeDataBaseFieldDecorator>> entry : dbFieldSelection.entrySet()) {
				final String key = entry.getKey();
				placeHolders.put(key, entry.getValue());
			}

			fieldNameMethodMap = checkSetMethodInPojoClass(dbFieldSelection,pojoClassType.getiType());
			if (fieldNameMethodMap != null) {
				placeHolders.put("nameMethodMap", fieldNameMethodMap);
				placeHolders.put("pojo_class_instance", createDefaultInstance(pojoClassType.getiType().getElementName()));

			} else {
				placeHolders.put("nameMethodMap", null);
			}
		}

	}
}
