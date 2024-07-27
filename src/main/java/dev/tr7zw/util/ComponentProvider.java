package dev.tr7zw.util;

import net.minecraft.network.chat.Component;

// spotless:off
//#if MC >= 11900
import net.minecraft.network.chat.MutableComponent;
//#else
//$$ import net.minecraft.network.chat.TextComponent;
//$$ import net.minecraft.network.chat.TranslatableComponent;
//#endif
//spotless:on

/**
 * Utility class to get Components, for better bridging between 1.19+ and pre
 * 1.19
 * 
 * @author tr7zw
 *
 */
public class ComponentProvider {

    // spotless:off
    //#if MC >= 11900
    public static MutableComponent literal(String string) {
    	return Component.literal(string);
        //#else
    	//$$ public static TextComponent literal(String string) {
        //$$ return new TextComponent(string);
        //#endif
        //spotless:on
    }

    // spotless:off
    //#if MC >= 11900
    public static MutableComponent translatable(String string) {
    	return Component.translatable(string);
        //#else
    	//$$ public static TranslatableComponent translatable(String string) {
        //$$ return new TranslatableComponent(string);
        //#endif
        //spotless:on
    }

    // spotless:off
    //#if MC >= 11900
    public static MutableComponent translatable(String string, Object... objects) {
    	 return Component.translatable(string, objects);
        //#else
    	//$$ public static TranslatableComponent translatable(String string, Object... objects) {
        //$$ return new TranslatableComponent(string, objects);
        //#endif
        //spotless:on
    }

    // spotless:off
    //#if MC >= 11900
    public static MutableComponent empty() {
    	return Component.empty();
        //#else
    	//$$ public static Component empty() {
        //$$ return TextComponent.EMPTY;
        //#endif
        //spotless:on
    }

}