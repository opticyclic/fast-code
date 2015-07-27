package org.fastcode.preferences;

//PositionalXMLReader.java

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PositionalXMLReader {
	final static String	LINE_NUMBER_KEY_NAME	= "lineNumber";
	static int			lineNo					= 0;

	public static int readXML(final InputStream is, final String templateName) throws IOException, SAXException {
		final Document doc;

		SAXParser parser;

		try {
			final SAXParserFactory factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
		} catch (final ParserConfigurationException e) {
			throw new RuntimeException("Can't create SAX parser / DOM builder.", e);
		}

		//final Stack<Element> elementStack = new Stack<Element>();
		//final StringBuilder textBuffer = new StringBuilder();
		final DefaultHandler handler = new DefaultHandler() {
			private Locator	locator;
			boolean			TemplateFound	= false;
			int				tmplineno		= 0;

			@Override
			public void setDocumentLocator(final Locator locator) {
				this.locator = locator; // Save the locator, so that it can be used later for line tracking when traversing nodes.
			}

			@Override
			public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
					throws SAXException {
				if (this.tmplineno == 0) {
					for (int i = 0; i < attributes.getLength(); i++) {
						if (attributes.getQName(i).equals("name") && attributes.getValue(i).equals(templateName)) {
							this.TemplateFound = true;
							break;
						}

					}
					if (qName.equals("template-body") && this.TemplateFound) {
						lineNo = this.locator.getLineNumber();
						this.tmplineno = lineNo;
					}

				} else {
					return;
				}
			}

			@Override
			public void endElement(final String uri, final String localName, final String qName) {
				try {
					super.endElement(uri, localName, qName);
				} catch (final SAXException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		};
		parser.parse(is, handler);

		return lineNo;
	}
}
