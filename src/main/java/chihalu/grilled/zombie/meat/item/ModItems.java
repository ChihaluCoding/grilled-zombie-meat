package chihalu.grilled.zombie.meat.item;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import chihalu.grilled.zombie.meat.GrilledZombieMeat;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {
	private static final float DEFAULT_EAT_SECONDS = 1.6F;
	private static final String TRANSLATION_PREFIX = "item." + GrilledZombieMeat.MOD_ID + ".";

	public static final FoodComponent GRILLED_ZOMBIE_MEAT_FOOD = new FoodComponent.Builder()
		.nutrition(6)
		.saturationModifier(0.8F)
		.build();

	public static final Item GRILLED_ZOMBIE_MEAT = registerFoodItem("grilled_zombie_meat", GRILLED_ZOMBIE_MEAT_FOOD);

	private ModItems() {
	}

	private static Item registerFoodItem(String name, FoodComponent foodComponent) {
		Identifier id = Identifier.of(GrilledZombieMeat.MOD_ID, name);
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

		Item.Settings settings = new Item.Settings()
			.component(DataComponentTypes.FOOD, foodComponent);

		settings = applyOptionalConsumableComponent(settings, foodComponent);
		settings = applyOptionalTranslationKey(settings, name);
		settings = applyOptionalRegistryKey(settings, key);

		return Registry.register(Registries.ITEM, id, new Item(settings));
	}

	private static Item.Settings applyOptionalConsumableComponent(Item.Settings settings, FoodComponent foodComponent) {
		ComponentType<?> consumableType = Registries.DATA_COMPONENT_TYPE.get(Identifier.of("minecraft", "consumable"));
		if (consumableType == null) {
			return settings;
		}

		try {
			Class<?> consumableComponentClass = Class.forName("net.minecraft.component.type.ConsumableComponent");
			Class<?> builderClass = Class.forName("net.minecraft.component.type.ConsumableComponent$Builder");

			Object builder = consumableComponentClass.getMethod("builder").invoke(null);

			invokeIfPresent(builderClass, builder, "consumeSeconds", float.class, extractEatSeconds(foodComponent));

			Object consumable = builderClass.getMethod("build").invoke(builder);

			Object result = Item.Settings.class.getMethod("component", ComponentType.class, Object.class)
				.invoke(settings, consumableType, consumable);
			if (result instanceof Item.Settings updated) {
				return updated;
			}
		} catch (ClassNotFoundException ignored) {
			return settings;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException exception) {
			GrilledZombieMeat.LOGGER.warn("Failed to attach consumable component", exception);
		}

		return settings;
	}

	private static Item.Settings applyOptionalTranslationKey(Item.Settings settings, String name) {
		return mutateSettingsIfPresent(settings, "translationKey", String.class, TRANSLATION_PREFIX + name);
	}

	private static Item.Settings applyOptionalRegistryKey(Item.Settings settings, RegistryKey<Item> key) {
		if (key == null) {
			return settings;
		}

		return mutateSettingsIfPresent(settings, "registryKey", RegistryKey.class, key);
	}

	private static Item.Settings mutateSettingsIfPresent(Item.Settings settings, String methodName, Class<?> parameterType, Object argument) {
		try {
			Method method = Item.Settings.class.getMethod(methodName, parameterType);
			Object result = method.invoke(settings, argument);
			if (result instanceof Item.Settings updated) {
				return updated;
			}
		} catch (NoSuchMethodException ignored) {
		} catch (IllegalAccessException | InvocationTargetException exception) {
			GrilledZombieMeat.LOGGER.debug("Failed to call Item.Settings#{} reflectively", methodName, exception);
		}

		return settings;
	}

	private static void invokeIfPresent(Class<?> targetClass, Object instance, String name, Class<?> parameterType, Object argument) {
		try {
			Method method = targetClass.getMethod(name, parameterType);
			method.invoke(instance, argument);
		} catch (NoSuchMethodException ignored) {
		} catch (IllegalAccessException | InvocationTargetException exception) {
			GrilledZombieMeat.LOGGER.debug("Unable to call {} on {}", name, targetClass.getName(), exception);
		}
	}

	private static float extractEatSeconds(FoodComponent foodComponent) {
		try {
			Method eatSeconds = foodComponent.getClass().getMethod("eatSeconds");
			Object result = eatSeconds.invoke(foodComponent);
			if (result instanceof Float value) {
				return value;
			}
		} catch (NoSuchMethodException ignored) {
		} catch (IllegalAccessException | InvocationTargetException exception) {
			GrilledZombieMeat.LOGGER.debug("Failed to read eatSeconds from FoodComponent", exception);
		}

		try {
			Method getEatTicks = foodComponent.getClass().getMethod("getEatTicks");
			Object result = getEatTicks.invoke(foodComponent);
			if (result instanceof Integer ticks && ticks > 0) {
				return ticks / 20.0F;
			}
		} catch (NoSuchMethodException ignored) {
		} catch (IllegalAccessException | InvocationTargetException exception) {
			GrilledZombieMeat.LOGGER.debug("Failed to read eatTicks from FoodComponent", exception);
		}

		return DEFAULT_EAT_SECONDS;
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.add(GRILLED_ZOMBIE_MEAT));
		GrilledZombieMeat.LOGGER.info("Registered item: {}", identifiableName(GRILLED_ZOMBIE_MEAT));
	}

	private static String identifiableName(Item item) {
		return Registries.ITEM.getId(item).toString();
	}
}
