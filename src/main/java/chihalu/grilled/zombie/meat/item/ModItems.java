package chihalu.grilled.zombie.meat.item;

import chihalu.grilled.zombie.meat.GrilledZombieMeat;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public final class ModItems {
	public static final FoodComponent GRILLED_ZOMBIE_MEAT_FOOD = new FoodComponent.Builder()
		.nutrition(6)
		.saturationModifier(0.8F)
		.build();

	public static final ConsumableComponent GRILLED_ZOMBIE_MEAT_CONSUMABLE = ConsumableComponent.builder()
		.build();

	public static final Item GRILLED_ZOMBIE_MEAT = register("grilled_zombie_meat",
		new Item.Settings()
			.component(DataComponentTypes.CONSUMABLE, GRILLED_ZOMBIE_MEAT_CONSUMABLE)
			.component(DataComponentTypes.FOOD, GRILLED_ZOMBIE_MEAT_FOOD)
			.translationKey("item." + GrilledZombieMeat.MOD_ID + ".grilled_zombie_meat"));

	private ModItems() {
	}

	private static Item register(String name, Item.Settings settings) {
		Identifier id = Identifier.of(GrilledZombieMeat.MOD_ID, name);
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
		Item item = new Item(settings.registryKey(key));
		return Registry.register(Registries.ITEM, key, item);
	}

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.add(GRILLED_ZOMBIE_MEAT));
		GrilledZombieMeat.LOGGER.info("Registered item: {}", identifiableName(GRILLED_ZOMBIE_MEAT));
	}

	private static String identifiableName(Item item) {
		return Registries.ITEM.getId(item).toString();
	}
}
