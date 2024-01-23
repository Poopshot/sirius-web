/*******************************************************************************
 * Copyright (c) 2023, 2024 Obeo.
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
import { ServerContext, ServerContextValue, getCSSColor, useSelection } from '@eclipse-sirius/sirius-components-core';
import {
  GQLWidget,
  PreviewWidgetComponent,
  PreviewWidgetProps,
  getTextDecorationLineValue,
} from '@eclipse-sirius/sirius-components-forms';
import { GQLReferenceWidget } from '@eclipse-sirius/sirius-components-widget-reference';
import IconButton from '@material-ui/core/IconButton';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Typography from '@material-ui/core/Typography';
import { Theme, makeStyles } from '@material-ui/core/styles';
import DeleteIcon from '@material-ui/icons/Delete';
import HelpOutlineOutlined from '@material-ui/icons/HelpOutlineOutlined';
import { useContext, useEffect, useRef, useState } from 'react';
import { GQLReferenceWidgetStyle } from './ReferenceWidgetFragment.types';

const isReferenceWidget = (widget: GQLWidget): widget is GQLReferenceWidget => widget.__typename === 'ReferenceWidget';

const useStyles = makeStyles<Theme, GQLReferenceWidgetStyle>((theme) => ({
  style: {
    color: ({ color }) => (color ? getCSSColor(color, theme) : undefined),
    fontSize: ({ fontSize }) => (fontSize ? fontSize : undefined),
    fontStyle: ({ italic }) => (italic ? 'italic' : 'unset'),
    fontWeight: ({ bold }) => (bold ? 'bold' : 'unset'),
    textDecorationLine: ({ underline, strikeThrough }) =>
      getTextDecorationLineValue(underline ?? null, strikeThrough ?? null),
  },
  selected: {
    color: theme.palette.primary.main,
  },
  propertySectionLabel: {
    display: 'flex',
    flexDirection: 'row',
    alignItems: 'center',
  },
}));

export const ReferencePreview: PreviewWidgetComponent = ({ widget }: PreviewWidgetProps) => {
  let style: GQLReferenceWidgetStyle | null = null;
  if (isReferenceWidget(widget)) {
    const referenceWidget: GQLReferenceWidget = widget;
    style = referenceWidget.style;
  }
  const props: GQLReferenceWidgetStyle = {
    color: style?.color ?? null,
    fontSize: style?.fontSize ?? null,
    italic: style?.italic ?? null,
    bold: style?.bold ?? null,
    underline: style?.underline ?? null,
    strikeThrough: style?.strikeThrough ?? null,
  };
  const classes = useStyles(props);
  const { httpOrigin } = useContext<ServerContextValue>(ServerContext);

  const [selected, setSelected] = useState<boolean>(false);
  const { selection } = useSelection();
  const ref = useRef<HTMLInputElement | null>(null);

  useEffect(() => {
    if (ref.current && selection.entries.find((entry) => entry.id === widget.id)) {
      ref.current.focus();
      setSelected(true);
    } else {
      setSelected(false);
    }
  }, [selection, widget]);

  if (!isReferenceWidget(widget)) {
    return null;
  }

  return (
    <div>
      <div className={classes.propertySectionLabel}>
        <Typography variant="subtitle2" className={selected ? classes.selected : ''}>
          {widget.label}
        </Typography>
        {widget.hasHelpText ? <HelpOutlineOutlined color="secondary" style={{ marginLeft: 8, fontSize: 16 }} /> : null}
      </div>
      <List dense>
        <ListItem>
          <ListItemIcon>
            <img width="16" height="16" alt={''} src={httpOrigin + '/api/images/icons/full/obj16/Entity.svg'} />
          </ListItemIcon>
          <ListItemText data-testid={`reference-value`} classes={{ primary: classes.style }}>
            Referenced Value
          </ListItemText>
          <IconButton aria-label="clearReference" onClick={() => {}} data-testid={`clear-reference-${widget.id}`}>
            <DeleteIcon />
          </IconButton>
        </ListItem>
      </List>
    </div>
  );
};
