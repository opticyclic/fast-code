package org.fastcode.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateStore {

	private static Map<String, ArrayList<Template>>	templatesList	= new HashMap<String, ArrayList<Template>>();
	private static TemplateStore					templateStore	= new TemplateStore();
	private static boolean							reload			= false;

	private TemplateStore() {

	}

	public static TemplateStore getInstance() {
		if (reload) {
			reload = false;
			templateStore = new TemplateStore();
		}
		return templateStore;

	}

	public boolean contains(final String prefpageName) {
		return templatesList.containsKey(prefpageName);

	}

	public boolean isEmpty() {
		return templatesList.isEmpty();
	}

	public void loadTemplateStore(final String prefPageName, final ArrayList<Template> templates) {

		templatesList.put(prefPageName, templates);

	}

	public List<Template> getTemplatesList(final String prefPageName) {

		return templatesList.get(prefPageName);
	}

	public static void addTemplate(final String prefPageName, final Template t) {

		reload = true;
		templatesList.get(prefPageName).add(templatesList.get(prefPageName).size(), t);

	}

	public void setReload(final boolean rLoad) {
		templatesList.clear();
	}

	public void clear(final String prefpageName) {
		templatesList.remove(prefpageName);

	}
}
