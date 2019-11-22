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
    @Index(columnList = "project_id,name"),
    @Index(columnList = "imageID", unique = true)
})
public class Build implements Serializable {
    private static final long serialVersionUID = 8960003619026674513L;

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

    @NotEmpty
    @Column(nullable = false, unique = true)
    @Size(min = 1)
    private String imageID;

    @CreatedDate
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdDate;

    public Build(@NotEmpty String name, @NotNull Project project, @NotEmpty String imageID) {
        this.name = name;
        this.project = project;
        this.imageID = imageID;
    }
}
