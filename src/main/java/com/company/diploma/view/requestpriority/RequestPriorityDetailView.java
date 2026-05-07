package com.company.diploma.view.requestpriority;

import com.company.diploma.entity.Participant;
import com.company.diploma.entity.Request;
import com.company.diploma.entity.RequestPriority;
import com.company.diploma.view.main.MainView;
import com.company.diploma.view.participantanalytics.ParticipantAnalyticsView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

@Route(value = "request-priorities/:id", layout = MainView.class)
@ViewController(id = "RequestPriority.detail")
@ViewDescriptor(path = "request-priority-detail-view.xml")
@EditedEntityContainer("requestPriorityDc")
public class RequestPriorityDetailView extends StandardDetailView<RequestPriority> {

    @Autowired
    private DialogWindows dialogWindows;

    @ViewComponent
    private EntityPicker<Participant> participantField;

    @Subscribe
    public void onInitEntity(final InitEntityEvent<RequestPriority> event) {
        RequestPriority priority = event.getEntity();
        Request request = priority.getRequest();

        if (request != null && request.getPriorities() != null) {
            int max = request.getPriorities().stream()
                    .map(RequestPriority::getPriorityNumber)
                    .filter(Objects::nonNull)
                    .max(Integer::compareTo)
                    .orElse(0);
            priority.setPriorityNumber(max + 1);
        } else {
            priority.setPriorityNumber(1);
        }
    }

    @Subscribe("participantField.showAnalyticsAction")
    public void onParticipantFieldShowAnalyticsAction(final ActionPerformedEvent event) {
        DialogWindow<ParticipantAnalyticsView> dialog = dialogWindows
                .lookup(this, Participant.class)
                .withViewClass(ParticipantAnalyticsView.class)
                .withSelectHandler(participants -> {
                    if (!participants.isEmpty()) {
                        participantField.setValue(participants.iterator().next());
                    }
                })
                .build();

        dialog.setWidth("1100px");
        dialog.setHeight("700px");
        dialog.setResizable(true);
        dialog.setDraggable(true);

        dialog.open();
    }
}