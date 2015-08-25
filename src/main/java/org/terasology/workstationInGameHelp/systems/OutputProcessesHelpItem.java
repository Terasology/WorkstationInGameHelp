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
import org.terasology.inGameHelp.ui.WidgetFlowRenderable;
import org.terasology.rendering.nui.widgets.browser.data.ParagraphData;
import org.terasology.rendering.nui.widgets.browser.data.basic.FlowParagraphData;
import org.terasology.workstation.process.WorkstationProcess;
import org.terasology.workstation.system.WorkstationRegistry;
import org.terasology.workstation.ui.ProcessSummaryWidget;
import org.terasology.workstationInGameHelp.WorkstationProcessRelatedAssetCache;

import java.util.List;

public class OutputProcessesHelpItem implements HelpItem {
    ResourceUrn resourceUrn;
    WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache;
    WorkstationRegistry workstationRegistry;

    public OutputProcessesHelpItem(
            ResourceUrn resourceUrn,
            WorkstationProcessRelatedAssetCache workstationProcessRelatedAssetCache,
            WorkstationRegistry workstationRegistry) {
        this.resourceUrn = resourceUrn;
        this.workstationProcessRelatedAssetCache = workstationProcessRelatedAssetCache;
        this.workstationRegistry = workstationRegistry;
    }

    @Override
    public String getTitle() {
        return "Created By";
    }

    @Override
    public Iterable<ParagraphData> getParagraphs() {
        List<ParagraphData> result = Lists.newLinkedList();
        for (WorkstationProcess workstationProcess : workstationProcessRelatedAssetCache.getOutputRelatedWorkstationProcesses(resourceUrn)) {
            FlowParagraphData paragraphData = new FlowParagraphData(null);
            paragraphData.append(new WidgetFlowRenderable(new ProcessSummaryWidget(workstationProcess), 550, 48, null));
            result.add(paragraphData);
        }

        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OutputProcessesHelpItem)) return false;

        OutputProcessesHelpItem that = (OutputProcessesHelpItem) o;

        if (!resourceUrn.equals(that.resourceUrn)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resourceUrn.hashCode();
    }
}
