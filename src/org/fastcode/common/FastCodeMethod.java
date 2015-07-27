/**
 *
 */
package org.fastcode.common;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_VOID;
import static org.fastcode.util.SourceUtil.getFQNameFromFieldTypeName;
import static org.fastcode.util.StringUtil.parseType;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.Signature;

/**
 * @author Gautam
 *
 */
public final class FastCodeMethod extends FastCodeEntity {

	private FastCodeType				returnType;
	//private final String[] exceptions;
	private boolean						staticMethod;
	private List<FastCodeAnnotation>	annotations	= new ArrayList<FastCodeAnnotation>();
	private final List<FastCodeParam>	parameters	= new ArrayList<FastCodeParam>();
	private List<FastCodeType>			exceptions	= new ArrayList<FastCodeType>();
	private IMethod						method;

	public FastCodeMethod(final IMethod method) throws Exception {
		if (method == null || !method.exists()) {
			this.isEmpty = true;
			return;
		}
		final List<FastCodeAnnotation> annotations = new ArrayList<FastCodeAnnotation>();
		final List<FastCodeType> exptnFCType = new ArrayList<FastCodeType>();
		for (final IAnnotation annotation : method.getAnnotations()) {
			final List<Pair> parameterList = new ArrayList<Pair>();
			for (final IMemberValuePair memValPair : annotation.getMemberValuePairs()) {
				parameterList.add(new Pair(memValPair.getMemberName(), memValPair.getValue()));
			}
			if (method.getCompilationUnit() != null) {
				annotations.add(new FastCodeAnnotation(parameterList, new FastCodeType(getFQNameFromFieldTypeName(annotation.getElementName(),
					method.getCompilationUnit()))));
			}
		}

		String[] paramTypeFQNames = {};
		FastCodeType fastCodeReturnType = null;

		if (!getSignatureSimpleName(method.getReturnType()).equals(METHOD_RETURN_TYPE_VOID)) {
			fastCodeReturnType = parseType(getSignatureSimpleName(method.getReturnType()), method.getCompilationUnit());
		}
		if (method.getParameterTypes() != null && method.getParameterTypes().length > 0) {
			FastCodeType fastCodeType = null;
			final StringBuffer paramFQNameBuffer = new StringBuffer();
			for (final String parameter : method.getParameterTypes()) {
				String paramFQName = EMPTY_STR;
				if (method.getCompilationUnit() != null) {
					fastCodeType = parseType(getSignatureSimpleName(parameter), method.getCompilationUnit());
					paramFQName = getFQNameFromFieldTypeName(fastCodeType.getName(), method.getCompilationUnit());
				} else {
					paramFQName = Signature.getSignatureQualifier(parameter) + "." + getSignatureSimpleName(parameter);
				}
				paramFQNameBuffer.append(EMPTY_STR.equals(paramFQNameBuffer.toString()) ? paramFQName : COMMA + paramFQName);
			}
			paramTypeFQNames = paramFQNameBuffer.toString().split(COMMA);
		}
		for (final String exp : method.getExceptionTypes()) {
			final FastCodeType expFCType = new FastCodeType(getFQNameFromFieldTypeName(getSignatureSimpleName(exp),
					method.getCompilationUnit()));
			exptnFCType.add(expFCType);
		}

		this.name = method.getElementName();
		this.returnType = fastCodeReturnType;
		//this.exceptions = exceptions;
		this.exceptions = exptnFCType;
		this.staticMethod = false;
		/*if (annotations != null) {
			for (final FastCodeAnnotation annotation : annotations) {
				this.annotations.add(annotation);
			}
		}*/
		this.annotations = annotations;
		if (paramTypeFQNames != null && paramTypeFQNames.length > 0) {
			for (int i = 0; i < paramTypeFQNames.length; i++) {
				final FastCodeParam fastCodeParam = new FastCodeParam(method.getParameterNames()[i], new FastCodeType(paramTypeFQNames[i]));
				this.parameters.add(fastCodeParam);
			}
		}

	}

	/**
	 * @param name
	 */
	public FastCodeMethod(final String name) {
		this(name, null, null, false, null, null, null);
	}

	/**
	 * @param name
	 * @param returnType
	 * @param annotations
	 *
	 */
	public FastCodeMethod(final String methodName, final FastCodeType returnType, final List<FastCodeType> exceptions,
			final boolean staticMethod, final List<FastCodeAnnotation> annotations, final String[] parameterTypes,
			final String[] parameterNames) {
		this.name = methodName;
		this.returnType = returnType;
		//this.exceptions = exceptions;
		this.exceptions = exceptions;
		this.staticMethod = staticMethod;
		/*if (annotations != null) {
			for (final FastCodeAnnotation annotation : annotations) {
				this.annotations.add(annotation);
			}
		}*/
		this.annotations = annotations;
		if (parameterTypes != null && parameterTypes.length > 0) {
			for (int i = 0; i < parameterTypes.length; i++) {
				final FastCodeParam fastCodeParam = new FastCodeParam(parameterNames[i], new FastCodeType(parameterTypes[i]));
				this.parameters.add(fastCodeParam);
			}
		}
	}

	public String getSource() throws Exception {
		return this.method.getSource();
	}

	public String getSignature() throws Exception {
		return this.method.getSignature();
	}

	/**
	 *
	 * @return
	 */
	public List<FastCodeAnnotation> getAnnotations() {
		return this.annotations;
	}

	/**
	 *
	 * @return
	 */
	public FastCodeType getReturnType() {
		return this.returnType;
	}

	/**
	 *
	 * @return
	 */
	/*
	public String[] getExceptions() {
	return this.exceptions;
	}
	*/
	/**
	 * @return the staticMethod
	 */
	public boolean isStaticMethod() {
		return this.staticMethod;
	}

	public List<FastCodeParam> getParameters() {
		return this.parameters;
	}

	public List<FastCodeType> getExceptions() {
		return this.exceptions;
	}

	public void addExceptions(final FastCodeType fastCodeType) {
		this.exceptions.add(fastCodeType);
	}

	/**
	 *
	 * getter method for method
	 * @return
	 *
	 */
	public IMethod getMethod() {
		return this.method;
	}

	/**
	 *
	 * setter method for method
	 * @param method
	 *
	 */
	public void setMethod(final IMethod method) {
		this.method = method;
	}

}
