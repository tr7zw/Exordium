package dev.tr7zw.animatedfirstperson.config.debug;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.screens.Screen;

public class DebugScreen {

    public static Screen createDebugGui(Screen parent, Supplier<Object> config) {
        return new CustomConfigScreen(parent, "debug") {

            @Override
            public void initialize() {
                this.background = false;
                this.footer = false;
                List<OptionInstance> options = new ArrayList<>();
                for (Field f : config.get().getClass().getDeclaredFields()) {
                    if (f.getType() == Float.class || f.getType() == float.class) {
                        f.setAccessible(true);
                        FloatSetting setting = f.getAnnotation(FloatSetting.class);
                        options.add(getDoubleOption(f.getName(), setting != null ? setting.min() : -1,
                                setting != null ? setting.max() : 1, setting != null ? setting.step() : 1,
                                () -> getFloat(config.get(), f), (v) -> setFloat(config.get(), f, v)));
                    }
                    if (f.getType() == Boolean.class || f.getType() == boolean.class) {
                        f.setAccessible(true);
                        options.add(getBooleanOption(f.getName(), () -> getBoolean(config.get(), f), (b) -> setBoolean(config.get(), f, b)));
                    }
                }

                getOptions().addSmall(options.toArray(new OptionInstance[0]));

            }

            @Override
            public void save() {
            }

            @Override
            public void reset() {

            }

        };
    }


    private static void setFloat(Object config, Field f, Double value) {
        try {
            f.setFloat(config, value.floatValue());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Double getFloat(Object config, Field f) {
        try {
            return (double) f.getFloat(config);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0d;
    }
    
    private static void setBoolean(Object config, Field f, Boolean value) {
        try {
            f.setBoolean(config, value.booleanValue());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Boolean getBoolean(Object config, Field f) {
        try {
            return (boolean) f.getBoolean(config);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


}
