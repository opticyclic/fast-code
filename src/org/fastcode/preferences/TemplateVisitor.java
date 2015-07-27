package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.DOT;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.visitor.BaseVisitor;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.templates.util.FastCodeLocalVariables;
import org.fastcode.templates.util.ForLoopVariable;
import org.fastcode.templates.util.InvalidVariables;
import org.fastcode.templates.util.SetVariable;
import org.fastcode.templates.util.ValidVariables;
import org.fastcode.util.TemplateDetailsForVelocity;
import org.fastcode.util.VelocityUtil;

public class TemplateVisitor extends BaseVisitor {
	final List<String>					varList				= new ArrayList<String>();
	final List<String>					localVars			= new ArrayList<String>();
	final List<String>					setVars				= new ArrayList<String>();
	boolean								isSetDirective		= false;
	String								invalidVariable		= EMPTY_STR;
	boolean								isForDirective		= false;
	boolean								isMacrodirective	= false;
	String								forLoopLocalVar		= EMPTY_STR;
	final List<String>					tmpvarList			= new ArrayList<String>();
	final List<FastCodeLocalVariables>	localVarsList		= new ArrayList<FastCodeLocalVariables>();
	List<InvalidVariables>				invalidVarsList		= new ArrayList<InvalidVariables>();
	List<ValidVariables>				validVarsList		= new ArrayList<ValidVariables>();
	boolean								inFCTag				= false;
	String								tagName				= EMPTY_STR;

	@Override
	public Object visit(final ASTReference node, final Object data) {
		final String name = node.literal();
		//System.out.println("visit ASTReference" + name);
		final String varName = VelocityUtil.getInstance().getVariableName(name);
		if (this.isSetDirective) {

			if (!this.setVars.contains(varName)) {
				this.setVars.add(varName);
			}
			final SetVariable setVar = new SetVariable(varName, node.getFirstToken().beginLine, node.getLastToken().endLine);
			this.localVarsList.add(setVar);
		}
		boolean isValidVar = false;
		if (!this.tmpvarList.contains(varName)) {
			if (((TemplateDetailsForVelocity) data).isDoValidation()) {
				isValidVar = new VelocityUtil().validateVariables(varName, this.isForDirective, this.setVars, this.isMacrodirective,
						this.forLoopLocalVar, data);
			} else {
				isValidVar = true;
			}
		} else {
			isValidVar = true;
		}

		if (isValidVar) {
			if (!this.varList.contains(name) && !this.isSetDirective) {
				this.varList.add(name);
				this.validVarsList.add(new ValidVariables(name, node.getLine(), node.getColumn()));
			}
			if (!this.tmpvarList.contains(varName)) {
				this.tmpvarList.add(varName);
			}
		} else {
			if (!this.invalidVariable.contains(varName)) {
				this.invalidVariable = this.invalidVariable + COMMA + varName;
			}
//				System.out.println("column" + node.getColumn());
//				System.out.println(node.getInfo());
//				System.out.println(node.getLine());
				this.invalidVarsList.add(new InvalidVariables(varName, node.getLine(), node.getColumn()));

		}
		this.isSetDirective = false;
		return super.visit(node, data);
	}

	public List<String> getVarList() {
		return this.varList;
	}

