package org.fastcode.common;

import java.util.ArrayList;
import java.util.List;

import org.fastcode.common.FastCodeConstants.ACTION_ENTITY;
import org.fastcode.common.FastCodeConstants.ACTION_TYPE;

public class Action {

	private ACTION_TYPE		type;
	private String			source;
	private ACTION_ENTITY	entity;
	private Object			target;
	private String			snippet;
	private List<Action>	subAction	= new ArrayList<Action>();
	private String			entityName;
	private String			nodeName;
	private String			rootNodeName;
	private Object			folderPath;
	private Object			packge;
	private Object			project;
	private String			labelMsg;
	private String			typeToCreate;
	private boolean			optional;
	private String			localVarType;
	private String			localVarSelectionMode;
	private String			localVarName;
	private String			projectSrcPath;
	private String			delimiter;
	private boolean			exist;
	private final boolean			overrideMethods;
	private String			classToImport;
	private String			imports;

	public ACTION_TYPE getType() {
		return this.type;
	}

	public void setType(final ACTION_TYPE type) {
		this.type = type;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(final String source) {
		this.source = source;
	}

	public ACTION_ENTITY getEntity() {
		return this.entity;
	}

	public void setEntity(final ACTION_ENTITY entity) {
		this.entity = entity;
	}

	public Object getTarget() {
		return this.target;
	}

	public void setTarget(final Object target) {
		this.target = target;
	}

	public String getSnippet() {
		return this.snippet;
	}

	public void setSnippet(final String snippet) {
		this.snippet = snippet;
	}

	public List<Action> getSubAction() {
		return this.subAction;
	}

	public void setSubAction(final List<Action> subAction) {
		this.subAction = subAction;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public void setEntityName(final String entityName) {
		this.entityName = entityName;
	}

	public String getNodeName() {
		return this.nodeName;
	}

	public void setNodeName(final String nodeName) {
		this.nodeName = nodeName;
	}

	public String getRootNodeName() {
		return this.rootNodeName;
	}

	public void setRootNodeName(final String rootNodeName) {
		this.rootNodeName = rootNodeName;
	}

	public Object getFolderPath() {
		return this.folderPath;
	}

	public void setFolderPath(final Object folderPath) {
		this.folderPath = folderPath;
	}

	public Object getPackge() {
		return this.packge;
	}

	public void setPackge(final Object packge) {
		this.packge = packge;
	}

	public Object getProject() {
		return this.project;
	}

	public void setProject(final Object project) {
		this.project = project;
	}

	public String getLabelMsg() {
		return this.labelMsg;
	}

	public void setLabelMsg(final String labelMsg) {
		this.labelMsg = labelMsg;
	}

	public String getTypeToCreate() {
		return this.typeToCreate;
	}

	public void setTypeToCreate(final String typeToCreate) {
		this.typeToCreate = typeToCreate;
	}

	public boolean isOptional() {
		return this.optional;
	}

	public void setOptional(final boolean optional) {
		this.optional = optional;
	}

	public String getLocalVarType() {
		return this.localVarType;
	}

	public void setLocalVarType(final String localVarType) {
		this.localVarType = localVarType;
	}

	public String getLocalVarSelectionMode() {
		return this.localVarSelectionMode;
	}

	public void setLocalVarSelectionMode(final String localVarSelectionMode) {
		this.localVarSelectionMode = localVarSelectionMode;
	}

	public String getLocalVarName() {
		return this.localVarName;
	}

	public void setLocalVarName(final String localVarName) {
		this.localVarName = localVarName;
	}

	public String getProjectSrcPath() {
		return this.projectSrcPath;
	}

	public void setProjectSrcPath(final String projectSrcPath) {
		this.projectSrcPath = projectSrcPath;
	}

	public String getDelimiter() {
		return this.delimiter;
	}

	public void setDelimiter(final String delimiter) {
		this.delimiter = delimiter;
	}

	public boolean isExist() {
		return this.exist;
	}

	public void setExist(final boolean exist) {
		this.exist = exist;
	}

	public boolean isOverrideMethods() {
		return this.overrideMethods;
	}

	public String getClassToImport() {
		return this.classToImport;
	}

	public void setClassToImport(final String classToImport) {
		this.classToImport = classToImport;
	}

	public String getImports() {
		return this.imports;
	}

	public void setImports(final String imports) {
		this.imports = imports;
	}

	private Action(final Builder builder) {
		this.type = builder.type;
		this.source = builder.source;
		this.entity = builder.entity;
		this.target = builder.target;
		this.snippet = builder.snippet;
		this.subAction = builder.subAction;
		this.entityName = builder.entityName;
		this.nodeName = builder.nodeName;
		this.rootNodeName = builder.rootNodeName;
		setFolderPath(builder.folderPath);
		this.packge = builder.packge;
		this.project = builder.project;
		this.labelMsg = builder.labelMsg;
		this.optional = builder.optional;
		this.typeToCreate = builder.typeToCreate;
		this.localVarType = builder.localVarType;
		this.localVarSelectionMode = builder.localVarSelectionMode;
		this.localVarName = builder.localVarName;
		this.projectSrcPath = builder.projectSrcPath;
		this.delimiter = builder.delimiter;
		this.exist = builder.exist;
		this.overrideMethods = builder.overrideMethods;
		this.classToImport = builder.classToImport;
		this.imports = builder.imports;
	}

	public static class Builder {
		private ACTION_TYPE		type;
		private String			source;
		private ACTION_ENTITY	entity;
		private Object			target;
		private String			snippet;
		private List<Action>	subAction;
		private String			entityName;
		private String			nodeName;
		private String			rootNodeName;
		private Object			folderPath;
		private Object			packge;
		private Object			project;
		private String			labelMsg;
		private boolean			optional;
		private String			typeToCreate;
		private String			localVarType;
		private String			localVarSelectionMode;
		private String			localVarName;
		private String			projectSrcPath;
		private String			delimiter;
		private boolean			exist;
		private boolean			overrideMethods;
		private String			classToImport;
		private String			imports;

		public Builder withType(final ACTION_TYPE type) {
			this.type = type;
			return this;
		}

		public Builder withSource(final String source) {
			this.source = source;
			return this;
		}

		public Builder withEntity(final ACTION_ENTITY entity) {
			this.entity = entity;
			return this;
		}

		public Builder withTarget(final Object target) {
			this.target = target;
			return this;
		}

		public Builder withSnippet(final String snippet) {
			this.snippet = snippet;
			return this;
		}

		public Builder withSubAction(final List<Action> subAction) {
			this.subAction = subAction;
			return this;
		}

		public Builder withEntityName(final String entityName) {
			this.entityName = entityName;
			return this;
		}

		public Builder withNodeName(final String nodeName) {
			this.nodeName = nodeName;
			return this;
		}

		public Builder withRootNodeName(final String rootNodeName) {
			this.rootNodeName = rootNodeName;
			return this;
		}

		public Builder withFolderPath(final Object folderPath) {
			this.folderPath = folderPath;
			return this;
		}

		public Builder withPackge(final Object packge) {
			this.packge = packge;
			return this;
		}

		public Builder withProject(final Object project) {
			this.project = project;
			return this;
		}

		public Builder withLabelMsg(final String labelMsg) {
			this.labelMsg = labelMsg;
			return this;
		}

		public Builder withOptional(final boolean optional) {
			this.optional = optional;
			return this;
		}

		public Builder withTypeToCreate(final String typeToCreate) {
			this.typeToCreate = typeToCreate;
			return this;
		}

		public Builder withLocalVarType(final String localVarType) {
			this.localVarType = localVarType;
			return this;
		}

		public Builder withLocalVarSelectionMode(final String localVarSelectionMode) {
			this.localVarSelectionMode = localVarSelectionMode;
			return this;
		}

		public Builder withLocalVarName(final String localVarName) {
			this.localVarName = localVarName;
			return this;
		}

		public Builder withProjectSrcPath(final String projectSrcPath) {
			this.projectSrcPath = projectSrcPath;
			return this;
		}

		public Builder withDelimiter(final String delimiter) {
			this.delimiter = delimiter;
			return this;
		}

		public Builder withExist(final boolean exist) {
			this.exist = exist;
			return this;
		}

		public Builder withOverrideMethods(final boolean overrideMethods) {
			this.overrideMethods = overrideMethods;
			return this;
		}

		public Builder withClassToImport(final String classToImport) {
			this.classToImport = classToImport;
			return this;
		}

		public Builder withImports(final String imports) {
			this.imports = imports;
			return this;
		}

		public Action build() {
			return new Action(this);
		}
	}
}
