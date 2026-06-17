package com.company.diploma.listener;

import io.jmix.bpm.engine.events.UserTaskCreatedEvent;
import io.jmix.notifications.NotificationManager;
import io.jmix.notifications.channel.impl.InAppNotificationChannel;
import io.jmix.notifications.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserTaskNotificationListener {

    @Autowired
    private NotificationManager notificationManager;

    @EventListener
    public void onUserTaskCreated(UserTaskCreatedEvent event) {
        // Получатель – пользователь, которому выдана задача
        String assigneeUsername = event.getUsername(); // в событии есть логин
        if (assigneeUsername == null) {
            return; // задача может быть без назначенного пользователя
        }


//        var task = event.getTask();
//        String taskName = task.getName();

        notificationManager.createNotification()
                .withSubject("Новая задача")
                .withRecipientUsernames(assigneeUsername)
                .toChannelsByNames(InAppNotificationChannel.NAME)
                .withContentType(ContentType.PLAIN)
                .withBody("Вам назначена новая задача.")
                .send();
    }
}