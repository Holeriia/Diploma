package com.company.diploma.view.headdasboard;


import com.company.diploma.entity.Workspace;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.router.Route;
import io.jmix.bpm.entity.TaskData;
import io.jmix.bpm.entity.UserGroup;
import io.jmix.bpm.multitenancy.BpmTenantProvider;
import io.jmix.bpm.service.UserGroupService;
import io.jmix.bpm.util.FlowableEntitiesConverter;
import io.jmix.bpmflowui.processform.ProcessFormViews;
import io.jmix.core.LoadContext;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "head-dasboard-view", layout = MainView.class)
@ViewController(id = "HeadDasboardView")
@ViewDescriptor(path = "head-dasboard-view.xml")
public class HeadDasboardView extends StandardView {

    @ViewComponent
    private EntityComboBox<Workspace> workspaceField;

    @Autowired
    private Notifications notifications;

    @Subscribe("applyBtn")
    public void onApplyBtnClick(final ClickEvent<JmixButton> event) {
        Workspace selectedWorkspace = workspaceField.getValue();
        if (selectedWorkspace == null) {
            notifications.create("Пожалуйста, выберите пространство")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }

        // Здесь мы позже добавим обновление данных в коллекциях на вкладках
        notifications.create("Данные обновлены для: " + selectedWorkspace.getName())
                .withType(Notifications.Type.SUCCESS)
                .show();
    }

    @ViewComponent
    private CollectionLoader<TaskData> tasksDl;
    @ViewComponent
    private CollectionContainer<TaskData> tasksDc;
    // Flowable & BPM API
    @Autowired
    private TaskService taskService;
    @Autowired
    private FlowableEntitiesConverter entitiesConverter;
    @Autowired(required = false)
    private BpmTenantProvider bpmTenantProvider;
    @Autowired
    private UserGroupService userGroupService;
    @Autowired
    private CurrentUserSubstitution currentUserSubstitution;

    private String currentUserName;
    private List<String> userGroupCodes;

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        currentUserName = currentUserSubstitution.getEffectiveUser().getUsername();
        userGroupCodes = userGroupService.getUserGroups(currentUserName)
                .stream()
                .map(UserGroup::getCode)
                .toList();

        tasksDl.load();
    }

    @Install(to = "tasksDl", target = Target.DATA_LOADER)
    private List<TaskData> tasksDlLoadDelegate(LoadContext<TaskData> loadContext) {
        TaskQuery taskQuery = taskService.createTaskQuery().active();
        addAssignmentCondition(taskQuery);
        long count = taskQuery.count();
        taskQuery.orderByTaskCreateTime().desc();
        List<Task> tasks = taskQuery.list();

        List<TaskData> result = tasks.stream()
                .map(entitiesConverter::createTaskData)
                .toList();

        return result;
    }

    @Autowired
    private ProcessFormViews processFormViews;

    @Subscribe("tasksDataGrid.openTaskForm")
    private void onTasksDataGridOpenTaskForm(ActionPerformedEvent event) {
        TaskData selected = tasksDc.getItemOrNull();
        if (selected == null) {
            return;
        }

        Task task = taskService.createTaskQuery()
                .taskId(selected.getId())
                .singleResult();

        if (task == null) {
            return;
        }

        processFormViews.openTaskProcessForm(task, this, dialog ->
                dialog.addAfterCloseListener(afterClose -> tasksDl.load())
        );
    }

    private void addAssignmentCondition(TaskQuery taskQuery) {
        // Если есть группы, используем блок OR, чтобы объединить пользователя и его группы
        if (userGroupCodes != null && !userGroupCodes.isEmpty()) {
            taskQuery.or()
                    .taskCandidateOrAssigned(currentUserName)
                    .taskCandidateGroupIn(userGroupCodes)
                    .endOr();
        } else {
            // Если групп нет, OR не нужен — просто ищем задачи пользователя
            taskQuery.taskCandidateOrAssigned(currentUserName);
        }

        // Условие тенента всегда идет отдельно (вне блока OR)
        if (bpmTenantProvider != null && bpmTenantProvider.isMultitenancyActive()) {
            taskQuery.taskTenantId(bpmTenantProvider.getCurrentUserTenantId());
        }
    }

}