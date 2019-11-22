package net.explorviz.eaas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.explorviz.eaas.security.Authorities;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.*;

@Entity
@Data
@NoArgsConstructor
@Table(indexes = @Index(columnList = "username", unique = true))
public class User implements UserDetails {
    private static final long serialVersionUID = -2609179636815086264L;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(unique = true, nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String username;

    @NotEmpty
    @Column(nullable = false)
    private String password;

    private boolean enabled;

    private boolean admin;

    @CreatedDate
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private List<Project> ownedProjects;

    public User(@NotEmpty String username, @NotEmpty String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.enabled = true;
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>(8);

        authorities.add(Authorities.READ_PUBLIC_AUTHORITY);
        authorities.add(Authorities.READ_PROJECT_LIST_AUTHORITY);
        authorities.add(Authorities.READ_OWNED_PROJECTS_AUTHORITY);
        authorities.add(Authorities.MANAGE_OWNED_PROJECTS_AUTHORITY);

        if (admin) {
            authorities.add(Authorities.READ_ALL_PROJECTS_AUTHORITY);
            authorities.add(Authorities.MANAGE_ALL_PROJECTS_AUTHORITY);
            authorities.add(Authorities.MANAGE_USERS_AUTHORITY);
        }

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
