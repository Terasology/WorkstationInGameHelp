// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0
package org.terasology.workstationInGameHelp.components;

import org.terasology.gestalt.entitysystem.component.Component;

/**
 * Component for items that have help information for a workstation help system.
 * Entities that have this component are processed in {@link org.terasology.workstationInGameHelp.systems.WorkstationItemsInGameHelpCommonSystem}.
 */
public class ParticipateInItemCategoryInGameHelpComponent implements Component<ParticipateInItemCategoryInGameHelpComponent> {
    @Override
    public void copy(ParticipateInItemCategoryInGameHelpComponent other) {

    }
}
