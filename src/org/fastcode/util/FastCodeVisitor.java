package org.fastcode.util;

import static org.fastcode.util.SourceUtil.getFQNameFromFieldTypeName;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.FastCodeType;

public class FastCodeVisitor extends ASTVisitor {

	MethodDeclaration		methodDecln		= null;
	String					methodName;
	List<FastCodeReturn>	fastCodeReturns	= new ArrayList<FastCodeReturn>();
	ICompilationUnit		compilationUnit;
	boolean					isMethodVisited;
	private CompilationUnit compUnit;
	final List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();
	final List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
	boolean					isFieldVisited;

	public FastCodeVisitor(final String methodName, final ICompilationUnit compilationUnit) {
		super();
		this.methodName = methodName;
		this.compilationUnit = compilationUnit;
		this.isMethodVisited = false;
	}

	public FastCodeVisitor(final CompilationUnit compilationUnit) {
		super();
		this.compUnit = compilationUnit;
	}

	@Override
	public boolean visit(final MethodDeclaration node) {

		if (isEmpty(this.methodName)) {
			 this.methods.add(node);
		} else if (node.getName().toString().equals(this.methodName)) {
			this.methodDecln = node;
			this.isMethodVisited = true;

		}
		return super.visit(node);
	}

	@Override
	public void endVisit(final MethodDeclaration node) {
		this.isMethodVisited = false;
	}

	@Override
	public boolean visit(final VariableDeclarationStatement node) {
		if (!this.isMethodVisited) {
			return false;
		}
		for (int i = 0; i < node.fragments().size(); ++i) {
			final VariableDeclarationFragment frag = (VariableDeclarationFragment) node.fragments().get(i);
			try {
				this.fastCodeReturns.add(new FastCodeReturn(frag.getName().getFullyQualifiedName(), new FastCodeType(
						getFQNameFromFieldTypeName(node.getType().toString(), this.compilationUnit))));
			} catch (final Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
			System.out.println("Fragment: " + node.getType() + " " + frag.getName());
		}

		return super.visit(node);
	}

	public List<FastCodeReturn> getFastCodeReturns() {
		return this.fastCodeReturns;
	}

	public MethodDeclaration getMethodDecln() {
		return this.methodDecln;
	}

	/**
	 *
	 * getter method for compUnit
	 * @return
	 *
	 */
	public CompilationUnit getCompUnit() {
		return this.compUnit;
	}

	/**
	 *
	 * setter method for compUnit
	 * @param compUnit
	 *
	 */
	public void setCompUnit(final CompilationUnit compUnit) {
		this.compUnit = compUnit;
	}

	public List<MethodDeclaration> getMethods() {
		return this.methods;
	}

	@Override
	public boolean visit(final FieldDeclaration node) {
		this.fields.add(node);
		this.isFieldVisited = true;
		return super.visit(node);
	}

	@Override
	public void endVisit(final FieldDeclaration node) {
		this.isFieldVisited = false;
		super.endVisit(node);
	}

	public List<FieldDeclaration> getFields() {
		return this.fields;
	}

}
