/**
 *
 */
package org.fastcode.util;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.BOOLEAN;
import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.DOUBLE;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FLOAT;
import static org.fastcode.common.FastCodeConstants.INT;
import static org.fastcode.common.FastCodeConstants.LEFT_CURL;
import static org.fastcode.common.FastCodeConstants.METHOD_ANNOTATIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_ARGS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_BODY_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_COMMENTS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_EXCEPTIONS_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_MODIFIER_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_NAME_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_PATTERN_DEFAULT;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_STR;
import static org.fastcode.common.FastCodeConstants.METHOD_RETURN_TYPE_VOID;
import static org.fastcode.common.FastCodeConstants.MODIFIER_PUBLIC;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.preferences.PreferenceConstants.P_COMMON_TEMPLATE_PREFIX;
import static org.fastcode.setting.GlobalSettings.getInstance;
import static org.fastcode.util.JUnitUtil.surroundWithTryCatchBlock;
import static org.fastcode.util.SourceUtil.findTypeForImport;
import static org.fastcode.util.SourceUtil.getFieldsOfType;
import static org.fastcode.util.SourceUtil.isJavaLangType;
import static org.fastcode.util.SourceUtil.isNativeType;
import static org.fastcode.util.StringUtil.changeToPlural;
import static org.fastcode.util.StringUtil.createDefaultInstance;
import static org.fastcode.util.StringUtil.createEmbeddedInstance;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.makePlaceHolder;
import static org.fastcode.util.StringUtil.parseType;
import static org.fastcode.util.StringUtil.replacePlaceHolder;
import static org.fastcode.util.StringUtil.replacePlaceHolderWithBlank;
import static org.fastcode.util.StringUtil.replacePlaceHolders;
import static org.fastcode.util.StringUtil.resetLineWithTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchEngine;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.HANDLE_EXCEPTION;
import org.fastcode.common.FastCodeField;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.Pair;
import org.fastcode.preferences.JunitPreferences;
import org.fastcode.setting.GlobalSettings;
import org.fastcode.setting.TemplateSettings;

/**
 * @author Gautam
 *
 */
public abstract class AbstractMethodBuilder implements MethodBuilder {

	protected static final Pattern	parmPattern	= Pattern.compile("([A-Za-z]+)\\s*<\\s*([A-Z0-9a-z ,]+)\\s*>\\s*");
	protected static final String[]	nativeTypes	= { "int", "long", "boolean", "byte", "float", "double" };

	protected FastCodeContext		fastCodeContext;

	/**
	 * @param fastCodeContext
	 */
	public AbstractMethodBuilder(final FastCodeContext fastCodeContext) {
		this.fastCodeContext = fastCodeContext;
	}

	/**
	 *
	 * @param method
	 * @param toType
	 * @return
	 * @throws Exception
	 */
	@Override
	public IMethod buildMethod(final IMethod method, final IType toType) throws Exception {
		return this.buildMethod(method, toType, null, null);
	}

