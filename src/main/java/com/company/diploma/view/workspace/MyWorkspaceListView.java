package com.company.diploma.view.workspace;

import com.company.diploma.entity.Workspace;
import com.company.diploma.view.main.MainView;
import com.company.diploma.view.workspacedashboard.WorkspaceDashboardView;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.Route;
import io.jmix.core.AccessManager;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.accesscontext.UiShowViewContext;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;


@Route(value = "myworkspaces", layout = MainView.class)
@ViewController(id = "MyWorkspace.list")
@ViewDescriptor(path = "my-workspace-list-view.xml")
@LookupComponent("workspacesDataGrid")
@DialogMode(width = "64em")
public class MyWorkspaceListView extends StandardListView<Workspace> {

    @ViewComponent
    private DataGrid<Workspace> workspacesDataGrid;

    @Autowired
    private UiComponents uiComponents;

    @Autowired
    private ViewNavigators viewNavigators;

    @Subscribe
    public void onInit(InitEvent event) {
        // Используем setRenderer для уже существующей колонки
        workspacesDataGrid.getColumnByKey("goToApplications")
                .setRenderer(new ComponentRenderer<>(this::createGoButton));
    }

    private JmixButton createGoButton(Workspace workspace) {
        JmixButton button = uiComponents.create(JmixButton.class);
        button.setText("→");
        button.setHeight("var(--lumo-size-m)");
        button.addClickListener(click ->
                openApplicationsFor(workspace)
        );
        return button;
    }

    private void openApplicationsFor(Workspace workspace) {
        // передаём id workspace как query‑параметр
        viewNavigators.view(this, WorkspaceDashboardView.class)
                .withQueryParameters(
                        QueryParameters.of("workspaceId", workspace.getId().toString())
                )
                .withBackwardNavigation(true)
                .navigate();
    }

    @Autowired
    private Notifications notifications;
    @Autowired
    private AccessManager accessManager;

    @Subscribe("workspacesDataGrid.editAction")
    public void onWorkspacesDataGridEditAction(final ActionPerformedEvent event) {
        Workspace selectedWorkspace = workspacesDataGrid.getSingleSelectedItem();

        if (selectedWorkspace == null) {
            return;
        }

        // Проверяем право открывать вьюху MyWorkspace.detail
        UiShowViewContext viewContext = new UiShowViewContext("MyWorkspace.detail");
        accessManager.applyRegisteredConstraints(viewContext);
        boolean hasAccess = viewContext.isPermitted();

        if (!hasAccess) {
            notifications.create("Доступ ограничен")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        // Если доступ есть — открываем detail вручную
        viewNavigators.detailView(workspacesDataGrid)
                .withViewId("MyWorkspace.detail")
                .editEntity(selectedWorkspace)
                .navigate();
    }
}