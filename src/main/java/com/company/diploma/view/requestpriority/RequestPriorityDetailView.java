package com.company.diploma.view.requestpriority;

import com.company.diploma.entity.RequestPriority;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "request-priorities/:id", layout = MainView.class)
@ViewController(id = "RequestPriority.detail")
@ViewDescriptor(path = "request-priority-detail-view.xml")
@EditedEntityContainer("requestPriorityDc")
public class RequestPriorityDetailView extends StandardDetailView<RequestPriority> {
}