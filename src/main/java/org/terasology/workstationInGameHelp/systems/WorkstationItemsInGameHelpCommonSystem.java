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
package org.terasology.workstationInGameHelp.systems;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.terasology.utilities.Assets;
import org.terasology.assets.ResourceUrn;
import org.terasology.entitySystem.prefab.Prefab;
import org.terasology.entitySystem.systems.BaseComponentSystem;
import org.terasology.entitySystem.systems.RegisterSystem;
import org.terasology.inGameHelp.ItemsCategoryInGameHelpRegistry;
import org.terasology.registry.In;
import org.terasology.registry.Share;
import org.terasology.workstation.process.DescribeProcess;
import org.terasology.workstation.process.ProcessPartDescription;
import org.terasology.workstation.process.WorkstationProcess;
import org.terasology.workstation.system.WorkstationRegistry;
import org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache;
import org.terasology.workstationInGameHelp.components.ParticipateInItemCategoryInGameHelpComponent;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * System that handles the resource urns and workstation processes of the prefabs that have the {@link org.terasology.workstationInGameHelp.components.ParticipateInItemCategoryInGameHelpComponent}.
 */
@RegisterSystem
@Share(WorkstationProcessRelatedAssetCache.class)
public class WorkstationItemsInGameHelpCommonSystem extends BaseComponentSystem implements WorkstationProcessRelatedAssetCache {
    /** Reference to the {@link org.terasology.inGameHelp.ItemsCategoryInGameHelpRegistry} that is used to add workstation help items. */
    @In
    ItemsCategoryInGameHelpRegistry itemsCategoryInGameHelpRegistry;
    
    /** Reference to the workstation registry that is used to get workstation processes. */
    @In
    WorkstationRegistry workstationRegistry;

    /** Maps resource urns to input related workstation processes. */
    Multimap<ResourceUrn, WorkstationProcess> inputAssetsToWorkstationProcesses = HashMultimap.create();

    /** Maps resource urns to output related workstation processes. */
    Multimap<ResourceUrn, WorkstationProcess> outputAssetsToWorkstationProcesses = HashMultimap.create();

    /**
     * Fills the workstation registry with input and output processes help items.
     * Fills the input and output workstation processes maps with the cooresponding resourceUrn and workstation process.
     */
    @Override
    public void postBegin() {
        super.postBegin();

        Set<String> processTypesWithAutoRegistration = Sets.newHashSet();

        //gets prefabs that have the {@link org.terasology.workstationInGameHelp.components.ParticipateInItemCategoryInGameHelpComponent}.
        Assets.list(Prefab.class).stream()
                .map(x -> Assets.get(x, Prefab.class).get())
                .filter(x -> x.hasComponent(ParticipateInItemCategoryInGameHelpComponent.class))
                .forEach(x -> processTypesWithAutoRegistration.add(x.getName()));

        Collection<WorkstationProcess> processes = workstationRegistry.getWorkstationProcesses(processTypesWithAutoRegistration);

        for (WorkstationProcess process : workstationRegistry.getWorkstationProcesses(processTypesWithAutoRegistration)) {
            if (process instanceof DescribeProcess) {
                DescribeProcess processRelatedAssets = (DescribeProcess) process;
                //adds input related workstation processes and resource urns to inputAssetsToWorkstationProcesses.
                //creates and adds input processes help items to the registry.
                for (ProcessPartDescription processPartDescription : processRelatedAssets.getInputDescriptions()) {
                    if (processPartDescription.getResourceUrn() != null) {
                        inputAssetsToWorkstationProcesses.put(processPartDescription.getResourceUrn(), process);
                        Optional<Prefab> assetPrefab = Assets.get(processPartDescription.getResourceUrn(), Prefab.class);
                        if (assetPrefab.isPresent()) {
                            itemsCategoryInGameHelpRegistry.addKnownPrefab(assetPrefab.get(), new InputProcessesHelpItem(processPartDescription.getResourceUrn(), this, workstationRegistry));
                        }
                    }
                }
                //adds output related workstation processes and resource urns to inputAssetsToWorkstationProcesses.
                //creates and adds output processes help items to the registry.

                for (ProcessPartDescription processPartDescription : processRelatedAssets.getOutputDescriptions()) {
                    if (processPartDescription.getResourceUrn() != null) {
                        outputAssetsToWorkstationProcesses.put(processPartDescription.getResourceUrn(), process);
                        Optional<Prefab> assetPrefab = Assets.get(processPartDescription.getResourceUrn(), Prefab.class);
                        if (assetPrefab.isPresent()) {
                            itemsCategoryInGameHelpRegistry.addKnownPrefab(assetPrefab.get(), new OutputProcessesHelpItem(processPartDescription.getResourceUrn(), this, workstationRegistry));
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets all input related workstation processes associated with resourceUrn.
     *
     * @param resourceUrn the resource urn that the workstation processes are associated with.
     * @return a collection of {@link org.terasology.workstation.process.WorkstationProcess}s.
     */
    @Override
    public Collection<WorkstationProcess> getInputRelatedWorkstationProcesses(ResourceUrn resourceUrn) {
        return inputAssetsToWorkstationProcesses.get(resourceUrn);
    }

    /**
     * Gets all output related workstation processes associated with resourceUrn.
     *
     * @param resourceUrn the resource urn that the workstation processes are associated with.
     * @return a collection of {@link org.terasology.workstation.process.WorkstationProcess}s.
     */
    @Override
    public Collection<WorkstationProcess> getOutputRelatedWorkstationProcesses(ResourceUrn resourceUrn) {
        return outputAssetsToWorkstationProcesses.get(resourceUrn);
    }
}
