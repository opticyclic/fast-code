package org.fastcode.templates.viewer;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_TEMPLATE_PREFIX;
import static org.fastcode.util.SourceUtil.getImage;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationBarHoverManager;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.AnnotationPainter;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.OverviewRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.FIELDS;
import org.fastcode.common.FastCodeConstants.FIRST_TEMPLATE;
import org.fastcode.common.FastCodeConstants.SECOND_TEMPLATE;
import org.fastcode.common.FastCodeFont;
import org.fastcode.templates.contentassist.FCTagAssistProcessor;
import org.fastcode.templates.contentassist.ITemplateContentAssistant;
import org.fastcode.templates.contentassist.TemplateAssistProcessor;
import org.fastcode.templates.rules.FastCodeRulesContext;
import org.fastcode.templates.rules.FastCodeTemplateCodeScanner;
import org.fastcode.templates.rules.IRulesStrategy;
import org.fastcode.templates.rules.ParameterRulesContext;

public class TemplateFieldEditor extends FieldEditor {

	FastCodeRulesContext					ctx						= new FastCodeRulesContext();
	private Composite					parent = null;
	private FastCodeTemplateViewer			fastCodeTemplateViewerField;
	public static final int					VALIDATE_ON_KEY_STROKE	= 0;

	public static final int					VALIDATE_ON_FOCUS_LOST	= 1;

	public static int						UNLIMITED				= -1;

	private int						style = 0;

	final IPreferenceStore					store					= new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

	int										validateStrategie		= VALIDATE_ON_KEY_STROKE;
	private String							oldValue;

	ITemplateContentAssistant[]				assistants				= null;
	ITemplateContentAssistant[]				fcTagAssistants			= null;
	IRulesStrategy[]						ruleStrategies			= null;
	IRulesStrategy[]						fcTagRuleStrategies		= null;
	ITextHover								textHover				= null;
	IAutoEditStrategy[]						autoEditStrategies		= null;
	TemplateAssistProcessor					templateAssistProcessor	= new TemplateAssistProcessor();
	FCTagAssistProcessor					fcTagAssistProcessor	= new FCTagAssistProcessor();
	Map<FIRST_TEMPLATE, SECOND_TEMPLATE>	templateItemsMap		= new HashMap<FIRST_TEMPLATE, SECOND_TEMPLATE>();
	public static String					ERROR_TYPE				= "error.type";
	public static Image						ERROR_IMAGE;
	public static final RGB					ERROR_RGB				= new RGB(255, 0, 0);
	private final AnnotationModel			fastCodeAnnotationModel	= new AnnotationModel();

	public TemplateFieldEditor(final String templateName, final String labelText, final Composite parent, final String templateType,
			final FIELDS field, final int style) {

		init(templateName, labelText);
		this.parent = parent;
		this.style = style;
		// createLabel(labelText);
		createField(templateName, templateType, labelText, field);

	}

	public TemplateFieldEditor() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * returns the content of the template field
	 */
	public String getStringValue() {

		return this.fastCodeTemplateViewerField.getDocument().get();
	}

	public IDocument getDocument() {
		return this.fastCodeTemplateViewerField.getDocument();
	}

