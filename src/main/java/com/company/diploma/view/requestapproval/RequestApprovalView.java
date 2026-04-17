package com.company.diploma.view.requestapproval;

import com.company.diploma.entity.Request;
import com.company.diploma.entity.RequestComment;
import com.company.diploma.entity.Participant;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.textfield.TextArea;
import io.jmix.bpmflowui.processform.ProcessFormContext;
import io.jmix.bpmflowui.processform.annotation.Outcome;
import io.jmix.bpmflowui.processform.annotation.ProcessForm;
import io.jmix.bpmflowui.processform.annotation.ProcessVariable;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.model.InstanceLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

@ViewController("Request.approval")
@ViewDescriptor("request-approval-view.xml")
@ProcessForm(
        outcomes = {
                @Outcome(id = "approve"),
                @Outcome(id = "reject"),
                @Outcome(id = "comment")
        },
        allowedProcessKeys = {"request-approval"}
)
public class RequestApprovalView extends StandardView {

    @Autowired
    private DataManager dataManager;

    @ViewComponent
    private InstanceContainer<Request> requestDc;

    @Autowired
    private ProcessFormContext processFormContext;

    @ProcessVariable(name = "requestId")
    private UUID requestId;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        Request loadedRequest = dataManager.load(Request.class)
                .id(requestId)
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("initiator", initiatorPlan -> {
                        initiatorPlan.addFetchPlan(FetchPlan.BASE);
                        initiatorPlan.add("user", userPlan -> {
                            userPlan.addFetchPlan(FetchPlan.BASE);
                            userPlan.add("interests", FetchPlan.INSTANCE_NAME);
                        });
                    });
                    plan.add("comments", FetchPlan.BASE);
                })
                .one();

        requestDc.setItem(loadedRequest);
    }

    private Participant getCurrentParticipant() {
        return dataManager.load(Participant.class)
                .query("select p from Participant p where p.user = :user and p.workspace = :workspace")
                .parameter("user", currentAuthentication.getUser()) // текущий залогиненный юзер
                .parameter("workspace", requestDc.getItem().getWorkspace()) // воркспейс из текущей заявки
                .one();
    }

    // --- КНОПКИ ---

    @Subscribe("approveBtn")
    protected void onApproveBtnClick(ClickEvent<JmixButton> event) {
        Participant participant = getCurrentParticipant();

        processFormContext.taskCompletion()
                .withOutcome("approve")
                .addProcessVariable("approverUsername", participant.getUser())
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

    @Subscribe("commentBtn")
    public void onCommentBtnClick(ClickEvent<JmixButton> event) {
        processFormContext.taskCompletion()
                .withOutcome("comment")
                .complete();

        closeWithDefaultAction();
    }

    // --- КОММЕНТАРИИ ---

    @ViewComponent
    private TextArea commentField;

    @ViewComponent
    private CollectionContainer<RequestComment> commentsDc;

    @Autowired
    private Notifications notifications;

    @Subscribe("addCommentBtn")
    public void onAddCommentBtnClick(ClickEvent<JmixButton> event) {
        String text = commentField.getValue();

        if (text == null || text.isBlank()) {
            notifications.create("Введите текст комментария")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        Request request = requestDc.getItem();
        Participant currentUser = getCurrentParticipant();

        RequestComment comment = dataManager.create(RequestComment.class);
        comment.setRequest(request);
        comment.setAuthor(currentUser);
        comment.setMessage(text);
        comment.setCreatedAt(new Date());

        dataManager.save(comment);

        commentsDc.getMutableItems().add(comment);
        commentField.clear();
    }
}