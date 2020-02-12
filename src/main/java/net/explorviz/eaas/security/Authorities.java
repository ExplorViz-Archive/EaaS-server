package net.explorviz.eaas.security;

import net.explorviz.eaas.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Central class to keep a list of all {@link org.springframework.security.core.GrantedAuthority}s used in the
 * application. Authorities granted to users are determined in {@link User#getAuthorities()}, acting like permissions.
 */
public final class Authorities {
    private Authorities() {
    }

    /*
     * TODO: Authorities should be managable per-project.
     *
     * Right now everyone can read !hidden projects but only the owner of a project can run builds and manage the
     * project. This is because the [READ_PROJECT, RUN_BUILD, MANAGE_PROJECT] authorities do not have a project attached
     * to them yet and only generally allow this action. There are additional checks in the views so users are limited
     * to interacting with their own projects only.
     *
     * Implementing this would require a complex UI for user management within the project settings, a user list and
     * maybe even profiles. Therefore, it is out of scope for now.
     */

    /**
     * Grants the permission to create new projects.
     */
    public static final GrantedAuthority CREATE_PROJECT_AUTHORITY = create("CREATE_PROJECT");

    /**
     * Grants the permission to see the project in listing and read them, which includes listing builds, even if it is
     * hidden. Projects that aren't hidden are always visible, including listing their builds.
     */
    public static final GrantedAuthority READ_PROJECT_AUTHORITY = create("READ_PROJECT");

    /**
     * Grants the permission to run builds. Needs {@link #READ_PROJECT_AUTHORITY} as well.
     */
    public static final GrantedAuthority RUN_BUILD_AUTHORITY = create("RUN_BUILD");

    /**
     * Grants the permission to manage projects, which includes deleting builds, reading and adding API keys. Also needs
     * {@link #READ_PROJECT_AUTHORITY}.
     */
    public static final GrantedAuthority MANAGE_PROJECT_AUTHORITY = create("MANAGE_PROJECT");

    /**
     * Grants the permission to manage ExplorViz instances, i.e. viewing the active instances, starting test instances
     * and killing running instances.
     */
    public static final GrantedAuthority MANAGE_INSTANCES_AUTHORITY = create("MANAGE_INSTANCES");

    /**
     * Grants the permission to manage all users, including creating new users, deleting users and changing their
     * properties (admin flag, password, username).
     */
    public static final GrantedAuthority MANAGE_USERS_AUTHORITY = create("MANAGE_USERS");

    private static GrantedAuthority create(String authority) {
        return new SimpleGrantedAuthority(authority);
    }
}
