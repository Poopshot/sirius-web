/*******************************************************************************
 * Copyright (c) 2022, 2023 Obeo.
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
package org.eclipse.sirius.components.diagrams.components;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.sirius.components.diagrams.description.DiagramDescription;
import org.eclipse.sirius.components.diagrams.description.NodeDescription;

/**
 * Find the requested node descriptions.
 *
 * @author sbegaudeau
 */
public class NodeDescriptionRequestor implements INodeDescriptionRequestor {

    private final Map<String, NodeDescription> id2nodeDescription = new HashMap<>();

    public NodeDescriptionRequestor(List<DiagramDescription> diagramDescriptions) {
        diagramDescriptions.stream().flatMap(description -> description.getNodeDescriptions().stream()).forEach(this::cache);
    }

    private void cache(NodeDescription nodeDescription) {
        this.id2nodeDescription.put(nodeDescription.getId(), nodeDescription);
        nodeDescription.getBorderNodeDescriptions().forEach(this::cache);
        nodeDescription.getChildNodeDescriptions().forEach(this::cache);
    }

    @Override
    public Optional<NodeDescription> findById(String nodeDescriptionId) {
        return Optional.ofNullable(this.id2nodeDescription.get(nodeDescriptionId));
    }

}
