package net.explorviz.eaas.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
    @Index(columnList = "project_id,name"),
    @Index(columnList = "dockerImage", unique = true)
})
public class Build extends BaseEntity {
    private static final long serialVersionUID = 8960003619026674513L;

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 255;

    /**
     * Build name, specified by the build upload script. Can be anything
     */
    @NotEmpty
    @Column(nullable = false)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String name;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Project project;

    @NotEmpty
    @Column(nullable = false, unique = true)
    private String dockerImage;

    public Build(@NotEmpty String name, @NotNull Project project, @NotEmpty String dockerImage) {
        this.name = name;
        this.project = project;
        this.dockerImage = dockerImage;
    }
}
