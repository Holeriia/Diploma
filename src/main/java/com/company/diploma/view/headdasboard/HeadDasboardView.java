package com.company.diploma.view.headdasboard;

import com.company.diploma.app.ExcelReportService;
import com.company.diploma.app.WordReportService;
import com.company.diploma.entity.*;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.TextRenderer;
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
import io.jmix.core.usersubstitution.CurrentUserSubstitution;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.download.ByteArrayDownloadDataProvider;
import io.jmix.flowui.download.DownloadFormat;
import io.jmix.flowui.download.Downloader;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.apache.poi.ss.usermodel.*;
import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@Route(value = "head-dasboard-view", layout = MainView.class)
@ViewController(id = "HeadDasboardView")
@ViewDescriptor(path = "head-dasboard-view.xml")
public class HeadDasboardView extends StandardView {

    @ViewComponent
    private EntityComboBox<Workspace> workspaceField;

    @Autowired
    private Notifications notifications;

    @ViewComponent
    private CollectionLoader<Assignment> assignmentsDl;

    @Subscribe("applyBtn")
    public void onApplyBtnClick(final ClickEvent<JmixButton> event) {
        Workspace selectedWorkspace = workspaceField.getValue();
        if (selectedWorkspace == null) {
            notifications.create("Пожалуйста, выберите пространство")
                    .withType(Notifications.Type.WARNING)
                    .show();
            return;
        }
        refreshPersonalData();
        refreshStatistics();
        refreshAssignments();

        // Здесь мы позже добавим обновление данных в коллекциях на вкладках
        notifications.create("Данные обновлены для: " + selectedWorkspace.getName())
                .withType(Notifications.Type.SUCCESS)
                .show();
    }

    @Autowired
    private WordReportService wordReportService;

    @Subscribe("exportWordReportBtn")
    public void onExportWordReportBtnClick(final ClickEvent<JmixButton> event) {
        Workspace selectedWorkspace = workspaceField.getValue();
        if (selectedWorkspace == null) {
            notifications.create("Сначала выберите пространство").show();
            return;
        }

        try {
            byte[] reportBytes = wordReportService.generateApprovedTopicsReport(selectedWorkspace);
            String fileName = "Утвержденные_темы_" + selectedWorkspace.getName() + ".docx";

            downloader.download(
                    new ByteArrayDownloadDataProvider(reportBytes, 8192, null),
                    fileName,
                    DownloadFormat.DOCX
            );

        } catch (Exception e) {
            notifications.create("Ошибка при генерации Word отчета: " + e.getMessage())
                    .withType(Notifications.Type.ERROR)
                    .show();
        }
    }

    @ViewComponent
    private VerticalLayout studentsTablesContainer;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private UiComponents uiComponents;

