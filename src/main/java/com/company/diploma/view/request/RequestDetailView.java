package com.company.diploma.view.request;

import com.company.diploma.entity.Request;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "requests/:id", layout = MainView.class)
@ViewController(id = "Request.detail")
@ViewDescriptor(path = "request-detail-view.xml")
@EditedEntityContainer("requestDc")
public class RequestDetailView extends StandardDetailView<Request> {
}