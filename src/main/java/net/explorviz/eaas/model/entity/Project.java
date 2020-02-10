package net.explorviz.eaas.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
    @Index(columnList = "name", unique = true),
    @Index(columnList = "owner_id")
})
public class Project extends BaseEntity {
    private static final long serialVersionUID = -1864944572450590914L;

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 64;

    @CreatedBy
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnore
    private User owner;

    @NotEmpty
    @Column(unique = true, nullable = false)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String name;

    /**
     * Hidden projects are only visible to users who have the
     * {@link net.explorviz.eaas.security.Authorities#READ_PROJECT_AUTHORITY} for it.
     */
    private boolean hidden;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    @JsonIgnore
    private List<Build> builds;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    @JsonIgnore
    private List<Secret> secrets;

    public Project(@NotEmpty String name, @NotNull User owner) {
        this.name = name;
        this.owner = owner;
    }
}