	/**
	 * @param type
	 * @param createSimilarDescriptor
	 * @param createSimilarDescriptorClass
	 * @throws Exception
	 *
	 */
	@Override
	public IMethod buildMethod(final IMethod method, final IType type, final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		final IType declaringType = method.getDeclaringType();

		final GlobalSettings globalSettings = getInstance();
		copyAllImports(createSimilarDescriptor, createSimilarDescriptorClass, method, type);
		final String methodName = createSimilarDescriptor == null ? method.getElementName() : makeMethodName(method, type,
				createSimilarDescriptor, createSimilarDescriptorClass);

		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		placeHolders.put(METHOD_NAME_STR, methodName);
		placeHolders.put(METHOD_MODIFIER_STR, MODIFIER_PUBLIC);

		final List<Pair<String, String>> methodParms = makeMethodParms(createSimilarDescriptor, createSimilarDescriptorClass, method, type);

		if (createSimilarDescriptor == null || !createSimilarDescriptorClass.isConvertMethodParam()) {
			final IMethod exstMeth = type.getMethod(methodName, method.getParameterTypes());
			if (exstMeth != null && exstMeth.exists()) {
				return null;
			}
		} else {
			if (doesMethodExist(type, methodName, methodParms)) {
				return null;
			}
		}

		placeHolders.put(METHOD_ARGS_STR, getMethodArgs(method, type, methodParms, placeHolders));
		placeHolders.put(METHOD_COMMENTS_STR, getMethodComments(method, type, methodParms, createSimilarDescriptor, placeHolders));

		String methodBody = EMPTY_STR;
		String fullMethodPattern = EMPTY_STR;
		final String templatePrefix = P_COMMON_TEMPLATE_PREFIX;

		if (createSimilarDescriptor == null && createSimilarDescriptorClass == null) {
			fullMethodPattern = type.isClass() ? globalSettings.getClassMethodBody() : globalSettings.getInterfaceMethodBody();
			if (type.isClass()) {
				TemplateSettings templateSettings = TemplateSettings.getTemplateSettings(P_COMMON_TEMPLATE_PREFIX + UNDERSCORE
						+ "method.body." + method.getElementName(), templatePrefix);
				if (templateSettings == null) {
					templateSettings = TemplateSettings.getTemplateSettings(P_COMMON_TEMPLATE_PREFIX + UNDERSCORE + "method.body."
							+ method.getDeclaringType().getFullyQualifiedName() + DOT + method.getElementName(), templatePrefix);
				}
				if (templateSettings != null) {
					final IField[] fieldsArr = getFieldsOfType(type);
					final Map<String, Object> plcHolders = new HashMap<String, Object>();
					final List<FastCodeField> fastCodeFields = new ArrayList<FastCodeField>();
					if (fieldsArr != null) {
						for (final IField field : fieldsArr) {
							fastCodeFields.add(new FastCodeField(field));
						}
						plcHolders.put("fields", fastCodeFields);
					}
					getGlobalSettings(plcHolders);
					methodBody = evaluateByVelocity(templateSettings.getTemplateBody(), plcHolders);
				}
			}
		} else if (createSimilarDescriptorClass.getClassType() == CLASS_TYPE.CLASS) {
			fullMethodPattern = globalSettings.getClassMethodBody();
			if (createSimilarDescriptorClass.isCreateMethodBody()) {
				final String instance = createDefaultInstance(declaringType.getElementName());
				methodBody = getMethodBody(method, instance, type, methodParms, createSimilarDescriptorClass);
			} else {
				final String returnType = getSignatureSimpleName(method.getReturnType()).trim();
				if (!createSimilarDescriptorClass.isCreateMethodBody()) {
					if (returnType.equals(METHOD_RETURN_TYPE_VOID)) {
						methodBody = "return;";
					} else if (returnType.equals(INT) || returnType.equals("long") || returnType.equals("short")
							|| returnType.equals(DOUBLE) || returnType.equals(FLOAT)) {
						methodBody = "return 0;";
					} else if (returnType.equals(BOOLEAN)) {
						methodBody = "return false;";
					} else {
						methodBody = "return null;";
					}
				}
			}
		} else if (createSimilarDescriptorClass.getClassType() == CLASS_TYPE.INTERFACE) {
			fullMethodPattern = globalSettings.getInterfaceMethodBody();
		}

		if (method.getExceptionTypes() != null && method.getExceptionTypes().length > 0) {
			final StringBuilder exceptions = new StringBuilder();
			int count = 0;
			for (final String exceptionType : method.getExceptionTypes()) {
				exceptions.append(getSignatureSimpleName(exceptionType));
				exceptions.append(count < method.getExceptionTypes().length - 1 ? COMMA + SPACE : EMPTY_STR);
				count++;
			}

			if (this.fastCodeContext != null && this.fastCodeContext.isUnitTest()) {
				final JunitPreferences junitPreferences = this.fastCodeContext.getJunitPreferences();
				if (junitPreferences != null) {
					final HANDLE_EXCEPTION handleException = junitPreferences.getCreateUnitTestData().getHandleException();
					if (handleException.equals(HANDLE_EXCEPTION.CONSUME)) {
						methodBody = surroundWithTryCatchBlock(methodBody, method, false);
						/*
						 * final String tryBegin = "try {" + NEWLINE; final
						 * String tryClose = NEWLINE + "} catch ("; final String
						 * catchClose = ") {"; final StringBuilder catchBlock =
						 * new StringBuilder(); for (final String exceptionType
						 * : method.getExceptionTypes()) {
						 * catchBlock.append(tryClose); final String excpName =
						 * getSignatureSimpleName(exceptionType); final String
						 * excpInstance =
						 * changeFirstLetterToLowerCase(excpName); final String
						 * callFail = "fail(\"Test failed \" + " + excpInstance
						 * + DOT + "getMessage());"; catchBlock.append(excpName
						 * + SPACE); catchBlock.append(excpInstance);
						 * catchBlock.append(catchClose);
						 * catchBlock.append(NEWLINE + TAB + excpInstance + DOT
						 * + "printStackTrace();" + NEWLINE + TAB + callFail +
						 * NEWLINE); } catchBlock.append("}"); methodBody =
						 * tryBegin + methodBody + catchBlock.toString();
						 */
					} else if (handleException.equals(HANDLE_EXCEPTION.THROW)) {
						placeHolders.put(METHOD_EXCEPTIONS_STR, exceptions.toString());
					}
				}
			} else {
				placeHolders.put(METHOD_EXCEPTIONS_STR, exceptions.toString());
			}

		}
		if (!placeHolders.containsKey(METHOD_EXCEPTIONS_STR)) {
			fullMethodPattern = replacePlaceHolderWithBlank(fullMethodPattern, "throws", METHOD_EXCEPTIONS_STR, LEFT_CURL);

		}
		final String annotation = createSimilarDescriptorClass == null ? EMPTY_STR : getAnnotations(method, type,
				createSimilarDescriptorClass.getMethodAnnotations(), placeHolders);
		// System.out.println(annotation);
		if (isEmpty(annotation)) {
			fullMethodPattern = resetLineWithTag(fullMethodPattern, METHOD_ANNOTATIONS_STR);
		} else {
			placeHolders.put(METHOD_ANNOTATIONS_STR, annotation == null ? EMPTY_STR : annotation.trim());
		}

		placeHolders.put(METHOD_BODY_STR, methodBody);
		placeHolders.put(METHOD_RETURN_TYPE_STR, getSignatureSimpleName(method.getReturnType()));

		if (createSimilarDescriptor != null) {
			placeHolders.put(METHOD_RETURN_TYPE_STR,
					makeMethodeturnType(type, method, createSimilarDescriptor, createSimilarDescriptorClass));
		}

		final IMethod sibling = findMethodsibling(type, method, createSimilarDescriptorClass);

		final String methodSrc = evaluateByVelocity(fullMethodPattern, placeHolders);
		final IMethod createdMethod = type.createMethod(methodSrc, sibling, false, null);

		if (this.fastCodeContext != null && this.fastCodeContext.isUnitTest()) {
			if (this.fastCodeContext.getJunitPreferences().getCreateUnitTestData().getExceptnIType() != null) {
				insertImport(type, this.fastCodeContext.getJunitPreferences().getCreateUnitTestData().getExceptnIType());
			}
		}
		return createdMethod != null && createdMethod.exists() ? createdMethod : null;
	}

