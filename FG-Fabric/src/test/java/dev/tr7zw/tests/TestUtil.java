package dev.tr7zw.tests;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableMap;

import dev.tr7zw.config.CustomConfigScreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

public class TestUtil {

    public static PlayerRenderer getPlayerRenderer() {
        EntityRendererProvider.Context context = new Context(null, null, null, null, null, new DummyModelSet(), null);
        return new PlayerRenderer(context, false);
    }

    private static class DummyModelSet extends EntityModelSet {

        @Override
        public ModelPart bakeLayer(ModelLayerLocation modelLayerLocation) {
            PartDefinition part = PlayerModel.createMesh(CubeDeformation.NONE, false).getRoot();
            part.getChild("head").addOrReplaceChild("jaw", CubeListBuilder.create(), PartPose.ZERO);
            part.getChild("head").addOrReplaceChild("left_ear", CubeListBuilder.create(), PartPose.ZERO);
            part.getChild("head").addOrReplaceChild("right_ear", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("left_wing", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("right_wing", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.ZERO);
            part.getChild("head").addOrReplaceChild("feather", CubeListBuilder.create(), PartPose.ZERO);
            part.addOrReplaceChild("box", CubeListBuilder.create(), PartPose.ZERO);
            return part.bake(0, 0);
        }

    }

    public static Language loadDefault(String file) throws Throwable {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        Objects.requireNonNull(builder);
        BiConsumer<String, String> biConsumer = builder::put;
        InputStream inputStream = Language.class.getResourceAsStream(file);
        try {
            Language.loadFromJson(inputStream, biConsumer);
            if (inputStream != null)
                inputStream.close();
        } catch (Throwable throwable) {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
            throw throwable;
        }
        final ImmutableMap<String, String> storage = builder.build();
        return new Language() {
            public String getOrDefault(String string) {
                return (String) storage.getOrDefault(string, string);
            }

            public boolean has(String string) {
                return storage.containsKey(string);
            }

            public boolean isDefaultRightToLeft() {
                return false;
            }

            public FormattedCharSequence getVisualOrder(FormattedText formattedText) {
                return null;
            }

            @Override
            public String getOrDefault(String paramString1, String paramString2) {
                return (String) storage.getOrDefault(paramString1, paramString2);
            }
        };
    }
    
    public static Set<String> getKeys(OptionInstance<?> optionsInstance, boolean tooltips) {
        Set<String> keys = new HashSet<>();
        keys.add(optionsInstance.toString());
        if(tooltips) {
            keys.add(optionsInstance.toString() + ".tooltip");
        }
        return keys;
    }
    
    public static List<OptionInstance<?>> bootStrapCustomConfigScreen(CustomConfigScreen screen) throws Exception {
        Field optionsField = CustomConfigScreen.class.getDeclaredField("list");
        optionsField.setAccessible(true);
        CustomOptionsList list = new CustomOptionsList();
        optionsField.set(screen, list);
        Method init = CustomConfigScreen.class.getDeclaredMethod("initialize");
        init.setAccessible(true);
        init.invoke(screen);
        return list.options;
    }
    
    private static class CustomOptionsList extends OptionsList {

        public List<OptionInstance<?>> options = new ArrayList<>();
        
        public CustomOptionsList() {
            super(null, 0, 0, 0, 0, 0);
        }

        @Override
        public int addBig(OptionInstance<?> optionInstance) {
            options.add(optionInstance);
            return 0;
        }

        @Override
        public void addSmall(OptionInstance<?> optionInstance, OptionInstance<?> optionInstance2) {
            options.add(optionInstance);
        }
        
    }

}
