package com.company.diploma.view.participantanalytics;

import com.company.diploma.entity.Interest;
import com.company.diploma.entity.Participant;
import com.company.diploma.entity.User;
import com.company.diploma.entity.UserRole;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import io.jmix.core.DataManager;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

@ViewController("ParticipantAnalyticsView")
@ViewDescriptor("participant-analytics-view.xml")
@LookupComponent("analyticsDataGrid")
public class ParticipantAnalyticsView extends StandardListView<Participant> {

    @Autowired
    private CurrentAuthentication currentAuthentication;

    @ViewComponent
    private CollectionLoader<Participant> participantsDl;

    @Autowired
    private DataManager dataManager;

    @Autowired
    private DialogWindows dialogWindows;

    @Subscribe
    public void onBeforeShow(final BeforeShowEvent event) {
        User authUser = (User) currentAuthentication.getUser();
        User currentUser = dataManager.load(User.class)
                .id(authUser.getId())
                .one();
        UserRole currentRole = currentUser.getUserRole();
        if (currentRole != null) {
            participantsDl.setParameter("currentRole", currentRole.getId());
        }
        participantsDl.load();
    }

    @Supply(to = "analyticsDataGrid.interestsTextColumn", subject = "renderer")
    private Renderer<Participant> interestsTextColumnRenderer() {
        return new ComponentRenderer<>(p -> {
            Span span = new Span();
            User user = p.getUser();
            if (user != null && user.getInterests() != null && !user.getInterests().isEmpty()) {
                String interestsText = user.getInterests().stream()
                        .map(Interest::getName)
                        .collect(Collectors.joining(", "));
                span.setText(interestsText);
            } else {
                span.setText("-");
                span.getStyle().set("color", "var(--lumo-secondary-text-color)");
            }
            span.getStyle().set("white-space", "normal");
            span.getStyle().set("word-break", "break-word");
            span.getStyle().set("display", "inline-block");
            span.getStyle().set("line-height", "1.4");
            return span;
        });
    }

    // Рендерер для кнопки открытия профиля в диалоговом окне
    @Supply(to = "analyticsDataGrid.profileActionsColumn", subject = "renderer")
    private Renderer<Participant> profileActionsColumnRenderer() {
        return new ComponentRenderer<>(p -> {
            Button profileButton = new Button("Профиль");
            profileButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            profileButton.setIcon(VaadinIcon.USER.create());

            profileButton.addClickListener(event -> {
                dialogWindows.detail(this, Participant.class)
                        .withViewId("Participant.profile")
                        .editEntity(p)
                        .open();
            });

            return profileButton;
        });
    }
}