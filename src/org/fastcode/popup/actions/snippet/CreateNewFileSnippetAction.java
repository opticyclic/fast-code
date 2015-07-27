package org.fastcode.popup.actions.snippet;

import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOUBLE_HASH;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.EXIT_KEY;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.HASH;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.preferences.PreferenceConstants.P_DELIMITER_FOR_FILE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.P_FILE_TEMPLATE_PLACHOLDER_NAME;
import static org.fastcode.util.FileUtil.getContentsFromFile;
import static org.fastcode.util.SourceUtil.isFileSaved;
import static org.fastcode.util.StringUtil.isEmpty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.CreateSnippetData;
import org.fastcode.util.VelocityUtil;

public class CreateNewFileSnippetAction {

	//Map<String, String>			headerValueMap			= null;

	//private Shell				shell					= null;

	List<Map<String, String>>			headerValueMapList		= new ArrayList<Map<String, String>>();

	boolean								exitCreateFileTemplate	= false;

	IFile								file					= null;
	CreateSnippetData					createSnippetData;

	private final Map<String, Object>	placeHolders;

	public CreateNewFileSnippetAction(final CreateSnippetData createSnippetData, final Map<String, Object> placeHolders) {
		this.createSnippetData = createSnippetData;
		this.placeHolders = placeHolders;

	}

	public void runAction() throws Exception {
		parseFileToMap(this.createSnippetData);
		this.placeHolders.put(EXIT_KEY, this.exitCreateFileTemplate);
		this.placeHolders.put(getPlaceholderForStringFileData(), this.headerValueMapList);

	}

	/*@Override
	public void runAction() throws Exception {

		super.runAction();

	}

	 (non-Javadoc)
	 * @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#getTemplateType()

	@Override
	protected String getTemplateType() {
		this.templateType = TEMPLATE_TYPE_SAMPLE_FILE_TEMPLATE;
		this.description = makeWord(this.templateType);
		return this.templateType;
	}

	*//**
		*
		* @param templateSettings
		* @param placeHolders
		* @throws Exception
		*/
	/*
	(non-Javadoc)
	* @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#initializePlaceHolders(org.fastcode.setting.TemplateSettings, java.util.Map)

	@Override
	protected void initializePlaceHolders(final TemplateSettings templateSettings, final Map<String, Object> placeHolders) throws Exception {
	parseFileToMap();
	placeHolders.put(EXIT_KEY, this.exitCreateFileTemplate);
	placeHolders.put(getPlaceholderForStringFileData(), this.headerValueMapList);

	}

	@Override
	protected boolean requireFields(final Map<String, Object> placeHolders, final TemplateSettings templateSetting) {
	return false;
	}

	@Override
	protected void addDefaultClassToPlaceHolders(final IType type, final Map<String, Object> placeHolders) throws Exception {

	}



	(non-Javadoc)
	* @see org.fastcode.popup.actions.snippet.AbstractCreateNewSnippetAction#getCreateSnippetData(java.lang.String)

	@Override
	public CreateSnippetData getCreateSnippetData(final String fileName) throws Exception {
	final CreateSnippetData createSnippetData = new CreateSnippetData();
	createSnippetData.setTemplatePrefix(this.templatePrefix);
	createSnippetData.setTemplateType(this.templateType);
	createSnippetData.setTemplateSettings(getTemplateSettings(this.templateType));
	parseFileToMap();
	if (this.headerValueMapList.isEmpty()) {
		return null;
	} else {
		return createSnippetData;
	}
	}*/

	private String getDelimiter() {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		return preferenceStore.getString(P_DELIMITER_FOR_FILE_TEMPLATE);
	}

