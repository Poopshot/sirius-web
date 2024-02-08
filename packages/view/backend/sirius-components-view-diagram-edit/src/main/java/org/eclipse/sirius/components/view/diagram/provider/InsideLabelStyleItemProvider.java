/*******************************************************************************
 * Copyright (c) 2024 Obeo.
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
package org.eclipse.sirius.components.view.diagram.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.sirius.components.view.diagram.DiagramPackage;
import org.eclipse.sirius.components.view.diagram.InsideLabelStyle;
import org.eclipse.sirius.components.view.provider.LabelStyleItemProvider;

/**
 * This is the item provider adapter for a {@link org.eclipse.sirius.components.view.diagram.InsideLabelStyle} object.
 * <!-- begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class InsideLabelStyleItemProvider extends LabelStyleItemProvider {

    /**
     * This constructs an instance from a factory and a notifier. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    public InsideLabelStyleItemProvider(AdapterFactory adapterFactory) {
        super(adapterFactory);
    }

    /**
     * This returns the property descriptors for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
        if (this.itemPropertyDescriptors == null) {
            super.getPropertyDescriptors(object);

            this.addLabelColorPropertyDescriptor(object);
            this.addShowIconPropertyDescriptor(object);
            this.addLabelIconPropertyDescriptor(object);
            this.addWithHeaderPropertyDescriptor(object);
            this.addDisplayHeaderSeparatorPropertyDescriptor(object);
        }
        return this.itemPropertyDescriptors;
    }

    /**
     * This adds a property descriptor for the Label Color feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected void addLabelColorPropertyDescriptor(Object object) {
        this.itemPropertyDescriptors.add(this.createItemPropertyDescriptor(((ComposeableAdapterFactory) this.adapterFactory).getRootAdapterFactory(), this.getResourceLocator(),
                this.getString("_UI_NodeLabelStyle_labelColor_feature"), this.getString("_UI_PropertyDescriptor_description", "_UI_NodeLabelStyle_labelColor_feature", "_UI_NodeLabelStyle_type"),
                DiagramPackage.Literals.NODE_LABEL_STYLE__LABEL_COLOR, true, false, true, null, null, null));
    }

    /**
     * This adds a property descriptor for the Show Icon feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected void addShowIconPropertyDescriptor(Object object) {
        this.itemPropertyDescriptors.add(this.createItemPropertyDescriptor(((ComposeableAdapterFactory) this.adapterFactory).getRootAdapterFactory(), this.getResourceLocator(),
                this.getString("_UI_NodeLabelStyle_showIcon_feature"), this.getString("_UI_PropertyDescriptor_description", "_UI_NodeLabelStyle_showIcon_feature", "_UI_NodeLabelStyle_type"),
                DiagramPackage.Literals.NODE_LABEL_STYLE__SHOW_ICON, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Label Icon feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected void addLabelIconPropertyDescriptor(Object object) {
        this.itemPropertyDescriptors.add(this.createItemPropertyDescriptor(((ComposeableAdapterFactory) this.adapterFactory).getRootAdapterFactory(), this.getResourceLocator(),
                this.getString("_UI_NodeLabelStyle_labelIcon_feature"), this.getString("_UI_PropertyDescriptor_description", "_UI_NodeLabelStyle_labelIcon_feature", "_UI_NodeLabelStyle_type"),
                DiagramPackage.Literals.NODE_LABEL_STYLE__LABEL_ICON, true, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the With Header feature. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    protected void addWithHeaderPropertyDescriptor(Object object) {
        this.itemPropertyDescriptors.add(this.createItemPropertyDescriptor(((ComposeableAdapterFactory) this.adapterFactory).getRootAdapterFactory(), this.getResourceLocator(),
                this.getString("_UI_InsideLabelStyle_withHeader_feature"), this.getString("_UI_PropertyDescriptor_description", "_UI_InsideLabelStyle_withHeader_feature", "_UI_InsideLabelStyle_type"),
                DiagramPackage.Literals.INSIDE_LABEL_STYLE__WITH_HEADER, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
    }

    /**
     * This adds a property descriptor for the Display Header Separator feature. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     *
     * @generated
     */
    protected void addDisplayHeaderSeparatorPropertyDescriptor(Object object) {
        this.itemPropertyDescriptors.add(this.createItemPropertyDescriptor(((ComposeableAdapterFactory) this.adapterFactory).getRootAdapterFactory(), this.getResourceLocator(),
                this.getString("_UI_InsideLabelStyle_displayHeaderSeparator_feature"),
                this.getString("_UI_PropertyDescriptor_description", "_UI_InsideLabelStyle_displayHeaderSeparator_feature", "_UI_InsideLabelStyle_type"),
                DiagramPackage.Literals.INSIDE_LABEL_STYLE__DISPLAY_HEADER_SEPARATOR, true, false, false, ItemPropertyDescriptor.BOOLEAN_VALUE_IMAGE, null, null));
    }

    /**
     * This returns InsideLabelStyle.gif. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     */
    @Override
    public Object getImage(Object object) {
        return this.overlayImage(object, this.getResourceLocator().getImage("full/obj16/Style.svg"));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    protected boolean shouldComposeCreationImage() {
        return true;
    }

    /**
     * This returns the label text for the adapted class. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    public String getText(Object object) {
        InsideLabelStyle insideLabelStyle = (InsideLabelStyle) object;
        return this.getString("_UI_InsideLabelStyle_type") + " " + insideLabelStyle.getFontSize();
    }

    /**
     * This handles model notifications by calling {@link #updateChildren} to update any cached children and by creating
     * a viewer notification, which it passes to {@link #fireNotifyChanged}. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @generated
     */
    @Override
    public void notifyChanged(Notification notification) {
        this.updateChildren(notification);

        switch (notification.getFeatureID(InsideLabelStyle.class)) {
            case DiagramPackage.INSIDE_LABEL_STYLE__SHOW_ICON:
            case DiagramPackage.INSIDE_LABEL_STYLE__LABEL_ICON:
            case DiagramPackage.INSIDE_LABEL_STYLE__WITH_HEADER:
            case DiagramPackage.INSIDE_LABEL_STYLE__DISPLAY_HEADER_SEPARATOR:
                this.fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
                return;
        }
        super.notifyChanged(notification);
    }

    /**
     * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children that can be created
     * under this object. <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated
     */
    @Override
    protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
        super.collectNewChildDescriptors(newChildDescriptors, object);
    }

}
