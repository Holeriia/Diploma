package com.company.diploma.view.requestcomment;

import com.company.diploma.entity.Participant;
import com.company.diploma.entity.Request;
import com.company.diploma.entity.RequestComment;
import com.company.diploma.entity.User;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.textfield.TextArea;
import io.jmix.bpmflowui.processform.ProcessFormContext;
import io.jmix.bpmflowui.processform.annotation.Outcome;
import io.jmix.bpmflowui.processform.annotation.ProcessForm;
import io.jmix.bpmflowui.processform.annotation.ProcessVariable;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.UUID;

@ViewController(id = "RequestCommentView")
@ViewDescriptor(path = "request-comment-view.xml")
@EditedEntityContainer("requestDc")
@ProcessForm(
        outcomes = {
                @Outcome(id = "reply")
        },
        allowedProcessKeys = {"request-approval"}
)
public class RequestCommentView extends StandardDetailView<Request> {

    @ViewComponent
    private InstanceContainer<Request> requestDc;

    @ViewComponent
    private CollectionContainer<RequestComment> commentsDc;

    @ViewComponent
    private TextArea commentField;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private Notifications notifications;

    @Autowired
    private ProcessFormContext processFormContext;

    @ProcessVariable(name = "requestId")
    private UUID requestId;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        // Загружаем Request по ID из переменной процесса
        if (requestId != null) {
            requestDc.setItem(
                    dataManager.load(Request.class)
                            .id(requestId)
                            .one()
            );
        }
    }

    @Subscribe(id = "addCommentBtn")
    public void onAddCommentBtnClick(final ClickEvent<JmixButton> event) {
        String text = commentField.getValue();
        if (text == null || text.isBlank()) {
            notifications.create("Введите текст комментария")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        Request request = requestDc.getItem();
        User currentUser = (User) currentAuthentication.getUser();

        // Поиск Participant, привязанного к текущему пользователю
        Participant author = dataManager.load(Participant.class)
                .query("select p from Participant p where p.user.id = :userId")
                .parameter("userId", currentUser.getId())
                .optional()
                .orElseThrow(() -> new IllegalStateException("Participant not found for current user"));

        // Создание и сохранение комментария
        RequestComment newComment = dataManager.create(RequestComment.class);
        newComment.setRequest(request);
        newComment.setAuthor(author);
        newComment.setMessage(text);
        newComment.setCreatedAt(new Date());

        dataManager.save(newComment);

        // Обновляем список в UI
        commentsDc.getMutableItems().add(newComment);
        commentField.clear();
    }

    @Subscribe(id = "replyCommentBtn")
    public void onReplyCommentBtnClick(final ClickEvent<JmixButton> event) {
        // Завершение задачи BPM
        processFormContext.taskCompletion()
                .withOutcome("reply")
                .complete();
        closeWithDefaultAction();
    }
}