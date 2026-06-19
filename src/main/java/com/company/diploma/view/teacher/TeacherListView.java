package com.company.diploma.view.teacher;

import com.company.diploma.entity.Teacher;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "teachers", layout = MainView.class)
@ViewController(id = "Teacher.list")
@ViewDescriptor(path = "teacher-list-view.xml")
@LookupComponent("teachersDataGrid")
@DialogMode(width = "64em")
public class TeacherListView extends StandardListView<Teacher> {
}