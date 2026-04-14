package com.company.diploma.app;

import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.AssignmentStatus;
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

@Component("SetAssignmentApprovedService")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SetAssignmentApprovedService implements JavaDelegate {

    private final DataManager dataManager;

    public SetAssignmentApprovedService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void execute(DelegateExecution execution) {
        // Извлекаем ID назначения из переменной процесса
        UUID assignmentId = (UUID) execution.getVariable("assignmentId");

        // 1. Загружаем назначение
        Assignment assignment = dataManager.load(Assignment.class)
                .id(assignmentId)
                .fetchPlan(FetchPlan.BASE)
                .one();
        // 2. Устанавливаем новый статус
        assignment.setStatus(AssignmentStatus.APPROVED);
        // 3. Сохраняем изменения в базе
        dataManager.save(assignment);

    }
}