package com.company.diploma.view.participant;

import com.company.diploma.entity.Interest;
import com.company.diploma.entity.Participant;
import com.company.diploma.entity.Topic;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;

@Route(value = "participant.profile/:id", layout = MainView.class)
@ViewController(id = "Participant.profile")
@ViewDescriptor(path = "participant-profile-view.xml")
@EditedEntityContainer("participantDc")
public class ParticipantProfileView extends StandardDetailView<Participant> {
    @ViewComponent
    private CollectionContainer<Interest> interestsDc;

    @ViewComponent
    private CollectionContainer<Topic> topicsDc;

    @Subscribe
    public void onReady(ReadyEvent event) {
        Participant participant = getEditedEntity();

        interestsDc.setItems(participant.getUser().getInterests());
        topicsDc.setItems(participant.getTopics());
    }
}