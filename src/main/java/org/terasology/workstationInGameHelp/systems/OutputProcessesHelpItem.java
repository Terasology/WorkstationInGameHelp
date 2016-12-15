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

import com.google.common.collect.Lists;
import org.terasology.assets.ResourceUrn;
import org.terasology.inGameHelp.components.HelpItem;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.FlowParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.flow.TextFlowRenderable;
import org.terasology.workstation.process.WorkstationProcess;
import org.terasology.workstation.system.WorkstationRegistry;
import org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache;

import java.util.List;

/**
 * Contains help item information for output related workstation processes for resourceUrn.
 */
public class OutputProcessesHelpItem implements HelpItem {
    /** The resource urn to get the help information from. */
    ResourceUrn resourceUrn;

    /** Reference to the {@link org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache}. Used for getting workstation processes associated with resourceUrn. */
    WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache;

    /** Reference to the workstation registry. */
    WorkstationRegistry workstationRegistry;

    /**
     * Constructor that sets the instance variables with the given parameters.
     *
     * @param resourceUrn the resource urn to set.
     * @param workstationProcessRelatedAssetCache the workstation process related asset cache to set.
     * @param workstationRegistry the workstation registry to set.
     */
    public OutputProcessesHelpItem(
            ResourceUrn resourceUrn,
            WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache,
            WorkstationRegistry workstationRegistry) {
        this.resourceUrn = resourceUrn;
        this.workstationProcessRelatedAssetCache = workstationProcessRelatedAssetCache;
        this.workstationRegistry = workstationRegistry;
    }

    /**
     * Gets the title of this help item.
     *
     * @return the title of this help item.
     */
    @Override
    public String getTitle() {
        return "Created By";
    }

    /**
     * Gets the category of this help item.
     *
     * @return the category of this help item.
     */
    @Override
    public String getCategory() {
        return "";
    }

    /**
     * Gets data for output workstation processes that are associated with resourceUrn.
     *
     * @return a sorted iterable of paragraph data.
     */
    @Override
    public Iterable<ParagraphData> getParagraphs() {
        List<ParagraphData> result = Lists.newLinkedList();
        List<WorkstationProcess> relatedWorkstationProcesses = Lists.newLinkedList(workstationProcessRelatedAssetCache.getOutputRelatedWorkstationProcesses(resourceUrn));
        relatedWorkstationProcesses.sort((x, y) -> x.getProcessType().compareTo(y.getProcessType()));
        String lastSeenProcessType = null;
        for (WorkstationProcess workstationProcess : relatedWorkstationProcesses) {
            if (!workstationProcess.getProcessType().equals(lastSeenProcessType)) {
                lastSeenProcessType = workstationProcess.getProcessType();

                // add in a title for this process
                FlowParagraphData titleParagraphData = new FlowParagraphData(null);
                titleParagraphData.append(new TextFlowRenderable(workstationProcess.getProcessTypeName(), null, null));
                result.add(titleParagraphData);
            }

            result.addAll(InputProcessesHelpItem.getWorkStationProcessParagraphData(workstationProcess));
        }

        return result;
    }


    /**
     * Compares this to another object to see if they are equal.
     *
     * @param o the object to compare to.
     * @return true if the object is also an output processes help item and has the same resource urn, false if otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutputProcessesHelpItem)) return false;

        OutputProcessesHelpItem that = (OutputProcessesHelpItem) o;

        if (!resourceUrn.equals(that.resourceUrn)) return false;

        return true;
    }

    /**
     * Gets the hashcode from resourceUrn.
     * 
     * @return the hashcode of this.
     */
    @Override
    public int hashCode() {
        return resourceUrn.hashCode();
    }
}