	/**
	 *
	 * @param toType
	 * @param methodName
	 * @param methodParms
	 * @return
	 * @throws Exception
	 */
	private boolean doesMethodExist(final IType toType, final String methodName, final List<Pair<String, String>> methodParms)
			throws Exception {
		boolean exist = false;
		for (final IMethod method : toType.getMethods()) {
			if (!method.getElementName().equals(methodName)) {
				continue;
			}
			if (methodParms.size() != method.getParameterTypes().length) {
				continue;
			}
			int count = 0;
			exist = true;
			for (final String parameterType : method.getParameterTypes()) {
				if (!getSignatureSimpleName(parameterType).equals(methodParms.get(count++).getLeft())) {
					exist = false;
					break;
				}
			}
		}
		return exist;
	}

	/**
	 *
	 * @param method
	 * @param toType
	 *            TODO
	 * @param createSimilarDescriptor
	 * @param createSimilarDescriptorClass
	 * @return
	 */
	protected String makeMethodName(final IMethod method, final IType toType, final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) {
		final String methodPattern = createSimilarDescriptor.getMethodNamePattern();
		String methodName = null;

		if (this.fastCodeContext != null && this.fastCodeContext.isUnitTest()) {
			final JunitPreferences junitPreferences = this.fastCodeContext.getJunitPreferences();
			return junitPreferences.getCreateUnitTestData().getTestMethodName();
		}

		if (createSimilarDescriptor.isDifferentName()) {
			final String replacePart = createSimilarDescriptor.getReplacePart();
			final String replaceValue = createSimilarDescriptor.getReplaceValue();
			methodName = getReplaceMethodName(method.getElementName(), replacePart, replaceValue);
		} else {
			methodName = replacePlaceHolder(
					methodPattern,
					METHOD_NAME_STR,
					methodPattern.startsWith(METHOD_PATTERN_DEFAULT) ? method.getElementName() : createEmbeddedInstance(method
							.getElementName()));
		}
		return methodName;
	}

