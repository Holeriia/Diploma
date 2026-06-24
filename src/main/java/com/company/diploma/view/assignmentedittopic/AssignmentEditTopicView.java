package com.company.diploma.view.assignmentedittopic;


import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.Topic;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.bpmflowui.processform.ProcessFormContext;
import io.jmix.bpmflowui.processform.annotation.ProcessForm;
import io.jmix.bpmflowui.processform.annotation.ProcessVariable;
import io.jmix.core.DataManager;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Route(value = "assignment-edit-topic-view", layout = MainView.class)
@ViewController(id = "AssignmentEditTopicView")
@ViewDescriptor(path = "assignment-edit-topic-view.xml")
@ProcessForm(allowedProcessKeys = {"assignment-approval"})
public class AssignmentEditTopicView extends StandardView {

    @Autowired
    private ProcessFormContext processFormContext;
    @Autowired
    private DataManager dataManager;

    @ProcessVariable(name = "assignmentId")
    private UUID assignmentId;

    @ViewComponent
    private InstanceLoader<Assignment> assignmentDl;
    @ViewComponent
    private InstanceContainer<Assignment> assignmentDc;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (assignmentId != null) {
            assignmentDl.setEntityId(assignmentId);
            assignmentDl.load();
        }
    }

    @Subscribe("sendAgainBtn")
    public void onSendAgainBtnClick(ClickEvent<JmixButton> event) {
        Assignment assignment = assignmentDc.getItem();
        Topic topic = assignment.getTopic();

        if (topic != null) {
            // Сохраняем и тему (так как изменили имя), и само назначение
            dataManager.save(topic, assignment);
        } else {
            // Если вдруг тема не выбрана, сохраняем только назначение
            dataManager.save(assignment);
        }

        // Завершаем задачу в BPM
        processFormContext.taskCompletion().complete();
        closeWithDefaultAction();
    }

    @Subscribe("closeBtn")
    public void onCloseBtnClick(ClickEvent<JmixButton> event) {
        closeWithDefaultAction();
    }
}