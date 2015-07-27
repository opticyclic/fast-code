package org.fastcode.templates.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.fastcode.templates.viewer.TemplateFieldEditor.ErrorAnnotation;

public class FastCodeAnnotationHover implements IAnnotationHover, ITextHover {

	private final ISourceViewer	sourceViewer;

	public FastCodeAnnotationHover(final ISourceViewer sourceViewer) {
		this.sourceViewer = sourceViewer;
	}

	@Override
	public String getHoverInfo(final ISourceViewer sourceViewer, final int lineNumber) {
		//return getHoverInfoInternal(lineNumber, -1);

		final Object fAnnotationModel = sourceViewer.getAnnotationModel();
		final Iterator ite = ((IAnnotationModel) fAnnotationModel).getAnnotationIterator();

		final ArrayList all = new ArrayList();
		final List<Integer> lineNoList = new ArrayList<Integer>();
		while (ite.hasNext()) {
			final Annotation a = (Annotation) ite.next();
			if (a instanceof ErrorAnnotation) {
				//lineNoList.add(new Integer(((ErrorAnnotation) a).getLine()));
				if (((ErrorAnnotation) a).getLine() == lineNumber) {
					all.add(((ErrorAnnotation) a).getText());
				}
			}
		}

		final StringBuffer total = new StringBuffer();
		//need to check this logic
		for (int x = 0; x < all.size(); x++) {
			final String str = (String) all.get(x);
			total.append(" " + str + (x == all.size() - 1 ? "" : "\n"));
		}

		return total.toString();
	}

	@Override
	public String getHoverInfo(final ITextViewer textViewer, final IRegion hoverRegion) {
		int lineNumber;
		try {
			lineNumber = this.sourceViewer.getDocument().getLineOfOffset(hoverRegion.getOffset());
		} catch (final BadLocationException e) {
			return null;
		}
		return getHoverInfoInternal(lineNumber, hoverRegion.getOffset());
	}

	@Override
	public IRegion getHoverRegion(final ITextViewer textViewer, final int offset) {
		final Point selection = textViewer.getSelectedRange();
		if (selection.x <= offset && offset < selection.x + selection.y) {
			return new Region(selection.x, selection.y);
		}
		return new Region(offset, 0);
	}

	private String getHoverInfoInternal(final int lineNumber, final int offset) {
		final IAnnotationModel model = this.sourceViewer.getAnnotationModel();
		final List messages = new ArrayList();

		final Iterator iterator = model.getAnnotationIterator();
		while (iterator.hasNext()) {
			final Annotation annotation = (Annotation) iterator.next();
			if (!(annotation instanceof MarkerAnnotation)) {
				continue;
			}
			final MarkerAnnotation mAnno = (MarkerAnnotation) annotation;
			final int start = model.getPosition(mAnno).getOffset();
			final int end = start + model.getPosition(mAnno).getLength();

			if (offset > 0 && !(start <= offset && offset <= end)) {
				continue;
			}
			try {
				if (lineNumber != this.sourceViewer.getDocument().getLineOfOffset(start)) {
					continue;
				}
			} catch (final Exception x) {
				continue;
			}
			messages.add(mAnno.getText().trim());
		}
		return formatInfo(messages);
	}

	private StringBuffer	buffer;

	private String formatInfo(final List messages) {
		this.buffer = new StringBuffer();
		if (messages.size() > 1) {
			this.buffer.append("Multiple markers at this line\n");
			final Iterator e = messages.iterator();
			while (e.hasNext()) {
				splitInfo("- " + e.next() + "\n");
			}
		} else if (messages.size() == 1) {
			splitInfo((String) messages.get(0));
		}
		return this.buffer.toString();
	}

	private String splitInfo(String message) {
		String prefix = "";
		int pos;
		do {
			pos = message.indexOf(" ", 60);
			if (pos > -1) {
				this.buffer.append(prefix + message.substring(0, pos) + "\n");
				message = message.substring(pos);
				prefix = "  ";
			} else {
				this.buffer.append(prefix + message);
			}
		} while (pos > -1);
		return this.buffer.toString();
	}
}
