package com.company.diploma.view.request;

import com.company.diploma.app.RequestProcessService;
import com.company.diploma.entity.Request;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.util.OperationResult;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "request-create/:id", layout = MainView.class)
@ViewController(id = "Request.create")
@ViewDescriptor(path = "request-create-view.xml")
@EditedEntityContainer("requestDc")
public class RequestCreateView extends StandardDetailView<Request> {

    @Autowired
    private RequestProcessService requestProcessService;

    @Subscribe(id = "startProcessBtn")
    public void onStartProcessBtnClick(final ClickEvent<JmixButton> event) {
        // 1. Запускаем стандартное сохранение экрана
        OperationResult result = save();
        // 2. Если сохранение прошло успешно, выполняем запуск процесса
        result.then(() -> {
            Request savedRequest = getEditedEntity();
            // Запускаем процесс
            requestProcessService.startProcess(savedRequest);
            // 3. Закрываем экран с результатом COMMIT (это предотвратит вопрос о сохранении)
            close(StandardOutcome.SAVE);
        });
    }
}