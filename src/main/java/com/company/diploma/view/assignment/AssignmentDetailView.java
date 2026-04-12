package com.company.diploma.view.assignment;

import com.company.diploma.entity.Assignment;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "assignments/:id", layout = MainView.class)
@ViewController(id = "Assignment.detail")
@ViewDescriptor(path = "assignment-detail-view.xml")
@EditedEntityContainer("assignmentDc")
public class AssignmentDetailView extends StandardDetailView<Assignment> {
}