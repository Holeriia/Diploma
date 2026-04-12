package com.company.diploma.view.participant;

import com.company.diploma.entity.Participant;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "participants", layout = MainView.class)
@ViewController(id = "Participant.list")
@ViewDescriptor(path = "participant-list-view.xml")
@LookupComponent("participantsDataGrid")
@DialogMode(width = "64em")
public class ParticipantListView extends StandardListView<Participant> {
}