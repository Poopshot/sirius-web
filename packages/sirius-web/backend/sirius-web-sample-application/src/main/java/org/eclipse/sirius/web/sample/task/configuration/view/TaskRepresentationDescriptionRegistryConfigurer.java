/*******************************************************************************
 * Copyright (c) 2023 Obeo
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.sample.task.configuration.view;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter;
import org.eclipse.sirius.components.core.configuration.IRepresentationDescriptionRegistry;
import org.eclipse.sirius.components.core.configuration.IRepresentationDescriptionRegistryConfigurer;
import org.eclipse.sirius.components.representations.IRepresentationDescription;
import org.eclipse.sirius.components.view.View;
import org.eclipse.sirius.components.view.ViewFactory;
import org.eclipse.sirius.components.view.emf.IViewConverter;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.eclipse.sirius.web.services.api.representations.IInMemoryViewRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * Registers representation description related to task model.
 *
 * @author lfasani
 */
@Configuration
public class TaskRepresentationDescriptionRegistryConfigurer implements IRepresentationDescriptionRegistryConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepresentationDescriptionRegistryConfigurer.class);

    private final IViewConverter viewConverter;

    private final EPackage.Registry ePackagesRegistry;

    private final IInMemoryViewRegistry inMemoryViewRegistry;

    public TaskRepresentationDescriptionRegistryConfigurer(EPackage.Registry ePackagesRegistry, IViewConverter viewConverter, IInMemoryViewRegistry inMemoryViewRegistry) {
        this.viewConverter = Objects.requireNonNull(viewConverter);
        this.ePackagesRegistry = Objects.requireNonNull(ePackagesRegistry);
        this.inMemoryViewRegistry = Objects.requireNonNull(inMemoryViewRegistry);
    }

    @Override
    public void addRepresentationDescriptions(IRepresentationDescriptionRegistry registry) {

        View newView = this.createView();

        this.register(registry, newView);
    }

    private View createView() {
        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.eAdapters().add(new ECrossReferenceAdapter());

        View view = this.createView(resourceSet, ViewGanttDescriptionBuilder.GANTT_REP_DESC_NAME);
        new ViewGanttDescriptionBuilder().createRepresentationDescription(view);

        return view;
    }

    private View createView(ResourceSet resourceSet, String representationName) {
        View view = ViewFactory.eINSTANCE.createView();
        JsonResourceImpl impl = new JsonResourceImpl(URI.createURI("task-rep:///" + UUID.nameUUIDFromBytes(representationName.getBytes())), this.ePackagesRegistry);
        resourceSet.getResources().add(impl);
        impl.getContents().add(view);

        return view;
    }

    private void register(IRepresentationDescriptionRegistry registry, View view) {
        // @formatter:off
        List<EPackage> staticEPackages = this.ePackagesRegistry.values().stream()
                .filter(EPackage.class::isInstance)
                .map(EPackage.class::cast)
                .collect(Collectors.toList());
        // @formatter:on

        List<IRepresentationDescription> representationDescriptions = this.viewConverter.convert(Collections.singletonList(view), staticEPackages);
        representationDescriptions.forEach(registry::add);

        this.inMemoryViewRegistry.register(view);
    }
}
