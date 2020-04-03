package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.vaadin.flow.dom.ElementFactory.createHeading2;

@Route(value = ChangePasswordView.ROUTE, layout = MainLayout.class)
@PageTitle("Change Password - " + Application.PAGE_TITLE)
@Slf4j
public class ChangePasswordView extends VerticalLayout {
    private static final long serialVersionUID = 2125206642494586777L;

    public static final String ROUTE = "changepassword";

    private final UserDetailsPasswordService passwordService;
    private final PasswordEncoder passwordEncoder;
    private final int minimumPasswordLength;

    private final PasswordField oldPassword;
    private final PasswordField newPassword;
    private final PasswordField repeatPassword;
    private final Button changeButton;

    public ChangePasswordView(UserDetailsPasswordService passwordService, PasswordEncoder passwordEncoder,
                              @Value("${eaas.security.minimumPasswordLength}") int minimumPasswordLength) {
        this.passwordService = passwordService;
        this.passwordEncoder = passwordEncoder;
        this.minimumPasswordLength = minimumPasswordLength;

        getElement().appendChild(createHeading2("Change Password"));

        // TODO: Replace form with binder

        oldPassword = new PasswordField();
        oldPassword.setPlaceholder("Old password");

        newPassword = new PasswordField();
        newPassword.setPlaceholder("New password");
        newPassword.setMinLength(minimumPasswordLength);

        repeatPassword = new PasswordField();
        repeatPassword.setPlaceholder("Repeat password");
        repeatPassword.setMinLength(minimumPasswordLength);

        changeButton = new Button("Change password", click -> this.doChangePassword());
        changeButton.setIcon(VaadinIcon.EDIT.create());
        changeButton.setDisableOnClick(true);

        add(oldPassword, newPassword, repeatPassword, changeButton);
    }

    private void doChangePassword() {
        User user = SecurityUtils.getCurrentUser().orElseThrow(() ->
            new IllegalStateException("Tried to change password without authentication"));

        String password = newPassword.getValue();
        boolean fail = false;

        if (password.length() < minimumPasswordLength) {
            newPassword.setInvalid(true);
            newPassword.setErrorMessage("Password is too short");
            fail = true;
        } else {
            newPassword.setInvalid(false);
            newPassword.setErrorMessage(null);
        }

        if (!password.equals(repeatPassword.getValue())) {
            repeatPassword.setInvalid(true);
            repeatPassword.setErrorMessage("Passwords do not match");
            fail = true;
        } else {
            repeatPassword.setInvalid(false);
            repeatPassword.setErrorMessage(null);
        }

        if (!passwordEncoder.matches(oldPassword.getValue(), user.getPassword())) {
            oldPassword.setInvalid(true);
            oldPassword.setErrorMessage("Password is invalid");
            fail = true;
        } else {
            oldPassword.setInvalid(false);
            oldPassword.setErrorMessage(null);
        }

        if (!fail) {
            passwordService.updatePassword(user, passwordEncoder.encode(password));
            getUI().ifPresent(ui -> ui.navigate(MainView.class));
            Notification.show("Password changed");
        }

        changeButton.setEnabled(true);
    }
}
