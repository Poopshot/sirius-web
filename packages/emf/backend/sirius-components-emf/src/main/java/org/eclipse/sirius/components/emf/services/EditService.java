/*******************************************************************************
 * Copyright (c) 2019, 2023 Obeo.
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
package org.eclipse.sirius.components.emf.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.CreateChildCommand;
import org.eclipse.emf.edit.command.CreateChildCommand.Helper;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.sirius.components.core.api.ChildCreationDescription;
import org.eclipse.sirius.components.core.api.IEditService;
import org.eclipse.sirius.components.core.api.IEditingContext;
import org.eclipse.sirius.components.emf.services.api.IEMFKindService;
import org.eclipse.sirius.emfjson.resource.JsonResourceImpl;
import org.springframework.stereotype.Service;

/**
 * Used to provide support for the edition of EMF models.
 *
 * @author sbegaudeau
 * @author lfasani
 */
@Service
public class EditService implements IEditService {

    private final IEMFKindService emfKindService;

    private final ComposedAdapterFactory composedAdapterFactory;

    private final ISuggestedRootObjectTypesProvider suggestedRootObjectTypesProvider;

    public EditService(IEMFKindService emfKindService, ComposedAdapterFactory composedAdapterFactory, ISuggestedRootObjectTypesProvider suggestedRootObjectsProvider) {
        this.emfKindService = Objects.requireNonNull(emfKindService);
        this.composedAdapterFactory = Objects.requireNonNull(composedAdapterFactory);
        this.suggestedRootObjectTypesProvider = Objects.requireNonNull(suggestedRootObjectsProvider);
    }

    private Optional<EClass> getEClass(EPackage.Registry ePackageRegistry, String kind) {
        String ePackageName = this.emfKindService.getEPackageName(kind);
        String eClassName = this.emfKindService.getEClassName(kind);

        return this.emfKindService.findEPackage(ePackageRegistry, ePackageName)
                .map(ePackage -> ePackage.getEClassifier(eClassName))
                .filter(EClass.class::isInstance)
                .map(EClass.class::cast);
    }

    private Optional<EPackage.Registry> getPackageRegistry(IEditingContext editingContext) {
        return Optional.of(editingContext)
                .filter(EditingContext.class::isInstance)
                .map(EditingContext.class::cast)
                .map(EditingContext::getDomain)
                .map(EditingDomain::getResourceSet)
                .map(ResourceSet::getPackageRegistry);
    }

    @Override
    public List<ChildCreationDescription> getChildCreationDescriptions(IEditingContext editingContext, String kind, String referenceKind) {
        List<ChildCreationDescription> childCreationDescriptions = new ArrayList<>();

        this.getPackageRegistry(editingContext).ifPresent(ePackageRegistry -> {

            AdapterFactoryEditingDomain editingDomain = new AdapterFactoryEditingDomain(this.composedAdapterFactory, new BasicCommandStack());
            ResourceSet resourceSet = editingDomain.getResourceSet();
            resourceSet.setPackageRegistry(ePackageRegistry);
            Resource resource = new JsonResourceImpl(URI.createURI("inmemory"), Map.of());
            resourceSet.getResources().add(resource);

            var optionalEClass = this.getEClass(ePackageRegistry, kind)
                    .filter(eClass -> !eClass.isAbstract() && !eClass.isInterface());

            Optional<EClass> optionalEClassReference;
            if (referenceKind != null) {
                optionalEClassReference = this.getEClass(ePackageRegistry, referenceKind);
            } else {
                optionalEClassReference = Optional.empty();
            }


            if (optionalEClass.isPresent()) {
                EClass eClass = optionalEClass.get();
                EObject eObject = EcoreUtil.create(eClass);
                resource.getContents().add(eObject);

                Collection<?> newChildDescriptors = editingDomain.getNewChildDescriptors(eObject, null);

                List<CommandParameter> commandParameters = newChildDescriptors.stream()
                        .filter(CommandParameter.class::isInstance)
                        .map(CommandParameter.class::cast)
                        .filter(commandParameter -> optionalEClassReference.map(eClassReference -> eClassReference.isInstance(commandParameter.getValue()))
                                .orElse(true))
                        .toList();

                Adapter adapter = editingDomain.getAdapterFactory().adapt(eObject, IEditingDomainItemProvider.class);

                if (adapter instanceof IEditingDomainItemProvider editingDomainItemProvider) {
                    if (editingDomainItemProvider instanceof Helper helper) {
                        for (CommandParameter commandParameter : commandParameters) {
                            String text = helper.getCreateChildText(eObject, commandParameter.getFeature(), commandParameter.getValue(), null);
                            ChildCreationDescription childCreationDescription = new ChildCreationDescription(text, text);
                            childCreationDescriptions.add(childCreationDescription);
                        }
                    }
                }
            }
        });

        return childCreationDescriptions;
    }

