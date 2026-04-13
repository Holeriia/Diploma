package com.company.diploma.app;

import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.Participant;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AssignmentProcessService {

    private final RuntimeService runtimeService;
    private final DataManager dataManager;

    public AssignmentProcessService(RuntimeService runtimeService,
                                    DataManager dataManager) {
        this.runtimeService = runtimeService;
        this.dataManager = dataManager;
    }

    public void startProcess(Assignment assignment) {
        // Загружаем назначение с необходимым планом (нужны менти и ментор для переменных процесса)
        Assignment entity = dataManager.load(Assignment.class)
                .id(assignment.getId())
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    // Важно: загружаем участников и ИХ пользователей с полем username
                    plan.add("mentee", sp -> sp.add("user", up -> up.add("username")));
//                    plan.add("mentor", sp -> sp.add("user", up -> up.add("username")));
                    plan.add("workspace", FetchPlan.BASE);
                })
                .one();

        Map<String, Object> variables = new HashMap<>();
        variables.put("assignmentId", entity.getId());
        variables.put("menteeUsername", entity.getMentee().getUser());

        // Запуск процесса по ключу
        runtimeService.startProcessInstanceByKey(
                "assignment-approval",
                entity.getId().toString(),
                variables
        );

    }
}