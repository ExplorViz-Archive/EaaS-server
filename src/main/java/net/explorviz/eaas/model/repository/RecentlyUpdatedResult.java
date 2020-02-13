package net.explorviz.eaas.model.repository;

import net.explorviz.eaas.model.entity.Build;
import net.explorviz.eaas.model.entity.Project;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

public interface RecentlyUpdatedResult {
    /**
     * Returns the project, which has recently been updated (or created)
     */
    @NotNull
    @NonNull
    Project getProject();

    /**
     * Returns the most recent build for the {@link #getProject()}, or {@code null} if none exist yet
     */
    @Nullable
    Build getBuild();
}
