package dev.tr7zw.animatedfirstperson.config.debug;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public abstract class CustomConfigScreen extends Screen {

	protected final Screen lastScreen;
	private OptionsList list;
	public boolean background = true;
	public boolean footer = true;

	public CustomConfigScreen(Screen lastScreen, String title) {
		super(Component.translatable(title));
		this.lastScreen = lastScreen;
	}

	@Override
	public void removed() {
		save();
	}

	@Override
	public void onClose() {
		this.minecraft.setScreen(this.lastScreen);
	}

	public OptionsList getOptions() {
		return list;
	}

	protected void init() {
		this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		this.addWidget(this.list);
	    initialize();
		if(footer)
		    this.createFooter();
	}

	public abstract void initialize();
	
	public abstract void reset();
	
	public abstract void save();

	protected void createFooter() {
		this.addRenderableWidget(
				new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, new OnPress() {

					@Override
					public void onPress(Button button) {
						CustomConfigScreen.this.onClose();
					}
				}));
		this.addRenderableWidget(
                new Button(this.width / 2 + 110, this.height - 27, 60, 20, Component.translatable("controls.reset"), new OnPress() {

                    @Override
                    public void onPress(Button button) {
                        reset();
                        CustomConfigScreen.this.resize(minecraft, width, height); // refresh
                    }
                }));
	}

	public void render(PoseStack poseStack, int i, int j, float f) {
	    if(background)
	        this.renderBackground(poseStack);
	    this.list.setRenderBackground(background);
        this.list.setRenderTopAndBottom(background);
		this.list.render(poseStack, i, j, f);
		drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
		super.render(poseStack, i, j, f);
		List<FormattedCharSequence> list = OptionsSubScreen.tooltipAt(this.list, i, j);
		this.renderTooltip(poseStack, list, i, j);
	}

	public OptionInstance<Boolean> getBooleanOption(String translationKey, Supplier<Boolean> current,
			Consumer<Boolean> update) {
		return OptionInstance.createBoolean(translationKey, current.get(),update);
	}

	public OptionInstance<Boolean> getOnOffOption(String translationKey, Supplier<Boolean> current,
			Consumer<Boolean> update) {
		return getBooleanOption(translationKey, current, update);
	}
	
	public OptionInstance<Double> getDoubleOption(String translationKey, float min, float max, float steps, Supplier<Double> current,
			Consumer<Double> update) {
	    Double sliderValue = ((current.get()-min) / (max-min));
	    return new OptionInstance<Double>(translationKey, OptionInstance.noTooltip(), (comp, d) -> {
            double lvt_4_1_ = min + (d * (max-min));
            lvt_4_1_ = (int)(lvt_4_1_/steps);
            lvt_4_1_ *= steps;
	        return comp.copy().append(": " + round(lvt_4_1_, 3));
	        }, OptionInstance.UnitDouble.INSTANCE, Codec.doubleRange(min, max), sliderValue, (d) ->{
            double lvt_4_1_ = min + (d * (max-min));
            lvt_4_1_ = (int)(lvt_4_1_/steps);
            lvt_4_1_ *= steps;
            update.accept(lvt_4_1_);
	    });
	}

	public OptionInstance<Integer> getIntOption(String translationKey, int min, int max, Supplier<Integer> current,
			Consumer<Integer> update) {
	    return new OptionInstance<Integer>(translationKey, OptionInstance.noTooltip(), (comp, d) -> comp.copy().append(": " + d), new OptionInstance.IntRange(min, max), current.get(), (d) -> update.accept(d));
	}

	@SuppressWarnings("rawtypes")
    public <T extends Enum> OptionInstance<T> getEnumOption(String translationKey, Class<T> targetEnum, Supplier<T> current,
			Consumer<T> update) {
	    Map<String, T> mapping = new HashMap<>();
	    Arrays.asList(targetEnum.getEnumConstants()).forEach(t -> mapping.put(t.name(), t));
	    return new OptionInstance<T>(translationKey, OptionInstance.noTooltip(), (comp, t) -> Component.translatable(translationKey + "." + t.name()), new OptionInstance.Enum<T>(Arrays.asList(targetEnum.getEnumConstants()), Codec.STRING.xmap(s -> mapping.get(s), e -> e.name())), current.get(), update);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

}