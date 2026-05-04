package com.company.diploma.view.assignmentcommentdialog;


import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.view.*;

@Route(value = "assignment-comment-dialog", layout = MainView.class)
@ViewController("AssignmentCommentDialog")
@ViewDescriptor("assignment-comment-dialog.xml")
public class AssignmentCommentDialog extends StandardView {
    @ViewComponent
    private TextArea commentField;

    public String getComment() {
        return commentField.getValue();
    }

    @Subscribe("okBtn")
    public void onOkBtnClick(ClickEvent<JmixButton> event) {
        if (commentField.getValue() != null && !commentField.getValue().isBlank()) {
            close(StandardOutcome.SAVE);
        }
    }

    @Subscribe("cancelBtn")
    public void onCancelBtnClick(ClickEvent<JmixButton> event) {
        close(StandardOutcome.CLOSE);
    }
}