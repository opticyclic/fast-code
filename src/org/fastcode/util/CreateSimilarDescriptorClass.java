/**
 *
 */
package org.fastcode.util;

import static org.eclipse.jdt.core.Flags.isFinal;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.util.SourceUtil.findTypeForImport;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.fastcode.common.FastCodeConstants.CLASS_TYPE;
import org.fastcode.common.FastCodeConstants.RELATION_TYPE;

/**
 * @author Gautam
 *
 */
public class CreateSimilarDescriptorClass {

	private final CLASS_TYPE					classType;
	private RELATION_TYPE						relationTypeToParent;

	final private String						toPattern;
	private final String						subPackage;
	final private String						project;
	final private String						sourcePath;
	final private String						packge;
	private final String						classHeader;
	private final String						classBody;
	private final String						classInsideBody;
	private final String[]						methodAnnotations;
	private final String[]						classAnnotations;
	private final String[]						fieldAnnotations;
	private final boolean						createDefaultConstructor;
	private final boolean						createMethodBody;
	private final boolean						createInstanceConstructor;
	private final boolean						createEqualsHashcode;
	private final boolean						createToString;
	// private List<IType> superTypes = new ArrayList<IType>();
	// private List<IType> implementTypes = new ArrayList<IType>();
	// private List<IType> importTypes = new ArrayList<IType>();
	private final String[]						superTypes;
	private final String[]						implementTypes;
	private final String[]						importTypes;
	private final boolean						inclInstance;
	private final boolean						finalClass;
	private final boolean						inclGetterSetterForInstance;
	private final boolean						createFields;
	private final String						createFieldsName;
	private final boolean						createUnitTest;
	/*private final boolean						createReturnVariable;
	private String								returnVariableName;*/
	private final boolean						convertMethodParam;
	private final String						convertMethodParamFrom;
	private final String						convertMethodParamTo;

	private CreateSimilarDescriptorClass		parentDescriptor;
	private List<CreateSimilarDescriptorClass>	relatedDescriptors	= new ArrayList<CreateSimilarDescriptorClass>();
	private final IPackageFragment				userInputPackage;
	private final IType[]						userInputFieldTypes;
	private List<IType>							userInputInterface	= new ArrayList<IType>();
	private List<IType>							userInputImports	= new ArrayList<IType>();
	private String								superClass			= EMPTY_STR;
	private final IType							userInputSuperClass;

	/**
	 * @param classType
	 * @param toPattern
	 * @param project
	 * @param createUnitTest
	 * @param packge
	 */
	/*public CreateSimilarDescriptorClass(final CLASS_TYPE classType, final String toPattern, final String project, final String sourcePath, final String packge,
			final String classHeader, final String classInsideBody, final boolean createUnitTest) {
		this.classType = classType;
		this.toPattern = toPattern;
		this.project = project;
		this.sourcePath = sourcePath;
		this.packge = packge;
		this.classHeader = classHeader;
		this.createUnitTest = createUnitTest;
		this.classInsideBody = classInsideBody;
	}
	*/
	/**
	 *
	 * @param classType
	 * @param toPattern
	 * @param project
	 * @param sourcePath
	 * @param classHeader
	 * @param classInsideBody
	 * @param createUnitTest
	 * @param convertMethodParam
	 * @param convertMethodParamFrom
	 * @param convertMethodParamTo
	 */
	/*public CreateSimilarDescriptorClass(final CLASS_TYPE classType, final String toPattern, final String project, final String sourcePath, final String packge,
			final String classHeader, final String classInsideBody, final boolean createUnitTest, final boolean convertMethodParam,
			final String convertMethodParamFrom, final String convertMethodParamTo) {
		this(classType, toPattern, project, sourcePath, packge, classHeader, classInsideBody, createUnitTest);
		this.convertMethodParam = convertMethodParam;
		this.convertMethodParamFrom = convertMethodParamFrom;
		this.convertMethodParamTo = convertMethodParamTo;
	}*/

