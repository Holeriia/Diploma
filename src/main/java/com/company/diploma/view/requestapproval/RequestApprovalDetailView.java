package com.company.diploma.view.requestapproval;

import com.company.diploma.entity.RequestApproval;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "request-approvals/:id", layout = MainView.class)
@ViewController(id = "RequestApproval.detail")
@ViewDescriptor(path = "request-approval-detail-view.xml")
@EditedEntityContainer("requestApprovalDc")
public class RequestApprovalDetailView extends StandardDetailView<RequestApproval> {
}