	/**
	 *
	 * @param methodName
	 * @param replacePart
	 * @param replaceValue
	 * @return
	 */
	private static String getReplaceMethodName(final String methodName, final String replacePart, final String replaceValue) {
		String newMethodName = null;
		final String plural = changeToPlural(replacePart);

		if (methodName.contains(plural)) {
			newMethodName = methodName.replaceAll(plural, changeToPlural(replaceValue));
		} else {
			newMethodName = methodName.replaceAll(replacePart, replaceValue);
		}

		return newMethodName;
	}

	/**
	 *
	 * @param toType
	 * @param method
	 * @param createSimilarDescriptorClass
	 * @return
	 * @throws Exception
	 */
	protected String makeMethodeturnType(final IType toType, final IMethod method, final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		final String methRetType = getSignatureSimpleName(method.getReturnType());
		if (createSimilarDescriptor == null || methRetType.equals(METHOD_RETURN_TYPE_VOID) || isNativeType(methRetType)
				|| isJavaLangType(methRetType, toType)) {
			return methRetType;
		}

		// if (createSimilarDescriptor.getReturnType() ==
		// RETURN_TYPE.RETURN_TYPE_PASS_THRU) {
		if (createSimilarDescriptorClass != null && createSimilarDescriptorClass.isConvertMethodParam()) {
			final FastCodeType fastCodeType = parseType(methRetType, method.getCompilationUnit());
			return this.convertType(fastCodeType, method.getDeclaringType(), toType, createSimilarDescriptorClass);
			// IType convertType = convertType(methRetType,
			// method.getDeclaringType(), toType, createSimilarDescriptorClass);
			// if (convertType != null) {
			// methRetType = convertType.getElementName();
			// }
		}
		// }

		return methRetType;
	}

	/**
	 *
	 * @param toType
	 * @param method
	 * @param createSimilarDescriptorClass
	 * @return
	 */
	protected abstract IMethod findMethodsibling(final IType toType, final IMethod method,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass);

	/**
	 *
	 * @param createSimilarDescriptorClass
	 * @param method
	 * @param toType
	 * @throws Exception
	 */
	protected void copyAllImports(final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final IMethod method, final IType toType) throws Exception {
		if (!METHOD_RETURN_TYPE_VOID.equals(getSignatureSimpleName(method.getReturnType()))) {
			copyImport(createSimilarDescriptor, createSimilarDescriptorClass, method,
					parseType(getSignatureSimpleName(method.getReturnType()), method.getCompilationUnit()), toType);
		}
		for (final String type : method.getParameterTypes()) {
			copyImport(createSimilarDescriptor, createSimilarDescriptorClass, method,
					parseType(getSignatureSimpleName(type), method.getCompilationUnit()), toType);
		}

		for (final String type : method.getExceptionTypes()) {
			copyImport(createSimilarDescriptor, createSimilarDescriptorClass, method,
					parseType(getSignatureSimpleName(type), method.getCompilationUnit()), toType);
		}
	}

