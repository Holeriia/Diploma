package com.company.diploma.app;

import com.company.diploma.entity.*;
import io.jmix.core.DataManager;
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
                .fetchPlan("_base")
                .one();

        Participant initiator = req.getInitiator();
        User initiatorUser = initiator.getUser();

        req.setStatus(RequestStatus.IN_REVIEW);

        Map<String, Object> variables = new HashMap<>();
        variables.put("requestId", req.getId());
        variables.put("assigneeUsername", initiatorUser);
        variables.put("previousAssignee", initiatorUser);

        runtimeService.startProcessInstanceByKey(
                "request-approval",
                req.getId().toString(),
                variables
        );

        dataManager.save(req);
    }
}