// Copyright 2020 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.workstationInGameHelp.systems;

import org.terasology.assets.ResourceUrn;
import org.terasology.inGameHelpAPI.components.HelpItem;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.workstation.process.WorkstationProcess;
import org.terasology.workstation.system.WorkstationRegistry;
import org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache;
import org.terasology.workstationInGameHelp.ui.WorkstationProcesses;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Contains help item information for input related workstation processes for resourceUrn.
 */
public class InputProcessesHelpItem implements HelpItem {
    /**
     * The resource urn to get the help information from.
     */
    ResourceUrn resourceUrn;

    /**
     * Reference to the {@link org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache}. Used for
     * getting workstation processes associated with resourceUrn.
     */
    WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache;

    /**
     * Reference to the workstation registry.
     */
    WorkstationRegistry workstationRegistry;

    /**
     * Constructor that sets the instance variables from the given parameters.
     *
     * @param resourceUrn the resource urn to set.
     * @param workstationProcessRelatedAssetCache the workstation process related asset cache to set.
     * @param workstationRegistry the workstation registry to set.
     */
    public InputProcessesHelpItem(
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
        return "Used to Create";
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
     * Gets data for input workstation processes that are associated with resourceUrn.
     *
     * @return a sorted iterable of paragraph data.
     */
    @Override
    public Iterable<ParagraphData> getParagraphs() {
        Collection<WorkstationProcess> relatedWorkstationProcesses =
                workstationProcessRelatedAssetCache.getInputRelatedWorkstationProcesses(resourceUrn);

        //TODO: group by `workstationProcess.getProcessType()` and add title for the process?
        /*
        FlowParagraphData titleParagraphData = new FlowParagraphData(null);
        titleParagraphData.append(new TextFlowRenderable(workstationProcess.getProcessTypeName(), null, null));
        result.add(titleParagraphData);
        */

        return relatedWorkstationProcesses.stream()
                .sorted(Comparator.comparing(WorkstationProcess::getProcessType))
                .map(WorkstationProcesses::getInputHelpParagraphs)
                .collect(Collectors.toList());
    }

    /**
     * Compares this to another object to see if it is equal.
     *
     * @param o the object to compare to.
     * @return true if the object is also an input processes help item and has the same resource urn, false if
     *         otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InputProcessesHelpItem)) {
            return false;
        }

        InputProcessesHelpItem that = (InputProcessesHelpItem) o;

        return resourceUrn.equals(that.resourceUrn);
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