    private void refreshPersonalData() {
        // Очищаем контейнер
        studentsTablesContainer.removeAll();

        Workspace selectedWorkspace = workspaceField.getValue();
        if (selectedWorkspace == null) return;

        // Загружаем Workspace с коллекцией групп
        Workspace workspace = dataManager.load(Workspace.class)
                .id(selectedWorkspace.getId())
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("groups", FetchPlan.BASE);
                })
                .one();

        // Создаем секции для каждой группы
        for (Group group : workspace.getGroups()) {
            createGroupSection(group, workspace);
        }
    }

    private void createGroupSection(Group group, Workspace workspace) {
        // 1. Создаем заголовок (Vaadin компонент)
        H3 groupHeader = new H3("Группа: " + group.getName());
        groupHeader.getStyle().set("margin-top", "0");
        groupHeader.getStyle().set("margin-bottom", "var(--lumo-space-s)");
        studentsTablesContainer.add(groupHeader);

        // 2. Создаем DataGrid (Jmix компонент)
        DataGrid<Student> grid = uiComponents.create(DataGrid.class);
        grid.setWidthFull();
        grid.setAllRowsVisible(true); // Чтобы сетка не скроллилась, а росла вниз

        // 3. Загружаем данные студентов через JPQL
        List<Student> students = dataManager.load(Student.class)
                .query("select s from Student s where s.group = :group " +
                        "and s.user in (select p.user from Participant p where p.workspace = :ws)")
                .parameter("group", group)
                .parameter("ws", workspace)
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("user", FetchPlan.BASE);
                })
                .list();

        // 4. Настраиваем колонки через лямбда-выражения
        grid.addColumn(student -> {
            User u = student.getUser();
            String patronymic = u.getPatronymic() != null ? u.getPatronymic() : "";
            return String.format("%s %s %s", u.getLastName(), u.getFirstName(), patronymic).trim();
        }).setHeader("ФИО").setAutoWidth(true);

        grid.addColumn(student -> student.getUser().getPhoneNumber()).setHeader("Телефон");
        grid.addColumn(student -> student.getUser().getEmail()).setHeader("Почта");
        grid.addColumn(Student::getSnils).setHeader("СНИЛС");

        // Безопасное получение Enum BasisOfLearning
        grid.addColumn(student -> {
            return student.getBasisOfLearning() != null ? student.getBasisOfLearning().name() : "";
        }).setHeader("Основа обучения");

        // 5. Заполняем данными и добавляем на макет
        grid.setItems(students);
        studentsTablesContainer.add(grid);
    }

    @Autowired
    private Downloader downloader;
    @Autowired
    private ExcelReportService excelReportService;

    @Subscribe("exportReportBtn")
    public void onExportReportBtnClick(final ClickEvent<JmixButton> event) {
        Workspace selectedWorkspace = workspaceField.getValue();
        if (selectedWorkspace == null) {
            notifications.create("Сначала выберите пространство").show();
            return;
        }

        try {
            byte[] reportBytes = excelReportService.generateWorkspaceStudentsReport(selectedWorkspace);

            String fileName = "Личная_информация_" + selectedWorkspace.getName() + ".xlsx";

            downloader.download(
                    new ByteArrayDownloadDataProvider(reportBytes, 8192, null),
                    fileName,
                    DownloadFormat.XLSX
            );

        } catch (Exception e) {
            notifications.create("Ошибка при генерации Excel")
                    .withType(Notifications.Type.ERROR)
                    .show();
        }
    }

    @ViewComponent
    private VerticalLayout statisticsTableContainer;


    private void refreshStatistics() {
        statisticsTableContainer.removeAll();
        Workspace selectedWorkspace = workspaceField.getValue();
        if (selectedWorkspace == null) return;

        // 1. Получаем группы этого пространства
        List<Group> groups = dataManager.load(Group.class)
                .query("select g from Group_ g join g.workspaces w where w = :ws")
                .parameter("ws", selectedWorkspace)
                .list();

        // 2. Получаем преподавателей
        List<User> teachers = dataManager.load(User.class)
                .query("select u from User u where u.userRole = :role")
                .parameter("role", UserRole.TEACHER.getId())
                .list();

        List<TeacherStatsRow> rows = new ArrayList<>();

        // 3. Собираем данные по назначениям (Assignment)
        for (User teacher : teachers) {
            TeacherStatsRow row = new TeacherStatsRow();
            row.setTeacherName(teacher.getDisplayName());
            boolean hasData = false;

            for (Group group : groups) {
                // Считаем назначения, где ментор - текущий препод,
                // а менти - студент из текущей группы
                Long count = dataManager.unconstrained().loadValue(
                                "select count(a) from Assignment a " +
                                        "where a.workspace = :ws " +
                                        "and a.mentor.user = :teacher " +
                                        "and a.mentee.user in (select s.user from Student s where s.group = :group)", Long.class)
                        .parameter("ws", selectedWorkspace)
                        .parameter("teacher", teacher)
                        .parameter("group", group)
                        .one();

                if (count > 0) {
                    row.setGroupCount(group.getName(), count.intValue());
                    hasData = true;
                }
            }
            if (hasData) rows.add(row);
        }

        // 4. Создаем чистый Vaadin Grid без метаданных Jmix
        Grid<TeacherStatsRow> grid = new Grid<>();
        grid.setWidthFull();
        grid.setAllRowsVisible(true);

        // 5. Настройка колонок
        grid.addColumn(r -> r.getTeacherName())
                .setHeader("Преподаватель / Группа")
                .setFrozen(true)
                .setAutoWidth(true);

        for (Group group : groups) {
            final String gName = group.getName();
            grid.addColumn(r -> r.getGroupCount(gName))
                    .setHeader(gName)
                    .setTextAlign(ColumnTextAlign.CENTER);
        }

        grid.addColumn(r -> r.getTotal())
                .setHeader("Всего")
                .setTextAlign(ColumnTextAlign.END);

        grid.setItems(rows);
        statisticsTableContainer.add(grid);
    }

    private void refreshAssignments() {
        Workspace selectedWorkspace = workspaceField.getValue();
        if (selectedWorkspace != null) {
            // Устанавливаем параметр в запрос и запускаем загрузку
            assignmentsDl.setParameter("workspace", selectedWorkspace);
            assignmentsDl.load();
        }
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