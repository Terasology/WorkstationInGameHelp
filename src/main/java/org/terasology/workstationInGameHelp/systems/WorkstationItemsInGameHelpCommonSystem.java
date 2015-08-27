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
import org.terasology.asset.Assets;
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

@RegisterSystem
@Share(WorkstationProcessRelatedAssetCache.class)
public class WorkstationItemsInGameHelpCommonSystem extends BaseComponentSystem implements WorkstationProcessRelatedAssetCache {
    @In
    ItemsCategoryInGameHelpRegistry itemsCategoryInGameHelpRegistry;
    @In
    WorkstationRegistry workstationRegistry;

    Multimap<ResourceUrn, WorkstationProcess> inputAssetsToWorkstationProcesses = HashMultimap.create();
    Multimap<ResourceUrn, WorkstationProcess> outputAssetsToWorkstationProcesses = HashMultimap.create();

    @Override
    public void postBegin() {
        super.postBegin();

        Set<String> processTypesWithAutoRegistration = Sets.newHashSet();

        Assets.list(Prefab.class).stream()
                .map(x -> Assets.get(x, Prefab.class).get())
                .filter(x -> x.hasComponent(ParticipateInItemCategoryInGameHelpComponent.class))
                .forEach(x -> processTypesWithAutoRegistration.add(x.getName()));

        for (WorkstationProcess process : workstationRegistry.getWorkstationProcesses(processTypesWithAutoRegistration)) {
            if (process instanceof DescribeProcess) {
                DescribeProcess processRelatedAssets = (DescribeProcess) process;
                for (ProcessPartDescription processPartDescription : processRelatedAssets.getInputDescriptions()) {
                    if (processPartDescription.getResourceUrn() != null) {
                        inputAssetsToWorkstationProcesses.put(processPartDescription.getResourceUrn(), process);
                        Optional<Prefab> assetPrefab = Assets.get(processPartDescription.getResourceUrn(), Prefab.class);
                        if (assetPrefab.isPresent()) {
                            itemsCategoryInGameHelpRegistry.addKnownPrefab(assetPrefab.get(), new InputProcessesHelpItem(processPartDescription.getResourceUrn(), this, workstationRegistry));
                        }
                    }
                }
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

    @Override
    public Collection<WorkstationProcess> getInputRelatedWorkstationProcesses(ResourceUrn resourceUrn) {
        return inputAssetsToWorkstationProcesses.get(resourceUrn);
    }

    @Override
    public Collection<WorkstationProcess> getOutputRelatedWorkstationProcesses(ResourceUrn resourceUrn) {
        return outputAssetsToWorkstationProcesses.get(resourceUrn);
    }
}
