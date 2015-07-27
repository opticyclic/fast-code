/**
 *
 */
package org.fastcode.common;

import static org.eclipse.jdt.core.Signature.getSignatureSimpleName;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.util.SourceUtil.getFQNameFromFieldTypeName;
import static org.fastcode.util.SourceUtil.isNativeType;
import static org.fastcode.util.StringUtil.changeToCamelCase;
import static org.fastcode.util.StringUtil.flattenType;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.StringUtil.parseType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMemberValuePair;

/**
 * @author Gautam
 *
 */
public class FastCodeField extends AbstractFastCodeField {

	final private String							fullName;
	final private String							getter;
	final private String							setter;
	final private IField							field;
	private final FastCodeField						parentField;
	private final Map<String, Map<String, String>>	annotations		= new HashMap<String, Map<String, String>>();
	public static final Map<String, String>			defaultValues	= new HashMap<String, String>();
	private boolean									array;
	final private String							fullTypeName;
	private int										arrayDimension;
	private final List<FastCodeField>				childFields		= new ArrayList<FastCodeField>();
	private final boolean							typeNative;
	private final FastCodeType						type;
	private String									gettersetter;
	private boolean									builderPattern;

	static {
		defaultValues.put("Boolean", "false");
		defaultValues.put("boolean", "false");
		defaultValues.put("Integer", "0");
		defaultValues.put("int", "0");
		defaultValues.put("float", "0.0f");
		defaultValues.put("Float", "0.0f");
		defaultValues.put("Double", "0.0");
		defaultValues.put("double", "0.0");
		defaultValues.put("String", "\"\"");
		defaultValues.put("byte", "0");
		defaultValues.put("char", "''");
		defaultValues.put("long", "0");
		defaultValues.put("short", "0");
	}

	/**
	 * @param field
	 * @throws Exception
	 */
	public FastCodeField(final IField field) throws Exception {
		this(field, field.getElementName());
	}

	/**
	 *
	 * @param field
	 * @param name
	 * @throws Exception
	 */
	public FastCodeField(final IField field, final String name) throws Exception {
		this(field, name, null, null);
	}

	/**
	 *
	 * @param field
	 * @param name
	 * @param value
	 * @throws Exception
	 */
	public FastCodeField(final IField field, final String name, final String value, final FastCodeField parentField) throws Exception {
		parseTypeForArray(getSignatureSimpleName(field.getTypeSignature()));
		final ICompilationUnit fieldCompUnit = field.getCompilationUnit();
		this.field = field;
		this.name = name; // parseTypeForArray(name);
		this.type = parseType(getSignatureSimpleName(this.field.getTypeSignature()), field.getCompilationUnit());
		if (isArray()) {
			this.fullTypeName = fieldCompUnit != null && fieldCompUnit.exists() ? getFQNameFromFieldTypeName(
					parseTypeForArray(this.type.getName()), fieldCompUnit) : parseTypeForArray(this.type.getName());

		} else {
			this.fullTypeName = fieldCompUnit != null && fieldCompUnit.exists() ? getFQNameFromFieldTypeName(this.type.getName(),
					fieldCompUnit) : this.type.getName();
		}

		this.parentField = parentField;
		this.arrayDimension = 0;
		// this.type = getSignatureSimpleName(field.getTypeSignature());//
		// parseTypeForArray(getSignatureSimpleName(field.getTypeSignature()));
		this.value = defaultValues.containsKey(this.type.getName()) ? defaultValues.get(this.type.getName()) : "null";

		this.typeNative = isNativeType(this.type.getName());
		for (final IAnnotation annot : field.getAnnotations()) {
			final Map<String, String> annotElements = new HashMap<String, String>();
			for (final IMemberValuePair memberValuePair : annot.getMemberValuePairs()) {
				annotElements.put(memberValuePair.getMemberName(), memberValuePair.getValue().toString());
			}
			this.annotations.put(isEmpty(annot.getElementName()) ? "value" : annot.getElementName(), annotElements);
		}

		FastCodeField parenField = this.parentField;
		String fullFldName = this.name;
		while (parenField != null) {
			fullFldName = parenField.name + DOT + fullFldName;
			parenField = parenField.parentField;
		}
		this.fullName = fullFldName;

		final String getPrefix = this.type.getName().equalsIgnoreCase("boolean") ? "is" : "get", setPrefix = "set";
		String getter;
		String setter;
		if (this.name != null && this.name.length() > 1) {
			getter = getPrefix + this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
			parenField = this.parentField;
			if (parenField != null) {
				getter = parenField.getter + "()." + getter;
				parenField = parenField.parentField;
			}
		} else if (this.name != null) {
			getter = getPrefix + this.name.toUpperCase();
		} else {
			getter = EMPTY_STR;
		}
		this.getter = getter;
		if (this.name != null && this.name.length() > 1) {
			setter = setPrefix + this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
			parenField = this.parentField;
			if (parenField != null) {
				setter = parenField.getter + "()." + setter;
				parenField = parenField.parentField;
			}
		} else if (this.name != null) {
			setter = setPrefix + this.name.toUpperCase();
		} else {
			setter = EMPTY_STR;
		}
		this.setter = setter;
		this.type.setFullyQualifiedName(flattenType(this.type, true));
		this.type.setName(flattenType(this.type, false));

	}

