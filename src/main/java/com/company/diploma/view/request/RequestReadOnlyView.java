package com.company.diploma.view.request;

import com.company.diploma.entity.Request;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "requestsReadOnly/:id", layout = MainView.class)
@ViewController(id = "Request.read")
@ViewDescriptor(path = "request-readonly-view.xml")
@EditedEntityContainer("requestDc")
public class RequestReadOnlyView extends StandardDetailView<Request> {

    @Subscribe
    public void onReady(ReadyEvent event) {
        // делаем весь экран только для чтения
        setReadOnly(true);
    }

}