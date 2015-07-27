package org.fastcode.popup.actions.snippet;

import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.fastcode.common.CreateTableData;

public class CreateNewDatabaseAlterTableSnippetAction extends CreateNewDatabaseTableSnippetAction implements IActionDelegate,
		IWorkbenchWindowActionDelegate {

	@Override
	protected CreateTableData getCreateTableData() throws Exception {
		this.createTableData = new CreateTableData();
		this.createTableData.setAddColumnsToExistingTable(true);
		this.createTableData.setCreateTableWithColumns(false);
		return super.getCreateTableData();
	}
}