	/**
	 *
	 * @return
	 */
	public String makeWord() {
		if (isEmpty(this.name)) {
			return EMPTY_STR;
		}
		final String word = changeToCamelCase(this.name, EMPTY_CHAR);
		final String[] words = word.split(SPACE);
		final StringBuilder wordBuilder = new StringBuilder();
		for (final String w : words) {
			wordBuilder.append(w.length() > 1 ? w.substring(0, 1).toUpperCase() + w.substring(1) : w.substring(0, 1).toUpperCase());
			wordBuilder.append(SPACE);

		}
		return wordBuilder.toString().trim();
	}

	/**
	 *
	 * @param type
	 * @return
	 */
	private String parseTypeForArray(final String type) {
		final StringBuilder builder = new StringBuilder();
		boolean arrOpen = false;
		for (final char c : type.trim().toCharArray()) {
			if (Character.isWhitespace(c)) {
				continue;
			}
			if (Character.isJavaIdentifierPart(c) || c == '<' || c == '>') {
				builder.append(c);
			} else if (!arrOpen && c == '[') {
				arrOpen = true;
				this.array = true;
			} else if (arrOpen && c == ']') {
				arrOpen = false;
				this.arrayDimension++;
			}
		}
		return builder.toString();
	}

	/**
	 *
	 * @return
	 */
	public String getNameAsWord() {
		return makeWord();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * @return
	 */
	public IField getField() {
		return this.field;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getValue() {
		return this.value;
	}

	/**
	 *
	 * @return
	 */
	public String getGetter() {
		return this.getter;
	}

	/**
	 *
	 * @return
	 */
	public String getSetter() {
		return this.setter;
	}

	/**
	 *
	 * @return
	 */
	public FastCodeField getParentField() {
		return this.parentField;
	}

	/**
	 *
	 * @return
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 *
	 * getter method for annotations
	 *
	 * @return
	 *
	 */
	public Map<String, Map<String, String>> getAnnotations() {
		return this.annotations;
	}

	/**
	 *
	 * getter method for array
	 *
	 * @return
	 *
	 */
	public boolean isArray() {
		return this.array;
	}

	public String getFullTypeName() {
		return this.fullTypeName;
	}

	public int getArrayDimension() {
		return this.arrayDimension;
	}

	/**
	 *
	 * getter method for childFields
	 * @return
	 *
	 */
	public List<FastCodeField> getChildFields() {
		return this.childFields;
	}

	/**
	 *
	 * add method for childFields
	 * @param aFastCodeField
	 *
	 */
	public void addChildField(final FastCodeField aFastCodeField) {
		this.childFields.add(aFastCodeField);
	}

	public boolean isTypeNative() {
		return this.typeNative;
	}

	public FastCodeType getType() {
		return this.type;
	}

	/**
	 *
	 * getter method for gettersetter
	 * @return
	 *
	 */
	public String getGettersetter() {
		return this.gettersetter;
	}

	/**
	 *
	 * setter method for gettersetter
	 * @param gettersetter
	 *
	 */
	public void setGettersetter(final String gettersetter) {
		this.gettersetter = gettersetter;
	}

	/**
	 *
	 * getter method for builderPattern
	 * @return
	 *
	 */
	public boolean isBuilderPattern() {
		return this.builderPattern;
	}

	/**
	 *
	 * setter method for builderPattern
	 * @param builderPattern
	 *
	 */
	public void setBuilderPattern(final boolean builderPattern) {
		this.builderPattern = builderPattern;
	}

}
