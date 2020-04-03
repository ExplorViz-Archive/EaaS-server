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
     * Instead of allowing RUN_BUILD for all readable projects, and only having the coarse *_OWNED_PROJECTS and
     * *_ALL_PROJECTS authorities, user permissions should be configurable per-project. This means we would have
     * authorities [READ, RUN_BUILD, DELETE_BUILD, MANAGE_SECRETS, MANAGE_COLLABORATORS, DELETE_RPOJECT] each with
     * the project attached to them they are for.
     *
     * Implementing this would require a database table for authorities, a complex UI for user management within the
     * project settings, a user list and maybe even profiles. Therefore, it is out of scope for now.
     */

    /**
     * Grants the permission to create new projects. The user who creates a project becomes the owner of it.
     */
    public static final GrantedAuthority CREATE_PROJECT_AUTHORITY = create("CREATE_PROJECT");

    /**
     * Grants the permission to run and stop builds in ExplorViz, as well as reading logs from the running instances.
     * Also shows the per-project instances page. Requires read access to the project, i.e. by being !hidden or if one
     * of the READ authorities applies.
     */
    public static final GrantedAuthority RUN_BUILD_AUTHORITY = create("RUN_BUILD");

    /**
     * Grants access to owned projects, which includes seeing them in listings and listing their builds, even if they
     * are hidden.
     */
    public static final GrantedAuthority READ_OWNED_PROJECTS_AUTHORITY = create("READ_OWNED_PROJECTS");

    /**
     * Grants access to all projects, even if they are hidden, no matter who owns them.
     */
    public static final GrantedAuthority READ_ALL_PROJECTS_AUTHORITY = create("READ_ALL_PROJECTS");

    /**
     * Grants the permission to manage projects owned by yourself, which includes changing project settings, reading and
     * adding API keys and deleting the project.
     */
    public static final GrantedAuthority MANAGE_OWNED_PROJECTS_AUTHORITY = create("MANAGE_OWNED_PROJECTS");

    /**
     * Grants the permission to manage all projects, no matter who owns them.
     */
    public static final GrantedAuthority MANAGE_ALL_PROJECTS_AUTHORITY = create("MANAGE_ALL_PROJECTS");

    /**
     * Grants the permission to globally manage ExplorViz instances, i.e. viewing all active instances, starting test
     * instances and killing running instances.
     */
    public static final GrantedAuthority MANAGE_INSTANCES_AUTHORITY = create("MANAGE_INSTANCES");

    /**
     * Grants the permission to globally manage users, including creating new users, deleting users and changing their
     * permissions.
     */
    public static final GrantedAuthority MANAGE_USERS_AUTHORITY = create("MANAGE_USERS");

    private static GrantedAuthority create(String authority) {
        return new SimpleGrantedAuthority(authority);
    }
}
