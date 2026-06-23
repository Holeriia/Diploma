package com.company.diploma.view.topic;

import com.company.diploma.entity.Participant;
import com.company.diploma.entity.Topic;
import com.company.diploma.entity.TopicStatus;
import com.company.diploma.entity.User;
import com.company.diploma.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route(value = "topics/:id", layout = MainView.class)
@ViewController(id = "Topic.detail")
@ViewDescriptor(path = "topic-detail-view.xml")
@EditedEntityContainer("topicDc")
public class TopicDetailView extends StandardDetailView<Topic> {

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @Autowired
    private DataManager dataManager;

    @Subscribe
    public void onInitEntity(final InitEntityEvent<Topic> event) {
        Topic topic = event.getEntity();

        // 1. Получаем текущего системного пользователя
        if (currentAuthentication.getUser() instanceof User currentUser) {

            // 2. Ищем связанную сущность Participant в базе данных по текущему пользователю
            Optional<Participant> participantOpt = dataManager.load(Participant.class)
                    .query("select p from Participant p where p.user = :currentUser")
                    .parameter("currentUser", currentUser)
                    .optional();

            // 3. Если нашли участника, то именно его устанавливаем автором темы
            participantOpt.ifPresent(topic::setAuthor);
        }

        // 4. Устанавливаем статус темы
        topic.setStatus(TopicStatus.FREE);
    }
}