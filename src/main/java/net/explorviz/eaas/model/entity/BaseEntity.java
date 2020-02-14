package net.explorviz.eaas.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.PastOrPresent;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable, Persistable<Long> {
    private static final long serialVersionUID = 3255268320796480569L;

    // TODO: Replace class with org.springframework.data.jpa.domain.AbstractAuditable

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;

    //@CreatedBy
    //@ManyToOne(optional = false)
    //@JoinColumn(nullable = false)
    //@JsonIgnore
    //private User createdBy;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @PastOrPresent
    private ZonedDateTime createdDate;

    //@LastModifiedBy
    //@ManyToOne(optional = false)
    //@JoinColumn(nullable = false)
    //@JsonIgnore
    //private User lastModifiedBy;

    @LastModifiedDate
    @Column(nullable = false)
    @PastOrPresent
    private ZonedDateTime lastModifiedDate;

    @Transient
    @JsonIgnore
    @Override
    public boolean isNew() {
        return id == null;
    }
}
