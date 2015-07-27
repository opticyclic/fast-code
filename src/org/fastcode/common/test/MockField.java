package org.fastcode.common.test;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

public class MockField implements IField {

	private final String	name;
	private final String	type;
	private final String	signatureType;

	public MockField(final String name, final String type, final String signatureType) {
		super();
		this.name = name;
		this.type = type;
		this.signatureType = signatureType;
	}

	@Override
	public Object getConstant() throws JavaModelException {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public String getElementName() {

		return this.name;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeSignature() throws JavaModelException {
		return this.signatureType;
	}

	@Override
	public boolean isEnumConstant() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String[] getCategories() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IClassFile getClassFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICompilationUnit getCompilationUnit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getDeclaringType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFlags() throws JavaModelException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ISourceRange getJavadocRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISourceRange getNameRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOccurrenceCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IType getType(final String arg0, final int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeRoot getTypeRoot() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isBinary() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IJavaElement getAncestor(final int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAttachedJavadoc(final IProgressMonitor arg0) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getCorrespondingResource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getElementType() {
		return 0;
	}

	@Override
	public String getHandleIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaModel getJavaModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaProject getJavaProject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOpenable getOpenable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPath getPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement getPrimaryElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IResource getUnderlyingResource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStructureKnown() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getAdapter(final Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSource() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISourceRange getSourceRange() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void copy(final IJavaElement arg0, final IJavaElement arg1, final String arg2, final boolean arg3, final IProgressMonitor arg4)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(final boolean arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void move(final IJavaElement arg0, final IJavaElement arg1, final String arg2, final boolean arg3, final IProgressMonitor arg4)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rename(final String arg0, final boolean arg1, final IProgressMonitor arg2) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public IJavaElement[] getChildren() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IAnnotation getAnnotation(final String arg0) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public IAnnotation[] getAnnotations() throws JavaModelException {
		// TODO Auto-generated method stub
		return new IAnnotation[] {};

	}

	/**
	 *
	 * getter method for name
	 * @return
	 *
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * getter method for type
	 * @return
	 *
	 */
	public String getType() {
		return this.type;
	}

	/**
	 *
	 * getter method for signatureType
	 * @return
	 *
	 */
	public String getSignatureType() {
		return this.signatureType;
	}

}