	@Override
	public Object visit(final ASTDirective node, final Object data) {
		//this.localVarsList.clear();
//		System.out.println("visit ASTDirective" + node.getDirectiveName());

		if (node.getDirectiveName().equalsIgnoreCase("foreach")) {
			/*System.out.println(data);
			System.out.println(node.getLine());
			System.out.println(node.state);
			System.out.println(node.getInfo());
			System.out.println(node.getLastToken().endLine);
			System.out.println(node.getLastToken().beginLine);
			System.out.println(node.getFirstToken().beginLine);
			System.out.println(node.getFirstToken().endLine);*/
			this.isForDirective = true;
			this.forLoopLocalVar = node.jjtGetChild(0).literal().substring(1);
			this.localVars.add(node.jjtGetChild(0).literal());
			this.localVars.add(node.jjtGetChild(2).literal());
			/*System.out.println(node.jjtGetChild(2).getType());
			System.out.println(node.jjtGetChild(0).literal());
			System.out.println(node.jjtGetChild(2).literal());*/
			final ForLoopVariable forLoopVar = new ForLoopVariable(this.forLoopLocalVar, node.getFirstToken().beginLine,
					node.getLastToken().endLine);
			//updatelocalVarList(this.forLoopLocalVar, node.getFirstToken().beginLine, node.getLastToken().endLine);
			this.localVarsList.add(forLoopVar);
		}
		if (node.getDirectiveName().equalsIgnoreCase("macro")) {
			this.isMacrodirective = true;
		} else {
			for (final FastCodeAdditionalParams additionalParams : getEmptyListForNull(((TemplateDetailsForVelocity) data)
					.getAdditionalPram())) {
				if (node.getDirectiveName().equalsIgnoreCase(additionalParams.getName())) {
					this.varList.add(node.getDirectiveName());
					this.validVarsList.add(new ValidVariables(node.getDirectiveName(), node.getLine(), node.getColumn()));
					break;
				}
			}

		}
		node.childrenAccept(this, data);
		this.isForDirective = false;
		this.isMacrodirective = false;
		return data;

	}

	private void updatelocalVarList(final String forLoopLoclVar, final int startLine, final int endLine) {
		boolean update = false;
		ForLoopVariable varToUpdate = null;
		for (final FastCodeLocalVariables fcLocalVars : this.localVarsList) {
			if (fcLocalVars instanceof ForLoopVariable) {
				final ForLoopVariable forLopVar = (ForLoopVariable) fcLocalVars;
				if (forLopVar.getScopeStartLine() == startLine && forLopVar.getScopeEndLine() == endLine) {
					update = true;
					varToUpdate = forLopVar;
					break;
				}
			}
		}

		if (update) {
			this.localVarsList.remove(varToUpdate);
		}

		final ForLoopVariable forLoopVar = new ForLoopVariable(forLoopLoclVar, startLine, endLine);
		this.localVarsList.add(forLoopVar);

	}

	@Override
	public Object visit(final ASTSetDirective node, final Object data) {
		this.isSetDirective = true;

		return super.visit(node, data);
	}

	public List<String> getLocalVars() {
		return this.localVars;
	}

	@Override
	public Object visit(final ASTStringLiteral node, final Object data) {
		String name = node.literal();
//		System.out.println("visit ASTStringLiteral" + name);
		name = name.replaceAll("\"", "");
		if (name.trim().length() > 0) {
			String varName = EMPTY_STR;
			if (name.contains("${")) {
				final String[] tempStrArr = name.split("\\$\\{");
				for (int i = 0; i < tempStrArr.length; i++) {
					if (tempStrArr[i].trim().length() == 0) {
						continue;
					}
					String tempVar = tempStrArr[i];
					if (tempStrArr[i].indexOf("}") < 0) {
						continue;
					}
					tempVar = tempStrArr[i].substring(0, tempStrArr[i].indexOf("}"));
					if (tempVar.contains(DOT)) {
						varName = tempVar.substring(0, tempVar.indexOf(DOT));
					}
					final boolean isValidVar = new VelocityUtil().validateVariables(varName, this.isForDirective, this.setVars,
							this.isMacrodirective, this.forLoopLocalVar, data);
					if (isValidVar) {
						if (!this.varList.contains(tempVar)) {
							this.varList.add("${" + tempVar + "}");
							this.validVarsList.add(new ValidVariables(tempVar, node.getLine(), node.getColumn()));
						}
					} else {
						if (!this.invalidVariable.contains(varName)) {
							this.invalidVariable = this.invalidVariable + COMMA + varName;
						}

					}
				}

			}
		}
		return data;
	}



	public String getInvalidVariable() {
		return this.invalidVariable;
	}

	public void setInvalidVariable(final String invalidVariable) {
		this.invalidVariable = invalidVariable;
	}

	public List<String> getSetVars() {
		return this.setVars;
	}

	public String getForLoopLocalVar() {
		return this.forLoopLocalVar;
	}

	public List<FastCodeLocalVariables> getLocalVarsList() {
		return this.localVarsList;
	}

	public List<InvalidVariables> getInvalidVarsList() {
		return this.invalidVarsList;
	}

	public List<ValidVariables> getValidVarsList() {
		return this.validVarsList;
	}

}
