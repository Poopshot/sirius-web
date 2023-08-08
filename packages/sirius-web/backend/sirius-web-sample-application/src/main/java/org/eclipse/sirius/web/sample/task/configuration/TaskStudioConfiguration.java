/*******************************************************************************
 * Copyright (c) 2023 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.sample.task.configuration;

import java.util.Objects;
import java.util.UUID;

import org.eclipse.sirius.components.core.api.IEditingContextPersistenceService;
import org.eclipse.sirius.components.core.api.IEditingContextSearchService;
import org.eclipse.sirius.components.core.api.IPayload;
import org.eclipse.sirius.web.persistence.entities.ProjectEntity;
import org.eclipse.sirius.web.persistence.entities.ProjectNatureEntity;
import org.eclipse.sirius.web.persistence.repositories.IDocumentRepository;
import org.eclipse.sirius.web.persistence.repositories.IProjectNatureRepository;
import org.eclipse.sirius.web.services.api.projects.CreateProjectInput;
import org.eclipse.sirius.web.services.api.projects.CreateProjectSuccessPayload;
import org.eclipse.sirius.web.services.projects.ProjectService;
import org.springframework.context.annotation.Configuration;

/**
 * Configure to create the "Task Studio" (domain & view).
 *
 * @author lfasani
 */
@Configuration
public class TaskStudioConfiguration {
    private static final String TASK_STUDIO_NAME = "Task Studio";

    private final ProjectService projectService;

    private final IDocumentRepository documentRepository;

    private final TaskStudioTemplatesInitializer taskStudioTemplatesInitializer;

    private final IProjectNatureRepository projectNatureRepository;

    private final IEditingContextSearchService editingContextSearchService;

    private final IEditingContextPersistenceService editingContextPersistenceService;

    public TaskStudioConfiguration(ProjectService projectService, IDocumentRepository documentRepository, TaskStudioTemplatesInitializer taskStudioTemplatesInitializer,
            IProjectNatureRepository projectNatureRepository, IEditingContextSearchService editingContextSearchService, IEditingContextPersistenceService editingContextPersistenceService) {
        this.projectService = Objects.requireNonNull(projectService);
        this.documentRepository = Objects.requireNonNull(documentRepository);
        this.taskStudioTemplatesInitializer = Objects.requireNonNull(taskStudioTemplatesInitializer);
        this.projectNatureRepository = Objects.requireNonNull(projectNatureRepository);
        this.editingContextSearchService = Objects.requireNonNull(editingContextSearchService);
        this.editingContextPersistenceService = Objects.requireNonNull(editingContextPersistenceService);

        this.initializeProjectStudio();
    }

    /**
     * If not yet existing, create a project with task domain and associated views.
     */
    private void initializeProjectStudio() {
        boolean isStudioAlreadyInitialized = this.documentRepository.findAll().stream()
                .filter(documentEntity -> TaskStudioTemplatesInitializer.TASK_DOMAIN_DOCUMENT_NAME.equals(documentEntity.getName())
                        || TaskStudioTemplatesInitializer.GANTT_VIEW_DOCUMENT_NAME.equals(documentEntity.getName()))
                .findAny()
                .isPresent();

        if (!isStudioAlreadyInitialized) {
            this.createProjectFromTemplate();
        }
    }

    private void createProjectFromTemplate() {
        var createProjectInput = new CreateProjectInput(UUID.nameUUIDFromBytes(TASK_STUDIO_NAME.getBytes()), TASK_STUDIO_NAME);
        IPayload payload = this.projectService.createProject(createProjectInput);
        if (payload instanceof CreateProjectSuccessPayload createProjectSuccessPayload) {
            var projectId = createProjectSuccessPayload.project().getId();

            this.projectNatureRepository.save(this.createProjectNatureEntity(projectId));

            var optionalEditingContext = this.editingContextSearchService.findById(projectId.toString());
            if (optionalEditingContext.isPresent()) {
                var editingContext = optionalEditingContext.get();
                this.taskStudioTemplatesInitializer.handle(TaskStudioTemplatesInitializer.TASK_STUDIO_TEMPLATE_ID, editingContext).orElse(null);

                this.editingContextPersistenceService.persist(editingContext);
            }
        }
    }

    private ProjectNatureEntity createProjectNatureEntity(UUID projectId) {
        ProjectNatureEntity projectNatureEntity = new ProjectNatureEntity();
        projectNatureEntity.setName("siriusComponents://nature?kind=studio");
        var projectEntity = new ProjectEntity();
        projectEntity.setId(projectId);
        projectNatureEntity.setProject(projectEntity);
        return projectNatureEntity;
    }
}
