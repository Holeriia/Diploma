package com.company.diploma.view.requestapproval;

import com.company.diploma.entity.RequestApproval;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "request-approvals", layout = MainView.class)
@ViewController(id = "RequestApproval.list")
@ViewDescriptor(path = "request-approval-list-view.xml")
@LookupComponent("requestApprovalsDataGrid")
@DialogMode(width = "64em")
public class RequestApprovalListView extends StandardListView<RequestApproval> {
}