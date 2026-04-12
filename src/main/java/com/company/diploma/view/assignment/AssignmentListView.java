package com.company.diploma.view.assignment;

import com.company.diploma.entity.Assignment;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "assignments", layout = MainView.class)
@ViewController(id = "Assignment.list")
@ViewDescriptor(path = "assignment-list-view.xml")
@LookupComponent("assignmentsDataGrid")
@DialogMode(width = "64em")
public class AssignmentListView extends StandardListView<Assignment> {
}