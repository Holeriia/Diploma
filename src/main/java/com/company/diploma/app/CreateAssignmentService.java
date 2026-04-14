package com.company.diploma.app;

import com.company.diploma.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("CreateAssignmentService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CreateAssignmentService implements JavaDelegate {

    private static final Logger log = LoggerFactory.getLogger(CreateAssignmentService.class);

    private final DataManager dataManager;

    public CreateAssignmentService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void execute(DelegateExecution execution) {
        UUID requestId = (UUID) execution.getVariable("requestId");
        User rawApproverUser = (User) execution.getVariable("approverUsername");

        if (requestId == null || rawApproverUser == null) {
            log.error("Переменные процесса requestId или approverUsername пусты!");
            return;
        }

        // 1. Загружаем заявку. Добавляем FetchPlan.BASE для всех уровней
        Request request = dataManager.load(Request.class)
                .id(requestId)
                .fetchPlan(fetchPlanBuilder -> {
                    fetchPlanBuilder.addFetchPlan(FetchPlan.BASE); // Базовые поля заявки
                    fetchPlanBuilder.add("workspace", FetchPlan.BASE);
                    fetchPlanBuilder.add("initiator", prop -> {
                        prop.addFetchPlan(FetchPlan.BASE); // Поля участника
                        prop.add("user", userProp -> {
                            userProp.addFetchPlan(FetchPlan.BASE); // Это загрузит username!
                            userProp.add("userRole"); // А это — твой enum
                        });
                    });
                })
                .one();

        // 2. Перезагружаем одобряющего пользователя. Обязательно добавляем BASE
        User approverUser = dataManager.load(User.class)
                .id(rawApproverUser.getId())
                .fetchPlan(fetchPlanBuilder -> {
                    fetchPlanBuilder.addFetchPlan(FetchPlan.BASE); // Загрузит username
                    fetchPlanBuilder.add("userRole"); // Загрузит роль
                })
                .one();

        // 3. Находим Participant для одобряющего
        Participant approverParticipant = dataManager.load(Participant.class)
                .query("select p from Participant p where p.user = :user and p.workspace = :workspace")
                .parameter("user", approverUser)
                .parameter("workspace", request.getWorkspace())
                .fetchPlan(FetchPlan.BASE) // На всякий случай грузим базу и тут
                .one();

        // 4. Создаем Assignment
        Assignment assignment = dataManager.create(Assignment.class);
        assignment.setRequest(request);
        assignment.setWorkspace(request.getWorkspace());
        assignment.setStatus(AssignmentStatus.NOT_APPROVED);

        Participant initiatorParticipant = request.getInitiator();
        User initiatorUser = initiatorParticipant.getUser();

        // --- ЛОГИРОВАНИЕ (теперь не упадет) ---
        log.info("Инициатор: {}, Роль: {}", initiatorUser.getUsername(), initiatorUser.getUserRole());
        log.info("Одобряющий: {}, Роль: {}", approverUser.getUsername(), approverUser.getUserRole());

        // 5. Распределение ролей
        UserRole role = initiatorUser.getUserRole();

        if (UserRole.STUDENT.equals(role)) {
            log.info("Сработала ветка: Инициатор - СТУДЕНТ. Назначаем ментора: {}", approverUser.getUsername());
            assignment.setMentee(initiatorParticipant);
            assignment.setMentor(approverParticipant);
        } else if (UserRole.TEACHER.equals(role)) {
            log.info("Сработала ветка: Инициатор - УЧИТЕЛЬ. Назначаем менти: {}", approverUser.getUsername());
            assignment.setMentor(initiatorParticipant);
            assignment.setMentee(approverParticipant);
        } else {
            log.warn("Роль инициатора {} не совпала ни с STUDENT, ни с TEACHER", role);
        }

        // 6. Сохраняем
        dataManager.save(assignment);
        log.info("Assignment сохранен успешно.");
    }
}