package org.fastcode.templates.velocity.contentassist;

import static org.fastcode.common.FastCodeConstants.DOLLAR;
import static org.fastcode.common.FastCodeConstants.DOT_CHAR;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL_CHAR;
import static org.fastcode.common.FastCodeConstants.NOT_CHAR;
import static org.fastcode.common.FastCodeConstants.RIGHT_CURL_CHAR;

import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.fastcode.FastCodeColorManager;
import org.fastcode.templates.rules.FastCodeTemplateCodeScanner;
import org.fastcode.templates.rules.IRulesStrategy;
import org.fastcode.templates.velocity.rules.TemplateRulesStartStrategie;

/**
 * Text hover for Velocity references.
 */
public class TemplateTextHover implements ITextHover {
	private final Map<String, String>	properties;
	private final ITokenScanner			referenceScanner;

	/**
	 * Instantiates a new reference text hover.
	 */
	public TemplateTextHover(final Map<String, String> properties) {
		final IRulesStrategy[] rulesStrategies = { new TemplateRulesStartStrategie() };
		this.referenceScanner = new FastCodeTemplateCodeScanner(rulesStrategies);

		this.properties = properties;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHover#getHoverInfo(org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
	 */
	@Override
	public String getHoverInfo(final ITextViewer textViewer, final IRegion hoverRegion) {
		String info = null;
		try {
			info = TemplateManager.getElementDescription(textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength()),
					this.properties);
		} catch (final Exception e) {/* ignore */
		}

		return info;
	}

	/*
	 * @see org.eclipse.jface.text.ITextHover#getHoverRegion(org.eclipse.jface.text.ITextViewer, int)
	 */
	@Override
	public IRegion getHoverRegion(final ITextViewer textViewer, final int offset) {
		IRegion region = null;
		try {
			region = getElementRegion(textViewer.getDocument(), offset);
		} catch (final Exception e) {/* ignore */
		}

		return region;
	}

	private IRegion getElementRegion(final IDocument document, final int offset) throws BadLocationException {
		if (offset == document.getLength()) {
			return null;
		}

		// get start of line
		final IRegion lineInfo = document.getLineInformationOfOffset(offset);
		final int lineOffset = lineInfo.getOffset();

		// find start of reference
		int referenceOffset = offset;
		for (; lineOffset <= referenceOffset; --referenceOffset) {
			if (document.getChar(referenceOffset) == DOLLAR) {
				break;
			}
		}
		if (referenceOffset < lineOffset) {
			return null;
		}

		// find reference
		final int length = lineInfo.getLength() - (referenceOffset - lineOffset);
		this.referenceScanner.setRange(document, referenceOffset, length);
		final IToken token = this.referenceScanner.nextToken();
		if (!token.equals(FastCodeColorManager.getToken("REFERENCE"))) {
			return null; // invalid reference
		}

		// extract element / function
		final int elementStart = getElementStart(document, offset);
		final int elementEnd = getElementEnd(document, offset);
		if (elementStart < elementEnd) {
			return new Region(elementStart, elementEnd - elementStart);
		}

		return null;
	}

	private int getElementStart(final IDocument document, final int offset) throws BadLocationException {
		int elementStart = offset;
		for (; this.referenceScanner.getTokenOffset() <= elementStart; --elementStart) {
			final char ch = document.getChar(elementStart);
			if (ch == DOLLAR || ch == NOT_CHAR || ch == LEFT_CURL_CHAR || ch == DOT_CHAR) {
				++elementStart;
				break;
			}
		}
		return elementStart;
	}

	private int getElementEnd(final IDocument document, final int offset) throws BadLocationException {
		int elementEnd = offset;
		final int referenceEnd = this.referenceScanner.getTokenOffset() + this.referenceScanner.getTokenLength();
		for (; elementEnd < referenceEnd; ++elementEnd) {
			final char ch = document.getChar(elementEnd);
			if (ch == NOT_CHAR || ch == LEFT_CURL_CHAR || ch == RIGHT_CURL_CHAR || ch == DOT_CHAR) {
				break;
			}
		}
		return elementEnd;
	}
}
