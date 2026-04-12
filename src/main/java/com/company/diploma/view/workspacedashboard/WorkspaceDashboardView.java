package com.company.diploma.view.workspacedashboard;


import com.company.diploma.entity.Request;
import com.company.diploma.entity.RequestStatus;
import com.company.diploma.view.main.MainView;
import com.company.diploma.view.request.RequestCreateView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.action.list.EditAction;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "workspace-dashboard-view", layout = MainView.class)
@ViewController(id = "WorkspaceDashboardView")
@ViewDescriptor(path = "workspace-dashboard-view.xml")
public class WorkspaceDashboardView extends StandardView {

    @ViewComponent
    private DataGrid<Request> requestsGrid;

    @ViewComponent("requestsGrid.editAction")
    private EditAction<Request> editAction;

    @Autowired
    private ViewNavigators viewNavigators;

    @Subscribe("requestsGrid.editAction")
    public void onRequestsGridEditActionPerformed(ActionPerformedEvent event) {
        Request request = requestsGrid.getSingleSelectedItem();
        if (request == null) {
            return;
        }

        if (request.getStatus() == RequestStatus.DRAFT) {
            // Открываем кастомный вид вместо стандартного detail
            viewNavigators.detailView(this, Request.class)
                    .withViewId("Request.create")
                    .editEntity(request)
                    .navigate();
        } else {
            // Для остальных статусов - стандартное поведение list_edit
            editAction.execute();
        }
    }
}