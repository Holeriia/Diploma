package com.company.diploma.app;

import com.company.diploma.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("CreateAssignmentService")
public class CreateAssignmentService implements JavaDelegate {

    private final DataManager dataManager;

    public CreateAssignmentService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void execute(DelegateExecution execution) {
        UUID requestId = (UUID) execution.getVariable("requestId");
        User approverUser = (User) execution.getVariable("approverUsername");

        if (requestId == null || approverUser == null) return;

        // Загружаем заявку с необходимыми связями
        Request request = dataManager.load(Request.class)
                .id(requestId)
                .fetchPlan(fp -> {
                    fp.addFetchPlan(FetchPlan.BASE);
                    fp.add("workspace", FetchPlan.BASE);
                    fp.add("initiator", initiator -> {
                        initiator.addFetchPlan(FetchPlan.BASE);
                        initiator.add("user", user -> user.addFetchPlan(FetchPlan.BASE));
                    });
                })
                .one();

        // Находим Participant для одобряющего пользователя
        Participant approverParticipant = dataManager.load(Participant.class)
                .query("select p from Participant p where p.user = :user and p.workspace = :workspace")
                .parameter("user", approverUser)
                .parameter("workspace", request.getWorkspace())
                .one();

        Assignment assignment = dataManager.create(Assignment.class);
        assignment.setRequest(request);
        assignment.setWorkspace(request.getWorkspace());
        assignment.setStatus(AssignmentStatus.NOT_APPROVED);

        request.setStatus(RequestStatus.ACCEPTED);

        Participant initiatorPart = request.getInitiator();
        UserRole initiatorRole = initiatorPart.getUser().getUserRole();

        // Распределение ролей в зависимости от того, кто подал заявку
        if (UserRole.STUDENT.equals(initiatorRole)) {
            assignment.setMentee(initiatorPart);
            assignment.setMentor(approverParticipant);
        } else if (UserRole.TEACHER.equals(initiatorRole)) {
            assignment.setMentor(initiatorPart);
            assignment.setMentee(approverParticipant);
        }

        // Увеличиваем счетчик у инициатора заявки
        Integer initNow = initiatorPart.getAssignmentsNow();
        initiatorPart.setAssignmentsNow(initNow == null ? 1 : initNow + 1);

        // Увеличиваем счетчик у одобряющего
        Integer apprNow = approverParticipant.getAssignmentsNow();
        approverParticipant.setAssignmentsNow(apprNow == null ? 1 : apprNow + 1);

        dataManager.save(assignment, request, initiatorPart, approverParticipant);
    }
}