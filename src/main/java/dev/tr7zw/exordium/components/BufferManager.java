package dev.tr7zw.exordium.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import dev.tr7zw.exordium.ExordiumModBase;
import dev.tr7zw.exordium.access.ChatAccess;
import dev.tr7zw.exordium.access.GuiAccess;
import dev.tr7zw.exordium.access.TablistAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.CrosshairOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.DebugOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.ExperienceBarOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.HotbarOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.ScoreBoardOverlayAccess;
import dev.tr7zw.exordium.access.VanillaBufferAccess.VignetteOverlayAccess;
import dev.tr7zw.exordium.components.support.XaerosMinimapComponent;
import dev.tr7zw.exordium.components.vanilla.BossHealthBarComponent;
import dev.tr7zw.exordium.components.vanilla.ChatComponent;
import dev.tr7zw.exordium.components.vanilla.CrosshairComponent;
import dev.tr7zw.exordium.components.vanilla.DebugOverlayComponent;
import dev.tr7zw.exordium.components.vanilla.ExperienceComponent;
import dev.tr7zw.exordium.components.vanilla.HealthComponent;
import dev.tr7zw.exordium.components.vanilla.HotbarComponent;
import dev.tr7zw.exordium.components.vanilla.PlayerListComponent;
import dev.tr7zw.exordium.components.vanilla.ScoreboardComponent;
import dev.tr7zw.exordium.components.vanilla.VignetteComponent;
import dev.tr7zw.exordium.versionless.config.Config;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

@NoArgsConstructor
public class BufferManager {

    private final Map<ResourceLocation, BufferInstance<?>> buffers = new HashMap<>();

    public void initialize() {
        ExordiumModBase inst = ExordiumModBase.instance;
        Minecraft minecraft = Minecraft.getInstance();
        registerBuffer(DebugOverlayComponent.getId(), new DebugOverlayComponent(),
                () -> inst.config.debugScreenSettings);
        registerBuffer(CrosshairComponent.getId(), new CrosshairComponent(), () -> inst.config.crosshairSettings);
        registerBuffer(VignetteComponent.getId(), new VignetteComponent(), () -> inst.config.vignetteSettings);
        registerBuffer(ExperienceComponent.getId(), new ExperienceComponent(), () -> inst.config.experienceSettings);
        registerBuffer(ScoreboardComponent.getId(), new ScoreboardComponent(), () -> inst.config.scoreboardSettings);
        registerBuffer(HotbarComponent.getId(), new HotbarComponent(), () -> inst.config.hotbarSettings);
        registerBuffer(ChatComponent.getId(), new ChatComponent(), () -> inst.config.chatSettings);
        registerBuffer(XaerosMinimapComponent.getId(), new XaerosMinimapComponent(),
                () -> inst.config.xaerosMinimapSettings);
        registerBuffer(PlayerListComponent.getId(), new PlayerListComponent(), () -> inst.config.tablistSettings);
        registerBuffer(BossHealthBarComponent.getId(), new BossHealthBarComponent(), () -> inst.config.bossbarSettings);
        registerBuffer(HealthComponent.getId(), new HealthComponent(), () -> inst.config.healthSettings);

    }

    @SuppressWarnings("unchecked")
    public <T> BufferInstance<T> getBufferInstance(ResourceLocation id, Class<T> context) {
        return (BufferInstance<T>) buffers.get(id);
    }

    public BufferInstance<?> registerBuffer(ResourceLocation id, BufferComponent<?> component,
            Supplier<Config.ComponentSettings> settings) {
        BufferInstance<?> buffer = new BufferInstance<>(id, component, settings);
        this.buffers.put(id, buffer);
        return buffer;
    }

}
