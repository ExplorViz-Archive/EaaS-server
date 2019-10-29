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
public class Build implements Serializable {
    private static final long serialVersionUID = 6487170222760228978L;

    @Id
    @GeneratedValue
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = 1, max = 255)
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @CreatedDate
    @CreationTimestamp
    @NotNull
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    public Build(@NotEmpty String name, @NotNull Project project) {
        this.name = name;
        this.project = project;
    }
}