	/**
	 *
	 * @param createSimilarDescriptorClass
	 * @param method
	 * @param typeName
	 * @param toType
	 * @throws Exception
	 */
	protected void copyImport(final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final IMethod method, final FastCodeType fastCodeType,
			final IType toType) throws Exception {

		handleSingleTypeImport(createSimilarDescriptor, createSimilarDescriptorClass, fastCodeType.getFullyQualifiedName(),
				method.getDeclaringType(), toType);

		for (final FastCodeType parmType : fastCodeType.getParameters()) {
			handleSingleTypeImport(createSimilarDescriptor, createSimilarDescriptorClass, parmType.getFullyQualifiedName(),
					method.getDeclaringType(), toType);
			copyImport(createSimilarDescriptor, createSimilarDescriptorClass, method, parmType, toType);
		}
	}

	/**
	 *
	 * @param createSimilarDescriptorClass
	 * @param typeName
	 * @param fromType
	 * @param toType
	 * @throws Exception
	 */
	protected void handleSingleTypeImport(final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final String typeName, final IType fromType, final IType toType)
			throws Exception {
		if (isNativeType(typeName) || isJavaLangType(typeName, toType)) {
			return;
		}
		final IType importType = new SearchUtil().searchForItype(typeName, IJavaSearchConstants.TYPE, SearchEngine.createWorkspaceScope());

		//final IType importType = findTypeForImport(fromType, typeName, toType);
		if (importType == null) {
			return;
		}
		if (importType.getFullyQualifiedName().startsWith("java")) {
			insertImport(toType, importType);
			return;
		}
		final boolean convertMethodParam = createSimilarDescriptorClass == null ? false : createSimilarDescriptorClass
				.isConvertMethodParam();

		if (convertMethodParam && doConvertType(importType, createSimilarDescriptorClass)) {
			final IType convType = this.convertType(typeName, fromType, toType, createSimilarDescriptorClass);
			if (convType != null) {
				insertImport(toType, convType);
			}
			if (!createSimilarDescriptor.isDifferentName() && toType.isClass()) {
				insertImport(toType, importType);
			}
		} else {
			insertImport(toType, importType);
		}
	}

	/**
	 *
	 * @param toType
	 * @param importType
	 * @throws Exception
	 */
	protected void insertImport(final IType toType, final IType importType) throws Exception {
		final IImportDeclaration imprt = toType.getCompilationUnit().getImport(importType.getFullyQualifiedName());
		if (imprt == null || !imprt.exists()) {
			toType.getCompilationUnit().createImport(importType.getFullyQualifiedName(), null, null);
		}
	}

	/**
	 *
	 * @param CreateSimilarDescriptor
	 *            TODO
	 * @param createSimilarDescriptorClass
	 * @param method
	 * @param toType
	 * @return
	 * @throws Exception
	 */
	protected List<Pair<String, String>> makeMethodParms(final CreateSimilarDescriptor createSimilarDescriptor,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass, final IMethod method, final IType toType) throws Exception {
		final List<Pair<String, String>> methodParms = new ArrayList<Pair<String, String>>();
		int count = 0;

		for (final String type : method.getParameterTypes()) {

			String newType = getSignatureSimpleName(type);
			String newName = method.getParameterNames()[count];

			if (createSimilarDescriptorClass != null && createSimilarDescriptorClass.isConvertMethodParam()) {
				final FastCodeType fastCodeType = parseType(newType, method.getCompilationUnit());
				newType = this.convertType(fastCodeType, method.getDeclaringType(), toType, createSimilarDescriptorClass);
				if (!newType.equals(getSignatureSimpleName(type))) {
					newName = createDefaultInstance(newType);
				}
			}

			methodParms.add(new Pair<String, String>(newType, newName));
			count++;
		}

		return methodParms;
	}

	/**
	 *
	 * @param origClass
	 * @param origName
	 * @param convClass
	 * @param convdName
	 * @return
	 * @throws Exception
	 */
	protected String convertMethodFragment(final String origClass, final String origName, final String convClass, final String convdName)
			throws Exception {
		final GlobalSettings globalSettings = getInstance();

		final Map<String, Object> placeHolders = new HashMap<String, Object>();
		placeHolders.put("original_class", origClass);
		placeHolders.put("original_name", origName);
		placeHolders.put("converted_class", convClass);
		placeHolders.put("converted_name", convdName);

		return replacePlaceHolders(globalSettings.getConvertMethodParamPattern(), placeHolders);
	}

