/**
 *
 */
package org.fastcode.util;

import static org.eclipse.jface.window.Window.CANCEL;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DEFAULT_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FALSE_STR;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.TAB;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_COMMON_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_COMMON_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_ALL_TEMPLATES;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_GLOBAL_PROPS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ADDITIONAL_PARAMETERS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOWED_FILE_NAMES;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ALLOW_MULTIPLE_VARIATION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_BODY;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_DESCRIPTION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ENABLE_TEMPLATE;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_FIRST_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_GETTER_SETTER;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_ITEM_PATTERN;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_NUMBER_REQUIRED_ITEMS;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_SECOND_TEMPLATE_ITEM;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_VARIATION;
import static org.fastcode.preferences.PreferenceConstants.P_TEMPLATE_VARIATION_FIELD;
import static org.fastcode.preferences.PreferenceConstants.TEMPLATE;
import static org.fastcode.preferences.PreferenceUtil.getTemplatePreferenceKey;
import static org.fastcode.util.SourceUtil.getFolderFromPath;
import static org.fastcode.util.StringUtil.formatSnippet;
import static org.fastcode.util.StringUtil.isArrayEquals;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.isObjectsEquals;
import static org.fastcode.util.StringUtil.makeWord;
import static org.w3c.dom.Node.ELEMENT_NODE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.GETTER_SETTER;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.FastCodeSelectionDialog;
import org.fastcode.common.StringSelectionDialog;
import org.fastcode.common.Template;
import org.fastcode.common.TemplateStore;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;
import org.fastcode.setting.TemplateSettingsContainer;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Gautam
 *
 */
public class TemplateUtil {

	public static final String	PROJECT_NAME	= "Fast Code Eclipse Plugin";

	/**
	 *
	 * @param templateSettings
	 * @return
	 */
	public static String[] getTemplateVariationsFromUser(final TemplateSettings templateSettings) {
		final String[] snippetVariations = templateSettings.getTemplateVariations();

		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final Shell parentShell = MessageUtil.getParentShell();
		final Shell shell = parentShell == null ? new Shell() : parentShell;

		if (snippetVariations == null || snippetVariations.length == 0) {
			return new String[0];
		}

		if (snippetVariations.length == 1) {
			return snippetVariations;
		}

		final String title;
		if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
			title = templateSettings.getTemplateVariationField() + SPACE + "Variation";
		} else {
			title = "Template Variation";
		}

