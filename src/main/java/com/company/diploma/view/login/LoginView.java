package com.company.diploma.view.login;

import com.company.diploma.entity.User;
import com.company.diploma.entity.UserRole;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.login.AbstractLogin.LoginEvent;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.i18n.LocaleChangeObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import io.jmix.core.CoreProperties;
import io.jmix.core.DataManager;
import io.jmix.core.MessageTools;
import io.jmix.core.security.AccessDeniedException;
import io.jmix.core.security.SystemAuthenticator;
import io.jmix.core.security.UserRepository;
import io.jmix.flowui.Notifications;
import io.jmix.flowui.component.loginform.JmixLoginForm;
import io.jmix.flowui.component.textfield.JmixPasswordField;
import io.jmix.flowui.component.textfield.TypedTextField;
import io.jmix.flowui.kit.component.ComponentUtils;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.loginform.JmixLoginI18n;
import io.jmix.flowui.view.*;
import io.jmix.securitydata.entity.RoleAssignmentEntity;
import io.jmix.securityflowui.authentication.AuthDetails;
import io.jmix.securityflowui.authentication.LoginViewSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

@Route(value = "login")
@ViewController(id = "LoginView")
@ViewDescriptor(path = "login-view.xml")
public class LoginView extends StandardView implements LocaleChangeObserver {

    private static final Logger log = LoggerFactory.getLogger(LoginView.class);

    @Autowired
    private CoreProperties coreProperties;

    @Autowired
    private LoginViewSupport loginViewSupport;

    @Autowired
    private MessageTools messageTools;

    @ViewComponent
    private JmixLoginForm login;

    @ViewComponent
    private MessageBundle messageBundle;

    @Value("${ui.login.defaultUsername:}")
    private String defaultUsername;

    @Value("${ui.login.defaultPassword:}")
    private String defaultPassword;

    @Subscribe
    public void onInit(final InitEvent event) {
        initLocales();
        initDefaultCredentials();
    }

    private void initLocales() {
        LinkedHashMap<Locale, String> locales = coreProperties.getAvailableLocales().stream()
                .collect(Collectors.toMap(Function.identity(), messageTools::getLocaleDisplayName, (s1, s2) -> s1,
                        LinkedHashMap::new));

        ComponentUtils.setItemsMap(login, locales);

        login.setSelectedLocale(VaadinSession.getCurrent().getLocale());
    }

    private void initDefaultCredentials() {
        if (StringUtils.isNotBlank(defaultUsername)) {
            login.setUsername(defaultUsername);
        }

        if (StringUtils.isNotBlank(defaultPassword)) {
            login.setPassword(defaultPassword);
        }
    }

    @Subscribe("login")
    public void onLogin(final LoginEvent event) {
        try {
            loginViewSupport.authenticate(
                    AuthDetails.of(event.getUsername(), event.getPassword())
                            .withLocale(login.getSelectedLocale())
                            .withRememberMe(login.isRememberMe())
            );
        } catch (final BadCredentialsException | DisabledException | LockedException | AccessDeniedException e) {
            log.warn("Login failed for user '{}': {}", event.getUsername(), e.toString());
            event.getSource().setError(true);
        }
    }

    @Override
    public void localeChange(final LocaleChangeEvent event) {
        UI.getCurrent().getPage().setTitle(messageBundle.getMessage("LoginView.title"));

        final JmixLoginI18n loginI18n = JmixLoginI18n.createDefault();

        final JmixLoginI18n.JmixForm form = new JmixLoginI18n.JmixForm();
        form.setTitle(messageBundle.getMessage("loginForm.headerTitle"));
        form.setUsername(messageBundle.getMessage("loginForm.username"));
        form.setPassword(messageBundle.getMessage("loginForm.password"));
        form.setSubmit(messageBundle.getMessage("loginForm.submit"));
        form.setForgotPassword(messageBundle.getMessage("loginForm.forgotPassword"));
        form.setRememberMe(messageBundle.getMessage("loginForm.rememberMe"));
        loginI18n.setForm(form);

        final LoginI18n.ErrorMessage errorMessage = new LoginI18n.ErrorMessage();
        errorMessage.setTitle(messageBundle.getMessage("loginForm.errorTitle"));
        errorMessage.setMessage(messageBundle.getMessage("loginForm.badCredentials"));
        errorMessage.setUsername(messageBundle.getMessage("loginForm.errorUsername"));
        errorMessage.setPassword(messageBundle.getMessage("loginForm.errorPassword"));
        loginI18n.setErrorMessage(errorMessage);

        login.setI18n(loginI18n);
    }

    @ViewComponent
    private TypedTextField<String> regUsernameField;
    @ViewComponent
    private TypedTextField<String> regLastNameField;
    @ViewComponent
    private TypedTextField<String> regFirstNameField;
    @ViewComponent
    private TypedTextField<String> regPatronymicField;
    @ViewComponent
    private com.vaadin.flow.component.textfield.PasswordField regPasswordField;
    @ViewComponent
    private com.vaadin.flow.component.textfield.PasswordField regConfirmPasswordField;

    @Autowired
    private DataManager dataManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Notifications notifications;
    @Autowired
    private SystemAuthenticator systemAuthenticator;

    @Subscribe("registerBtn")
    public void onRegisterBtnClick(final ClickEvent<JmixButton> event) {
        String username = regUsernameField.getValue();
        String password = regPasswordField.getValue();
        String confirm = regConfirmPasswordField.getValue();

        if (username == null || password == null || !password.equals(confirm)) {
            notifications.create("Пароли не совпадают или не заполнены").show();
            return;
        }

        // Выполняем под системной записью, так как аноним не может писать в таблицу User
        systemAuthenticator.runWithSystem(() -> {
            // Проверка на уникальность логина
            boolean exists = dataManager.load(User.class)
                    .query("select u from User u where u.username = :username")
                    .parameter("username", username)
                    .optional().isPresent();

            if (exists) {
                notifications.create("Пользователь с таким логином уже есть").show();
                return;
            }

            User newUser = dataManager.create(User.class);
            newUser.setUsername(username);
            newUser.setLastName(regLastNameField.getValue());
            newUser.setFirstName(regFirstNameField.getValue());
            newUser.setPatronymic(regPatronymicField.getValue());
            newUser.setPassword(passwordEncoder.encode(password));
            newUser.setActive(true);
            newUser.setUserRole(UserRole.STUDENT);

            dataManager.save(newUser);
            assignRoles(username);
            notifications.create("Успех! Теперь войдите под своим логином").show();
            clearFields();
        });
    }

    private void clearFields() {
        regUsernameField.clear();
        regLastNameField.clear();
        regFirstNameField.clear();
        regPatronymicField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
    }

    /**
     * Метод для назначения ролей пользователю
     */
    private void assignRoles(String username) {
        // Список кодов ролей, которые нужно назначить
        List<String> rolesToAssign = List.of(
                "bpm-process-task-performer",
                "student-role",
                "ui-minimal",
                "participant-role"
        );

        for (String roleCode : rolesToAssign) {
            RoleAssignmentEntity assignment = dataManager.create(RoleAssignmentEntity.class);
            assignment.setUsername(username);
            assignment.setRoleCode(roleCode);
            assignment.setRoleType("resource"); // Для ресурсных ролей (Resource Roles)
            dataManager.save(assignment);
        }
    }
}
