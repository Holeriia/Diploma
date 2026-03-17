package com.company.diploma.view.workspace;

import com.company.diploma.entity.Workspace;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "workspaces", layout = MainView.class)
@ViewController(id = "Workspace.list")
@ViewDescriptor(path = "workspace-list-view.xml")
@LookupComponent("workspacesDataGrid")
@DialogMode(width = "64em")
public class WorkspaceListView extends StandardListView<Workspace> {
}