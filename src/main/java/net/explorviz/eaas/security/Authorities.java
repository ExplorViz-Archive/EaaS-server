package net.explorviz.eaas.security;

import net.explorviz.eaas.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Central class to keep a list of all {@link org.springframework.security.core.GrantedAuthority}s used in the
 * application. Authorities granted to users are determined in {@link User#getAuthorities()}, acting like permissions.
 */
public final class Authorities {
    private Authorities() {
    }

    /**
     * Grants the permission to enter public views, containing no project- or user-specific information.
     */
    public static final GrantedAuthority READ_PUBLIC_AUTHORITY = create("READ_PUBLIC");

    /**
     * Grants the permission to view the list of projects.
     */
    public static final GrantedAuthority READ_PROJECT_LIST_AUTHORITY = create("READ_PROJECT_LIST");

    /**
     * Grants the permission to read projects owned by the user in question, which includes listing and running builds.
     */
    public static final GrantedAuthority READ_OWNED_PROJECTS_AUTHORITY = create("READ_OWNED_PROJECTS");

    /**
     * Grants the permission to manage projects owned by the user in question, which includes deleting builds, reading
     * and adding API keys. Requires {@link #READ_OWNED_PROJECTS_AUTHORITY}.
     */
    public static final GrantedAuthority MANAGE_OWNED_PROJECTS_AUTHORITY = create("MANAGE_OWNED_PROJECTS");

    /**
     * Grants the permission to read all projects.
     */
    public static final GrantedAuthority READ_ALL_PROJECTS_AUTHORITY = create("READ_ALL_PROJECTS");

    /**
     * Grants the permission to manage all projects. Requires {@link #READ_ALL_PROJECTS_AUTHORITY}.
     */
    public static final GrantedAuthority MANAGE_ALL_PROJECTS_AUTHORITY = create("MANAGE_ALL_PROJECTS");

    /**
     * Grants the permission to manage users, including creating new users, deleting users and changing their properties
     * (admin flag, password, username).
     */
    public static final GrantedAuthority MANAGE_USERS_AUTHORITY = create("MANAGE_USERS");

    private static GrantedAuthority create(String authority) {
        return new SimpleGrantedAuthority(authority);
    }
}
