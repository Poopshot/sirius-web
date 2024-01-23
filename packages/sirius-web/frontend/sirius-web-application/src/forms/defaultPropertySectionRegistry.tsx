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

import {
  GQLWidget,
  PropertySectionComponent,
  PropertySectionComponentProps,
  PropertySectionComponentRegistry,
  WidgetContribution,
} from '@eclipse-sirius/sirius-components-forms';
import {
  GQLReferenceWidget,
  ReferenceIcon,
  ReferencePreview,
  ReferencePropertySection,
} from '@eclipse-sirius/sirius-components-widget-reference';
import { GQLSlider, SliderPreview, SliderPropertySection } from '@eclipse-sirius/sirius-components-widget-slider';
import LinearScaleOutlinedIcon from '@material-ui/icons/LinearScaleOutlined';

const isSlider = (widget: GQLWidget): widget is GQLSlider => widget.__typename === 'Slider';
const isReferenceWidget = (widget: GQLWidget): widget is GQLReferenceWidget => widget.__typename === 'ReferenceWidget';

export const propertySectionsRegistry: PropertySectionComponentRegistry = {
  getComponent: (widget: GQLWidget): PropertySectionComponent<GQLWidget> | null => {
    let propertySectionComponent: PropertySectionComponent<GQLWidget> | null = null;

    if (isSlider(widget)) {
      propertySectionComponent = withPropertySectionWrapper(isSlider, SliderPropertySection);
    } else if (isReferenceWidget(widget)) {
      propertySectionComponent = withPropertySectionWrapper(isReferenceWidget, ReferencePropertySection);
    }

    return propertySectionComponent;
  },

  getPreviewComponent: (widget: GQLWidget) => {
    if (widget.__typename === 'Slider') {
      return SliderPreview;
    } else if (widget.__typename === 'ReferenceWidget') {
      return ReferencePreview;
    }
    return null;
  },

  getWidgetContributions: () => {
    const sliderWidgetContribution: WidgetContribution = {
      name: 'Slider',
      fields: `label iconURL minValue maxValue currentValue`,
      icon: <LinearScaleOutlinedIcon />,
    };
    const referenceWidget: WidgetContribution = {
      name: 'ReferenceWidget',
      fields: `label
               iconURL
               ownerId
               descriptionId
               reference {
                 ownerKind
                 referenceKind
                 containment
                 manyValued
               }
               referenceValues {
                 id
                 label
                 kind
                 iconURL
               }
               style {
                 color
                 fontSize
                 italic
                 bold
                 underline
                 strikeThrough
               }`,
      icon: <ReferenceIcon />,
    };
    return [sliderWidgetContribution, referenceWidget];
  },
};

function withPropertySectionWrapper<W extends GQLWidget>(
  predicate: (widget: W) => boolean,
  Component: PropertySectionComponent<W>
): PropertySectionComponent<GQLWidget> {
  const wrapper: PropertySectionComponent<GQLWidget> = ({
    widget,
    ...props
  }: PropertySectionComponentProps<GQLWidget>) => {
    if (predicate(widget as W)) {
      const componentProps: PropertySectionComponentProps<W> = {
        ...props,
        widget: widget as W,
      };
      return <Component {...componentProps} />;
    }
    return null;
  };
  return wrapper;
}
