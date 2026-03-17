package com.company.diploma.view.teacherprofile;

import com.company.diploma.entity.Teacher;
import com.company.diploma.entity.User;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.DataContext;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "teacher-profile-view", layout = MainView.class)
@ViewController(id = "TeacherProfileView")
@ViewDescriptor(path = "teacher-profile-view.xml")
public class TeacherProfileView extends StandardView {

    @ViewComponent
    private InstanceContainer<Teacher> teacherDc;

    @ViewComponent
    private DataContext dataContext;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @ViewComponent
    private FormLayout form;

    @ViewComponent
    private Button editBtn;

    @ViewComponent
    private Button saveBtn;

    @Subscribe
    public void onReady(final ReadyEvent event) {
        User currentUser = (User) currentAuthentication.getUser();

        Teacher teacher = loadTeacherByUser(currentUser)
                .orElseGet(() -> {
                    Teacher t = dataManager.create(Teacher.class);
                    t.setUser(currentUser);
                    return t;
                });

        Teacher managed = dataContext.merge(teacher);
        teacherDc.setItem(managed);

        setEditMode(false);
    }

    private Optional<Teacher> loadTeacherByUser(User user) {
        return dataManager.load(Teacher.class)
                .query("select t from Teacher t where t.user = :user")
                .parameter("user", user)
                .fetchPlan(builder -> builder
                        .addFetchPlan(FetchPlan.BASE)
                        .add("user", FetchPlan.BASE)
                        .add("user.interests", FetchPlan.BASE) // ВАЖНО: загружаем интересы
                        .add("department", FetchPlan.BASE)
                        .add("degree", FetchPlan.BASE)
                        .add("title", FetchPlan.BASE)
                        .add("position", FetchPlan.BASE)
                )
                .optional();
    }

    @Subscribe("editBtn")
    public void onEditBtnClick(final ClickEvent<JmixButton> event) {
        setEditMode(true);
    }

    @Subscribe("saveBtn")
    public void onSaveBtnClick(final ClickEvent<JmixButton> event) {
        // DataContext сам сохранит и Teacher, и User с его интересами
        dataContext.save();
        setEditMode(false);
    }

    private void setEditMode(boolean enabled) {
        form.getChildren().forEach(component -> {
            if (component instanceof HasValue<?, ?> hasValue) {
                hasValue.setReadOnly(!enabled);
            }
            if (component instanceof HasEnabled hasEnabled) {
                hasEnabled.setEnabled(true);
            }
        });

        editBtn.setEnabled(!enabled);
        saveBtn.setEnabled(enabled);
    }
}