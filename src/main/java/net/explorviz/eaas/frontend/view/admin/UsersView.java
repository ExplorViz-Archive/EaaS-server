package net.explorviz.eaas.frontend.view.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.InformationDialog;
import net.explorviz.eaas.frontend.component.list.RichList;
import net.explorviz.eaas.frontend.component.list.UserListEntry;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.frontend.view.DynamicView;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.UserRepository;
import net.explorviz.eaas.security.KeyGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static com.vaadin.flow.dom.ElementFactory.*;

@PageTitle("Manage Users - " + Application.PAGE_TITLE)
@Route(value = "manage/users", layout = MainLayout.class)
@Secured("MANAGE_USERS")
@Slf4j
public class UsersView extends DynamicView {
    private static final long serialVersionUID = 4767534558255466871L;

    private static final Pattern USERNAME_PATTERN = Pattern.compile(User.USERNAME_PATTERN);

    private final UserRepository userRepo;
    private final KeyGenerator keyGenerator;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsPasswordService passwordService;

    private TextField userName;
    private RichList<User> userList;

    public UsersView(UserRepository userRepo, KeyGenerator keyGenerator, PasswordEncoder passwordEncoder,
                     UserDetailsPasswordService passwordService) {
        this.userRepo = userRepo;
        this.keyGenerator = keyGenerator;
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
    }

    @Override
    protected void build() {
        getElement().appendChild(createHeading2("Users"),
            createParagraph("Changes to user permissions only apply after the next login."),
            createHeading4("Create new user"));

        // TODO: Replace form with binder

        userName = new TextField();
        userName.setMinLength(User.USERNAME_MIN_LENGTH);
        userName.setMaxLength(User.USERNAME_MAX_LENGTH);
        userName.setPlaceholder("Project name");

        Button create = new Button("Create");
        create.addClickListener(click -> this.doCreateUser());

        add(new HorizontalLayout(userName, create));

        Page<User> users = userRepo.findAll(Pageable.unpaged());

        userList = new RichList<>(user -> new UserListEntry(user, userRepo, keyGenerator, passwordEncoder,
            passwordService, userList::removeEntry));
        userList.addEntries(users);
        add(userList);
    }

    private void doCreateUser() {
        String name = StringUtils.trimWhitespace(userName.getValue());

        if (!StringUtils.hasText(name)) {
            userName.setInvalid(true);
            userName.setErrorMessage("User name may not be empty");
        } else if (userRepo.existsByUsernameIgnoreCase(name)) {
            userName.setInvalid(true);
            userName.setErrorMessage("A user with this name already exists!");
        } else if (!USERNAME_PATTERN.matcher(name).matches()) {
            userName.setInvalid(true);
            userName.setErrorMessage("Username does not match allowed pattern");
        } else {
            userName.setInvalid(false);
            userName.setErrorMessage(null);

            String password = keyGenerator.generatePassword();
            User user = userRepo.save(new User(name, passwordEncoder.encode(password), true, false, false, false,
                false));
            userName.clear();
            log.info("Created new user #{} ('{}')", user.getId(), user.getUsername());
            userList.addEntry(user);
            Notification.show("Created user " + user.getUsername());
            new InformationDialog("User created",
                "User '" + user.getUsername() + "' has been created. Their password is '" + password
                    + "'. They can change it with the Change Password functionality.").open();
        }
    }
}
