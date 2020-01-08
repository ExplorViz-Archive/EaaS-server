package net.explorviz.eaas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
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

    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 64;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(unique = true, nullable = false, length = NAME_MAX_LENGTH)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String name;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = 1, max = 255)
    @JsonIgnore
    private String secret;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @CreatedDate
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @PastOrPresent
    private Instant createdDate;

    @PastOrPresent
    private Instant lastUsedDate;

    public Secret(@NotEmpty String name, @NotEmpty String secret, @NotNull Project project) {
        this.name = name;
        this.secret = secret;
        this.project = project;
    }
}
