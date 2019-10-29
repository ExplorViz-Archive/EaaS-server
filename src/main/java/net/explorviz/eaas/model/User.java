package net.explorviz.eaas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = -7165033681160161427L;

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

    private boolean admin;

    @CreatedDate
    @CreationTimestamp
    @NotNull
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "owner")
    private List<Project> ownedProjects;

    public User(@NotEmpty String username, @NotEmpty String password, boolean admin) {
        this.username = username;
        this.password = password;
        this.admin = admin;
    }
}
