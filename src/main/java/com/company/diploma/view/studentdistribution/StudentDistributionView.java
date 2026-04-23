package com.company.diploma.view.studentdistribution;


import com.company.diploma.entity.*;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "student-distribution-view", layout = MainView.class)
@ViewController(id = "StudentDistributionView")
@ViewDescriptor(path = "student-distribution-view.xml")
public class StudentDistributionView extends StandardView {

    @Autowired
    private UiComponents uiComponents;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private Notifications notifications;
    @Autowired
    private io.jmix.core.Metadata metadata;

    @ViewComponent
    private CollectionLoader<Student> studentsDl;
    @ViewComponent
    private CollectionLoader<User> teachersDl;
    @ViewComponent
    private CollectionContainer<User> teachersDc;
    @ViewComponent
    private CollectionContainer<Student> studentsDc;
    @ViewComponent
    private VerticalLayout distributionContainer;

    private Workspace workspace;

    private final Map<Student, EntityComboBox<User>> distributionMap = new HashMap<>();

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Subscribe
    public void onReady(ReadyEvent event) {
        if (workspace == null) return;

        studentsDl.setParameter("workspace", workspace);
        studentsDl.load();

        teachersDl.setParameter("workspace", workspace);
        teachersDl.load();

        buildDistributionRows();
    }

    private void buildDistributionRows() {
        distributionContainer.removeAll();
        distributionMap.clear();

        List<Student> students = studentsDc.getItems();

        // Программная фильтрация: гарантирует отсутствие проблем с JPQL и Enum
        List<User> teachers = teachersDc.getItems().stream()
                .filter(user -> {

                    return "TEACHER".equals(user.getUserRole().toString());
                })
                .toList();

        if (teachers.isEmpty()) {
            notifications.create("Преподаватели-участники не найдены").show();
        }

        for (Student student : students) {
            HorizontalLayout row = uiComponents.create(HorizontalLayout.class);
            row.setWidthFull();
            row.setSpacing(true);
            row.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.END);

            EntityPicker<Student> studentPicker = uiComponents.create(EntityPicker.class);
            studentPicker.setMetaClass(metadata.getClass(Student.class));
            studentPicker.setValue(student);
            studentPicker.setReadOnly(true);
            studentPicker.setLabel("Студент");
            studentPicker.setWidthFull();

            EntityComboBox<User> teacherCombo = uiComponents.create(EntityComboBox.class);
            teacherCombo.setMetaClass(metadata.getClass(User.class));
            teacherCombo.setItems(teachers); // Передаем уже отфильтрованный список
            teacherCombo.setLabel("Назначить преподавателя");
            teacherCombo.setWidthFull();

            row.add(studentPicker, teacherCombo);
            distributionContainer.add(row);
            distributionMap.put(student, teacherCombo);
        }
    }

    @Subscribe("saveAllBtn")
    public void onSaveAllBtnClick(ClickEvent<JmixButton> event) {
        int savedCount = 0;

        for (Map.Entry<Student, EntityComboBox<User>> entry : distributionMap.entrySet()) {
            Student student = entry.getKey();
            User teacher = entry.getValue().getValue();

            if (teacher != null) {
                createAssignment(student, teacher);
                savedCount++;
            }
        }

        if (savedCount > 0) {
            notifications.create("Сохранено назначений: " + savedCount)
                    .withType(Notifications.Type.SUCCESS)
                    .show();
            close(StandardOutcome.SAVE);
        } else {
            notifications.create("Ни одного преподавателя не выбрано")
                    .withType(Notifications.Type.WARNING)
                    .show();
        }
    }

    private void createAssignment(Student student, User teacherUser) {
        // Загружаем участников
        Participant mentor = dataManager.load(Participant.class)
                .query("select p from Participant p where p.user = :u and p.workspace = :ws")
                .parameter("u", teacherUser)
                .parameter("ws", workspace)
                .one();

        Participant mentee = dataManager.load(Participant.class)
                .query("select p from Participant p where p.user = :u and p.workspace = :ws")
                .parameter("u", student.getUser())
                .parameter("ws", workspace)
                .one();

        Assignment assignment = dataManager.create(Assignment.class);
        assignment.setWorkspace(workspace);
        assignment.setMentor(mentor);
        assignment.setMentee(mentee);
        assignment.setStatus(AssignmentStatus.NOT_APPROVED);
        dataManager.save(assignment);
    }

    @Subscribe("closeBtn")
    public void onCloseBtnClick(ClickEvent<JmixButton> event) {
        close(StandardOutcome.CLOSE);
    }
}