		final FastCodeSelectionDialog variationsSelectionDialog = new StringSelectionDialog(shell, title,
				"Please select one of the variations", snippetVariations, templateSettings.isAllowMultipleVariation());
		if (variationsSelectionDialog.open() == CANCEL || variationsSelectionDialog.getResult().length == 0) {
			return null;
		}
		if (templateSettings.isAllowMultipleVariation()) {
			// return
			// Arrays.asList(variationsSelectionDialog.getResult()).toArray(new
			// String[0]);
			return variationsAsArray(variationsSelectionDialog.getResult(), snippetVariations);
		} else {
			final String[] retStrings = { (String) variationsSelectionDialog.getResult()[0] };
			return retStrings;
		}
	}

	/**
	 * This will return an array of srings from results with ordering the same
	 * as snippetVariations.
	 *
	 * @param results
	 * @param snippetVariations
	 * @return
	 */
	private static String[] variationsAsArray(final Object[] results, final String[] snippetVariations) {
		if (results == null || results.length == 0) {
			return null;
		}
		final String[] variationsAsArray = new String[results.length];

		int i = 0;
		for (final String variation : snippetVariations) {
			for (final Object result : results) {
				if (!(result instanceof String)) {
					continue;
				}
				if (variation.equals(result)) {
					variationsAsArray[i++] = (String) result;
					break;
				}
			}
		}
		return variationsAsArray;
	}

	/**
	 *
	 * @param template
	 * @return
	 */
	public static String makeTemplateLabel(final String template, final String templatePrefix) {
		final String templateString = template.startsWith(templatePrefix + UNDERSCORE) ? template.substring(templatePrefix.length() + 1)
				: template;

		return templateString.equals(templateString.toUpperCase()) ? makeWord(templateString) : templateString;

	}

	/**
	 * @param store
	 * @param templateResource
	 * @param allTemplatesPreferenceKey
	 *
	 * @param store
	 * @throws Exception
	 */
	public static void updateTemplateStore(final IPreferenceStore store, final InputStream templateStream, final String templatePrefix,
			final String allTemplatesPreferenceKey, final boolean init) throws Exception {
		//TemplateUtil.templatePrefix = templatePrefix;
		final List<TemplateSettingsContainer> templateSettingsContainers = new ArrayList<TemplateSettingsContainer>();

		final String allTemplates = getTemplateSettings(store, templateStream, templatePrefix, templateSettingsContainers, init);

		store.setValue(allTemplatesPreferenceKey, allTemplates.toString());

		persist(store, templateSettingsContainers, init);
		System.out.println("In class TemplateUtil line 189");
	}

	/**
	 *
	 * @param store
	 * @param templateStream
	 * @param templateSettingsContainers
	 * @param init
	 * @return
	 * @throws Exception
	 */
	private static String getTemplateSettings(final IPreferenceStore store, final InputStream templateStream, final String templatePrefix,
			final List<TemplateSettingsContainer> templateSettingsContainers, final boolean init) throws Exception {

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		InputStream entityStream = null;
		final StringBuilder allTemplates = new StringBuilder(EMPTY_STR);
		try {
			final DocumentBuilder docBuilder = factory.newDocumentBuilder();
			entityStream = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/template.dtd"), false);
			docBuilder.setEntityResolver(new FastCodeResolver(entityStream));
			final Document document = docBuilder.parse(templateStream);
			final NodeList parentNodeList = document.getElementsByTagName("templates");
			final Node configNode = parentNodeList.item(0);
			Node node = configNode.getFirstChild();
			// final int numTemplates =
			// document.getElementsByTagName("template").getLength();

			while (node != null) {
				if (node.getNodeType() != ELEMENT_NODE) {
					node = node.getNextSibling();
					continue;
				}
				if ("template".equals(node.getNodeName())) {
					final NamedNodeMap attributes = node.getAttributes();
					if (attributes == null || attributes.getLength() == 0) {
						continue;
					}
					String templateName = null;
					boolean enabled = true;

					for (int i = 0; i < attributes.getLength(); i++) {
						final String attrName = attributes.item(i).getNodeName();
						final String attrValue = attributes.item(i).getNodeValue();
						if ("name".equalsIgnoreCase(attrName)) {
							// get the attribute name or type. name is
							// preferred.
							// setDefaultValue(store, node, templateName, init);

							templateName = templatePrefix + UNDERSCORE + attrValue;
							allTemplates.append(EMPTY_STR.equals(allTemplates.toString()) ? templateName : COLON + templateName);

						} else if (templateName == null && "type".equalsIgnoreCase(attrName)) {
							templateName = templatePrefix + UNDERSCORE + attrValue;
							allTemplates.append(EMPTY_STR.equals(allTemplates.toString()) ? templateName : COLON + templateName);

						}
						if ("enabled".equalsIgnoreCase(attrName)) {
							enabled = !attrValue.equals("false");
						}
					}

					final TemplateSettings templSettings = makeTemplateSettings(node, templateName);
					final TemplateSettings exstTemplSettings = init ? null : TemplateSettings.getTemplateSettings(templateName,
							templatePrefix);
					final TemplateSettingsContainer settingsContainer;
					if (exstTemplSettings == null || !isTemplateEquals(templSettings, exstTemplSettings)) {
						settingsContainer = new TemplateSettingsContainer(templSettings, exstTemplSettings);
						templateSettingsContainers.add(settingsContainer);
					}

				}
				node = node.getNextSibling();
			}

		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage(), ex);
		} finally {
			FastCodeUtil.closeInputStream(entityStream);
		}

		/*final ArrayList<String> defaultTemplateNames = new ArrayList<String>(Arrays.asList(store.getDefaultString(templatePrefix).split(
				COLON)));
		final ArrayList<String> allTemplatesList = new ArrayList<String>(Arrays.asList(allTemplates.toString().split(COLON)));
		final List result = new ArrayList<String>(defaultTemplateNames);
		result.removeAll(allTemplatesList);
		if (!result.isEmpty()) {
			throw new Exception(((String) result.get(0)).substring(templatePrefix.length() + 1)
					+ " is a default template. Do not rename or delete it.");
		}*/
		return allTemplates.toString();
	}

	/**
	 *
	 * @param node
	 * @param templateName
	 * @return
	 */
	public static TemplateSettings makeTemplateSettings(final Node templateNode, final String templateName) {
		// Creating new instance of TemplateSettings
		String[] templateVariations = null;
		String[] allowedFileNames = null;
		String templateVariationField = null;
		String templateDescription = null;
		String itemPattern = null;
		boolean allowMultipleVariation = false;
		GETTER_SETTER getterSetterRequired = GETTER_SETTER.NONE;
		final boolean enabled = true;
		int numberRequiredItems = 0;
		String templateBody = null;
		// final String templateName = null;
		SECOND_TEMPLATE secondTemplate = SECOND_TEMPLATE.none;
		FIRST_TEMPLATE firstTemplate = FIRST_TEMPLATE.None;
		String additionalParameters = null;

		Node node = templateNode.getFirstChild();

		while (node != null) {
			if (node.getNodeType() != ELEMENT_NODE) {
				node = node.getNextSibling();
				continue;
			}
			final String nodeName = node.getNodeName();
			final String nodeContent = node.getTextContent().trim();
			if (nodeName.equals("description")) {
				templateDescription = nodeContent;
			} /*else if (nodeName.equals("class-pattern")) {
				className = nodeContent;
				}*/else if (nodeName.equals("allowed-file-names")) {
				allowedFileNames = nodeContent.split("\\s+");
			} else if (nodeName.equals("allow-multiple-variation")) {
				allowMultipleVariation = nodeContent.equals("true");
			} else if (nodeName.equals("variation")) {
				templateVariations = nodeContent.split("\\s+");
			} else if (nodeName.equals("template-body")) {
				templateBody = nodeContent;
			} else if (nodeName.equals("variation-field")) {
				templateVariationField = nodeContent;
			} else if (nodeName.equals("second-template-item")) {
				secondTemplate = SECOND_TEMPLATE.getSecondTemplate(nodeContent);
				final NamedNodeMap attributes = node.getAttributes();
				if (attributes == null || attributes.getLength() == 0) {
					continue;
				}
				for (int i = 0; i < attributes.getLength(); i++) {
					if ("getter-setter".equalsIgnoreCase(attributes.item(i).getNodeName())) {
						getterSetterRequired = GETTER_SETTER.getGetterSetter(attributes.item(i).getNodeValue());
					}
				}
			} else if (nodeName.equals("first-template-item")) {
				firstTemplate = FIRST_TEMPLATE.getFirstTemplate(nodeContent);
				final NamedNodeMap attributes = node.getAttributes();
				if (attributes == null || attributes.getLength() == 0) {
					continue;
				}
				for (int i = 0; i < attributes.getLength(); i++) {
					if ("item-pattern".equalsIgnoreCase(attributes.item(i).getNodeName())) {
						itemPattern = attributes.item(i).getNodeValue();
					}
					if ("number-required-items".equalsIgnoreCase(attributes.item(i).getNodeName())) {
						numberRequiredItems = Integer.parseInt(attributes.item(i).getNodeValue());
					}
				}
			} else if (nodeName.equals("additional-parameters")) {
				additionalParameters = nodeContent; //.split("\\s+");
			} else if (nodeName.equals("class-file-choice")) {
				firstTemplate = FIRST_TEMPLATE.getFirstTemplate(nodeContent);
			} else if (nodeName.equals("method-field-choice")) {
				secondTemplate = SECOND_TEMPLATE.getSecondTemplate(nodeContent);
			} else if (nodeName.equals("class-pattern")) {
				itemPattern = nodeContent;
			} else if (nodeName.equals("number-required-classes")) {
				numberRequiredItems = Integer.parseInt(nodeContent);
			} else if (nodeName.equals("getter-setter")) {
				getterSetterRequired = GETTER_SETTER.getGetterSetter(nodeContent);
			}

			node = node.getNextSibling();
		}

		return new TemplateSettings(enabled, templateName, itemPattern, templateVariations, allowedFileNames, templateVariationField,
				templateDescription, allowMultipleVariation, getterSetterRequired, numberRequiredItems, templateBody, firstTemplate,
				secondTemplate, additionalParameters);
	}

	/**
	 *
	 * @param store
	 * @param templateName
	 * @param templateSettingsContainers
	 * @param init
	 */
	private static void persist(final IPreferenceStore store, final List<TemplateSettingsContainer> templateSettingsContainers,
			final boolean init) {
		for (final TemplateSettingsContainer templateSettingsContainer : templateSettingsContainers) {
			persist(store, templateSettingsContainer.getTemplateSettings(), templateSettingsContainer.getExstTemplateSettings(), init);
		}
	}

	/**
	 *
	 * @param store
	 * @param templateName
	 * @param templateSettings
	 * @param exstTemplateSettings
	 * @param init
	 */
	private static void persist(final IPreferenceStore store, final TemplateSettings templateSettings,
			final TemplateSettings exstTemplateSettings, final boolean init) {
		final boolean isNew = exstTemplateSettings == null;
		final String templateName = templateSettings.getTemplateName();
		if (isNew || !isObjectsEquals(templateSettings.getItemPattern(), exstTemplateSettings.getItemPattern())) {
			setPreferenceValue(store, "item-pattern", templateSettings.getItemPattern(), templateName, init);
		}
		if (isNew || !isArrayEquals(templateSettings.getTemplateVariations(), exstTemplateSettings.getTemplateVariations())) {
			setPreferenceValue(store, "variation", templateSettings.getTemplateVariations(), templateName, init);
		}
		if (isNew || !isArrayEquals(templateSettings.getAllowedFileNames(), exstTemplateSettings.getAllowedFileNames())) {
			setPreferenceValue(store, "allowed-file-names", templateSettings.getAllowedFileNames(), templateName, init);
		}
		if (isNew || !isObjectsEquals(templateSettings.getTemplateBody(), exstTemplateSettings.getTemplateBody())) {
			setPreferenceValue(store, "template-body", formatSnippet(templateSettings.getTemplateBody()), templateName, init);
		}
		if (isNew || !isObjectsEquals(templateSettings.getTemplateDescription(), exstTemplateSettings.getTemplateDescription())) {
			setPreferenceValue(store, "description", templateSettings.getTemplateDescription(), templateName, init);
		}
		if (isNew || templateSettings.isAllowMultipleVariation() != exstTemplateSettings.isAllowMultipleVariation()) {
			setPreferenceValue(store, "allow-multiple-variation", templateSettings.isAllowMultipleVariation(), templateName, init);
		}
		if (isNew || templateSettings.getNumberRequiredItems() != exstTemplateSettings.getNumberRequiredItems()) {
			setPreferenceValue(store, "number-required-items", templateSettings.getNumberRequiredItems(), templateName, init);
		}
		if (isNew || templateSettings.getGetterSetterRequired() != exstTemplateSettings.getGetterSetterRequired()) {
			setPreferenceValue(store, "getter-setter", templateSettings.getGetterSetterRequired().getValue(), templateName, init);
		}
		if (isNew || templateSettings.isEnabled() != exstTemplateSettings.isEnabled()) {
			setPreferenceValue(store, "enable-template", templateSettings.isEnabled(), templateName, init);
		}
		if (templateSettings.getFirstTemplateItem() != null
				&& (isNew || templateSettings.getFirstTemplateItem() != exstTemplateSettings.getFirstTemplateItem())) {
			setPreferenceValue(store, "first-template-item", templateSettings.getFirstTemplateItem().getValue(), templateName, init);
		}

		if (templateSettings.getSecondTemplateItem() != null
				&& (isNew || templateSettings.getSecondTemplateItem() != exstTemplateSettings.getSecondTemplateItem())) {
			setPreferenceValue(store, "second-template-item", templateSettings.getSecondTemplateItem().getValue(), templateName, init);
		}
		if (isNew || templateSettings.getAdditionalParamaters() != exstTemplateSettings.getAdditionalParamaters()) {
			setPreferenceValue(store, "additional-parameters", templateSettings.getAdditionalParamaters(), templateName, init);
		}
	}

	/**
	 *
	 * @param store
	 * @param node
	 * @param templateType
	 *            private static void setDefaultValue(final IPreferenceStore
	 *            store, final Node node, final String templateType, final
	 *            boolean init) { final NodeList childList =
	 *            node.getChildNodes(); for (int i = 0; i <
	 *            childList.getLength(); i++) { final Node n =
	 *            childList.item(i); if (n.getNodeType() != ELEMENT_NODE) {
	 *            continue; } setPreferenceValue(store, "enable-template",
	 *            "true", templateType, init, Boolean.class); final String
	 *            nodeName = n.getNodeName(); if
	 *            (nodeName.equals("number-required-classes")) {
	 *            setPreferenceValue(store, nodeName, n.getTextContent(),
	 *            templateType, init, Integer.class); } else if
	 *            (nodeName.equals("allow_multiple_variation")) {
	 *            setPreferenceValue(store, nodeName, n.getTextContent(),
	 *            templateType, init, Boolean.class); } else {
	 *            setDefaultValue(store, nodeName, n.getTextContent(),
	 *            templateType, init); } } }
	 */

	/**
	 *
	 * @param store
	 * @param nodeName
	 * @param class1
	 * @param templateItem
	 *            private static void setDefaultValue(final IPreferenceStore
	 *            store, final String nodeName, final String content, final
	 *            String templateType, final boolean init) {
	 *            setPreferenceValue(store, nodeName, content, templateType,
	 *            init, String.class); }
	 */

	/**
	 *
	 * @param store
	 * @param nodeName
	 * @param string
	 */
	private static void setPreferenceValue(final IPreferenceStore store, final String nodeName, final Object content,
			final String templateName, final boolean init) {
		final String templateItem = nodeName;
		final String templatePreferenceKey = getTemplatePreferenceKey(templateName,
				templateItem.toUpperCase().replaceAll(HYPHEN, UNDERSCORE));
		final Object value;
		if (content == null) {
			store.setDefault(templatePreferenceKey, EMPTY_STR);
			if (!init) {
				store.setValue(templatePreferenceKey, EMPTY_STR);
			}
			return;
		}
		if (content.getClass().isArray()) {
			String val = EMPTY_STR;
			final String[] values = (String[]) content;
			for (int i = 0; values != null && i < values.length; i++) {
				val += values[i] + SPACE;
			}
			value = val;
		} else {
			value = content;
		}
		if (value instanceof String) {
			// final boolean isTemplateBody = nodeName.equals("template-body");
			store.setDefault(templatePreferenceKey, (String) value);
			if (!init) {
				store.setValue(templatePreferenceKey, (String) value);
			}
		} else if (value instanceof Integer) {
			store.setDefault(templatePreferenceKey, (Integer) value);
			if (!init) {
				store.setValue(templatePreferenceKey, (Integer) value);
			}
		} else if (value instanceof Boolean) {
			store.setDefault(templatePreferenceKey, (Boolean) value);
			if (!init) {
				store.setValue(templatePreferenceKey, (Boolean) value);
			}
		} else {
			throw new RuntimeException("Wrong type received " + content.getClass().getName());
		}
	}

	/**
	 *
	 * @param store
	 * @param preferenceKey
	 * @param value
	 */
	private static void setDefaultValue(final IPreferenceStore store, final String preferenceKey, final String value) {
		final String defaultValue = store.getDefaultString(preferenceKey);
		if (defaultValue == null || !defaultValue.equals(value)) {
			store.setDefault(preferenceKey, value);
		}
	}

	/**
	 *
	 * @param store
	 * @param preferenceKey
	 * @param value
	 */
	private static void setValue(final IPreferenceStore store, final String preferenceKey, final String value) {
		final String oldValue = store.getString(preferenceKey);
		if (oldValue == null || !oldValue.equals(value)) {
			store.setValue(preferenceKey, value);
		}
	}

	/**
	 * @param store
	 * @param preferenceKey
	 * @param value
	 */
	private static void setDefaultValue(final IPreferenceStore store, final String preferenceKey, final Integer value) {
		final int defaultInt = store.getDefaultInt(preferenceKey);
		if (defaultInt != value) {
			store.setDefault(preferenceKey, value.intValue());
		}
	}

	/**
	 * @param store
	 * @param preferenceKey
	 * @param value
	 */
	private static void setValue(final IPreferenceStore store, final String preferenceKey, final Integer value) {
		final int oldValue = store.getInt(preferenceKey);
		if (oldValue != value) {
			store.setValue(preferenceKey, value.intValue());
		}
	}

	/**
	 * @param store
	 * @param preferenceKey
	 * @param value
	 */
	private static void setDefaultValue(final IPreferenceStore store, final String preferenceKey, final boolean value) {
		final boolean defaultBoolean = store.getDefaultBoolean(preferenceKey);
		if (defaultBoolean != value) {
			store.setDefault(preferenceKey, value);
		}
	}

	/**
	 * @param store
	 * @param preferenceKey
	 * @param value
	 */
	private static void setValue(final IPreferenceStore store, final String preferenceKey, final boolean value) {
		final boolean oldValue = store.getBoolean(preferenceKey);
		if (oldValue != value) {
			store.setValue(preferenceKey, value);
		}
	}

	/**
	 *
	 * @param file
	 * @throws Exception
	 */
	public static IFile findOrcreateTemplate(final String fileName, final String folderName) throws Exception {
		return findOrcreateTemplate(fileName, folderName, true);
	}

	/**
	 *
	 * @param file
	 * @throws Exception
	 */
	public static IFile findOrcreateTemplate(final String fileName, final String folderName, final boolean create) throws Exception {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		if (workspace == null || workspace.getRoot() == null) {
			return null;
		}

		IProject project = null;
		for (final IProject prjct : workspace.getRoot().getProjects()) {
			if (prjct == null || !prjct.exists()) {
				continue;
			}
			if (PROJECT_NAME.equals(prjct.getName())) {
				project = prjct;
				break;
			}
		}
		if (project == null) {
			if (create) {
				project = workspace.getRoot().getProject(PROJECT_NAME);
				project.create(new NullProgressMonitor());
				if (!project.isOpen()) {
					project.open(null);
				}
			} else {
				return null;
			}
		} else if (!project.isOpen()) {
			project.open(null);
		}

		// final IFile templatefile = findFileFromPath("/" + PROJECT_NAME +
		// "/resources/" + fileName);
		//IFolder folder = null;
		//if (create) {
		final IFolder folder = getFolderFromPath(project, "resources" + FORWARD_SLASH + folderName);
		if (folder != null && folder.exists()) {
			return (IFile) (folder.getFile(fileName) != null ? folder.getFile(fileName) : create ? new File(fileName) : null);
		}
		//}

		return null;
		//return (IFile) (folder.getFile(fileName) != null ? folder.getFile(fileName) : create ? new File(fileName) : null);
	}

	/**
	 *
	 * @param additional
	 * @return
	 */
	public static String getAllTemplates(final String templatePreferenceName, final String templatePrefix) {

		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final GlobalSettings globalSettings = GlobalSettings.getInstance();
		final String allTemplates = preferenceStore.getString(templatePreferenceName);
		final String TAB2 = TAB + TAB;

		final StringBuilder templateBuffer = new StringBuilder();

		if (templatePreferenceName.equals(P_GLOBAL_PROPS)) {
			final Map<Object, Object> property = globalSettings.getProperties();

			for (final Object prop : property.entrySet()) {

				templateBuffer.append(prop + NEWLINE);

			}
		} else {

			templateBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			templateBuffer.append("<!DOCTYPE templates PUBLIC \"//UNKNOWN/\" \"http://fast-code.sourceforge.net/dtd/template.dtd\">\n");
			templateBuffer.append("<templates>\n");
			/*
						for (final String template : allTemplates.split(COLON)) {
							if (isEmpty(template)) {
								continue;
							}*/

			String templateName;
			final TemplateStore templateStore = TemplateStore.getInstance();
			for (final Template template : templateStore.getTemplatesList(templatePreferenceName)) {

				if (template.isTemplateDeleted()) {
					continue;
				}
				templateName = templatePrefix + UNDERSCORE + template.getTemplateName();
				if (templateName.startsWith(templatePrefix + UNDERSCORE)) {
					templateBuffer.append(TAB + "<template name=\"" + templateName.substring((templatePrefix + UNDERSCORE).length())
							+ "\">\n");
				} else {
					templateBuffer.append(TAB + "<template name=\"" + templateName + "\">\n");
				}
				templateBuffer.append(makeTemplateNode(templateName, "description", P_TEMPLATE_DESCRIPTION, TAB2) + NEWLINE);

				templateBuffer.append(makeTemplateNode(templateName, "variation", P_TEMPLATE_VARIATION, TAB2).equals(EMPTY_STR) ? EMPTY_STR
						: makeTemplateNode(templateName, "variation", P_TEMPLATE_VARIATION, TAB2) + NEWLINE);

				if (FALSE_STR.equalsIgnoreCase(globalSettings.getPropertyValue(DEFAULT_TEMPLATE_VARIATION_FIELD, TRUE_STR))) {
					templateBuffer.append(makeTemplateNode(templateName, "variation-field", P_TEMPLATE_VARIATION_FIELD, TAB2).equals(
							EMPTY_STR) ? EMPTY_STR : makeTemplateNode(templateName, "variation-field", P_TEMPLATE_VARIATION_FIELD, TAB2)
							+ NEWLINE);
				}
				if (templatePreferenceName.equals(P_ALL_TEMPLATES) || templatePreferenceName.equals(P_DATABASE_ALL_TEMPLATES)
				/*|| templatePreferenceName.equals(P_FILE_ALL_TEMPLATES)*/) {
					if (templatePreferenceName.equals(P_ALL_TEMPLATES)) {

						templateBuffer.append(makeTemplateNode(templateName, "allow-multiple-variation",
								P_TEMPLATE_ALLOW_MULTIPLE_VARIATION, false, TAB2, Boolean.class).equals(EMPTY_STR) ? EMPTY_STR
								: makeTemplateNode(templateName, "allow-multiple-variation", P_TEMPLATE_ALLOW_MULTIPLE_VARIATION, false,
										TAB2, Boolean.class) + NEWLINE);
					}

					templateBuffer.append(makeTemplateNode(templateName, "allowed-file-names", P_TEMPLATE_ALLOWED_FILE_NAMES, TAB2).equals(
							EMPTY_STR) ? EMPTY_STR : makeTemplateNode(templateName, "allowed-file-names", P_TEMPLATE_ALLOWED_FILE_NAMES,
							TAB2) + NEWLINE);

					if (templatePreferenceName.equals(P_ALL_TEMPLATES)) {
						templateBuffer.append(makeTemplateNode(templateName, "first-template-item", P_TEMPLATE_FIRST_TEMPLATE_ITEM, false,
								TAB2, String.class).equals(EMPTY_STR) ? EMPTY_STR : makeTemplateNode(templateName, "first-template-item",
								P_TEMPLATE_FIRST_TEMPLATE_ITEM, false, TAB2, String.class) + NEWLINE);

						templateBuffer.append(makeTemplateNode(templateName, "second-template-item", P_TEMPLATE_SECOND_TEMPLATE_ITEM,
								false, TAB2, String.class).equals(EMPTY_STR) ? EMPTY_STR : makeTemplateNode(templateName,
								"second-template-item", P_TEMPLATE_SECOND_TEMPLATE_ITEM, false, TAB2, String.class) + NEWLINE);

						templateBuffer.append(makeTemplateNode(templateName, "item-pattern", P_TEMPLATE_ITEM_PATTERN, false, TAB2).equals(
								EMPTY_STR) ? EMPTY_STR : makeTemplateNode(templateName, "item-pattern", P_TEMPLATE_ITEM_PATTERN, false,
								TAB2) + NEWLINE);

						/*templateBuffer.append(makeTemplateNode(template, "getter-setter", P_TEMPLATE_GETTER_SETTER, false, TAB2).equals(
								EMPTY_STR) ? EMPTY_STR : makeTemplateNode(template, "getter-setter", P_TEMPLATE_GETTER_SETTER, false, TAB2)
								+ NEWLINE);

						templateBuffer.append(makeTemplateNode(template, "number-required-items", P_TEMPLATE_NUMBER_REQUIRED_ITEMS, false,
								TAB2, Integer.class).equals(EMPTY_STR) ? EMPTY_STR : makeTemplateNode(template, "number-required-items",
								P_TEMPLATE_NUMBER_REQUIRED_ITEMS, false, TAB2, Integer.class) + NEWLINE);*/

					}

					templateBuffer.append(makeTemplateNode(templateName, "additional-parameters", P_TEMPLATE_ADDITIONAL_PARAMETERS, false,
							TAB2, String.class).equals(EMPTY_STR) ? EMPTY_STR : makeTemplateNode(templateName, "additional-parameters",
							P_TEMPLATE_ADDITIONAL_PARAMETERS, false, TAB2, String.class) + NEWLINE);

				}
				templateBuffer.append(makeTemplateNode(templateName, "template-body", P_TEMPLATE_BODY, true, TAB2) + NEWLINE);

				templateBuffer.append(TAB + "</template>" + NEWLINE);
				templateBuffer.append(NEWLINE);
			}
			templateBuffer.append("</templates>" + NEWLINE);
		}
		return templateBuffer.toString();
	}

	/**
	 *
	 * @param template
	 * @param nodeName
	 * @param preferenceKey
	 * @param cdata
	 * @return
	 */
	private static String makeTemplateNode(final String template, final String nodeName, final String preferenceKey, final boolean cdata,
			final String leadingWhiteSpace) {
		return makeTemplateNode(template, nodeName, preferenceKey, cdata, leadingWhiteSpace, String.class);
	}

	/**
	 *
	 * @param template
	 * @param nodeName
	 * @param preferenceKey
	 * @return
	 */
	public static String makeTemplateNode(final String template, final String nodeName, final String preferenceKey,
			final String leadingWhiteSpace) {
		return makeTemplateNode(template, nodeName, preferenceKey, false, leadingWhiteSpace, String.class);
	}

	/**
	 *
	 * @param templateSettingsA
	 * @param templateSettingsB
	 * @return
	 */
	public static boolean isTemplateEquals(final TemplateSettings templateSettingsA, final TemplateSettings templateSettingsB) {
		if (!isObjectsEquals(templateSettingsA.getItemPattern(), templateSettingsB.getItemPattern())) {
			return false;
		}
		if (!isObjectsEquals(templateSettingsA.getTemplateDescription(), templateSettingsB.getTemplateDescription())) {
			return false;
		}
		if (templateSettingsA.isAllowMultipleVariation() != templateSettingsB.isAllowMultipleVariation()) {
			return false;
		}
		if (templateSettingsA.getGetterSetterRequired() != templateSettingsB.getGetterSetterRequired()) {
			return false;
		}
		if (templateSettingsA.getNumberRequiredItems() != templateSettingsB.getNumberRequiredItems()) {
			return false;
		}
		if (!isArrayEquals(templateSettingsA.getTemplateVariations(), templateSettingsB.getTemplateVariations())) {
			return false;
		}
		if (!isArrayEquals(templateSettingsA.getAllowedFileNames(), templateSettingsB.getAllowedFileNames())) {
			return false;
		}
		if (!isObjectsEquals(templateSettingsA.getTemplateBody(), templateSettingsB.getTemplateBody())) {
			return false;
		}
		if (!isObjectsEquals(templateSettingsA.getFirstTemplateItem(), templateSettingsB.getFirstTemplateItem())) {
			return false;
		}

		if (!isObjectsEquals(templateSettingsA.getSecondTemplateItem(), templateSettingsB.getSecondTemplateItem())) {
			return false;
		}
		if (!isObjectsEquals(templateSettingsA.getAdditionalParamaters(), templateSettingsB.getAdditionalParamaters())) {
			return false;
		}

		return true;
	}

	/**
	 *
	 * @param nodeName
	 * @param preferenceKey
	 * @param template
	 * @return
	 */
	private static String makeTemplateNode(final String template, final String nodeName, final String preferenceKey, final boolean cdata,
			final String leadingWhiteSpace, final Class class1) {
		//		final IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		final IPreferenceStore preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		final String templatePreferenceKey = getTemplatePreferenceKey(template, preferenceKey);
		Object preferenceValue;
		if (Boolean.class.equals(class1)) {
			preferenceValue = preferenceStore.getBoolean(templatePreferenceKey);
			if (preferenceValue.equals(false)) {
				preferenceValue = EMPTY_STR;
			}
		} else if (Integer.class.equals(class1)) {
			preferenceValue = preferenceStore.getInt(templatePreferenceKey);
		} else {
			preferenceValue = preferenceStore.getString(templatePreferenceKey);

		}
		if (preferenceValue.equals(SPACE)) {
			preferenceValue = EMPTY_STR;
		}

		String attributes = EMPTY_STR;
		if (nodeName.equals("first-template-item") || nodeName.equals("second-template-item")) {
			if (preferenceValue.equals("none")) {
				preferenceValue = EMPTY_STR;
			}
			attributes = getTemplateNodeAttributes(template, preferenceStore, nodeName);
		}
		return cdata ? leadingWhiteSpace + "<" + nodeName + attributes + ">\n" + leadingWhiteSpace + TAB + "<![CDATA[\n"
				+ formatCDATA(leadingWhiteSpace + TAB + TAB, (String) preferenceValue) + leadingWhiteSpace + TAB + "]]>\n"
				+ leadingWhiteSpace + "</" + nodeName + ">" : preferenceValue.equals(EMPTY_STR) ? EMPTY_STR : leadingWhiteSpace + "<"
				+ nodeName + attributes + ">" + preferenceValue + "</" + nodeName + ">";
	}

	/**
	 *
	 * @param string
	 * @param preferenceValue
	 * @return
	 */
	public static String formatCDATA(final String leading, final String preferenceValue) {
		final String[] lines = preferenceValue.split(NEWLINE);
		final StringBuilder cdata = new StringBuilder();
		for (final String line : lines) {
			cdata.append(leading + line + NEWLINE);
		}
		return cdata.toString().toString();
	}

	/**
	 *
	 * @author Gautam
	 *
	 */
	public static class FastCodeResolver implements EntityResolver {

		private final InputStream	inputStream;

		/**
		 * @param inputStream
		 */
		public FastCodeResolver(final InputStream inputStream) {
			super();
			this.inputStream = inputStream;
		}

		/**
		 *
		 */
		@Override
		public InputSource resolveEntity(final String publicID, final String systemID) throws SAXException, IOException {
			//			if (systemID.equals("")) {
			final InputSource inputSource = new InputSource(this.inputStream);
			return inputSource;
			//			}
			//			return null;
		}

	}

	/**
	 * @param template
	 * @param preferenceStore
	 * @param nodeName
	 * @return
	 */
	private static String getTemplateNodeAttributes(final String template, final IPreferenceStore preferenceStore, final String nodeName) {

		if (nodeName.equals("first-template-item")) {

			final int noOfRequiredItems = preferenceStore.getInt(getTemplatePreferenceKey(template, P_TEMPLATE_NUMBER_REQUIRED_ITEMS));
			final String itemPattern = preferenceStore.getString(getTemplatePreferenceKey(template, P_TEMPLATE_ITEM_PATTERN));
			if (isEmpty(itemPattern)) {
				return noOfRequiredItems == 0 ? EMPTY_STR : SPACE + "number-required-items=\"" + noOfRequiredItems + "\"";
			} else {
				return noOfRequiredItems == 0 ? SPACE + "item-pattern=\"" + itemPattern + "\"" : SPACE + "number-required-items=\""
						+ noOfRequiredItems + "\"" + SPACE + "item-pattern=\"" + itemPattern + "\"";
			}
		} else if (nodeName.equals("second-template-item")) {
			final String getterSetter = preferenceStore.getString(getTemplatePreferenceKey(template, P_TEMPLATE_GETTER_SETTER));
			return getterSetter.equals(GETTER_SETTER.NONE.getValue()) ? EMPTY_STR : SPACE + "getter-setter=\"" + getterSetter + "\"";
		}
		return EMPTY_STR;

	}

	/**
	 * @param store
	 * @param templatePrefix
	 * @param allTemplatesPreferenceKey
	 */
	public static synchronized void loadTemplates(final IPreferenceStore store, final String templatePrefix,
			final String allTemplatesPreferenceKey) {

		String templateName = null;
		final ArrayList<Template> templates = new ArrayList<Template>();

		final TemplateStore templateStore = TemplateStore.getInstance();
		if (templateStore.contains(allTemplatesPreferenceKey)) {
			templateStore.getTemplatesList(allTemplatesPreferenceKey).removeAll(templates);
		}
		for (final String template : store.getString(allTemplatesPreferenceKey).split(":")) {
			if (isEmpty(template.trim())) {
				continue;
			}

			if (template.startsWith("TEMPLATE_")) {
				templateName = template.substring(9).trim();
			}/* else if (template.startsWith("FILE_TEMPLATE_")) {
				templateName = template.substring(14).trim();
				}*/else if (template.startsWith("DATABASE_TEMPLATE_")) {
				templateName = template.substring(18).trim();
			} else if (template.startsWith("COMMON_TEMPLATE_")) {
				templateName = template.substring(16).trim();
			}/* else if (template.startsWith("ADDITIONAL_DATABASE_TEMPLATE_")) {
				templateName = template.substring(29).trim();
				}*/

			templates.add(createNewTemplate(store, templateName, template, allTemplatesPreferenceKey));
		}
		templateStore.loadTemplateStore(allTemplatesPreferenceKey, templates);

	}

	/**
	 * @param store
	 * @param templateName
	 * @param template
	 * @param allTemplatesPreferenceKey
	 * @return
	 */
	private static Template createNewTemplate(final IPreferenceStore store, final String templateName, final String template,
			final String allTemplatesPreferenceKey) {

		Template newTemplate;

		if (allTemplatesPreferenceKey.equals(P_ALL_TEMPLATES)) {

			newTemplate = new Template(templateName, store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_DESCRIPTION)),
					store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_VARIATION)), store.getString(getTemplatePreferenceKey(
							template, P_TEMPLATE_ALLOWED_FILE_NAMES)),
					store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_BODY)), store.getBoolean(getTemplatePreferenceKey(
							template, P_TEMPLATE_ENABLE_TEMPLATE)), store.getString(getTemplatePreferenceKey(template,
							P_TEMPLATE_FIRST_TEMPLATE_ITEM)), store.getString(getTemplatePreferenceKey(template,
							P_TEMPLATE_SECOND_TEMPLATE_ITEM)), store.getString(getTemplatePreferenceKey(template,
							P_TEMPLATE_NUMBER_REQUIRED_ITEMS)),
					store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_GETTER_SETTER)),
					store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_ADDITIONAL_PARAMETERS)),
					store.getBoolean(getTemplatePreferenceKey(template, P_TEMPLATE_ALLOW_MULTIPLE_VARIATION)), null, false);

		} else {
			newTemplate = new Template(templateName, store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_DESCRIPTION)),
					store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_VARIATION)), store.getString(getTemplatePreferenceKey(
							template, P_TEMPLATE_ALLOWED_FILE_NAMES)),
					store.getString(getTemplatePreferenceKey(template, P_TEMPLATE_BODY)), store.getBoolean(getTemplatePreferenceKey(
							template, P_TEMPLATE_ENABLE_TEMPLATE)), false);
		}
		store.setValue(getTemplatePreferenceKey(template, P_TEMPLATE_NAME), templateName);
		return newTemplate;
	}

	/**
	 *
	 */
	public static void reloadTemplates() {
		final IPreferenceStore store = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		loadTemplates(store, TEMPLATE, P_ALL_TEMPLATES);
		loadTemplates(store, P_COMMON_TEMPLATE_PREFIX, P_ALL_COMMON_TEMPLATES);
		loadTemplates(store, P_DATABASE_TEMPLATE_PREFIX, P_DATABASE_ALL_TEMPLATES);

	}
}
