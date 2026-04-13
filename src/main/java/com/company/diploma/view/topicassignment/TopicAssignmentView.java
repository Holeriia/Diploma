package com.company.diploma.view.topicassignment;

import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.Request;
import com.company.diploma.entity.Topic;
import com.company.diploma.entity.TopicStatus;
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

    @ViewComponent
    private EntityComboBox<Topic> topicField;

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

        closeWithSave();

        notifications.create("Назначено и отправлено на согласование")
                .withType(Notifications.Type.SUCCESS)
                .show();
    }

    @Subscribe(id = "closeBtn")
    public void onCloseBtnClick(final ClickEvent<JmixButton> event) {
        // Закрыть без сохранения
        closeWithDiscard();
    }
}