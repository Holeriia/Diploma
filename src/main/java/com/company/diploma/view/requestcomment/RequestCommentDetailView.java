package com.company.diploma.view.requestcomment;

import com.company.diploma.entity.RequestComment;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "request-comments/:id", layout = MainView.class)
@ViewController(id = "RequestComment.detail")
@ViewDescriptor(path = "request-comment-detail-view.xml")
@EditedEntityContainer("requestCommentDc")
public class RequestCommentDetailView extends StandardDetailView<RequestComment> {
}