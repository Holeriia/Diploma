package com.company.diploma.view.workspacedashboard;


import com.company.diploma.entity.*;
import com.company.diploma.view.main.MainView;
import io.jmix.core.Messages;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import io.jmix.bpm.entity.TaskData;
import io.jmix.bpm.entity.UserGroup;
import io.jmix.bpm.multitenancy.BpmTenantProvider;
import io.jmix.bpm.service.UserGroupService;
import io.jmix.bpm.util.FlowableEntitiesConverter;
import io.jmix.bpmflowui.processform.ProcessFormViews;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import io.jmix.core.LoadContext;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.ViewNavigators;
import io.jmix.flowui.action.list.EditAction;
import io.jmix.flowui.component.grid.DataGrid;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Route(value = "workspace-dashboard-view", layout = MainView.class)
@ViewController(id = "WorkspaceDashboardView")
@ViewDescriptor(path = "workspace-dashboard-view.xml")
public class WorkspaceDashboardView extends StandardView {

    private UUID workspaceId;
    @Autowired
    private Messages messages;
    @Subscribe
    public void onQueryParametersChange(QueryParametersChangeEvent event) {

        List<String> workspaceIds = event.getQueryParameters()
                .getParameters()
                .get("workspaceId");

        if (workspaceIds == null || workspaceIds.isEmpty()) {
            return;
        }

        workspaceId = UUID.fromString(workspaceIds.get(0));
        // подставляем имя в заголовок страницы
        Workspace workspace = dataManager.load(Workspace.class)
                .id(workspaceId)
                .one();

        String title = String.format(
                messages.getMessage(WorkspaceDashboardView.class, "workspaceDashboardView.titleWithName"),
                workspace.getName()
        );
        setPageTitle(title);
    }


    @ViewComponent
    private DataGrid<Request> requestsGrid;

    @ViewComponent("requestsGrid.editAction")
    private EditAction<Request> editAction;

    @Autowired
    private ViewNavigators viewNavigators;


    @Subscribe("requestsGrid.editAction")
    public void onRequestsGridEditActionPerformed(ActionPerformedEvent event) {
        Request request = requestsGrid.getSingleSelectedItem();
        if (request == null) {
            return;
        }

        if (request.getStatus() == RequestStatus.DRAFT) {
            // Открываем кастомный вид вместо стандартного detail
            viewNavigators.detailView(this, Request.class)
                    .withViewId("Request.create")
                    .editEntity(request)
                    .navigate();
        } else {
            // Для остальных статусов - стандартное поведение list_edit
            editAction.execute();
        }
    }

    @Autowired
    private DataManager dataManager;

    @Autowired
    private CurrentAuthentication currentAuthentication;


    @Install(to = "requestsGrid.createAction", subject = "initializer")
    private void requestsGridCreateActionInitializer(Request request) {
        if (workspaceId == null) return;

        Workspace workspace = dataManager.load(Workspace.class)
                .id(workspaceId)
                .one();
        request.setWorkspace(workspace);

        User user = (User) currentAuthentication.getUser();

        // Теперь мы уверены, что participant существует, так как ensureUserIsParticipant
        Participant participant = dataManager.load(Participant.class)
                .query("select p from Participant p where p.user = :user and p.workspace = :workspace")
                .parameter("user", user)
                .parameter("workspace", workspace)
                .one();

        request.setInitiator(participant);
        request.setStatus(RequestStatus.DRAFT);
    }


    @ViewComponent
    private CollectionLoader<TaskData> tasksDl;

    @ViewComponent
    private CollectionContainer<TaskData> tasksDc;

    @ViewComponent
    private DataGrid<TaskData> tasksDataGrid;

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

        ensureUserIsParticipant();

        currentUserName = currentUserSubstitution.getEffectiveUser().getUsername();
        userGroupCodes = userGroupService.getUserGroups(currentUserName)
                .stream()
                .map(UserGroup::getCode)
                .toList();

