package com.company.diploma.app;

import com.company.diploma.entity.Assignment;
import com.company.diploma.entity.AssignmentStatus;
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

        Map<String, Object> variables = new HashMap<>();
        variables.put("assignmentId", assignment.getId());
        variables.put("menteeUsername", assignment.getMentee().getUser());

        runtimeService.startProcessInstanceByKey(
                "assignment-approval",
                assignment.getId().toString(),
                variables
        );
    }
}