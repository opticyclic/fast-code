package org.fastcode.common.test;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;

public class MockMethod implements IMethod {

	private final String	mySignature;
	private final String	name;

	public MockMethod(final String mySignature, final String name) {
		super();
		this.mySignature = mySignature;
		this.name = name;
	}

	@Override
	public IMemberValuePair getDefaultValue() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getExceptionTypes() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfParameters() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getParameterNames() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParameterTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getRawParameterNames() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReturnType() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSignature() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeParameter getTypeParameter(final String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getTypeParameterSignatures() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConstructor() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMainMethod() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSimilar(final IMethod arg0) {
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
		// TODO Auto-generated method stub
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
		return null;
	}

	/**
	 *
	 * getter method for mySignature
	 * @return
	 *
	 */
	public String getMySignature() {
		return this.mySignature;
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

	@Override
	public ILocalVariable[] getParameters() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLambdaMethod() {
		// TODO Auto-generated method stub
		return false;
	}

}
