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
import { Selection } from '@eclipse-sirius/sirius-components-core';
import { Task } from '@SemanticBoard/gantt-task-react';

export interface GanttProps {
  tasks: Task[];
  setSelection: (selection: Selection) => void;
  onTaskChange: (Task) => void;
  onTaskDelete: (Task) => void;
  onExpandCollapse: (Task) => void;
  onSelect: (Task, isSelected: boolean) => void;
}