	/**
	 * @param classType
	 * @param project
	 * @param superTypes
	 * @param createDefaultConstructor
	 * @param createInstanceConstructor
	 * @param inclInstance
	 * @param inclGetterSetterForInstance
	 * @param implementTypes
	 * @param classAnnotations
	 * @param fieldAnnotations
	 * @param createMethodBody
	 * @param breakDateFields
	 * @param methodAnnotations
	 * @param createReturnVariable
	 * @param returnVariableName
	 * @param createUnitTest
	 * @param convertMethodParam
	 * @param convertMethodParamFrom
	 * @param convertMethodParamTo
	 */
	/*	public CreateSimilarDescriptorClass(final CLASS_TYPE classType, final String toPattern, final RELATION_TYPE relationTypeToParent, final String project,
				final String sourcePath, final String packge, final String subPackage, final String[] superTypes, final String classHeader,
				final String classInsideBody, final boolean finalClass, final boolean createDefaultConstructor, final boolean createInstanceConstructor,
				final boolean inclInstance, final boolean createEqualsHashcode, final boolean createToString, final boolean ceateFields,
				final String ceateFieldsName, final boolean inclGetterSetterForInstance, final String[] implementTypes, final String[] importTypes,
				final String[] classAnnotations, final String[] fieldAnnotations, final boolean createMethodBody, final String[] methodAnnotations,
				final boolean createReturnVariable, final String returnVariableName, final boolean createUnitTest, final boolean convertMethodParam,
				final String convertMethodParamFrom, final String convertMethodParamTo) {

			this(classType, toPattern, project, sourcePath, packge, classHeader, classInsideBody, createUnitTest);

			this.subPackage = subPackage;
			this.superTypes = superTypes;
			this.relationTypeToParent = relationTypeToParent;
			this.createDefaultConstructor = createDefaultConstructor;
			this.createInstanceConstructor = createInstanceConstructor;
			this.inclInstance = inclInstance;
			this.createFields = ceateFields;
			this.createFieldsName = ceateFieldsName;
			this.inclGetterSetterForInstance = inclGetterSetterForInstance;
			this.finalClass = finalClass;
			this.implementTypes = implementTypes;
			this.importTypes = importTypes;
			this.createEqualsHashcode = createEqualsHashcode;
			this.createToString = createToString;
			this.classAnnotations = classAnnotations;
			this.fieldAnnotations = fieldAnnotations;
			this.createMethodBody = createMethodBody;
			this.methodAnnotations = methodAnnotations;
			this.createReturnVariable = createReturnVariable;
			this.returnVariableName = returnVariableName;
			this.convertMethodParam = convertMethodParam;
			this.convertMethodParamFrom = convertMethodParamFrom;
			this.convertMethodParamTo = convertMethodParamTo;
		}*/

