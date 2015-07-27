package org.fastcode.templates.viewer;

import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationAccess;
import org.eclipse.jface.text.source.IAnnotationAccessExtension;
import org.eclipse.jface.text.source.ImageUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.fastcode.templates.viewer.TemplateFieldEditor.ErrorAnnotation;

class FastCodeAnnotationAccessHelper implements IAnnotationAccess, IAnnotationAccessExtension {
	@Override
	public Object getType(final Annotation annotation) {
		return annotation.getType();
	}

	@Override
	public boolean isMultiLine(final Annotation annotation) {
		return true;
	}

	@Override
	public boolean isTemporary(final Annotation annotation) {
		return !annotation.isPersistent();
	}

	@Override
	public String getTypeLabel(final Annotation annotation) {
		if (annotation instanceof ErrorAnnotation) {
			return "Errors";
		}

		return null;
	}

	@Override
	public int getLayer(final Annotation annotation) {
		if (annotation instanceof ErrorAnnotation) {
			return ((ErrorAnnotation) annotation).getLayer();
		}

		return 0;
	}

	@Override
	public void paint(final Annotation annotation, final GC gc, final Canvas canvas, final Rectangle bounds) {
		ImageUtilities.drawImage(((ErrorAnnotation) annotation).getImage(), gc, canvas, bounds, SWT.CENTER, SWT.TOP);
	}

	@Override
	public boolean isPaintable(final Annotation annotation) {
		if (annotation instanceof ErrorAnnotation) {
			return ((ErrorAnnotation) annotation).getImage() != null;
		}

		return false;
	}

	@Override
	public boolean isSubtype(final Object annotationType, final Object potentialSupertype) {
		if (annotationType.equals(potentialSupertype)) {
			return true;
		}

		return false;

	}

	@Override
	public Object[] getSupertypes(final Object annotationType) {
		return new Object[0];
	}

}


