package net.explorviz.eaas.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(indexes = {
    @Index(columnList = "project_id,name", unique = true),
    @Index(columnList = "project_id,secret", unique = true)
})
public class Secret extends BaseEntity {
    private static final long serialVersionUID = -5164775067113669305L;

    public static final int NAME_MIN_LENGTH = 1;
    public static final int NAME_MAX_LENGTH = 64;

    public static final int SECRET_MIN_LENGTH = 1;
    public static final int SECRET_MAX_LENGTH = 255;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Project project;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = NAME_MIN_LENGTH, max = NAME_MAX_LENGTH)
    private String name;

    @NotEmpty
    @Column(nullable = false)
    @Size(min = SECRET_MIN_LENGTH, max = SECRET_MAX_LENGTH)
    @JsonIgnore
    private String secret;

    @PastOrPresent
    @Nullable
    private ZonedDateTime lastUsedDate;

    public Secret(@NotEmpty String name, @NotEmpty String secret, @NotNull Project project) {
        this.name = name;
        this.secret = secret;
        this.project = project;
    }
}
