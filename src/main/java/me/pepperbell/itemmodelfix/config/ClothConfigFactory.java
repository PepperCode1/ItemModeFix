package me.pepperbell.itemmodelfix.config;

import java.util.Locale;
import java.util.Optional;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import me.pepperbell.itemmodelfix.util.ModelGenerationType;
import me.pepperbell.itemmodelfix.util.ParsingUtil;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

@SuppressWarnings("deprecation")
public class ClothConfigFactory implements ConfigScreenFactory<Screen> {
	private Config config;

	public ClothConfigFactory(Config config) {
		this.config = config;
	}

	@Override
	public Screen create(Screen parent) {
		SavingRunnable savingRunnable = new SavingRunnable();

		ConfigBuilder builder = ConfigBuilder.create()
				.setParentScreen(parent)
				.setTitle(new TranslatableText("screen.itemmodelfix.config.title"))
				.setSavingRunnable(savingRunnable);
		ConfigEntryBuilder entryBuilder = builder.entryBuilder();

		ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.itemmodelfix.general"));
		general.addEntry(entryBuilder.startEnumSelector(new TranslatableText("option.itemmodelfix.generation_type"), ModelGenerationType.class, config.getOptions().generationType)
				.setSaveConsumer((value) -> {
					if (config.getOptions().generationType != value) {
						savingRunnable.reloadResources = true;
					}
					config.getOptions().generationType = value;
				})
				.setEnumNameProvider((value) -> {
					return new TranslatableText("option.itemmodelfix.generation_type." + value.name().toLowerCase(Locale.ROOT));
				})
				.setTooltipSupplier((value) -> {
					return Optional.ofNullable(ParsingUtil.parseNewlines("option.itemmodelfix.generation_type." + value.name().toLowerCase(Locale.ROOT) + ".tooltip"));
				})
				.setDefaultValue(Config.Options.DEFAULT.generationType)
				.build());

		return builder.build();
	}

	private class SavingRunnable implements Runnable {
		public boolean reloadResources = false;

		@Override
		public void run() {
			config.save();
			if (reloadResources) {
				MinecraftClient.getInstance().reloadResources();
			}
			reloadResources = false;
		}
	}
}
