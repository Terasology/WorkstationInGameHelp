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
import org.terasology.utilities.Assets;
import org.terasology.assets.ResourceUrn;
import org.terasology.inGameHelpAPI.components.HelpItem;
import org.terasology.inGameHelpAPI.ui.WidgetFlowRenderable;
import org.terasology.rendering.assets.texture.TextureRegion;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.FlowParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.flow.ImageFlowRenderable;
import org.terasology.rendering.nui.widgets.browser.data.basic.flow.TextFlowRenderable;
import org.terasology.workstation.process.DescribeProcess;
import org.terasology.workstation.process.ProcessPartDescription;
import org.terasology.workstation.process.WorkstationProcess;
import org.terasology.workstation.system.WorkstationRegistry;
import org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache;

import java.util.List;

/**
 * Contains help item information for input related workstation processes for resourceUrn.
 */
public class InputProcessesHelpItem implements HelpItem {
    /** The resource urn to get the help information from. */ 
    ResourceUrn resourceUrn;

    /** Reference to the {@link org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache}. Used for getting workstation processes associated with resourceUrn. */
    WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache;
    
    /** Reference to the workstation registry. */
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
        //gets and sorts a list of input related workstation processes associated with resourceUrn
        List<ParagraphData> result = Lists.newLinkedList();
        List<WorkstationProcess> relatedWorkstationProcesses = Lists.newLinkedList(workstationProcessRelatedAssetCache.getInputRelatedWorkstationProcesses(resourceUrn));
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

            result.addAll(getWorkStationProcessParagraphData(workstationProcess));
        }

        return result;
    }

    /**
     * Gets the description of the workstation process. 
     *
     * @param workstationProcess the workstation process to get the description from. 
     * @return a list of paragraph data for workstationProcess.
     */
    static List<ParagraphData> getWorkStationProcessParagraphData(WorkstationProcess workstationProcess) {
        List<ParagraphData> processParagraphData = Lists.newLinkedList();

        FlowParagraphData paragraphData = new FlowParagraphData(null);
        TextureRegion plusTexture = Assets.getTextureRegion("workstation:plus").get();
        ImageFlowRenderable plus = new ImageFlowRenderable(plusTexture, plusTexture.getWidth(), plusTexture.getWidth(), null);
        TextureRegion eqTexture = Assets.getTextureRegion("workstation:equals").get();
        ImageFlowRenderable eq = new ImageFlowRenderable(eqTexture, eqTexture.getWidth(), eqTexture.getWidth(), null);

        if (workstationProcess instanceof DescribeProcess) {
            DescribeProcess describeProcess = (DescribeProcess) workstationProcess;
            boolean isFirst = true;
            // add all input widgets
            for (ProcessPartDescription inputDesc : describeProcess.getInputDescriptions()) {
                if (!isFirst) {
                    paragraphData.append(plus);
                }
                isFirst = false;
                String hyperlink = inputDesc.getResourceUrn() != null ? inputDesc.getResourceUrn().toString() : null;
                paragraphData.append(new WidgetFlowRenderable(inputDesc.getWidget(), 48, 48, hyperlink));
            }

            // add the equals separator
            paragraphData.append(eq);

            // add the output widgets
            isFirst = true;
            for (ProcessPartDescription outputDesc : describeProcess.getOutputDescriptions()) {
                if (!isFirst) {
                    paragraphData.append(plus);
                }
                isFirst = false;
                String hyperlink = outputDesc.getResourceUrn() != null ? outputDesc.getResourceUrn().toString() : null;
                paragraphData.append(new WidgetFlowRenderable(outputDesc.getWidget(), 32, 32, hyperlink));
            }
        } else {
            paragraphData.append(new TextFlowRenderable(workstationProcess.getId() + " cannot be displayed", null, null));
        }

        processParagraphData.add(paragraphData);
        return processParagraphData;
    }

    /**
     * Compares this to another object to see if it is equal.
     *
     * @param o the object to compare to.
     * @return true if the object is also an input processes help item and has the same resource urn, false if otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputProcessesHelpItem)) return false;

        InputProcessesHelpItem that = (InputProcessesHelpItem) o;

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
