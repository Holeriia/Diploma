package com.company.diploma.view.topicassignment;

import com.company.diploma.app.AssignmentProcessService;
import com.company.diploma.entity.*;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;
import com.vaadin.flow.component.ClickEvent;
import org.springframework.beans.factory.annotation.Autowired;

@ViewController("TopicAssignmentView")
@ViewDescriptor("topic-assignment-view.xml")
@EditedEntityContainer("assignmentDc")
public class TopicAssignmentView extends StandardDetailView<Assignment> {

    @ViewComponent
    private TypedTextField<String> newTopicNameField;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private Notifications notifications;

    @Subscribe(id = "showRequestBtn")
    public void onShowRequestBtnClick(final ClickEvent<JmixButton> event) {
        Request request = getEditedEntity().getRequest();
        if (request != null) {
            dialogWindows.detail(this, Request.class)
                    .editEntity(request)
                    .open();
        }
    }
    @Autowired
    private AssignmentProcessService assignmentProcessService;

    @Subscribe(id = "saveAndSendBtn")
    public void onSaveAndSendBtnClick(final ClickEvent<JmixButton> event) {
        String newName = newTopicNameField.getValue();

        // Если введено имя новой темы, создаем и сохраняем её
        if (newName != null && !newName.isBlank()) {
            Topic newTopic = dataManager.create(Topic.class);
            newTopic.setName(newName);
            // Устанавливаем автора темы (ментор из текущего назначения)
            newTopic.setAuthor(getEditedEntity().getMentor());

            Topic savedTopic = dataManager.save(newTopic);
            getEditedEntity().setTopic(savedTopic);
        }

        if (getEditedEntity().getTopic() == null) {
            notifications.create("Выберите тему или введите название новой")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        // 1. Сохраняем основную сущность
        Assignment assignment = getEditedEntity();
        assignment.setStatus(AssignmentStatus.FOR_APPROVAL);
        dataManager.save(assignment);

        // 2. Запускаем процесс через сервис
        assignmentProcessService.startProcess(assignment);

        closeWithSave();

    }

    @Subscribe(id = "closeBtn")
    public void onCloseBtnClick(final ClickEvent<JmixButton> event) {
        // Закрыть без сохранения
        closeWithDiscard();
    }
}