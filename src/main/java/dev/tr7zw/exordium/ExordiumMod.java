package dev.tr7zw.exordium;

import dev.tr7zw.exordium.util.ReloadTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ExordiumMod extends ExordiumModBase implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        super.onInitialize();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleResourceReloadListener<>() {
                    @Override
                    public ResourceLocation getFabricId() {
                        return new ResourceLocation("exordium", "reload_listener");
                    }

                    @Override
                    public CompletableFuture<Object> load(ResourceManager manager, ProfilerFiller profiler,
                            Executor executor) {
                        return CompletableFuture.completedFuture(null);
                    }

                    @Override
                    public CompletableFuture<Void> apply(Object data, ResourceManager manager, ProfilerFiller profiler,
                            Executor executor) {
                        ReloadTracker.reload();
                        return CompletableFuture.completedFuture(null);
                    }
                });
    }

    @Override
    public void initModloader() {

    }

}
