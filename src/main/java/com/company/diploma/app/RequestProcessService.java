package com.company.diploma.app;

import com.company.diploma.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import org.flowable.engine.RuntimeService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RequestProcessService {

    private final RuntimeService runtimeService;
    private final DataManager dataManager;

    public RequestProcessService(RuntimeService runtimeService,
                                 DataManager dataManager) {
        this.runtimeService = runtimeService;
        this.dataManager = dataManager;
    }

    public void startProcess(Request request) {
        Request req = dataManager.load(Request.class)
                .id(request.getId())
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("initiator", sp -> sp.add("user", up -> up.add("username")));
                    plan.add("priorities", sp -> {
                        sp.addFetchPlan(FetchPlan.BASE);
                        sp.add("participant", pp -> pp.add("user", up -> up.add("username")));
                    });
                })
                .one();

        RequestPriority firstPriority = req.getPriorities().stream()
                .filter(p -> p.getPriorityNumber() != null && p.getPriorityNumber() == 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Нет участника с приоритетом №1"));

        User firstAssigneeUser = firstPriority.getParticipant().getUser();
        User initiatorUser = req.getInitiator().getUser();

        req.setStatus(RequestStatus.IN_REVIEW);

        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", req.getId());
        variables.put("assigneeUsername", firstAssigneeUser);
        variables.put("previousAssignee", initiatorUser);

        runtimeService.startProcessInstanceByKey(
                "request-approval",
                req.getId().toString(),
                variables
        );

        dataManager.save(req);
    }
}