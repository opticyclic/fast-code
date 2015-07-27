package org.fastcode.templates.util;

import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.preferences.PreferenceConstants.P_FILE_TEMPLATE_PLACHOLDER_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.common.FastCodeConstants.RETURN_TYPES;
import org.fastcode.templates.contentassist.ElementProposal;
import org.fastcode.util.VelocityUtil;

public class VariablesUtil {

	private static VariablesUtil		variablesUtil;
	private static boolean				reLoad;
	private final List<ElementProposal>	variablesList			= new ArrayList<ElementProposal>();
	private final List<ElementProposal>	addlparamList			= new ArrayList<ElementProposal>();
	private String						filePlaceholderValue;
	private static Map<String, String>	templateItemPluralMap	= new HashMap<String, String>();
	List<FastCodeAdditionalParams> additnlParamList = new ArrayList<FastCodeAdditionalParams>();

	public static VariablesUtil getInstance() {
		if (variablesUtil == null || reLoad == true) {
			variablesUtil = new VariablesUtil();
			getItemsPluralMap();
			reLoad = false;
		}
		return variablesUtil;

	}

	public List<ElementProposal> getVariablesList() {

		return this.addlparamList;
	}

	public List<ElementProposal> getAddlParamList() {
		return this.variablesList;
	}

	public void setadditionalParamList(final List<FastCodeAdditionalParams> fcAdditnlParamList) {
		ElementProposal proposal;
		if (this.addlparamList != null) {
			this.addlparamList.clear();
		}
		if (this.variablesList != null) {
			this.variablesList.clear();
		}
		if (this.additnlParamList != null) {
			this.additnlParamList.clear();
		}
		this.additnlParamList = fcAdditnlParamList;
		if (fcAdditnlParamList != null) {
			for (final FastCodeAdditionalParams param : fcAdditnlParamList.toArray(new FastCodeAdditionalParams[0])) {
				proposal = new ElementProposal(param.getName(), param.getName(), "-");
				if (!this.addlparamList.contains(proposal)) {
					this.addlparamList.add(proposal);
				}
				if (param.getReturnTypes() != null) {
					if (!(param.getReturnTypes().equals(RETURN_TYPES.STRING) || param.getReturnTypes().equals(RETURN_TYPES.BOOLEAN) || param.getReturnTypes().equals(RETURN_TYPES.LOCALVAR) || param.getReturnTypes().equals(RETURN_TYPES.INTRANGE) || param.getReturnTypes().equals(RETURN_TYPES.INT))) {
						this.variablesList.add(proposal);
					}
					final ArrayList<String> functions = ContentAssistUtil.getTypefunctionmap(param.getReturnTypes().getValue());
					if (functions != null) {
						for (final String function : functions) {
							proposal = new ElementProposal(param.getName() + DOT + function, param.getName(), SPACE
									+ HYPHEN
									+ SPACE
									+ (function.contains(DOT) ? function.substring(0, function.indexOf(DOT)) + SPACE
											+ function.substring(function.indexOf(DOT) + 1, function.length()) : function));
							if (!this.addlparamList.contains(proposal)) {
								this.addlparamList.add(proposal);
							}
						}
					}
				}
			}
		}
		/*final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		setFilePlaceHolderValue(store.getString(P_FILE_TEMPLATE_PLACHOLDER_NAME));*/

	}

	public static void reload(final boolean reload) {
		reLoad = reload;
	}

	public void setFilePlaceHolderValue(final String placeHolderValue) {
		this.filePlaceholderValue = placeHolderValue;
		this.addlparamList.add(new ElementProposal(placeHolderValue, placeHolderValue, SPACE + HYPHEN));

	}

	public String getFilePlaceholderValue() {
		return this.filePlaceholderValue;
	}

	public static void getItemsPluralMap() {
		templateItemPluralMap = VelocityUtil.getInstance().getPluralMap();

	}

	public static String getPlural(final String templateItem) {
		return templateItemPluralMap.get(templateItem);

	}

	public List<FastCodeAdditionalParams> getAdditnlParamList() {
		return this.additnlParamList;
	}
}