	/**
	 * @param createSnippetData
	 * @throws IOException
	 */
	private void parseFileToMap(final CreateSnippetData createSnippetData) throws IOException {
		BufferedReader br = null;
		InputStreamReader inputStreamReader = null;
		this.file = createSnippetData.getResourceFile();
		try {
			if (this.file != null && !isFileSaved(this.file.getName(), this.file)) {
				this.exitCreateFileTemplate = true;
				openError(new Shell(), "Error", "File - " + this.file.getName() + " - is not saved. please save and try again.");
				return;
			}

			if (this.file != null && isEmpty(getContentsFromFile(this.file).trim())) {
				this.exitCreateFileTemplate = true;
				openError(new Shell(), "Error", "Blank file,Please try again with another file which has some content");
				return;
			}

			final String placeholderRecord = getPlaceholderForStringFileData();
			if (isEmpty(placeholderRecord)) {
				this.exitCreateFileTemplate = true;
				openError(new Shell(), "Error",
						"Please give the placeholder name for file template, in window->prefernces->Template preferences.");
				return;
			}

			this.headerValueMapList.clear();
			/*this.shell = this.editorPart == null || this.editorPart.getSite() == null ? new Shell() : this.editorPart.getSite().getShell();

			final OpenResourceDialog resourceDialog = new OpenResourceDialog(this.shell, ResourcesPlugin.getWorkspace().getRoot(),
					IResource.FILE);
			resourceDialog.setTitle("Select File ");
			resourceDialog.setMessage("Select the text file for html tags");
			final int res = resourceDialog.open();
			if (res == CANCEL || resourceDialog.getResult() == null || resourceDialog.getResult().length == 0) {
				return;
			}
			final Object result[] = resourceDialog.getResult();

			this.file = (IFile) result[0];*/

			inputStreamReader = new InputStreamReader(this.file.getContents());

			br = new BufferedReader(inputStreamReader);
			String strLine;
			String headerLine = null;
			String delimiter = getDelimiter();
			String delimToAdd = delimiter;
			if (isEmpty(delimiter)) {
				/*if (!MessageDialog.openConfirm(new Shell(), "Delimiter Empty",
						"Delimiter is empty. Yes to continue with white space as delimiter, No to configure the delimiter in window->prefernces->Template preferences.")) {
					this.exitCreateFileTemplate = true;
					return;
				}*/
				delimiter = "\\s+";
				delimToAdd = SPACE;
			}
			final String grouper = "\"";
			this.exitCreateFileTemplate = false;

			String[] headerArray = {};
			while ((strLine = br.readLine()) != null) {
				if (strLine.startsWith(DOUBLE_HASH)) {
					continue;
				} else if (strLine.startsWith(HASH)) {
					headerLine = strLine.substring(1);
					headerArray = headerLine.split(delimiter);
					if (!validHeader(headerArray, createSnippetData)) {
						this.exitCreateFileTemplate = true;

						return;
					}

				} else {
					if (headerLine == null) {
						this.exitCreateFileTemplate = true;
						openError(new Shell(), "Error",
								"There is no header line in the selected file, Please put some header line and try again");
						return;
					}
					final Map<String, String> headerValueMap = new LinkedHashMap<String, String>();
					final String valueLine = strLine;
					/*final Pattern p = Pattern.compile("(?u)((?<=(\"))[\\w ]*(?=(\"(\\s|$))))|((?<!\")\\w+(?!\"))");
					final Matcher m = p.matcher(strLine);*/
					final StringBuilder builder = new StringBuilder();
					final String[] valueArray = valueLine.split(delimiter);
					if (isEmpty(valueLine)) {
						return;
					}
					final List<String> valueList = new ArrayList<String>();
					for (int i = 0; i < valueArray.length; i++) {
						if (valueArray[i].startsWith(grouper)) {
							builder.append(valueArray[i].substring(1)).append(delimToAdd);
						} else {
							valueList.add((valueArray[i].endsWith(grouper) ? builder.append(valueArray[i].substring(0,
									valueArray[i].length() - 1)) : builder.append(valueArray[i])).toString());
							builder.delete(0, builder.length());
						}
					}
					if (valueArray.length < headerArray.length) {
						valueList.add(EMPTY_STR);
					}
					for (int i = 0; i < headerArray.length; i++) {
						headerValueMap.put(headerArray[i], valueList.get(i));
					}
					this.headerValueMapList.add(headerValueMap);

				}
			}

		} catch (final Exception e) {
			e.printStackTrace();

		} finally {
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (br != null) {
				br.close();
			}
		}
	}

	private boolean validHeader(final String[] headerArray, final CreateSnippetData createSnippetData) {
		final List<String> fieldsList = getFieldsFromTemplateBody(createSnippetData.getTemplateSettings().getTemplateBody());
		final StringBuilder fieldBuilder = new StringBuilder(EMPTY_STR);
		for (final String fieldInTemplate : fieldsList) {
			fieldBuilder.append(EMPTY_STR.equals(fieldBuilder.toString()) ? fieldInTemplate + COMMA : fieldInTemplate);
		}
		boolean fieldInHeader = false;
		for (final String fieldInTemplate : fieldsList) {
			fieldBuilder.append(EMPTY_STR.equals(fieldBuilder.toString()) ? fieldInTemplate + COMMA : fieldInTemplate);
			for (final String fileHeader : headerArray) {
				if (fieldInTemplate.equals(fileHeader)) {
					fieldInHeader = true;
					break;
				}
			}
			if (!fieldInHeader) {
				openError(new Shell(), "Error", "The placeholder - " + fieldInTemplate
						+ " - used in the template, is not any of the header in the file.\n Expecting " + fieldBuilder.toString()
						+ ". Please check and try again.");
				return false;
			}
		}

		return true;
	}

	private List<String> getFieldsFromTemplateBody(final String templateBody) {
		final String placeHolder = getPlaceholderForStringFileData();
		final VelocityUtil veloUtil = VelocityUtil.getInstance();
		try {
			return veloUtil.getListofHeaders(placeHolder, templateBody);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String getPlaceholderForStringFileData() {
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		return preferenceStore.getString(P_FILE_TEMPLATE_PLACHOLDER_NAME);
	}
}
