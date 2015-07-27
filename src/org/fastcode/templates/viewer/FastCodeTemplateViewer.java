package org.fastcode.templates.viewer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.CursorLinePainter;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationBarHoverManager;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.common.FastCodeColor;
import org.fastcode.preferences.OpenEditTemplateDialog;
import org.fastcode.templates.rules.FastCodeTemplatePartitionScanner;
import org.fastcode.templates.rules.FastCodeTemplatePartitions;

/**
 * Viewer for fastcode templates.
 */
public class FastCodeTemplateViewer extends SourceViewer {

	public static String	ERROR_TYPE			= "error.type";
	public static Image		ERROR_IMAGE;
	public static final RGB	ERROR_RGB			= new RGB(255, 0, 0);
	private AnnotationModel	fAnnotationModel	= null;

	/*CompositeRuler commpositeRuler = null;
	OverviewRuler overviewRuler = null;*/

	/**
	 * Instantiates a new template viewer.
	 *
	 * @param parent the parent
	 * @param styles the styles
	 */
	public FastCodeTemplateViewer(final Composite parent, final IVerticalRuler commpositeRuler, final IOverviewRuler overviewRuler,
			final int styles) {

		super(parent, commpositeRuler, overviewRuler, true, styles);

		//final IAnnotationAccess fAnnotationAccess = new AnnotationMarkerAccess();

		if (commpositeRuler != null) {
			this.fAnnotationModel = (AnnotationModel) commpositeRuler.getModel();
		}
		final Font font = JFaceResources.getFont(PreferenceConstants.EDITOR_TEXT_FONT);
		getTextWidget().setFont(font);

		if (commpositeRuler != null) {
			final CursorLinePainter curLinePainter = new CursorLinePainter(this);
			curLinePainter.setHighlightColor(FastCodeColor.getLineHighlightColor());
			final ITextViewerExtension2 extension = this;
			extension.addPainter(curLinePainter);
		}

		IUndoManager undoManager = getUndoManager();
		if (undoManager != null) {
			undoManager.reset();
		} else {
			undoManager = new TextViewerUndoManager(25);
			setUndoManager(undoManager);
			undoManager.connect(this);
		}

		getTextWidget().addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(final TraverseEvent event) {
				if (event.detail == SWT.TRAVERSE_TAB_NEXT) {
					if (FastCodeTemplateViewer.this.canDoOperation(ITextOperationTarget.SHIFT_RIGHT)) {
						FastCodeTemplateViewer.this.doOperation(ITextOperationTarget.SHIFT_RIGHT);
					}
					event.doit = false;
				} else if (event.detail == SWT.TRAVERSE_TAB_PREVIOUS) {
					if (FastCodeTemplateViewer.this.canDoOperation(ITextOperationTarget.SHIFT_LEFT)) {
						FastCodeTemplateViewer.this.doOperation(ITextOperationTarget.SHIFT_LEFT);
					}
					event.doit = false;
				}
			}
		});

		getTextWidget().addVerifyKeyListener(new VerifyKeyListener() {
			@Override
			public void verifyKey(final VerifyEvent verifyEvent) {
				if (verifyEvent.keyCode == SWT.TAB && FastCodeTemplateViewer.this.getSelectedRange().y > 0) {
					verifyEvent.doit = false;
				}
			}
		});
		getTextWidget().addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(final KeyEvent e) {
				/*if (commpositeRuler != null) {
					valueChanged();
				}*/
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				if (isUndoKeyPress(e)) {
					FastCodeTemplateViewer.this.doOperation(ITextOperationTarget.UNDO);
				} else if (isRedoKeyPress(e)) {
					FastCodeTemplateViewer.this.doOperation(ITextOperationTarget.REDO);
				} else if (isCopyKeyPress(e)) {
					FastCodeTemplateViewer.this.doOperation(ITextOperationTarget.COPY);
				} else if (isCutKeyPress(e)) {
					FastCodeTemplateViewer.this.doOperation(ITextOperationTarget.CUT);
				} else if (isPasteKeyPress(e)) {
					FastCodeTemplateViewer.this.doOperation(ITextOperationTarget.PASTE);
				} else if (isFormatKeyPress(e)) {
					FastCodeTemplateViewer.this.doOperation(ISourceViewer.FORMAT);
				}
			}

			private boolean isRedoKeyPress(final KeyEvent e) {
				return (e.stateMask & SWT.CONTROL) > 0 && e.keyCode == 'y' || e.keyCode == 'Y';
			}

			private boolean isUndoKeyPress(final KeyEvent e) {
				return (e.stateMask & SWT.CONTROL) > 0 && e.keyCode == 'z' || e.keyCode == 'Z';
			}

			private boolean isCopyKeyPress(final KeyEvent e) {
				return (e.stateMask & SWT.CONTROL) > 0 && e.keyCode == 'c' || e.keyCode == 'C';
			}

			private boolean isCutKeyPress(final KeyEvent e) {
				return (e.stateMask & SWT.CONTROL) > 0 && e.keyCode == 'x' || e.keyCode == 'X';
			}

			private boolean isPasteKeyPress(final KeyEvent e) {
				return (e.stateMask & SWT.CONTROL) > 0 && e.keyCode == 'p' || e.keyCode == 'P';
			}

			private boolean isFormatKeyPress(final KeyEvent e) {
				return (e.stateMask & SWT.CONTROL) > 0 && e.keyCode == 'f' || e.keyCode == 'F';
			}

		});

		/*getTextWidget().addFocusListener(new FocusListener() {

			public void focusLost(final FocusEvent arg0) {
				//OpenEditTemplateDialog oetd = new OpenEditTemplateDialog();
				//OpenEditTemplateDialog.this.setErrorMessage(null);
				if (commpositeRuler != null) {
					valueChanged();
				}
				System.out.println("focus lost");
			}

			public void focusGained(final FocusEvent arg0) {
				if (commpositeRuler != null) {
					valueChanged();
				}
				System.out.println("focus gained");

			}
		});*/

		/*getControl().addKeyListener(new KeyAdapter() {

			 (non-Javadoc)
			 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)

			@Override
			public void keyReleased(final KeyEvent e) {
				System.out.println(e.keyCode);
				clearErrorMessage();
			}
		});
		getControl().addFocusListener(new FocusAdapter() {
			// Ensure that the value is checked on focus loss in case we
			// missed a keyRelease or user hasn't released key.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
			@Override
			public void focusLost(final FocusEvent e) {
				clearErrorMessage();
			}
		});*/
	}

	protected void valueChanged() {
		final TemplateFieldEditor tfe = new TemplateFieldEditor();
		tfe.valueChanged();
	}

	/**
	      * Find the next range after the current
	      * selection.
	      */
	protected StyleRange findNextRange(final StyledText text) {
		final StyleRange[] ranges = text.getStyleRanges();
		final int currentSelectionEnd = text.getSelection().y;

		for (int i = 0; i < ranges.length; i++) {
			if (ranges[i].start >= currentSelectionEnd) {
				return ranges[i];
			}
		}
		return null;
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewer#setDocument(org.eclipse.jface.text.IDocument)
	 */
	@Override
	public void setDocument(final IDocument document) {

		if (document != null) {
			final Map<String, IDocumentPartitioner> partitioners = new HashMap<String, IDocumentPartitioner>();
			partitioners.put(FastCodeTemplatePartitions.TEMPLATE_PARTITIONING, new FastPartitioner(new FastCodeTemplatePartitionScanner(),
					new String[] { FastCodeTemplatePartitions.SINGLE_LINE_COMMENT, FastCodeTemplatePartitions.MULTI_LINE_COMMENT,
							FastCodeTemplatePartitions.FC_METHOD, FastCodeTemplatePartitions.FC_FIELD, FastCodeTemplatePartitions.FC_CLASS,
							FastCodeTemplatePartitions.FC_FILE, FastCodeTemplatePartitions.FC_PACKAGE,
							FastCodeTemplatePartitions.FC_FOLDER, FastCodeTemplatePartitions.FC_PROJECT,
							FastCodeTemplatePartitions.FC_MESSAGE, FastCodeTemplatePartitions.FC_EXIT,
							FastCodeTemplatePartitions.FC_IMPORT, FastCodeTemplatePartitions.FC_XML, FastCodeTemplatePartitions.FC_CLASSES,
							FastCodeTemplatePartitions.FC_FILES, FastCodeTemplatePartitions.FC_PROPERTY,
							FastCodeTemplatePartitions.FC_INFO, FastCodeTemplatePartitions.FC_SNIPPET /*,
																										FastCodeTemplatePartitions.SINGLE_LINE_JAVA_COMMENT,
																										FastCodeTemplatePartitions.MULTI_LINE_JAVA_COMMENT*/
					}));
			TextUtilities.addDocumentPartitioners(document, partitioners);

		}
		if (this.fAnnotationModel != null) {
			//this.fAnnotationModel.connect(document);
			super.setDocument(document, this.fAnnotationModel);
		} else {
			super.setDocument(document);
		}

	}

	@Override
	public void setHyperlinkPresenter(final IHyperlinkPresenter hyperlinkPresenter) throws IllegalStateException {
		//		if (this.fHyperlinkManager != null) {
		//			throw new IllegalStateException();
		//		}

		this.fHyperlinkPresenter = hyperlinkPresenter;

	}

}
