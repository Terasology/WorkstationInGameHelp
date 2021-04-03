/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.workstationInGameHelp;

import org.terasology.gestalt.assets.ResourceUrn;
import org.terasology.workstation.process.WorkstationProcess;

import java.util.Collection;

/**
 * Cache of all workstation processes. Used for getting workstation processes associated with a resource urn.
 */
public interface WorkstationProcessRelatedAssetCache {
    /**
     * Gets all input workstation processes related to the resourceUrn.
     *
     * @param resourceUrn the resource urn that the workstation processes are associated with.
     * @return a collection of {@link org.terasology.workstation.process.WorkstationProcess}s.
     */
    Collection<WorkstationProcess> getInputRelatedWorkstationProcesses(ResourceUrn resourceUrn);

    /**
     * Gets all output workstation processes related to resourceUrn.
     *
     * @param resourceUrn the resource urn that the workstation processes are associated with.
     * @return a collection of {@link org.terasology.workstation.process.WorkstationProcess}s.
     */
    Collection<WorkstationProcess> getOutputRelatedWorkstationProcesses(ResourceUrn resourceUrn);
}