	/**
	 *
	 * @param type
	 * @param toClass
	 * @return
	 */
	public static CreateSimilarDescriptorClass createSimilarDescriptor(final IType type, final String toClass) {
		CreateSimilarDescriptorClass createSimilarDescriptorClass = null;
		try {
			final CLASS_TYPE clType = type.isClass() ? CLASS_TYPE.CLASS : CLASS_TYPE.INTERFACE;

			final String supclassNm = type.getSuperclassName();

			String[] superTypes = null;
			if (supclassNm != null) {
				final IType superType = findTypeForImport(type, supclassNm, type);
				superTypes = new String[1];
				superTypes[0] = superType != null ? superType.getFullyQualifiedName() : null;
			}
			final String[] intfaceNames = type.getSuperInterfaceNames();
			int cnt = 0;
			for (final String intfaceName : intfaceNames) {
				final IType type1 = findTypeForImport(type, intfaceName, type);
				intfaceNames[cnt++] = type1 != null ? type1.getFullyQualifiedName() : null;
			}
			final IAnnotation[] annots = type.getAnnotations();
			final String[] annotations = new String[annots.length];
			cnt = 0;
			for (final IAnnotation annotation : annots) {
				annotations[cnt++] = annotation.getElementName();
			}
			final boolean isFinal = isFinal(type.getFlags());
			final IMethod constr = type.getMethod(type.getElementName(), null);
			final boolean createDefaultConstructor = constr != null && constr.exists();
			createSimilarDescriptorClass = new CreateSimilarDescriptorClass.Builder().withClassType(clType)
					.withToPattern(type.getPackageFragment().getElementName() + DOT + toClass).withSuperTypes(superTypes)
					.withFinalClass(isFinal).withCreateDefaultConstructor(createDefaultConstructor).withImplementTypes(intfaceNames)
					.withClassAnnotations(annotations).build();
			//new CreateSimilarDescriptorClass(clType, type.getPackageFragment().getElementName() + "." + toClass, null, null,null, null, null, superTypes, null, null, isFinal, createDefaultConstructor, false, false, false, false, false, null, false, intfaceNames,null, annotations, null, false, null, false, null, false, false, null, null);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
		return createSimilarDescriptorClass;
	}

	/**
	 * @return the methodAnnotations
	 */
	public String[] getMethodAnnotations() {
		return this.methodAnnotations;
	}

	/**
	 *
	 * @return
	 */
	public boolean isCreateMethodBody() {
		return this.createMethodBody;
	}

	/**
	 *
	 * @return
	 */
	public String[] getClassAnnotations() {
		return this.classAnnotations;
	}

	/**
	 *
	 * @return
	 */
	public boolean isInclGetterSetterForInstance() {
		return this.inclGetterSetterForInstance;
	}

	/**
	 *
	 * @return
	 */
	public String[] getFieldAnnotations() {
		return this.fieldAnnotations;
	}

	/**
	 *
	 * @return
	 */
	public boolean isCreateDefaultConstructor() {
		return this.createDefaultConstructor;
	}

	/**
	 * @return the classType
	 */
	public CLASS_TYPE getClassType() {
		return this.classType;
	}

	/**
	 * @return the inclInstance
	 */
	public boolean isInclInstance() {
		return this.inclInstance;
	}

	/**
	 * @return the createReturnVariable
	 */
	/*public boolean isCreateReturnVariable() {
		return this.createReturnVariable;
	}*/

	/**
	 *
	 * @return
	 */
	public boolean isCreateInstanceConstructor() {
		return this.createInstanceConstructor;
	}

	/**
	 * @param returnVariableName
	 *            the returnVariableName to set
	 */
	/*
	public void setReturnVariableName(final String returnVariableName) {
	this.returnVariableName = returnVariableName;
	}

	*//**
		* @return the returnVariableName
		*/
	/*
	public String getReturnVariableName() {
	return this.returnVariableName;
	}*/

	/**
	 * @param parentDescriptor
	 *            the parentDescriptor to set
	 */
	public void setParentDescriptor(final CreateSimilarDescriptorClass parentDescriptor) {
		this.parentDescriptor = parentDescriptor;
	}

	/**
	 * @return the parentDescriptor
	 */
	public CreateSimilarDescriptorClass getParentDescriptor() {
		return this.parentDescriptor;
	}

	/**
	 * @return the relatedDescriptors
	 */
	public List<CreateSimilarDescriptorClass> getRelatedDescriptors() {
		return this.relatedDescriptors;
	}

	/**
	 * @return the relatedDescriptors
	 */
	public void addRelatedDescriptors(final CreateSimilarDescriptorClass createSimilarDescriptorClass) {
		this.relatedDescriptors.add(createSimilarDescriptorClass);
	}

	/**
	 * @return the implementTypes
	 */
	public String[] getImplementTypes() {
		return this.implementTypes;
	}

	/**
	 *
	 * @return
	 */
	public String[] getImportTypes() {
		return this.importTypes;
	}

	/**
	 * @return the superTypes
	 */
	public String[] getSuperTypes() {
		return this.superTypes;
	}

	/**
	 * @param relationTypeToParent
	 *            the relationTypeToParent to set
	 */
	public void setRelationTypeToParent(final RELATION_TYPE relationTypeToParent) {
		this.relationTypeToParent = relationTypeToParent;
	}

	/**
	 * @return the relationTypeToParent
	 */
	public RELATION_TYPE getRelationTypeToParent() {
		return this.relationTypeToParent;
	}

	/**
	 * @return the toPattern
	 */
	public String getToPattern() {
		return this.toPattern;
	}

	/**
	 * @return the createUnitTest
	 */
	public boolean isCreateUnitTest() {
		return this.createUnitTest;
	}

	/**
	 * @return the subPackage
	 */
	public String getSubPackage() {
		return this.subPackage;
	}

	/**
	 *
	 * @return
	 */
	public String getProject() {
		return this.project;
	}

	/**
	 *
	 * @return
	 */
	public String getClassHeader() {
		return this.classHeader;
	}

	/**
	 *
	 * @return
	 */
	public String getClassBody() {
		return this.classBody;
	}

	/**
	 *
	 * @return
	 */
	public String getSourcePath() {
		return this.sourcePath;
	}

	/**
	 * @return the classInsideBody
	 */
	public String getClassInsideBody() {
		return this.classInsideBody;
	}

	/**
	 *
	 * @return
	 */
	public boolean isCreateFields() {
		return this.createFields;
	}

	/**
	 *
	 * @return
	 */
	public String getCreateFieldsName() {
		return this.createFieldsName;
	}

	/**
	 * @return the convertMethodParam
	 */
	public boolean isConvertMethodParam() {
		return this.convertMethodParam;
	}

	/**
	 * @return the convertMethodParamFrom
	 */
	public String getConvertMethodParamFrom() {
		return this.convertMethodParamFrom;
	}

	/**
	 *
	 * @return
	 */
	public String getConvertMethodParamTo() {
		return this.convertMethodParamTo;
	}

	/**
	 * @return the createEqualsHashcode
	 */
	public boolean isCreateEqualsHashcode() {
		return this.createEqualsHashcode;
	}

	/**
	 *
	 * @return
	 */
	public boolean isCreateToString() {
		return this.createToString;
	}

	/**
	 *
	 * @return
	 */
	public boolean isFinalClass() {
		return this.finalClass;
	}

	/**
	 *
	 * @return
	 */
	public String getPackge() {
		return this.packge;
	}

	public IPackageFragment getUserInputPackage() {
		return this.userInputPackage;
	}

	public Object[] getUserInputFieldTypes() {
		return this.userInputFieldTypes;
	}

	public List<IType> getUserInputInterface() {
		return this.userInputInterface;
	}

	public List<IType> getUserInputImports() {
		return this.userInputImports;
	}

	public String getSuperClass() {
		return this.superClass;
	}

	public IType getUserInputSuperClass() {
		return this.userInputSuperClass;
	}

	private CreateSimilarDescriptorClass(final Builder builder) {

		this.classType = builder.classType;
		this.relationTypeToParent = builder.relationTypeToParent;
		this.toPattern = builder.toPattern;
		this.subPackage = builder.subPackage;
		this.project = builder.project;
		this.sourcePath = builder.sourcePath;
		this.packge = builder.packge;
		this.classHeader = builder.classHeader;
		this.classBody = builder.classBody;
		this.classInsideBody = builder.classInsideBody;
		this.methodAnnotations = builder.methodAnnotations;
		this.classAnnotations = builder.classAnnotations;
		this.fieldAnnotations = builder.fieldAnnotations;
		this.createDefaultConstructor = builder.createDefaultConstructor;
		this.createMethodBody = builder.createMethodBody;
		this.createInstanceConstructor = builder.createInstanceConstructor;
		this.createEqualsHashcode = builder.createEqualsHashcode;
		this.createToString = builder.createToString;
		this.superTypes = builder.superTypes;
		this.implementTypes = builder.implementTypes;
		this.importTypes = builder.importTypes;
		this.inclInstance = builder.inclInstance;
		this.finalClass = builder.finalClass;
		this.inclGetterSetterForInstance = builder.inclGetterSetterForInstance;
		this.createFields = builder.createFields;
		this.createFieldsName = builder.createFieldsName;
		this.createUnitTest = builder.createUnitTest;
		/*this.createReturnVariable = builder.createReturnVariable;
		this.returnVariableName = builder.returnVariableName;*/
		this.convertMethodParam = builder.convertMethodParam;
		this.convertMethodParamFrom = builder.convertMethodParamFrom;
		this.convertMethodParamTo = builder.convertMethodParamTo;
		this.parentDescriptor = builder.parentDescriptor;
		this.relatedDescriptors = builder.relatedDescriptors;
		this.userInputPackage = builder.userInputPackage;
		this.userInputFieldTypes = builder.userInputFieldTypes;
		this.userInputInterface = builder.userInputInterface;
		this.userInputImports = builder.userInputImports;
		this.superClass = builder.superClass;
		this.userInputSuperClass = builder.userInputSuperClass;

	}

	public static class Builder {
		private CLASS_TYPE							classType;
		private RELATION_TYPE						relationTypeToParent;
		private String								toPattern;
		private String								subPackage;
		private String								project;
		private String								sourcePath;
		private String								packge;
		private String								classHeader;
		private String								classBody;
		private String								classInsideBody;
		private String[]							methodAnnotations;
		private String[]							classAnnotations;
		private String[]							fieldAnnotations;
		private boolean								createDefaultConstructor;
		private boolean								createMethodBody;
		private boolean								createInstanceConstructor;
		private boolean								createEqualsHashcode;
		private boolean								createToString;
		private String[]							superTypes;
		private String[]							implementTypes;
		private String[]							importTypes;
		private boolean								inclInstance;
		private boolean								finalClass;
		private boolean								inclGetterSetterForInstance;
		private boolean								createFields;
		private String								createFieldsName;
		private boolean								createUnitTest;
		/*private boolean								createReturnVariable;
		private String								returnVariableName;*/
		private boolean								convertMethodParam;
		private String								convertMethodParamFrom;
		private String								convertMethodParamTo;
		private CreateSimilarDescriptorClass		parentDescriptor;
		private List<CreateSimilarDescriptorClass>	relatedDescriptors;
		private IPackageFragment					userInputPackage;
		private IType[]								userInputFieldTypes;
		private List<IType>							userInputInterface;
		private List<IType>							userInputImports;
		private String								superClass	= EMPTY_STR;
		private IType								userInputSuperClass;

		public Builder withClassType(final CLASS_TYPE classType) {
			this.classType = classType;
			return this;
		}

		public Builder withRelationTypeToParent(final RELATION_TYPE relationTypeToParent) {
			this.relationTypeToParent = relationTypeToParent;
			return this;
		}

		public Builder withToPattern(final String toPattern) {
			this.toPattern = toPattern;
			return this;
		}

		public Builder withSubPackage(final String subPackage) {
			this.subPackage = subPackage;
			return this;
		}

		public Builder withProject(final String project) {
			this.project = project;
			return this;
		}

		public Builder withSourcePath(final String sourcePath) {
			this.sourcePath = sourcePath;
			return this;
		}

		public Builder withPackge(final String packge) {
			this.packge = packge;
			return this;
		}

		public Builder withClassHeader(final String classHeader) {
			this.classHeader = classHeader;
			return this;
		}

		public Builder withClassBody(final String classBody) {
			this.classBody = classBody;
			return this;
		}

		public Builder withClassInsideBody(final String classInsideBody) {
			this.classInsideBody = classInsideBody;
			return this;
		}

		public Builder withMethodAnnotations(final String[] methodAnnotations) {
			this.methodAnnotations = methodAnnotations;
			return this;
		}

		public Builder withClassAnnotations(final String[] classAnnotations) {
			this.classAnnotations = classAnnotations;
			return this;
		}

		public Builder withFieldAnnotations(final String[] fieldAnnotations) {
			this.fieldAnnotations = fieldAnnotations;
			return this;
		}

		public Builder withCreateDefaultConstructor(final boolean createDefaultConstructor) {
			this.createDefaultConstructor = createDefaultConstructor;
			return this;
		}

		public Builder withCreateMethodBody(final boolean createMethodBody) {
			this.createMethodBody = createMethodBody;
			return this;
		}

		public Builder withCreateInstanceConstructor(final boolean createInstanceConstructor) {
			this.createInstanceConstructor = createInstanceConstructor;
			return this;
		}

		public Builder withCreateEqualsHashcode(final boolean createEqualsHashcode) {
			this.createEqualsHashcode = createEqualsHashcode;
			return this;
		}

		public Builder withCreateToString(final boolean createToString) {
			this.createToString = createToString;
			return this;
		}

		public Builder withSuperTypes(final String[] superTypes) {
			this.superTypes = superTypes;
			return this;
		}

		public Builder withImplementTypes(final String[] implementTypes) {
			this.implementTypes = implementTypes;
			return this;
		}

		public Builder withImportTypes(final String[] importTypes) {
			this.importTypes = importTypes;
			return this;
		}

		public Builder withInclInstance(final boolean inclInstance) {
			this.inclInstance = inclInstance;
			return this;
		}

		public Builder withFinalClass(final boolean finalClass) {
			this.finalClass = finalClass;
			return this;
		}

		public Builder withInclGetterSetterForInstance(final boolean inclGetterSetterForInstance) {
			this.inclGetterSetterForInstance = inclGetterSetterForInstance;
			return this;
		}

		public Builder withCreateFields(final boolean createFields) {
			this.createFields = createFields;
			return this;
		}

		public Builder withCreateFieldsName(final String createFieldsName) {
			this.createFieldsName = createFieldsName;
			return this;
		}

		public Builder withCreateUnitTest(final boolean createUnitTest) {
			this.createUnitTest = createUnitTest;
			return this;
		}

		/*public Builder withCreateReturnVariable(final boolean createReturnVariable) {
			this.createReturnVariable = createReturnVariable;
			return this;
		}

		public Builder withReturnVariableName(final String returnVariableName) {
			this.returnVariableName = returnVariableName;
			return this;
		}*/

		public Builder withConvertMethodParam(final boolean convertMethodParam) {
			this.convertMethodParam = convertMethodParam;
			return this;
		}

		public Builder withConvertMethodParamFrom(final String convertMethodParamFrom) {
			this.convertMethodParamFrom = convertMethodParamFrom;
			return this;
		}

		public Builder withConvertMethodParamTo(final String convertMethodParamTo) {
			this.convertMethodParamTo = convertMethodParamTo;
			return this;
		}

		public Builder withParentDescriptor(final CreateSimilarDescriptorClass parentDescriptor) {
			this.parentDescriptor = parentDescriptor;
			return this;
		}

		public Builder withRelatedDescriptors(final List<CreateSimilarDescriptorClass> relatedDescriptors) {
			this.relatedDescriptors = relatedDescriptors;
			return this;
		}

		public Builder withUserInputPackage(final IPackageFragment userInputPackage) {
			this.userInputPackage = userInputPackage;
			return this;
		}

		public Builder withUserInputFieldTypes(final IType[] userInputFieldTypes) {
			this.userInputFieldTypes = userInputFieldTypes;
			return this;
		}

		public Builder withUserInputInterface(final List<IType> userInputInterface) {
			this.userInputInterface = userInputInterface;
			return this;
		}

		public Builder withUserInputImports(final List<IType> userInputImports) {
			this.userInputImports = userInputImports;
			return this;
		}

		public Builder withSuperClass(final String superClass) {
			this.superClass = superClass;
			return this;
		}

		public Builder withUserInputSuperClass(final IType userInputSuperClass) {
			this.userInputSuperClass = userInputSuperClass;
			return this;
		}

		public CreateSimilarDescriptorClass build() {
			return new CreateSimilarDescriptorClass(this);
		}
	}
}
