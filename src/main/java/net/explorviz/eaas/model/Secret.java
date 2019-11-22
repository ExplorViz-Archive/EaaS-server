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

@Entity
@Data
@NoArgsConstructor
@Table(indexes = {
    @Index(columnList = "name", unique = true),
    @Index(columnList = "secret")
})
public class Secret implements Serializable {
    private static final long serialVersionUID = -5164775067113669305L;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(unique = true, nullable = false, length = 64)
    @Size(min = 2, max = 64)
    private String name;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = 1, max = 255)
    private String secret;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @CreatedDate
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    private Instant lastUsedDate;

    public Secret(@NotEmpty String name, @NotEmpty String secret, @NotNull Project project) {
        this.name = name;
        this.secret = secret;
        this.project = project;
    }
}
