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
package org.eclipse.sirius.web.sample.task.configuration.view;

import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.components.gantt.TaskDetail;
import org.eclipse.sirius.components.gantt.TaskType;
import org.eclipse.sirius.ecore.extender.business.internal.accessor.ecore.EcoreIntrinsicExtender;


/**
 * Java Service for the task related views.
 *
 * @author lfasani
 */
@SuppressWarnings("checkstyle:MultipleStringLiterals")
public class TaskJavaService {

    private final EcoreIntrinsicExtender ecoreIntrinsicExtender = new EcoreIntrinsicExtender();

    public TaskJavaService() {
    }

    public TaskDetail getTaskDetail(EObject task) {

        String name = Optional.ofNullable((String) this.ecoreIntrinsicExtender.eGet(task, "name")).orElse("");
        String description = Optional.ofNullable((String) this.ecoreIntrinsicExtender.eGet(task, "description")).orElse("");
        String startDate = Optional.ofNullable((String) this.ecoreIntrinsicExtender.eGet(task, "startDate")).orElse("");
        String endDate = Optional.ofNullable((String) this.ecoreIntrinsicExtender.eGet(task, "endDate")).orElse("");
        Integer progress = Optional.ofNullable((Integer) this.ecoreIntrinsicExtender.eGet(task, "progress")).orElse(0);
        Integer taskType = Optional.ofNullable((Integer) this.ecoreIntrinsicExtender.eGet(task, "type")).orElse(0);

        return new TaskDetail(name, description, TaskType.values()[taskType], startDate, endDate, progress);
    }
}
