package org.fastcode;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.util.FastCodeUtil.closeInputStream;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.text.JavaColorManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.graphics.RGB;

/**
 * Class for managing resources.
 */
@SuppressWarnings("restriction")
public final class FastCodeColorManager {

	private static JavaColorManager				colorManager;

	private static Map<String, IToken>			tokens;
	private static Map<String, TextAttribute>	attributes;
	static {
		colorManager = new JavaColorManager();

		attributes = new HashMap<String, TextAttribute>();
		tokens = new HashMap<String, IToken>();

		final Properties prop = readProperties();
		final Enumeration<Object> keys = prop.keys();
		Object element = keys.nextElement();

		while (keys.hasMoreElements()) {

			final String string = prop.getProperty((String) element);
			final int[] colorValues = new int[4];
			int i = 0;
			final String[] colorsArry = string.split(COMMA);
			for (final String s : colorsArry) {
				colorValues[i] = Integer.valueOf(s.trim());
				i++;
			}
			TextAttribute textAttribute = null;
			if (colorValues[3] == 0) {
				textAttribute = new TextAttribute(colorManager.getColor(new RGB(colorValues[0], colorValues[1], colorValues[2])));
			} else {
				textAttribute = new TextAttribute(colorManager.getColor(new RGB(colorValues[0], colorValues[1], colorValues[2])), null,
						colorValues[3]);
			}
			attributes.put(element.toString(), textAttribute);
			tokens.put((String) element, new Token(getTextAttribute((String) element)));
			element = keys.nextElement();
		}

		new ImageRegistry();

	}

	public static Properties readProperties() {
		InputStream input = null;
		final String propertiesFile = "TemplateColoring.properties";
		final Properties prop = new Properties();

		try {

			input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path("resources/" + propertiesFile), false);
			prop.load(input);
			input.close();

		} catch (final Exception ex) {
			ex.printStackTrace();
		} finally {
			closeInputStream(input);
		}

		return prop;
	}

	/* no instantiation */
	private FastCodeColorManager() {
	}

	/**
	 * Gets the token with the given name.
	 *
	 * @param name
	 *            the name
	 *
	 * @return the token
	 */
	public static IToken getToken(final String name) {
		IToken token = tokens.get(name);
		if (token == null) {
			token = tokens.get("DEFAULT");
		}
		return token;
	}

	/**
	 * Gets the text attribute with the given name.
	 *
	 * @param name
	 *            the name
	 *
	 * @return the text attribute
	 */
	public static TextAttribute getTextAttribute(final String name) {
		TextAttribute attribute = attributes.get(name);
		if (attribute == null) {
			attribute = attributes.get("DEFAULT");
		}
		return attribute;
	}
}
