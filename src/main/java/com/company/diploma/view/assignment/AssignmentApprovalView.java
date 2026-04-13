package com.company.diploma.view.assignment;

import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.Participant;
import com.vaadin.flow.component.ClickEvent;
import io.jmix.bpmflowui.processform.ProcessFormContext;
import io.jmix.bpmflowui.processform.annotation.Outcome;
import io.jmix.bpmflowui.processform.annotation.ProcessForm;
import io.jmix.bpmflowui.processform.annotation.ProcessVariable;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@ViewController("AssignmentApprovalView")
@ViewDescriptor("assignment-approval-view.xml")
@ProcessForm(
        outcomes = {
                @Outcome(id = "approve"),
                @Outcome(id = "reject")
        }
)
public class AssignmentApprovalView extends StandardView {

    @Autowired
    private ProcessFormContext processFormContext;


    @ProcessVariable(name = "assignmentId")
    private UUID assignmentId;

    @ViewComponent
    private InstanceLoader<Assignment> assignmentDl;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        if (assignmentId != null) {
            assignmentDl.setEntityId(assignmentId);
            assignmentDl.load();
        }
    }

    @Subscribe("approveBtn")
    protected void onApproveBtnClick(ClickEvent<JmixButton> event) {
        processFormContext.taskCompletion()
                .withOutcome("approve")
                .complete();

        closeWithDefaultAction();
    }

    @Subscribe("rejectBtn")
    protected void onRejectBtnClick(ClickEvent<JmixButton> event) {
        processFormContext.taskCompletion()
                .withOutcome("reject")
                .complete();

        closeWithDefaultAction();
    }
}