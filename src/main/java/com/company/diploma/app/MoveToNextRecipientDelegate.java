package com.company.diploma.app;

import com.company.diploma.entity.*;
import io.jmix.core.DataManager;
import io.jmix.core.FetchPlan;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component("MoveToNextRecipientDelegate")
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MoveToNextRecipientDelegate implements JavaDelegate {

    private final DataManager dataManager;

    public MoveToNextRecipientDelegate(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public void execute(DelegateExecution execution) {
        UUID requestId = (UUID) execution.getVariable("requestId");
        Integer currentIndex = (Integer) execution.getVariable("currentPriorityIndex");
        if (currentIndex == null) currentIndex = 0;

        Request request = dataManager.load(Request.class)
                .id(requestId)
                .fetchPlan(FetchPlan.BASE)
                .one();

        List<RequestPriority> priorities = dataManager.load(RequestPriority.class)
                .query("select p from RequestPriority p where p.request = :request and p.priorityNumber is not null order by p.priorityNumber asc")
                .parameter("request", request)
                .fetchPlan(plan -> {
                    plan.addFetchPlan(FetchPlan.BASE);
                    plan.add("participant", pp -> {
                        pp.addFetchPlan(FetchPlan.BASE);
                        pp.add("user", userProp -> userProp.add("username"));
                    });
                })
                .list();

        boolean found = false;

        while (currentIndex < priorities.size() && !found) {
            RequestPriority p = priorities.get(currentIndex);
            Participant candidate = p.getParticipant();

            if (hasAvailableSlots(candidate)) {
                execution.setVariable("assigneeUsername", candidate.getUser());
                execution.setVariable("currentPriorityIndex", currentIndex + 1);
                execution.setVariable("hasNext", true);
                found = true;
            } else {
                currentIndex++;
            }
        }

        if (!found) {
            request.setStatus(RequestStatus.FINAL_REJECTED);
            dataManager.save(request);
            execution.setVariable("hasNext", false);
        }
    }

    private boolean hasAvailableSlots(Participant participant) {
        Integer max = participant.getMaxAssignments();
        if (max == null) return true;

        int currentCount = (participant.getAssignmentsNow() == null) ? 0 : participant.getAssignmentsNow();
        return currentCount < max;
    }
}