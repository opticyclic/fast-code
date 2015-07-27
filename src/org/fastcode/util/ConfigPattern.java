/**
 *
 */
package org.fastcode.util;

import static org.w3c.dom.Node.ELEMENT_NODE;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.fastcode.Activator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Gautam Dev
 *
 */
public class ConfigPattern {

	public static final String				propFile		= "resources/fast-code-config.xml";
	private static final ConfigPattern		configPattern	= new ConfigPattern();
	private CreateSimilarDescriptorConfig[]	configs;
	final private Map<String, String[]>		configTypes		= new HashMap<String, String[]>();

	protected ConfigPattern() {
		init();
	}

	public static ConfigPattern getInstance() {
		return configPattern;
	}

	/**
	 * @return the configs
	 */
	public CreateSimilarDescriptorConfig[] getConfigs() {
		return this.configs;
	}

	/**
	 *
	 */
	private void init() {
		InputStream input = null;
		try {
			final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

			final DocumentBuilder docBuilder = factory.newDocumentBuilder();

			input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path(propFile), false);
			final Document document = docBuilder.parse(input);
			final NodeList parentNodeList = document.getElementsByTagName("configurations");
			final Node configNode = parentNodeList.item(0);
			Node node = configNode.getFirstChild();
			final int numConfigs = document.getElementsByTagName("configuration").getLength();
			this.configs = new CreateSimilarDescriptorConfig[numConfigs];
			int configCount = 0;
			while (node != null) {
				// if (!node.hasChildNodes()) {
				// node = node.getNextSibling();
				// continue;
				// }
				if (node.getNodeType() != ELEMENT_NODE) {
					node = node.getNextSibling();
					continue;
				}
				if (node.getNodeName() != null && node.getNodeName().equals("configuration")) {
					final CreateSimilarDescriptorConfig config = createSimilarDescriptorConfigFromNode(node);
					this.configs[configCount++] = config;
				} else if (node.getNodeName() != null && node.getNodeName().equals("configuration-preference")) {
					createConfigTypes(node);
				}

				node = node.getNextSibling();
			}
			/*
			 * String contents1 = document.getTextContent(); String contents =
			 * document.getFirstChild().getNodeValue(); node =
			 * node.getFirstChild(); contents = node.getTextContent();
			 * document.getDocumentElement();
			 */
		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (input != null) {
					input.close();
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 *
	 * @param node
	 * @return
	 */
	private CreateSimilarDescriptorConfig createSimilarDescriptorConfigFromNode(final Node node) {

		if (node.getAttributes() == null || node.getAttributes().getLength() == 0) {
			return null;
		}

		final String type = node.getAttributes().item(0).getNodeValue();

		if (node.getAttributes() == null || node.getAttributes().getLength() == 0) {
			return null;
		}

		final NodeList childList = node.getChildNodes();
		final StringBuilder header = new StringBuilder();
		final StringBuilder start = new StringBuilder();
		final StringBuilder end = new StringBuilder();
		final StringBuilder body = new StringBuilder();
		final StringBuilder file = new StringBuilder();
		final StringBuilder location = new StringBuilder();
		final StringBuilder locale = new StringBuilder();

		for (int i = 0; i < childList.getLength(); i++) {
			final Node n = childList.item(i);
			if (n.getNodeType() != ELEMENT_NODE) {
				continue;
			}
			matchNodeValue(header, n, "header");
			matchNodeValue(start, n, "start");
			matchNodeValue(end, n, "end");
			matchNodeValue(body, n, "body");
			matchNodeValue(file, n, "file");
			matchNodeValue(location, n, "location");
			matchNodeValue(locale, n, "locale");
		}

		final CreateSimilarDescriptorConfig config = new CreateSimilarDescriptorConfig(type.toString(), file.toString(), null,
				location.toString(), locale.toString(), header.toString(), start.toString(), body.toString(), end.toString());
		return config;
	}

	/**
	 *
	 * @param node
	 */
	private void createConfigTypes(final Node node) {
		final NodeList childList = node.getChildNodes();

		for (int i = 0; i < childList.getLength(); i++) {
			final Node n = childList.item(i);
			if (n.getNodeType() != ELEMENT_NODE) {
				continue;
			}
			if (n.getNodeName() == null || !n.getNodeName().equals("preference")) {
				continue;
			}
			if (n.getAttributes() == null || n.getAttributes().getLength() < 2) {
				continue;
			}

			final String name = n.getAttributes().item(0).getNodeValue();
			final String type = n.getAttributes().item(1).getNodeValue();

			if (name != null && type != null) {
				this.configTypes.put(name, type.split("\\s+"));
			}
		}
	}

	/**
	 *
	 * @param builder
	 * @param node
	 * @param name
	 */
	private void matchNodeValue(final StringBuilder builder, final Node node, final String name) {
		if (node.getNodeName().equals(name)) {
			builder.append(node.getTextContent().trim());
		}
	}

	/**
	 * @return the configTypes
	 */
	public Map<String, String[]> getConfigTypes() {
		return this.configTypes;
	}
}
