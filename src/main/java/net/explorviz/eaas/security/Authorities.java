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
     * Grants the permission to read projects owned by themselves, which includes listing builds.
     * 
     * TODO: Should be a per-project authority
     */
    public static final GrantedAuthority READ_AUTHORITY = create("USE");

    /**
     * Grants the permission to run builds.
     * 
     * TODO: Should be a per-project authority
     */
    public static final GrantedAuthority RUN_AUTHORITY = create("RUN");

    /**
     * Grants the permission to manage projects, which includes deleting builds, reading and adding API keys. Depends on
     * {@link #READ_AUTHORITY}.
     * 
     * TODO: Should be a per-project authority
     */
    public static final GrantedAuthority MANAGE_AUTHORITY = create("MANAGE");

    /**
     * Grants the permission to read (see {@link #READ_AUTHORITY}) and manage (see {@link #MANAGE_AUTHORITY}) all
     * projects and to manage users, including creating new users, deleting users and changing their properties
     * (admin flag, password, username).
     */
    public static final GrantedAuthority ADMINISTER_AUTHORITY = create("ADMINISTER");

    private static GrantedAuthority create(String authority) {
        return new SimpleGrantedAuthority(authority);
    }
}