	/*
	 * @param template name creates an new template field
	 */
	private void createField(final String templateName, final String templateType, final String labelText, final FIELDS field) {

		getLabelControl(this.parent);

		/*final IOperationHistory operationHistory = OperationHistoryFactory
				.getOperationHistory();
		final TextOperations operation = new TextOperations(this.parent
				.getShell());
		operationHistory.add(operation);*/

		CompositeRuler compositeRuler;
		OverviewRuler fOverviewRuler = null;
		ERROR_IMAGE = getImage("error_obj.gif"); //PlatformUI.getWorkbench().getSharedImages().getImage("error_obj.gif");
		final IAnnotationAccess fAnnotationAccess = new FastCodeAnnotationAccessHelper();
		final AnnotationRulerColumn annotationRuler = new AnnotationRulerColumn(this.fastCodeAnnotationModel, 16, fAnnotationAccess);
		final ColorCache cc = new ColorCache();
		if (templateName.equals("snippet")) {
			///adding ruler to snippet dialog, stops everything from working
			//not sure how...so ignoring ruler for snippet dialog.
			compositeRuler = null;
		} else {
			compositeRuler = new CompositeRuler();
			fOverviewRuler = new OverviewRuler(fAnnotationAccess, 12, cc);

			compositeRuler.setModel(this.fastCodeAnnotationModel);
			fOverviewRuler.setModel(this.fastCodeAnnotationModel);

			compositeRuler.addDecorator(0, annotationRuler);
			annotationRuler.addAnnotationType(ERROR_TYPE);
			fOverviewRuler.addAnnotationType(ERROR_TYPE);
			fOverviewRuler.addHeaderAnnotationType(ERROR_TYPE);
			fOverviewRuler.setAnnotationTypeLayer(ERROR_TYPE, 3);
			fOverviewRuler.setAnnotationTypeColor(ERROR_TYPE, new Color(Display.getDefault(), ERROR_RGB));

			final LineNumberRulerColumn lnrc = new LineNumberRulerColumn();
			compositeRuler.addDecorator(1, lnrc);

			final FontData[] fD = this.parent.getFont().getFontData();
			compositeRuler.setFont(FastCodeFont.getLineNumberFont(fD[0].getName(), fD[0].getHeight()));
		}

		this.fastCodeTemplateViewerField = this.style == SWT.SINGLE ? new FastCodeTemplateViewer(this.parent, null, null, SWT.BORDER
				| SWT.RESIZE) : new FastCodeTemplateViewer(this.parent, compositeRuler, fOverviewRuler, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.MULTI | SWT.RESIZE);
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		this.fastCodeTemplateViewerField.getControl().setLayoutData(gd);

		// hover manager that shows text when we hover
		final AnnotationBarHoverManager fAnnotationHoverManager = new AnnotationBarHoverManager(compositeRuler,
				this.fastCodeTemplateViewerField, new FastCodeAnnotationHover(this.fastCodeTemplateViewerField), new AnnotationConfiguration());
		fAnnotationHoverManager.install(annotationRuler.getControl());

		// to paint the annotations
		final AnnotationPainter ap = new AnnotationPainter(this.fastCodeTemplateViewerField, fAnnotationAccess);
		ap.addAnnotationType(ERROR_TYPE);//, null); //addAnnotationType(ERROR_TYPE);
		ap.setAnnotationTypeColor(ERROR_TYPE, new Color(Display.getDefault(), ERROR_RGB));

		// this will draw the squigglies under the text
		this.fastCodeTemplateViewerField.addPainter(ap);

		configureFastCodeTemplateViewer(templateType, labelText, field, cc);

		/*final Button button = new Button(this.parent, SWT.PUSH);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// IUndoableOperation operation = new TextOperations(View.this
				// .getViewSite().getShell());
				try {
					//operationHistory.execute(operation, null, null);
					operationHistory.undoOperation(operation, null, null);
				} catch (final ExecutionException e1) {
				}
			}
		});*/
		/*final ErrorAnnotation errorAnnotation = new ErrorAnnotation(1, "Learn how to spell \"text!\"");

		// lets underline the word "texst"
		this.fastCodeAnnotationModel.addAnnotation(errorAnnotation, new Position(10, 2));

		final ErrorAnnotation errorAnnotation1 = new ErrorAnnotation(6, "changed to next line");

		// lets underline the word "texst"
		this.fastCodeAnnotationModel.addAnnotation(errorAnnotation1, new Position(134, 3));*/

		/*fOverviewRuler.update();
		compositeRuler.update();*/
		this.fastCodeTemplateViewerField.getTextWidget().addFocusListener(new  FocusListener() {

			@Override
			public void focusLost(final FocusEvent arg0) {
				//System.out.println("fastCodeTemplateViewerField focus lost");
				valueChanged();
				clearErrorMessage();
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				/*valueChanged();
				clearErrorMessage();*/
			}
		});

		this.fastCodeTemplateViewerField.getTextWidget().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(final KeyEvent arg0) {
				//System.out.println("fastCodeTemplateViewerField key released");
				valueChanged();
				clearErrorMessage();
			}

			@Override
			public void keyPressed(final KeyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		switch (this.validateStrategie) {
		case VALIDATE_ON_KEY_STROKE:
			this.fastCodeTemplateViewerField.getControl().addKeyListener(new KeyAdapter() {

				/* (non-Javadoc)
				 * @see org.eclipse.swt.events.KeyAdapter#keyReleased(org.eclipse.swt.events.KeyEvent)
				 */
				@Override
				public void keyReleased(final KeyEvent e) {
					//System.out.println(e.keyCode);
					//e.
					valueChanged();
					clearErrorMessage();
				}
			});
			this.fastCodeTemplateViewerField.getControl().addFocusListener(new FocusAdapter() {
				// Ensure that the value is checked on focus loss in case we
				// missed a keyRelease or user hasn't released key.
				// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=214716
				@Override
				public void focusLost(final FocusEvent e) {
					valueChanged();
					clearErrorMessage();
				}
			});

			break;
		case VALIDATE_ON_FOCUS_LOST:
			this.fastCodeTemplateViewerField.getControl().addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(final KeyEvent e) {
					clearErrorMessage();
				}
			});
			this.fastCodeTemplateViewerField.getControl().addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(final FocusEvent e) {
					refreshValidState();
				}

				@Override
				public void focusLost(final FocusEvent e) {
					valueChanged();
					clearErrorMessage();
				}
			});
			break;
		default:
			Assert.isTrue(false, "Unknown validate strategy");//$NON-NLS-1$
		}

	}

	protected void valueChanged() {
//		System.out.println("In value changed");
		fireValueChanged(VALUE, this.oldValue, this.fastCodeTemplateViewerField.getDocument().get());

		/*try {
			RuntimeSingleton.parse(new StringReader(this.fastCodeTemplateViewerField.getDocument().get()), templateName);
		} catch (final ParseException pe) {
			pe.printStackTrace();
			if (pe.currentToken != null && pe.currentToken.next != null) {
				int lineNo = pe.currentToken.next.beginLine - 1;
				final int colNo = pe.currentToken.next.beginColumn - 1;
				int lineOffset = 0;
				int docLen = 0;
				try {
					final IRegion reg = this.fastCodeTemplateViewerField.getDocument().getLineInformation(lineNo);
					docLen = this.fastCodeTemplateViewerField.getDocument().getLineLength(lineNo);
					lineOffset = this.fastCodeTemplateViewerField.getDocument().getLineOffset(lineNo);
					final int lineOfOffset = this.fastCodeTemplateViewerField.getDocument().getLineOfOffset(lineNo);
					final String lineContent = this.fastCodeTemplateViewerField.getDocument().get(reg.getOffset(), reg.getLength());
					System.out.println(reg);
					System.out.println(lineOffset);
					System.out.println(docLen);
					System.out.println("text-" +reg.toString());
					System.out.println("line content-" + this.fastCodeTemplateViewerField.getDocument().get(lineOffset - docLen, docLen));
					System.out.println(lineOffset - docLen);
					System.out.println("content-"+lineContent);
					if (isEmpty(lineContent)) {
						lineOffset = this.fastCodeTemplateViewerField.getDocument().getLineOffset(lineNo - 1);
						lineNo = lineNo-1;

					}
				} catch (final BadLocationException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				final ErrorAnnotation errorAnnotation = new ErrorAnnotation(lineNo, pe.getLocalizedMessage());

				addAnnotation(errorAnnotation, new Position(lineOffset, 5));
				//MessageDialog.openError(new Shell(), "Error", "Error in template body " + pe.getLocalizedMessage());
				//return;
			}
		}*/

	}

	public Control getControl() {
		return this.fastCodeTemplateViewerField.getControl();
	}

	public void setText(final String txt) {

		this.fastCodeTemplateViewerField.setDocument(new Document(txt));
	}

	public void setEmptyStringAllowed(final boolean emptyStringAllowed) {
	}

	public void setEditable(final boolean edit) {
		this.fastCodeTemplateViewerField.setEditable(edit);
	}

	@Override
	public void setEnabled(final boolean value, final Composite parent) {

		super.setEnabled(value, parent);
		this.fastCodeTemplateViewerField.getControl().setEnabled(value);
	}

	public void setLayout(final GridData gd) {
		this.fastCodeTemplateViewerField.getControl().setLayoutData(gd);
	}

	/*
	 * apply content assist and coloring rules for the template field
	 */
	public void configureFastCodeTemplateViewer(final String templateType, final String labelText, final FIELDS field, final ColorCache cc) {

		if (field.equals(FIELDS.ADDITIONAL_PARAMETER)) {

			final ParameterRulesContext pc = new ParameterRulesContext();
			this.assistants = pc.getTemplateContentAssistants(null);
			this.autoEditStrategies = pc.getTemplateAutoEditStrategies();
			this.textHover = pc.getTemplateTextHover(null);
			this.ruleStrategies = pc.getParameterRuleStrategies();
		} else {

			this.assistants = this.ctx.getTemplateContentAssistants(null);
			this.fcTagAssistants = this.ctx.getFCTagContentAssistants(null);
			this.ruleStrategies = this.ctx.getTemplateRuleStrategies();
			this.fcTagRuleStrategies = this.ctx.getFCTagRuleStrategies();
			if (templateType.equals(P_DATABASE_TEMPLATE_PREFIX)) {
				this.assistants = this.ctx.getDBTemplateContentAssistants(null);
			}
			/*if (templateType.equals(P_FILE_TEMPLATE_PREFIX)) {//commented as Sudha told not required
				this.assistants = this.ctx.getFileTemplateContentAssistants(null);
			}*/

			this.textHover = this.ctx.getTemplateTextHover(null);
			this.autoEditStrategies = this.ctx.getTemplateAutoEditStrategies();
		}

		this.templateAssistProcessor.setAssistants(this.assistants);
		//this.templateAssistProcessor.setFcMethodassistants(this.fcTagAssistants);
		this.fcTagAssistProcessor.setAssistants(this.fcTagAssistants);

		final SourceViewerConfiguration configuration = new FastCodeTemplateViewerConfiguration(new FastCodeTemplateCodeScanner(
				this.ruleStrategies), new FastCodeTemplateCodeScanner(this.fcTagRuleStrategies), this.textHover, this.autoEditStrategies,
				this.templateAssistProcessor, this.fcTagAssistProcessor, cc);
		this.fastCodeTemplateViewerField.configure(configuration);

	}

	public void updateSourceConfigurations(final FIRST_TEMPLATE firstTemplate, final SECOND_TEMPLATE secondTemplate) {

		this.templateItemsMap.clear();
		this.templateItemsMap.put(firstTemplate, secondTemplate);
		this.assistants = this.ctx.getTemplateContentAssistants(this.templateItemsMap);
		this.fcTagAssistants = this.ctx.getFCTagContentAssistants(this.templateItemsMap);
		this.templateAssistProcessor.setAssistants(this.assistants);
		//this.templateAssistProcessor.setFcMethodassistants(this.fcTagAssistants);
		this.fcTagAssistProcessor.setAssistants(this.fcTagAssistants);
		final SourceViewerConfiguration configuration = new FastCodeTemplateViewerConfiguration(new FastCodeTemplateCodeScanner(
				this.ruleStrategies), new FastCodeTemplateCodeScanner(this.fcTagRuleStrategies), this.textHover, this.autoEditStrategies,
				this.templateAssistProcessor, this.fcTagAssistProcessor, null);
		this.fastCodeTemplateViewerField.configure(configuration);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	@Override
	protected void adjustForNumColumns(final int numColumns) {
		final GridData gd = (GridData) this.fastCodeTemplateViewerField.getControl().getLayoutData();
		gd.horizontalSpan = numColumns - 1;
		// We only grab excess space if we have to
		// If another field editor has more columns then
		// we assume it is setting the width.
		// gd.grabExcessHorizontalSpace = gd.horizontalSpan == 1;
		gd.grabExcessVerticalSpace = true;
		gd.heightHint = 100;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.jface.preference.FieldEditor#doFillIntoGrid(org.eclipse.swt
	 * .widgets.Composite, int)
	 */
	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		// super.doFillIntoGrid(parent, numColumns);
		adjustForNumColumns(numColumns);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {
		if (this.fastCodeTemplateViewerField != null) {
			getPreferenceName();
			final String value = getPreferenceStore().getString(getPreferenceName());
			this.fastCodeTemplateViewerField.setDocument(new Document(value));
			this.oldValue = value;
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	@Override
	protected void doLoadDefault() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	@Override
	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(), this.fastCodeTemplateViewerField.getDocument().get());

	}

	@Override
	public int getNumberOfControls() {
		// TODO Auto-generated method stub
		return 2;
	}

	/*class TextOperations extends AbstractOperation {
		Shell shell;

		public TextOperations(final Shell shell) {
			super("Readme Operation");
			this.shell = shell;
		}

		@Override
		public IStatus execute(final IProgressMonitor monitor, final IAdaptable info) {
			MessageDialog.openInformation(this.shell, "Readme_Editor",
					"View_Action_executed");
			return Status.OK_STATUS;
		}

		@Override
		public IStatus undo(final IProgressMonitor monitor, final IAdaptable info) {
			MessageDialog.openInformation(this.shell, "Readme_Editor",
					"Undoing view action");
			return Status.OK_STATUS;
		}

		@Override
		public IStatus redo(final IProgressMonitor monitor, final IAdaptable info) {
			MessageDialog.openInformation(this.shell, "Readme_Editor",
					"Redoing view action");
			return Status.OK_STATUS;
		}
	}*/

	public void addAnnotation(final ErrorAnnotation errorAnnotation, final Position pos) {
		this.fastCodeAnnotationModel.addAnnotation(errorAnnotation, pos);
		//final Object hoverInfo = this.fastCodeTemplateViewerField.getCurrentAnnotationHover().getHoverInfo(this.fastCodeTemplateViewerField, pos.offset);
	}

	public void removeAllAnnotations() {
		this.fastCodeAnnotationModel.removeAllAnnotations();
		//final Object hoverInfo = this.fastCodeTemplateViewerField.getCurrentAnnotationHover().getHoverInfo(this.fastCodeTemplateViewerField, pos.offset);
	}


	class ColorCache implements ISharedTextColors {
		@Override
		public Color getColor(final RGB rgb) {
			return new Color(Display.getDefault(), rgb);
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}
	}

	public class ErrorAnnotation extends Annotation {
		private final IMarker	marker;
		private String			text;
		private int				line;
		private Position		position;

		public ErrorAnnotation(final IMarker marker) {
			this.marker = marker;
		}

		public ErrorAnnotation(final int line, final String text) {
			super(ERROR_TYPE, true, null);
			this.marker = null;
			this.line = line;
			this.text = text;
		}

		public IMarker getMarker() {
			return this.marker;
		}

		public int getLine() {
			return this.line;
		}

		@Override
		public String getText() {
			return this.text;
		}

		public Image getImage() {
			return ERROR_IMAGE;
		}

		public int getLayer() {
			return 3;
		}

		@Override
		public String getType() {
			return ERROR_TYPE;
		}

		public Position getPosition() {
			return this.position;
		}

		public void setPosition(final Position position) {
			this.position = position;
		}
	}

	class AnnotationConfiguration implements IInformationControlCreator {
		@Override
		public IInformationControl createInformationControl(final Shell shell) {
			return new DefaultInformationControl(shell);
		}
	}
}

