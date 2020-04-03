package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.frontend.component.ConfirmDialog;
import net.explorviz.eaas.frontend.component.InformationDialog;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.UserRepository;
import net.explorviz.eaas.security.KeyGenerator;
import net.explorviz.eaas.security.SecurityUtils;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Consumer;

import static com.vaadin.flow.dom.ElementFactory.createParagraph;

@Slf4j
public class UserListEntry extends AbstractListEntry {
    private static final long serialVersionUID = 6175532303648791631L;

    private final UserRepository userRepo;
    private final KeyGenerator keyGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsPasswordService passwordService;
    private final Consumer<? super User> deleteCallback;

    private User user;

    public UserListEntry(User user, UserRepository userRepo, KeyGenerator keyGenerator, PasswordEncoder passwordEncoder,
                         UserDetailsPasswordService passwordService, Consumer<? super User> deleteCallback) {
        this.user = user;
        this.userRepo = userRepo;
        this.keyGenerator = keyGenerator;
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
        this.deleteCallback = deleteCallback;

        // We do not allow editing our own user in a way that would lock us out of user management
        boolean isOurself = SecurityUtils.getCurrentUser()
            .map(currentUser -> currentUser.getId().equals(user.getId()))
            .orElse(false);

        add(RichHeader.create(isOurself ? VaadinIcon.USER_STAR.create() : VaadinIcon.USER.create(),
            user.getUsername() + (isOurself ? " (You)" : "")));

        getElement().appendChild(createParagraph("Created " + user.getCreatedDate().format(
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG))));

        // TODO: Replace form with Binder

        Checkbox createProjectsCheckbox = new Checkbox("Can create projects");
        createProjectsCheckbox.setValue(user.isCreateProjectsAllowed());
        createProjectsCheckbox.addValueChangeListener(this::onCreateProjectsChanged);

        Checkbox readAllProjectsCheckbox = new Checkbox("Can read all projects");
        readAllProjectsCheckbox.setValue(user.isReadAllProjectsAllowed());
        readAllProjectsCheckbox.addValueChangeListener(this::onReadAllProjectsChanged);

        Checkbox manageAllProjectsCheckbox = new Checkbox("Can manage all projects");
        manageAllProjectsCheckbox.setValue(user.isReadAllProjectsAllowed());
        manageAllProjectsCheckbox.addValueChangeListener(this::onManageAllProjectsChanged);

        Checkbox manageInstancesCheckbox = new Checkbox("Can manage global instances");
        manageInstancesCheckbox.setValue(user.isManageInstancesAllowed());
        manageInstancesCheckbox.addValueChangeListener(this::onManageInstancesChanged);

        Checkbox manageUsersCheckbox = new Checkbox("Can manage users");
        manageUsersCheckbox.setValue(user.isManageUsersAllowed());
        manageUsersCheckbox.setEnabled(!isOurself);
        manageUsersCheckbox.addValueChangeListener(this::onManageUsersChanged);

        add(new VerticalLayout(createProjectsCheckbox, readAllProjectsCheckbox, manageAllProjectsCheckbox),
            new VerticalLayout(manageInstancesCheckbox, manageUsersCheckbox));

        Checkbox enabledCheckbox = new Checkbox("Enabled");
        enabledCheckbox.setValue(user.isEnabled());
        enabledCheckbox.setEnabled(!isOurself);
        enabledCheckbox.addValueChangeListener(this::onEnabledChanged);

        Button resetPasswordButton = new Button("Reset password", click -> this.doRequestPasswordReset());
        resetPasswordButton.setEnabled(!isOurself);
        resetPasswordButton.setIcon(VaadinIcon.PASSWORD.create());

        Button deleteButton = new Button("Delete", click -> this.doRequestDeletion());
        deleteButton.setEnabled(!isOurself);
        deleteButton.setIcon(VaadinIcon.TRASH.create());

        add(new HorizontalLayout(enabledCheckbox, resetPasswordButton, deleteButton));
    }

    private void save(User user) {
        this.user = userRepo.save(user);
        Notification.show("Changes saved");
    }

    private void onEnabledChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        user.setEnabled(event.getValue());
        save(user);
    }

    private void onCreateProjectsChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        user.setCreateProjectsAllowed(event.getValue());
        save(user);
    }

    private void onReadAllProjectsChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        user.setReadAllProjectsAllowed(event.getValue());
        save(user);
    }

    private void onManageAllProjectsChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        user.setManageAllProjectsAllowed(event.getValue());
        save(user);
    }

    private void onManageInstancesChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        user.setManageInstancesAllowed(event.getValue());
        save(user);
    }

    private void onManageUsersChanged(AbstractField.ComponentValueChangeEvent<Checkbox, Boolean> event) {
        user.setManageUsersAllowed(event.getValue());
        save(user);
    }

    // TODO: Terminate any remaining sessions when disabing or deleting users or resetting their passwords

    private void doRequestDeletion() {
        new ConfirmDialog<>(user, "Really delete user?",
            "Are you sure you want to delete User '" + user.getUsername() +
                "'? This action can not be undone. Projects owned by this user will NOT be deleted.",
            this::doDeleteUser).open();
    }

    private void doDeleteUser(User user) {
        log.info("Deleting user #{} ('{}')", user.getId(), user.getUsername());
        userRepo.delete(user);
        Notification.show("Deleted user " + user.getUsername());
        deleteCallback.accept(user);
    }

    private void doRequestPasswordReset() {
        new ConfirmDialog<>(user, "Really reset password?",
            "Are you sure you want to reset the password for the User '" + user.getUsername() + "'?",
            this::doResetPassword).open();
    }

    private void doResetPassword(User user) {
        String newPassword = keyGenerator.generatePassword();
        user = (User) passwordService.updatePassword(user, passwordEncoder.encode(newPassword));

        new InformationDialog("New password set",
            "The new password for User '" + user.getUsername() + "' is '" + newPassword + "'.").open();
    }
}
