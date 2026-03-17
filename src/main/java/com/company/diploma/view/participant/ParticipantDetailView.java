package com.company.diploma.view.participant;

import com.company.diploma.entity.Participant;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "participants/:id", layout = MainView.class)
@ViewController(id = "Participant.detail")
@ViewDescriptor(path = "participant-detail-view.xml")
@EditedEntityContainer("participantDc")
public class ParticipantDetailView extends StandardDetailView<Participant> {
}