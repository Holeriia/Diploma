package com.company.diploma.view.request;

import com.company.diploma.entity.Request;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "request-create/:id", layout = MainView.class)
@ViewController(id = "Request.create")
@ViewDescriptor(path = "request-create-view.xml")
@EditedEntityContainer("requestDc")
public class RequestCreateView extends StandardDetailView<Request> {
}