package com.company.diploma.view.studentprofile;

import com.company.diploma.entity.Student;
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

import java.util.List;
import java.util.Optional;

@Route(value = "student-profile-view", layout = MainView.class)
@ViewController(id = "StudentProfileView")
@ViewDescriptor(path = "student-profile-view.xml")
public class StudentProfileView extends StandardView {

    @ViewComponent
    private InstanceContainer<Student> studentDc;

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

    private boolean editMode = false;

    @Subscribe
    public void onReady(ReadyEvent event) {
        User currentUser = (User) currentAuthentication.getUser();

        Student student = loadStudentByUser(currentUser)
                .orElseGet(() -> {
                    Student s = dataManager.create(Student.class);
                    s.setUser(currentUser);
                    return s;
                });

        // Подключаем к DataContext (чтобы изменения отслеживались UI)
        Student managed = dataContext.merge(student);
        studentDc.setItem(managed);

        // стартуем в режиме "только просмотр"
        setEditMode(false);
    }

    private Optional<Student> loadStudentByUser(User user) {

        List<Student> result = dataManager.load(Student.class)
                .query("select s from Student s where s.user = :user")
                .parameter("user", user)
                .fetchPlan(builder -> builder
                        .addFetchPlan(FetchPlan.BASE)
                        .add("user", FetchPlan.BASE)
                        .add("group", FetchPlan.BASE)
                        .add("department", FetchPlan.BASE)
                        .add("user.interests", FetchPlan.BASE)
                )
                .list();

        return result.stream().findFirst();
    }

    @Subscribe("editBtn")
    public void onEditBtnClick(final ClickEvent<JmixButton> event) {
        setEditMode(true);
    }

    @Subscribe("saveBtn")
    public void onSaveBtnClick(final ClickEvent<JmixButton> event) {
        dataContext.save();
        setEditMode(false);
    }

    private void setEditMode(boolean enabled) {
        this.editMode = enabled;

        // Делаем поля формы read-only / editable
        form.getChildren().forEach(component -> {
            if (component instanceof HasValue<?, ?> hasValue) {
                // В Vaadin readOnly=true запрещает редактирование
                // (оставляем возможность копировать текст)
                ((HasValue<?, ?>) component).setReadOnly(!enabled);
            }
            if (component instanceof HasEnabled hasEnabled) {
                // для некоторых компонентов достаточно setReadOnly,
                // но setEnabled тоже удобно для пикеров/кнопок внутри
                hasEnabled.setEnabled(true);
            }
        });

        editBtn.setEnabled(!enabled);
        saveBtn.setEnabled(enabled);
    }
}