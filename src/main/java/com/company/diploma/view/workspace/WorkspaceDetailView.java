package com.company.diploma.view.workspace;

import com.company.diploma.entity.Workspace;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "workspaces/:id", layout = MainView.class)
@ViewController(id = "Workspace.detail")
@ViewDescriptor(path = "workspace-detail-view.xml")
@EditedEntityContainer("workspaceDc")
public class WorkspaceDetailView extends StandardDetailView<Workspace> {
}