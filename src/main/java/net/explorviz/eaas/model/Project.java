package net.explorviz.eaas.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
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
@Table(indexes = @Index(columnList = "name", unique = true))
public class Project implements Serializable {
    private static final long serialVersionUID = -1864944572450590914L;

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 64;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(unique = true, nullable = false)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String name;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @CreatedDate
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    private List<Build> builds;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "project")
    private List<Secret> secrets;

    public Project(@NotEmpty String name, @NotNull User owner) {
        this.name = name;
        this.owner = owner;
    }
}