    @Override
    public Optional<Object> createChild(IEditingContext editingContext, Object object, String childCreationDescriptionId) {
        // @formatter:off
        var optionalEditingDomain = Optional.of(editingContext)
                .filter(EditingContext.class::isInstance)
                .map(EditingContext.class::cast)
                .map(EditingContext::getDomain);

        Optional<EObject> optionalEObject = Optional.of(object)
                .filter(EObject.class::isInstance)
                .map(EObject.class::cast);
        // @formatter:on

        if (optionalEditingDomain.isPresent() && optionalEObject.isPresent()) {
            AdapterFactoryEditingDomain editingDomain = optionalEditingDomain.get();
            EObject eObject = optionalEObject.get();

            Collection<?> newChildDescriptors = editingDomain.getNewChildDescriptors(eObject, null);

            // @formatter:off
            List<CommandParameter> commandParameters = newChildDescriptors.stream()
                    .filter(CommandParameter.class::isInstance)
                    .map(CommandParameter.class::cast)
                    .toList();
            // @formatter:on

            Adapter adapter = editingDomain.getAdapterFactory().adapt(eObject, IEditingDomainItemProvider.class);
            if (adapter instanceof IEditingDomainItemProvider editingDomainItemProvider) {
                if (editingDomainItemProvider instanceof Helper helper) {
                    for (CommandParameter commandParameter : commandParameters) {
                        String text = helper.getCreateChildText(eObject, commandParameter.getFeature(), commandParameter.getValue(), null);

                        if (childCreationDescriptionId.equals(text)) {
                            return this.createObject(editingDomain, eObject, commandParameter);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Object> createObject(AdapterFactoryEditingDomain editingDomain, EObject eObject, CommandParameter commandParameter) {
        Optional<Object> objectOptional = Optional.empty();
        Command createChildCommand = CreateChildCommand.create(editingDomain, eObject, commandParameter, Collections.singletonList(eObject));
        editingDomain.getCommandStack().execute(createChildCommand);
        Collection<?> result = createChildCommand.getResult();
        if (result.size() == 1) {
            Object child = result.iterator().next();
            if (child instanceof EObject && EcoreUtil.isAncestor(eObject, (EObject) child)) {
                objectOptional = Optional.of(child);
            }
        }
        return objectOptional;
    }

    @Override
    public void delete(Object object) {
        // @formatter:off
        Optional<EObject> optionalEObject = Optional.of(object)
                .filter(EObject.class::isInstance)
                .map(EObject.class::cast);
        // @formatter:on

        optionalEObject.ifPresent(eObject -> EcoreUtil.deleteAll(Collections.singleton(eObject), true));
    }

    @Override
    public List<ChildCreationDescription> getRootCreationDescriptions(IEditingContext editingContext, String domainId, boolean suggested, String referenceKind) {
        List<ChildCreationDescription> rootObjectCreationDescription = new ArrayList<>();

        this.getPackageRegistry(editingContext).ifPresent(ePackageRegistry -> {

            EPackage ePackage = ePackageRegistry.getEPackage(domainId);
            if (ePackage != null) {
                List<EClass> classes;
                if (suggested) {
                    classes = this.suggestedRootObjectTypesProvider.getSuggestedRootObjectTypes(ePackage);
                    if (classes.isEmpty()) {
                        classes = this.getConcreteClasses(ePackage);
                    }
                } else {
                    classes = this.getConcreteClasses(ePackage);
                }
                for (EClass suggestedClass : classes) {
                    if (referenceKind == null || this.getEClass(ePackageRegistry, referenceKind).map(eClassReference -> eClassReference.isSuperTypeOf(suggestedClass))
                            .orElse(true)) {
                        rootObjectCreationDescription.add(new ChildCreationDescription(suggestedClass.getName(), suggestedClass.getName()));
                    }
                }
            }
        });
        return rootObjectCreationDescription;
    }

    private List<EClass> getConcreteClasses(EPackage ePackage) {
        // @formatter:off
        return ePackage.getEClassifiers().stream()
                .filter(EClass.class::isInstance)
                .map(EClass.class::cast)
                .filter(eClass -> !eClass.isAbstract() && !eClass.isInterface())
                .toList();
        // @formatter:on
    }

    @Override
    public Optional<Object> createRootObject(IEditingContext editingContext, UUID documentId, String domainId, String rootObjectCreationDescriptionId) {
        Optional<Object> createdObjectOptional = Optional.empty();

        var optionalEClass = this.getMatchingEClass(editingContext, domainId, rootObjectCreationDescriptionId);

        // @formatter:off
        var optionalEditingDomain = Optional.of(editingContext)
                .filter(EditingContext.class::isInstance)
                .map(EditingContext.class::cast)
                .map(EditingContext::getDomain);
        // @formatter:on

        if (optionalEClass.isPresent() && optionalEditingDomain.isPresent()) {
            EClass eClass = optionalEClass.get();
            AdapterFactoryEditingDomain editingDomain = optionalEditingDomain.get();

            // @formatter:off
            var optionalResource = editingDomain.getResourceSet().getResources().stream()
                    .filter(resource -> documentId.toString().equals(resource.getURI().path().substring(1)))
                    .findFirst();
            // @formatter:on

            if (optionalResource.isPresent()) {
                EObject eObject = EcoreUtil.create(eClass);

                var resource = optionalResource.get();
                AddCommand command = new AddCommand(editingDomain, resource.getContents(), eObject);
                editingDomain.getCommandStack().execute(command);

                createdObjectOptional = Optional.of(eObject);
            }
        }
        return createdObjectOptional;
    }

    private Optional<EClass> getMatchingEClass(IEditingContext editingContext, String domainId, String rootObjectCreationDescriptionId) {
        // @formatter:off
        return Optional.of(editingContext)
                .filter(EditingContext.class::isInstance)
                .map(EditingContext.class::cast)
                .map(EditingContext::getDomain)
                .map(EditingDomain::getResourceSet)
                .map(ResourceSet::getPackageRegistry)
                .map(packageRegistry -> packageRegistry.getEPackage(domainId))
                .map(ePackage -> ePackage.getEClassifier(rootObjectCreationDescriptionId))
                .filter(EClass.class::isInstance)
                .map(EClass.class::cast)
                .filter(eClass -> !eClass.isAbstract() && !eClass.isInterface());
        // @formatter:on
    }

    @Override
    public void editLabel(Object object, String labelField, String newValue) {
        if (object instanceof EObject) {
            EClass eClass = ((EObject) object).eClass();
            EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(labelField);
            // @formatter:off
            if (eStructuralFeature instanceof EAttribute
                    && !eStructuralFeature.isMany()
                    && eStructuralFeature.isChangeable()
                    && eStructuralFeature.getEType().getInstanceClass() == String.class) {
                ((EObject) object).eSet(eStructuralFeature, newValue);
            }
            // @formatter:on
        }
    }
}
