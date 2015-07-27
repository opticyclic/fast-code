package org.fastcode.common.test;

import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

public class MockType implements IType {

	private final String	strField;
	private IField			field;
	private IMethod			method;

	public MockType(final String strField, final IField field) {
		super();
		this.strField = strField;
		this.field = field;
	}

	public MockType(final String strField, final IMethod method) {
		super();
		this.strField = strField;
		this.method = method;
	}

	@Override
	public void codeComplete(final char[] arg0, final int arg1, final int arg2, final char[][] arg3, final char[][] arg4, final int[] arg5,
			final boolean arg6, final ICompletionRequestor arg7) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(final char[] arg0, final int arg1, final int arg2, final char[][] arg3, final char[][] arg4, final int[] arg5,
			final boolean arg6, final CompletionRequestor arg7) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(final char[] arg0, final int arg1, final int arg2, final char[][] arg3, final char[][] arg4, final int[] arg5,
			final boolean arg6, final ICompletionRequestor arg7, final WorkingCopyOwner arg8) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(final char[] arg0, final int arg1, final int arg2, final char[][] arg3, final char[][] arg4, final int[] arg5,
			final boolean arg6, final CompletionRequestor arg7, final IProgressMonitor arg8) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(final char[] arg0, final int arg1, final int arg2, final char[][] arg3, final char[][] arg4, final int[] arg5,
			final boolean arg6, final CompletionRequestor arg7, final WorkingCopyOwner arg8) throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public void codeComplete(final char[] arg0, final int arg1, final int arg2, final char[][] arg3, final char[][] arg4, final int[] arg5,
			final boolean arg6, final CompletionRequestor arg7, final WorkingCopyOwner arg8, final IProgressMonitor arg9)
			throws JavaModelException {
		// TODO Auto-generated method stub

	}

	@Override
	public IField createField(final String arg0, final IJavaElement arg1, final boolean arg2, final IProgressMonitor arg3)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return this.field;
	}

	@Override
	public IInitializer createInitializer(final String arg0, final IJavaElement arg1, final IProgressMonitor arg2)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod createMethod(final String arg0, final IJavaElement arg1, final boolean arg2, final IProgressMonitor arg3)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType createType(final String arg0, final IJavaElement arg1, final boolean arg2, final IProgressMonitor arg3)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod[] findMethods(final IMethod arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IJavaElement[] getChildrenForCategory(final String arg0) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IField getField(final String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IField[] getFields() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedName(final char arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFullyQualifiedParameterizedName() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInitializer getInitializer(final int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IInitializer[] getInitializers() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod getMethod(final String arg0, final String[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMethod[] getMethods() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPackageFragment getPackageFragment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSuperInterfaceNames() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getSuperInterfaceTypeSignatures() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSuperclassName() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSuperclassTypeSignature() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType getType(final String arg0) {
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
	public String getTypeQualifiedName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTypeQualifiedName(final char arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IType[] getTypes() throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAnnotation() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAnonymous() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClass() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnum() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInterface() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isLocal() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMember() throws JavaModelException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITypeHierarchy loadTypeHierachy(final InputStream arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(final IProgressMonitor arg0) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(final ICompilationUnit[] arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(final IWorkingCopy[] arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newSupertypeHierarchy(final WorkingCopyOwner arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(final IProgressMonitor arg0) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(final IJavaProject arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(final ICompilationUnit[] arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(final IWorkingCopy[] arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(final WorkingCopyOwner arg0, final IProgressMonitor arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITypeHierarchy newTypeHierarchy(final IJavaProject arg0, final WorkingCopyOwner arg1, final IProgressMonitor arg2)
			throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] resolveType(final String arg0) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[][] resolveType(final String arg0, final WorkingCopyOwner arg1) throws JavaModelException {
		// TODO Auto-generated method stub
		return null;
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
	 * getter method for strField
	 * @return
	 *
	 */
	public String getStrField() {
		return this.strField;
	}

	public boolean isLambda() {
		// TODO Auto-generated method stub
		return false;
	}

}
