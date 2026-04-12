package com.company.diploma.view.request;

import com.company.diploma.app.RequestProcessService;
import com.company.diploma.entity.Request;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "request-create/:id", layout = MainView.class)
@ViewController(id = "Request.create")
@ViewDescriptor(path = "request-create-view.xml")
@EditedEntityContainer("requestDc")
public class RequestCreateView extends StandardDetailView<Request> {

    @Autowired
    private RequestProcessService requestProcessService;

    @ViewComponent
    private InstanceContainer<Request> requestDc;

    @Autowired
    private DataManager dataManager;

    @Subscribe(id = "startProcessBtn")
    public void onStartProcessBtnClick(final ClickEvent<JmixButton> event) {

        Request request = requestDc.getItem();
        // 1. сохраняем сущность
        dataManager.save(request);
        // 2. запускаем процесс
        requestProcessService.startProcess(request);
        // 3. закрываем экран
        closeWithDefaultAction();
    }
}