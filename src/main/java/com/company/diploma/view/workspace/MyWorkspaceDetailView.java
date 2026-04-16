
package com.company.diploma.view.workspace;

import com.company.diploma.entity.Participant;
import com.company.diploma.entity.User;
import com.company.diploma.entity.UserRole;
import com.company.diploma.entity.Workspace;

import com.company.diploma.view.main.MainView;

import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.MetadataTools;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

@Route(value = "my-workspaces/:id", layout = MainView.class)
@ViewController(id = "MyWorkspace.detail")
@ViewDescriptor(path = "my-workspace-detail-view.xml")
@EditedEntityContainer("workspaceDc")
public class MyWorkspaceDetailView extends StandardDetailView<Workspace> {
    @ViewComponent
    private DataGrid<User> teachersDataGrid;
    @ViewComponent
    private CollectionLoader<User> allTeachersDl;
    @ViewComponent
    private CollectionLoader<Participant> participantsDl;
    @ViewComponent
    private CollectionContainer<Participant> participantsDc;
    @ViewComponent
    private CollectionContainer<User> allTeachersDc;
    @Autowired
    private DataManager dataManager;

    @Autowired
    private MetadataTools metadataTools;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        // 1. Загружаем данные
        allTeachersDl.setParameter("role", UserRole.TEACHER.getId());
        allTeachersDl.load();

        participantsDl.setParameter("workspace", getEditedEntity());
        participantsDl.load();

        // 2. Настраиваем СУЩЕСТВУЮЩУЮ колонку
        teachersDataGrid.getColumnByKey("displayName")
                .setRenderer(new TextRenderer<>(user -> metadataTools.getInstanceName(user)));

        // 3. Выделяем текущих
        List<User> currentTeachers = participantsDc.getItems().stream()
                .map(Participant::getUser)
                .toList();

        teachersDataGrid.select(currentTeachers);
    }

    @Subscribe
    public void onBeforeSave(final BeforeSaveEvent event) {
        Workspace workspace = getEditedEntity();

        // Получаем список тех, у кого сейчас стоит чекбокс
        Set<User> selectedTeachers = teachersDataGrid.getSelectedItems();

        // Получаем текущих участников из БД (чтобы сравнить)
        List<Participant> currentParticipants = participantsDc.getMutableItems();

        // 1. Удаляем тех, с кого сняли чекбокс
        List<Participant> toRemove = currentParticipants.stream()
                .filter(p -> !selectedTeachers.contains(p.getUser()))
                .toList();
        currentParticipants.removeAll(toRemove);
        // Важно: если Participant - это отдельная сущность, Jmix удалит их при сохранении воркспейса

        // 2. Добавляем тех, на кого поставили чекбокс
        for (User teacher : selectedTeachers) {
            boolean alreadyExists = currentParticipants.stream()
                    .anyMatch(p -> p.getUser().equals(teacher));

            if (!alreadyExists) {
                Participant newParticipant = dataManager.create(Participant.class);
                newParticipant.setUser(teacher);
                newParticipant.setWorkspace(workspace);
                newParticipant.setAssignmentsNow(0);
                currentParticipants.add(newParticipant);
            }
        }
    }
}