        tasksDl.load();
    }

    private void ensureUserIsParticipant() {
        if (workspaceId == null) return;

        User currentUser = (User) currentAuthentication.getUser();

        // 1. Проверяем, существует ли уже участник
        boolean alreadyParticipant = dataManager.load(Participant.class)
                .query("select p from Participant p where p.workspace.id = :wsId and p.user = :user")
                .parameter("wsId", workspaceId)
                .parameter("user", currentUser)
                .optional().isPresent();

        if (alreadyParticipant) return;

        // 2. Если нет, ищем запись Студента для текущего пользователя
        Optional<Student> studentOpt = dataManager.load(Student.class)
                .query("select s from Student s where s.user = :user")
                .parameter("user", currentUser)
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("group");
                })
                .optional();

        if (studentOpt.isPresent()) {
            Group studentGroup = studentOpt.get().getGroup();

            // Загружаем Workspace, чтобы проверить его группы
            Workspace workspace = dataManager.load(Workspace.class)
                    .id(workspaceId)
                    .fetchPlan(plan -> {
                        plan.addFetchPlan(FetchPlan.BASE);
                        plan.add("groups");
                    })
                    .one();

            if (studentGroup != null && workspace.getGroups().contains(studentGroup)) {
                // 3. Создаем участника
                Participant newParticipant = dataManager.create(Participant.class);
                newParticipant.setWorkspace(workspace);
                newParticipant.setUser(currentUser);
                newParticipant.setAssignmentsNow(0);

                dataManager.save(newParticipant);
            }
        }
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

    /**
     * для перевода названия задач
     */

    @Supply(to = "tasksDataGrid.name", subject = "renderer")
    protected Renderer<TaskData> taskNameRenderer() {
        return new ComponentRenderer<>(taskData -> {
            String rawName = taskData.getName();
            if (rawName == null) {
                return new Span("");
            }

            String localized = messages.getMessage("com.company.diploma.bpm", rawName);
            return new Span(localized);
        });
    }


    @Autowired
    private DialogWindows dialogWindows;

    @Autowired
    private UiComponents uiComponents;


    @Supply(to = "assignmentsGrid.topicColumn", subject = "renderer")
    private Renderer<Assignment> assignmentsGridTopicColumnRenderer() {
        return new ComponentRenderer<>(assignment -> {
            // 1. Проверяем роль текущего пользователя
            User currentUser = (User) currentAuthentication.getUser();
            boolean isTeacher = UserRole.TEACHER.equals(currentUser.getUserRole());

            // 2. Логика отображения
            if (assignment.getTopic() == null) {
                if (isTeacher) {
                    // Создаем кнопку "Назначить"
                    JmixButton assignBtn = uiComponents.create(JmixButton.class);
                    assignBtn.setText("Назначить");
//                    assignBtn.addThemeNames("tertiary-inline"); // делаем аккуратной

                    assignBtn.addClickListener(event -> {
                        // Здесь ваша логика открытия выбора темы
                        openTopicSelection(assignment);
                    });
                    return assignBtn;
                } else {
                    // Если не учитель и темы нет — пишем заглушку
                    return new Span("Не назначена");
                }
            } else {
                // Если тема уже есть — просто выводим её название
                return new Span(assignment.getTopic().getName());
            }
        });
    }

    private void openTopicSelection(Assignment assignment) {
        dialogWindows.detail(this, Assignment.class)
                .withViewId("TopicAssignmentView")
                .editEntity(assignment)
                .open();
    }

    @Supply(to = "topicsGrid.interestsColumn", subject = "renderer")
    private Renderer<Topic> topicsGridInterestsColumnRenderer() {
        return new ComponentRenderer<>(topic -> {
            if (topic.getInterests() == null || topic.getInterests().isEmpty()) {
                return new Span("-");
            }
            // Собираем названия интересов через запятую
            String interestsString = topic.getInterests().stream()
                    .map(Interest::getName)
                    .collect(Collectors.joining(", "));

            Span span = new Span(interestsString);
            span.getElement().getStyle().set("font-size", "var(--lumo-font-size-s)");
            return span;
        });
    }
}