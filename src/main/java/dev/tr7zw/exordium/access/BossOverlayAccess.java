package dev.tr7zw.exordium.access;

import java.util.Map;
import java.util.UUID;

import net.minecraft.client.gui.components.LerpingBossEvent;

public interface BossOverlayAccess {

    Map<UUID, LerpingBossEvent> getEvents();

}
