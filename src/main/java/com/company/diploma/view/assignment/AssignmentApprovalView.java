package com.company.diploma.view.assignment;

import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.AssignmentComment;
import com.company.diploma.entity.Participant;
import com.company.diploma.entity.User;
import com.company.diploma.view.assignmentcommentdialog.AssignmentCommentDialog;
import com.vaadin.flow.component.ClickEvent;
import io.jmix.bpmflowui.processform.ProcessFormContext;
import io.jmix.bpmflowui.processform.annotation.Outcome;
import io.jmix.bpmflowui.processform.annotation.ProcessForm;
import io.jmix.bpmflowui.processform.annotation.ProcessVariable;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.DialogWindows;
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
    @Autowired
    private DialogWindows dialogWindows;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private CurrentAuthentication currentAuthentication;

    @ProcessVariable(name = "assignmentId")
    private UUID assignmentId;

    @ViewComponent
    private InstanceContainer<Assignment> assignmentDc;
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
        processFormContext.taskCompletion().withOutcome("approve").complete();
        closeWithDefaultAction();
    }

    @Subscribe("rejectBtn")
    protected void onRejectBtnClick(ClickEvent<JmixButton> event) {
        // Открываем диалоговое окно
        DialogWindow<AssignmentCommentDialog> window = dialogWindows.view(this, AssignmentCommentDialog.class).build();

        window.addAfterCloseListener(afterCloseEvent -> {
            if (afterCloseEvent.closedWith(StandardOutcome.SAVE)) {
                String commentText = afterCloseEvent.getView().getComment();
                saveCommentAndReject(commentText);
            }
        });
        window.open();
    }

    private void saveCommentAndReject(String text) {
        Assignment assignment = assignmentDc.getItem();

        // Получаем участника (автора комментария)
        User currentUser = (User) currentAuthentication.getUser();
        Participant author = dataManager.load(Participant.class)
                .query("select p from Participant p where p.user.id = :userId")
                .parameter("userId", currentUser.getId())
                .one();

        // Создаем и сохраняем комментарий
        AssignmentComment comment = dataManager.create(AssignmentComment.class);
        comment.setAssignment(assignment);
        comment.setAuthor(author);
        comment.setMessage(text);
        dataManager.save(comment);

        // Завершаем процесс
        processFormContext.taskCompletion()
                .withOutcome("reject")
                .complete();

        closeWithDefaultAction();
    }
}