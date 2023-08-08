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
package org.eclipse.sirius.web.sample.task.domain;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.domain.Attribute;
import org.eclipse.sirius.components.domain.DataType;
import org.eclipse.sirius.components.domain.Domain;
import org.eclipse.sirius.components.domain.DomainFactory;
import org.eclipse.sirius.components.domain.Entity;
import org.eclipse.sirius.components.domain.Relation;

/**
 * Used to create the Task domain.
 *
 * @author lfasani
 */
public class TaskDomainProvider {
    private Domain taskDomain;

    private Entity resourceEntity;
    private Entity companyEntity;

    private Entity taskEntity;

    private Entity teamEntity;

    private Entity personEntity;

    private Entity objectiveEntity;

    private Entity keyResultEntity;

    private Entity initiativeEntity;

    public List<EObject> getDomains() {
        this.taskDomain = DomainFactory.eINSTANCE.createDomain();
        this.taskDomain.setName("task");

        this.createEObjects();
        this.createFeatures();

        return List.of(this.taskDomain);
    }

    private void createEObjects() {
        this.resourceEntity = this.createEntity(this.taskDomain, "Resource", true, List.of());
        this.companyEntity = this.createEntity(this.taskDomain, "Company", false, List.of(this.resourceEntity));
        this.teamEntity = this.createEntity(this.taskDomain, "Team", false, List.of(this.resourceEntity));
        this.personEntity = this.createEntity(this.taskDomain, "Person", false, List.of(this.resourceEntity));

        this.taskEntity = this.createEntity(this.taskDomain, "Task", false, List.of());
        this.objectiveEntity = this.createEntity(this.taskDomain, "Objective", false, List.of(this.taskEntity));
        this.keyResultEntity = this.createEntity(this.taskDomain, "KeyResult", false, List.of(this.taskEntity));
        this.initiativeEntity = this.createEntity(this.taskDomain, "Initiative", false, List.of(this.taskEntity));

    }

    private void createFeatures() {
        this.resourceEntity.getAttributes().add(this.createAttribute("name", false, false, DataType.STRING));
        this.resourceEntity.getRelations().add(this.createRelation("ownedObjectives", true, true, true, this.objectiveEntity));

        this.companyEntity.getRelations().add(this.createRelation("ownedTeams", true, true, true, this.teamEntity));
        this.companyEntity.getRelations().add(this.createRelation("ownedPersons", true, true, true, this.personEntity));

        this.teamEntity.getRelations().add(this.createRelation("members", false, true, true, this.personEntity));

        this.personEntity.getAttributes().add(this.createAttribute("alias", false, true, DataType.STRING));
        this.personEntity.getAttributes().add(this.createAttribute("bio", false, true, DataType.STRING));
        this.personEntity.getAttributes().add(this.createAttribute("imageUrl", false, true, DataType.STRING));


        this.taskEntity.getAttributes().add(this.createAttribute("name", false, false, DataType.STRING));
        this.taskEntity.getAttributes().add(this.createAttribute("description", false, true, DataType.STRING));
        this.taskEntity.getAttributes().add(this.createAttribute("startDate", false, true, DataType.STRING));
        this.taskEntity.getAttributes().add(this.createAttribute("endDate", false, false, DataType.STRING));
        this.taskEntity.getAttributes().add(this.createAttribute("progress", false, false, DataType.NUMBER));
        this.taskEntity.getAttributes().add(this.createAttribute("type", false, false, DataType.NUMBER));
        this.taskEntity.getAttributes().add(this.createAttribute("tags", true, true, DataType.STRING));
        this.taskEntity.getAttributes().add(this.createAttribute("withDuration", false, false, DataType.BOOLEAN));
        this.taskEntity.getRelations().add(this.createRelation("subTasks", true, true, true, this.taskEntity));
        this.taskEntity.getRelations().add(this.createRelation("assignees", false, true, true, this.personEntity));
        this.taskEntity.getRelations().add(this.createRelation("dependencies", false, true, true, this.taskEntity));




        this.objectiveEntity.getRelations().add(this.createRelation("ownedKeyResults", true, true, true, this.keyResultEntity));
        this.keyResultEntity.getRelations().add(this.createRelation("ownedInitiatives", true, true, true, this.initiativeEntity));
    }

    private Entity createEntity(Domain domain, String name, boolean isAbstract, List<Entity> superTypes) {
        var entity = DomainFactory.eINSTANCE.createEntity();
        entity.setName(name);
        entity.setAbstract(isAbstract);
        entity.getSuperTypes().addAll(superTypes);
        domain.getTypes().add(entity);
        return entity;
    }

    private Attribute createAttribute(String name, boolean isMany, boolean isOptional, DataType type) {
        var attribute = DomainFactory.eINSTANCE.createAttribute();
        attribute.setName(name);
        attribute.setMany(isMany);
        attribute.setOptional(false);
        attribute.setType(type);
        return attribute;
    }

    private Relation createRelation(String name, boolean isContainment, boolean isMany, boolean isOptional, Entity type) {
        var relation = DomainFactory.eINSTANCE.createRelation();
        relation.setName(name);
        relation.setContainment(isContainment);
        relation.setMany(isMany);
        relation.setOptional(isOptional);
        relation.setTargetType(type);
        return relation;
    }
}
