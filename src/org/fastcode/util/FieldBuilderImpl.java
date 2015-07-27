/**
 *
 */
package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.EQUAL_WITH_SPACE;
import static org.fastcode.common.FastCodeConstants.FINAL;
import static org.fastcode.common.FastCodeConstants.JAVA_EXTENSION;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.LIST;
import static org.fastcode.common.FastCodeConstants.MAP;
import static org.fastcode.common.FastCodeConstants.NEW;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SEMICOLON;
import static org.fastcode.common.FastCodeConstants.SET;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.STATIC;
import static org.fastcode.util.StringUtil.getClsNmeFromFQClsNme;
import static org.fastcode.util.StringUtil.isEmpty;

import org.eclipse.jdt.core.IType;
import org.fastcode.common.CreateVariableData;
import org.fastcode.common.FastCodeConstants.ACCESS_MODIFIER;

/**
 * @author Gautam
 *
 */
public class FieldBuilderImpl implements FieldBuilder {

	/* (non-Javadoc)
	 * @see org.fastcode.util.FieldBuilder#buildFieldSource(org.eclipse.jdt.core.IType, org.fastcode.common.CreateVariableData, java.lang.String)
	 */
	@Override
	public String buildFieldSource(final IType type, final CreateVariableData createVariableData, final String fieldName) {

		String fldType = null;
		if (!isEmpty(createVariableData.getFieldType())) {
			fldType = getClsNmeFromFQClsNme(createVariableData.getFieldType());//createVariableData.getFieldType().substring(createVariableData.getFieldType().lastIndexOf(DOT) + 1, createVariableData.getFieldType().length());
		}

		if (createVariableData.getFieldParams() != null && createVariableData.getFieldParams().length > 0) {
			final StringBuilder params = new StringBuilder();
			int i = 1;
			for (final String param : createVariableData.getFieldParams()) {
				params.append(param);
				if (i++ < createVariableData.getFieldParams().length) {
					params.append(COMMA + SPACE);
				}
			}
			fldType = "<" + params.toString() + ">";
		}
		if (createVariableData.isArray()) {
			for (int k = 1; k <= createVariableData.getArrayDim(); k++) {
				fldType += "[]";
			}
		}

		String accMod = EMPTY_STR;
		if (createVariableData.getAccessModifier() != ACCESS_MODIFIER.DEFAULT) {
			accMod = createVariableData.getAccessModifier().getValue() + SPACE;
		}
		String specifiers = EMPTY_STR;
		if (createVariableData.isFinal() && createVariableData.isStatic()) {
			specifiers = STATIC + SPACE + FINAL + SPACE;
		} else if (createVariableData.isFinal()) {
			specifiers = FINAL + SPACE;
		} else if (createVariableData.isStatic()) {
			specifiers = STATIC + SPACE;
		}

		String initVal = EMPTY_STR;
		if (createVariableData.isList() && createVariableData.isInitialized()) {
			initVal = EQUAL_WITH_SPACE + NEW + SPACE + createVariableData.getListType() + fldType + LEFT_PAREN + RIGHT_PAREN + SEMICOLON;
			fldType = LIST + fldType;
		} else if (createVariableData.isList()) {
			initVal = SEMICOLON;
			fldType = LIST + fldType;
		} else if (createVariableData.isSet() && createVariableData.isInitialized()) {
			initVal = EQUAL_WITH_SPACE + NEW + SPACE + createVariableData.getSetType() + fldType + LEFT_PAREN + RIGHT_PAREN + SEMICOLON;
			fldType = SET + fldType;
		} else if (createVariableData.isSet()) {
			initVal = SEMICOLON;
			fldType = SET + fldType;
			/*} else if (createVariableData.isArray() && createVariableData.isInitialized()){
				initVal = EQUAL_WITH_SPACE + LEFT_PAREN + createVariableData.getInitialValue() + RIGHT_PAREN + SEMICOLON;*/
		} else if (createVariableData.isInitialized()) {
			/*if (fldType.equals("String") && !createVariableData.getInitialValue().equals(EMPTY_QUOTE_STR)) {
				initVal = EQUAL_WITH_SPACE + DOUBLE_QUOTES + createVariableData.getInitialValue() + DOUBLE_QUOTES + SEMICOLON;
			} else {*/
			initVal = EQUAL_WITH_SPACE + createVariableData.getInitialValue() + SEMICOLON;
			/*}*/
		} else if (createVariableData.isMap()) {
			initVal = EQUAL_WITH_SPACE + NEW + SPACE + createVariableData.getMapType() + fldType + LEFT_PAREN + RIGHT_PAREN + SEMICOLON;
			fldType = MAP + fldType;
		} else {
			if (createVariableData.getCompUnitType().equals(JAVA_EXTENSION)) {
				initVal = SEMICOLON;
			}
		}

		/*String lombokAnnotation = "";
		if (createVariableData.isUseLombokAnnotation()) {
			lombokAnnotation = createVariableData.getLombokGetSet() + SPACE;
			if (!createVariableData.getLombokAccess().equals(EMPTY_STR)) {
				lombokAnnotation = lombokAnnotation + LEFT_PAREN + LOMBOK_ACCESS_LEVEL + DOT + createVariableData.getLombokAccess() + RIGHT_PAREN + SPACE;
			}

		}*/

		String fieldAnnotation = EMPTY_STR;
		final StringBuilder stringBuilder = new StringBuilder();
		if (createVariableData.isUseAnnotation()) {
			for (final String annotation : createVariableData.getAnnotations()) {
				if (annotation != null) {
					stringBuilder.append(annotation + NEWLINE);
				}
			}
			fieldAnnotation = stringBuilder.toString().trim() + NEWLINE;
		}

		return fieldAnnotation + accMod + specifiers + fldType + SPACE + fieldName + initVal;

	}

	/**
	 * @param fieldName
	 */
	public void getFieldSrc(final String fieldName) {

	}

}
