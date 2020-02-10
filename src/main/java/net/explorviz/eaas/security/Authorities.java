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

    // TODO: Authorities should be managable per-project

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
     * Grants the permission globally manage all users and ExplorViz instances, including creating new users, deleting
     * users and changing their properties (admin flag, password, username).
     */
    public static final GrantedAuthority ADMINISTER_AUTHORITY = create("ADMINISTER");

    private static GrantedAuthority create(String authority) {
        return new SimpleGrantedAuthority(authority);
    }
}