	/**
	 *
	 * @param fastCodeType
	 * @param declaringType
	 * @param toType
	 * @param createSimilarDescriptorClass
	 * @return
	 * @throws Exception
	 */
	protected boolean doConvertType(final IType type, final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		final Pattern pattern = Pattern.compile(createSimilarDescriptorClass.getConvertMethodParamFrom());
		final Matcher matcher = pattern.matcher(type.getFullyQualifiedName());
		return matcher.matches();
	}

	/**
	 *
	 * @param fastCodeParamType
	 * @param declaringType
	 * @param toType
	 * @param createSimilarDescriptorClass
	 * @return
	 * @throws Exception
	 */
	protected String convertType(final FastCodeType fastCodeType, final IType declaringType, final IType toType,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		final StringBuilder retType = new StringBuilder();
		final IType convertType = this.convertType(fastCodeType.getName(), declaringType, toType, createSimilarDescriptorClass);
		if (convertType != null) {
			retType.append(convertType.getElementName());
		} else {
			retType.append(fastCodeType.getName());
		}
		final List<FastCodeType> params = fastCodeType.getParameters();
		if (params != null && !params.isEmpty()) {
			retType.append("<");
			for (final FastCodeType codeType : params) {
				retType.append(this.convertType(codeType, declaringType, toType, createSimilarDescriptorClass));
				retType.append(params.indexOf(codeType) < params.size() - 1 ? COMMA + SPACE : EMPTY_STR);
			}
			retType.append(">");
		}
		return retType.toString();
	}

	/**
	 *
	 * @param typeName
	 * @param declaringType
	 * @param createSimilarDescriptorClass
	 * @param toType
	 * @return
	 * @throws Exception
	 */
	protected IType convertType(final String typeName, final IType declaringType, final IType toType,
			final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception {
		// Matcher matchr = parmPattern.matcher(typeName);
		// if (matchr.matches()) {
		// return null;
		// }
		final IType fType = findTypeForImport(declaringType, typeName, toType);
		if (fType == null || !fType.exists()) {
			return null;
		}
		final Pattern pattern = Pattern.compile(createSimilarDescriptorClass.getConvertMethodParamFrom());

		final Matcher matcher = pattern.matcher(fType.getFullyQualifiedName());
		if (!matcher.matches()) {
			return fType;
		}
		String convertParam = createSimilarDescriptorClass.getConvertMethodParamTo();
		for (int i = 1; i <= matcher.groupCount(); i++) {
			convertParam = convertParam.replace(makePlaceHolder(EMPTY_STR + i), matcher.group(i));
		}
		if (this.fastCodeContext.getPlaceHolders().containsKey("input")) {
			convertParam = convertParam.replace(makePlaceHolder("input"),
					String.valueOf(this.fastCodeContext.getPlaceHolders().get("input")));
		}
		final IType convType = toType.getCompilationUnit().getJavaProject().findType(convertParam);

		return convType != null && convType.exists() ? convType : null;
	}

	/**
	 *
	 * @param method
	 * @param instance
	 * @param createSimilarDescriptorClass
	 * @return
	 */
	protected abstract String getMethodBody(final IMethod method, final String instance, final IType toType,
			final List<Pair<String, String>> methodParms, final CreateSimilarDescriptorClass createSimilarDescriptorClass) throws Exception;

	protected abstract String getAnnotations(final IMethod method, final IType toType, final String[] methodAnnotations,
			final Map<String, Object> placeHolders) throws Exception;

	protected abstract String getMethodComments(final IMethod method, final IType toType, final List<Pair<String, String>> methodParms,
			final CreateSimilarDescriptor createSimilarDescriptor, final Map<String, Object> placeHolders) throws Exception;

	protected abstract String getMethodArgs(final IMethod method, final IType toType, final List<Pair<String, String>> methodParms,
			final Map<String, Object> placeHolders) throws Exception;

	/**
	 * @param methodName
	 * @return
	 */
	protected String getObjectNameFromMethod(final String methodName) {

		final String[] commonMethodNames = { "find", "get", "search", "retrieve" };
		String retVarName;

		for (final String name : commonMethodNames) {
			if (methodName.toLowerCase().startsWith(name)) {
				retVarName = methodName.substring(name.length()).toLowerCase();
				return retVarName;
			}
		}

		return "result";
	}
}
