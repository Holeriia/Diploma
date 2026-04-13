package com.company.diploma.view.topic;

import com.company.diploma.entity.Topic;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "topics/:id", layout = MainView.class)
@ViewController(id = "Topic.detail")
@ViewDescriptor(path = "topic-detail-view.xml")
@EditedEntityContainer("topicDc")
public class TopicDetailView extends StandardDetailView<Topic> {
}