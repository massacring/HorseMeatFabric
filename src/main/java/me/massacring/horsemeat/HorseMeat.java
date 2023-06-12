package me.massacring.horsemeat;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.item.*;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.FurnaceSmeltLootFunction;
import net.minecraft.loot.function.LootingEnchantLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;

public class HorseMeat implements ModInitializer {
    public static final String MOD_ID = "horsemeat";
    private static final Item RAW_HORSE_MEAT = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(3).saturationModifier(0.3f).meat().build()));
    private static final Item COOKED_HORSE_MEAT = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(8).saturationModifier(0.8f).meat().build()));
    private static final Item RAW_WOLF_MEAT = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).meat().build()));
    private static final Item COOKED_WOLF_MEAT = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.8f).meat().build()));
    private static final Item RAW_CAT_MEAT = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3f).meat().build()));
    private static final Item COOKED_CAT_MEAT = new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(6).saturationModifier(0.8f).meat().build()));

    private static final Identifier HORSE_LOOT_TABLE_ID = EntityType.HORSE.getLootTableId();
    private static final Identifier WOLF_LOOT_TABLE_ID = EntityType.WOLF.getLootTableId();
    private static final Identifier CAT_LOOT_TABLE_ID = EntityType.CAT.getLootTableId();

    @Override
    public void onInitialize() {
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "horse_meat"), RAW_HORSE_MEAT);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "cooked_horse_meat"), COOKED_HORSE_MEAT);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "wolf_meat"), RAW_WOLF_MEAT);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "cooked_wolf_meat"), COOKED_WOLF_MEAT);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "cat_meat"), RAW_CAT_MEAT);
        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "cooked_cat_meat"), COOKED_CAT_MEAT);

        Collection<ItemStack> items = new ArrayList<>();
        items.add(RAW_HORSE_MEAT.getDefaultStack());
        items.add(COOKED_HORSE_MEAT.getDefaultStack());
        items.add(RAW_WOLF_MEAT.getDefaultStack());
        items.add(COOKED_WOLF_MEAT.getDefaultStack());
        items.add(RAW_CAT_MEAT.getDefaultStack());
        items.add(COOKED_CAT_MEAT.getDefaultStack());
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(content -> {
            content.addAfter(Items.COOKED_MUTTON, items);
        });

        modifyLootTables();
    }

    private void modifyLootTables() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (source.isBuiltin() && HORSE_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(RAW_HORSE_MEAT))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1, 2), false))
                        .apply(FurnaceSmeltLootFunction.builder()
                                .conditionally(EntityPropertiesLootCondition
                                        .builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create()
                                                .flags(EntityFlagsPredicate.Builder.create().onFire(true).build()))))
                        .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0, 1)));

                tableBuilder.pool(poolBuilder);
            }
            if (source.isBuiltin() && WOLF_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(RAW_WOLF_MEAT))
                        .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(0, 2), false))
                        .apply(FurnaceSmeltLootFunction.builder()
                                .conditionally(EntityPropertiesLootCondition
                                        .builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create()
                                                .flags(EntityFlagsPredicate.Builder.create().onFire(true).build()))))
                        .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0, 1)));

                tableBuilder.pool(poolBuilder);
            }
            if (source.isBuiltin() && CAT_LOOT_TABLE_ID.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1))
                        .with(ItemEntry.builder(RAW_CAT_MEAT))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(1), false))
                        .apply(FurnaceSmeltLootFunction.builder()
                                .conditionally(EntityPropertiesLootCondition
                                        .builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create()
                                                .flags(EntityFlagsPredicate.Builder.create().onFire(true).build()))))
                        .apply(LootingEnchantLootFunction.builder(UniformLootNumberProvider.create(0, 1)));

                tableBuilder.pool(poolBuilder);
            }
        });
    }
}
