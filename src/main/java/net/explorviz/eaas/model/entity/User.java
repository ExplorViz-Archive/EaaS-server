package net.explorviz.eaas.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.explorviz.eaas.security.Authorities;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(indexes = @Index(columnList = "username", unique = true))
public class User extends BaseEntity implements UserDetails {
    private static final long serialVersionUID = -2609179636815086264L;

    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 64;

    @NotEmpty
    @Column(unique = true, nullable = false, length = USERNAME_MAX_LENGTH)
    @Size(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH)
    private String username;

    @NotEmpty
    @Column(nullable = false)
    @JsonIgnore
    private String password;

    private boolean createProjectsAllowed;

    private boolean manageInstancesAllowed;

    private boolean manageUsersAllowed;

    private boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    @JsonIgnore
    private List<Project> ownedProjects;

    public User(@NotEmpty String username, @NotEmpty String password, boolean createProjectsAllowed,
                boolean manageInstancesAllowed, boolean manageUsersAllowed) {
        this.username = username;
        this.password = password;
        this.createProjectsAllowed = createProjectsAllowed;
        this.manageInstancesAllowed = manageInstancesAllowed;
        this.manageUsersAllowed = manageUsersAllowed;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>(7);

        if (createProjectsAllowed) {
            authorities.add(Authorities.CREATE_PROJECT_AUTHORITY);
        }

        authorities.add(Authorities.READ_PROJECT_AUTHORITY);
        authorities.add(Authorities.RUN_BUILD_AUTHORITY);
        authorities.add(Authorities.DELETE_BUILD_AUTHORITY);
        authorities.add(Authorities.MANAGE_PROJECT_AUTHORITY);

        if (manageInstancesAllowed) {
            authorities.add(Authorities.MANAGE_INSTANCES_AUTHORITY);
        }

        if (manageUsersAllowed) {
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
