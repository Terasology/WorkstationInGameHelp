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
import org.terasology.inGameHelp.components.HelpItem;
import org.terasology.inGameHelp.ui.WidgetFlowRenderable;
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

public class InputProcessesHelpItem implements HelpItem {
    ResourceUrn resourceUrn;
    WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache;
    WorkstationRegistry workstationRegistry;

    public InputProcessesHelpItem(
            ResourceUrn resourceUrn,
            WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache,
            WorkstationRegistry workstationRegistry) {
        this.resourceUrn = resourceUrn;
        this.workstationProcessRelatedAssetCache = workstationProcessRelatedAssetCache;
        this.workstationRegistry = workstationRegistry;
    }

    @Override
    public String getTitle() {
        return "Used to Create";
    }

    @Override
    public Iterable<ParagraphData> getParagraphs() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InputProcessesHelpItem)) return false;

        InputProcessesHelpItem that = (InputProcessesHelpItem) o;

        if (!resourceUrn.equals(that.resourceUrn)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resourceUrn.hashCode();
    }
}
