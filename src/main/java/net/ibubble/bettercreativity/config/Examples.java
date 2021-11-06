package net.ibubble.bettercreativity.config;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Examples {
    private static final Map<ItemGroup, Supplier<List<ItemStack>>> map = new HashMap<>();

    private static ItemStack toItemStack(String path) {
        if (path == null || path.isEmpty()) return ItemStack.EMPTY;
        return Registry.ITEM.get(new Identifier("minecraft", path)).getDefaultStack();
    }

    private static ItemStack toEnchantBook(Enchantment enchantment) {
        return EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, enchantment.getMaxLevel()));
    }

    private static ItemStack toTippedArrow(Potion potion) {
        return PotionUtil.setPotion(new ItemStack(Items.TIPPED_ARROW), potion);
    }

    private static ItemStack getEnchantedItem(Item item, Enchantment ...enchantments) {
        ItemStack stack = new ItemStack(item);
        for (Enchantment enchantment : enchantments) {
            stack.addEnchantment(enchantment, enchantment.getMaxLevel());
        }
        return stack;
    }

    public static boolean contains(ItemGroup group) {
        return map.containsKey(group);
    }

    public static List<ItemStack> get(ItemGroup group) {
        return map.get(group).get();
    }

    static {
        map.put(ItemGroup.BUILDING_BLOCKS, () -> {
            return Stream.of(
                    "grass_block", "podzol", "mycelium", "dirt_path", "dirt", "coarse_dirt", "rooted_dirt", "farmland", "cobblestone",
                    "cobblestone_stairs", "cobblestone_slab", "mossy_cobblestone", "mossy_cobblestone_stairs", "mossy_cobblestone_slab", "stone", "stone_stairs", "stone_slab", "stone_bricks",
                    "cracked_stone_bricks", "chiseled_stone_bricks", "stone_brick_stairs", "stone_brick_slab", "mossy_stone_bricks", "mossy_stone_brick_stairs", "mossy_stone_brick_slab", "smooth_stone", "smooth_stone_slab",
                    "granite", "granite_stairs", "granite_slab", "polished_granite", "polished_granite_stairs", "polished_granite_slab", "diorite", "diorite_stairs", "diorite_slab",
                    "polished_diorite", "polished_diorite_stairs", "polished_diorite_slab", "andesite", "andesite_stairs", "andesite_slab", "polished_andesite", "polished_andesite_stairs", "polished_andesite_slab",
                    "deepslate", "chiseled_deepslate", "cobbled_deepslate", "cobbled_deepslate_stairs", "cobbled_deepslate_slab", "polished_deepslate", "polished_deepslate_stairs", "polished_deepslate_slab", "deepslate_bricks",
                    "cracked_deepslate_bricks", "deepslate_brick_stairs", "deepslate_brick_slab", "deepslate_tiles", "cracked_deepslate_tiles", "deepslate_tile_stairs", "deepslate_tile_slab", "gravel", "basalt",
                    "polished_basalt", "smooth_basalt", "calcite", "tuff", "dripstone_block", "amethyst_block", "budding_amethyst", "clay", "bricks",
                    "brick_stairs", "brick_slab", "obsidian", "crying_obsidian", "bedrock", null, "oak_planks", "oak_stairs", "oak_slab",
                    "spruce_planks", "spruce_stairs", "spruce_slab", "birch_planks", "birch_stairs", "birch_slab", "acacia_planks", "acacia_stairs", "acacia_slab",
                    "jungle_planks", "jungle_stairs", "jungle_slab", "dark_oak_planks", "dark_oak_stairs", "dark_oak_slab", "crimson_planks", "crimson_stairs", "crimson_slab",
                    "warped_planks", "warped_stairs", "warped_slab", "oak_log", "oak_wood", "stripped_oak_log", "stripped_oak_wood", "spruce_log", "spruce_wood",
                    "stripped_spruce_log", "stripped_spruce_wood", "birch_log", "birch_wood", "stripped_birch_log", "stripped_birch_wood", "acacia_log", "acacia_wood", "stripped_acacia_log",
                    "stripped_acacia_wood", "jungle_log", "jungle_wood", "stripped_jungle_log", "stripped_jungle_wood", "dark_oak_log", "dark_oak_wood", "stripped_dark_oak_log", "stripped_dark_oak_wood",
                    "crimson_stem", "crimson_hyphae", "stripped_crimson_stem", "stripped_crimson_hyphae", "nether_wart_block", "warped_stem", "warped_hyphae", "stripped_warped_stem", "stripped_warped_hyphae",
                    "warped_wart_block", "sand", "sandstone", "sandstone_stairs", "sandstone_slab", "smooth_sandstone", "smooth_sandstone_stairs", "smooth_sandstone_slab", "cut_sandstone",
                    "chiseled_sandstone", "cut_sandstone_slab", "red_sand", "red_sandstone", "red_sandstone_stairs", "red_sandstone_slab", "smooth_red_sandstone", "smooth_red_sandstone_stairs", "smooth_red_sandstone_slab",
                    "cut_red_sandstone", "chiseled_red_sandstone", "cut_red_sandstone_slab", "snow_block", "ice", "packed_ice", "blue_ice", "prismarine", "prismarine_stairs",
                    "prismarine_slab", "prismarine_bricks", "prismarine_brick_stairs", "prismarine_brick_slab", "dark_prismarine", "dark_prismarine_stairs", "dark_prismarine_slab", "sea_lantern", "netherrack",
                    "crimson_nylium", "warped_nylium", "soul_sand", "soul_soil", "magma_block", "glowstone", "shroomlight", "nether_bricks", "cracked_nether_bricks",
                    "chiseled_nether_bricks", "nether_brick_stairs", "nether_brick_slab", "red_nether_bricks", "red_nether_brick_stairs", "red_nether_brick_slab", "quartz_block", "chiseled_quartz_block", "quartz_pillar",
                    "quartz_bricks", "quartz_stairs", "quartz_slab", "smooth_quartz", "smooth_quartz_stairs", "smooth_quartz_slab", "blackstone", "gilded_blackstone", "blackstone_stairs",
                    "blackstone_slab", "polished_blackstone", "chiseled_polished_blackstone", "polished_blackstone_stairs", "polished_blackstone_slab", "polished_blackstone_bricks", "cracked_polished_blackstone_bricks", "polished_blackstone_brick_stairs", "polished_blackstone_brick_slab",
                    "end_stone", "end_stone_bricks", "end_stone_brick_stairs", "end_stone_brick_slab", "purpur_block", "purpur_pillar", "purpur_stairs", "purpur_slab", "glass",
                    "white_stained_glass", "orange_stained_glass", "magenta_stained_glass", "light_blue_stained_glass", "yellow_stained_glass", "lime_stained_glass", "pink_stained_glass", "gray_stained_glass", "light_gray_stained_glass",
                    "cyan_stained_glass", "purple_stained_glass", "blue_stained_glass", "brown_stained_glass", "green_stained_glass", "red_stained_glass", "tinted_glass", "black_stained_glass", null,
                    "white_wool", "orange_wool", "magenta_wool", "light_blue_wool", "yellow_wool", "lime_wool", "pink_wool", "gray_wool", "light_gray_wool",
                    "cyan_wool", "purple_wool", "blue_wool", "brown_wool", "green_wool", "red_wool", "black_wool", null, null,
                    "white_concrete_powder", "orange_concrete_powder", "magenta_concrete_powder", "light_blue_concrete_powder", "yellow_concrete_powder", "lime_concrete_powder", "pink_concrete_powder", "gray_concrete_powder", "light_gray_concrete_powder",
                    "cyan_concrete_powder", "purple_concrete_powder", "blue_concrete_powder", "brown_concrete_powder", "green_concrete_powder", "red_concrete_powder", "black_concrete_powder", null, null,
                    "white_concrete", "orange_concrete", "magenta_concrete", "light_blue_concrete", "yellow_concrete", "lime_concrete", "pink_concrete", "gray_concrete", "light_gray_concrete",
                    "cyan_concrete", "purple_concrete", "blue_concrete", "brown_concrete", "green_concrete", "red_concrete", "black_concrete", null, null,
                    "white_terracotta", "orange_terracotta", "magenta_terracotta", "light_blue_terracotta", "yellow_terracotta", "lime_terracotta", "pink_terracotta", "gray_terracotta", "light_gray_terracotta",
                    "cyan_terracotta", "purple_terracotta", "blue_terracotta", "brown_terracotta", "green_terracotta", "red_terracotta", "black_terracotta", "terracotta", null,
                    "white_glazed_terracotta", "orange_glazed_terracotta", "magenta_glazed_terracotta", "light_blue_glazed_terracotta", "yellow_glazed_terracotta", "lime_glazed_terracotta", "pink_glazed_terracotta", "gray_glazed_terracotta", "light_gray_glazed_terracotta",
                    "cyan_glazed_terracotta", "purple_glazed_terracotta", "blue_glazed_terracotta", "brown_glazed_terracotta", "green_glazed_terracotta", "red_glazed_terracotta", "black_glazed_terracotta", null, null,
                    "sponge", "wet_sponge", "dried_kelp_block", "bone_block", "iron_block", "copper_block", "gold_block", "redstone_block", "emerald_block",
                    "lapis_block", "diamond_block", "netherite_block", "coal_ore", "deepslate_coal_ore", "coal_block", "iron_ore", "deepslate_iron_ore", "raw_iron_block",
                    "copper_ore", "deepslate_copper_ore", "raw_copper_block", "gold_ore", "deepslate_gold_ore", "raw_gold_block", "redstone_ore", "deepslate_redstone_ore", "emerald_ore",
                    "deepslate_emerald_ore", "lapis_ore", "deepslate_lapis_ore", "diamond_ore", "deepslate_diamond_ore", "nether_gold_ore", "nether_quartz_ore", "ancient_debris", null,
                    "copper_block", "waxed_copper_block", "cut_copper", "waxed_cut_copper", "cut_copper_stairs", "waxed_cut_copper_stairs", "cut_copper_slab", "waxed_cut_copper_slab", null,
                    "exposed_copper", "waxed_exposed_copper", "exposed_cut_copper", "waxed_exposed_cut_copper", "exposed_cut_copper_stairs", "waxed_exposed_cut_copper_stairs", "exposed_cut_copper_slab", "waxed_exposed_cut_copper_slab", null,
                    "weathered_copper", "waxed_weathered_copper", "weathered_cut_copper", "waxed_weathered_cut_copper", "weathered_cut_copper_stairs", "waxed_weathered_cut_copper_stairs", "weathered_cut_copper_slab", "waxed_weathered_cut_copper_slab", null,
                    "oxidized_copper", "waxed_oxidized_copper", "oxidized_cut_copper", "waxed_oxidized_cut_copper", "oxidized_cut_copper_stairs", "waxed_oxidized_cut_copper_stairs", "oxidized_cut_copper_slab", "waxed_oxidized_cut_copper_slab", null,
                    "moss_block", "honeycomb_block", "hay_block", "pumpkin", "carved_pumpkin", "jack_o_lantern", "melon", "mushroom_stem", "red_mushroom_block",
                    "brown_mushroom_block", "tube_coral_block", "brain_coral_block", "bubble_coral_block", "fire_coral_block", "horn_coral_block", "dead_tube_coral_block", "dead_brain_coral_block", "dead_bubble_coral_block",
                    "dead_fire_coral_block", "dead_horn_coral_block", "bookshelf", "tnt", "infested_stone", "infested_cobblestone", "infested_stone_bricks", "infested_mossy_stone_bricks", "infested_cracked_stone_bricks",
                    "infested_chiseled_stone_bricks", "infested_deepslate"
            ).map(Examples::toItemStack).collect(Collectors.toList());
        });
        map.put(ItemGroup.DECORATIONS, () -> {
            return Stream.of(
                    "crafting_table", "chest", "furnace", "flower_pot", "item_frame", "glow_item_frame", "painting", "torch", "lantern",
                    "oak_fence", "spruce_fence", "birch_fence", "jungle_fence", "acacia_fence", "dark_oak_fence", "crimson_fence", "warped_fence", "nether_brick_fence",
                    "oak_fence_gate", "spruce_fence_gate", "birch_fence_gate", "jungle_fence_gate", "acacia_fence_gate", "dark_oak_fence_gate", "crimson_fence_gate", "warped_fence_gate", "cobblestone_wall",
                    "mossy_cobblestone_wall", "stone_brick_wall", "mossy_stone_brick_wall", "granite_wall", "diorite_wall", "andesite_wall", "cobbled_deepslate_wall", "polished_deepslate_wall", "deepslate_brick_wall",
                    "deepslate_tile_wall", "brick_wall", "sandstone_wall", "red_sandstone_wall", "prismarine_wall", "nether_brick_wall", "red_nether_brick_wall", "blackstone_wall", "polished_blackstone_wall",
                    "polished_blackstone_brick_wall", "end_stone_brick_wall", "torch", "soul_torch", "lantern", "soul_lantern", "campfire", "soul_campfire", "end_rod",
                    "sea_pickle", "glow_berries", "beacon", "conduit", "chain", "iron_bars", "ladder", "scaffolding", "armor_stand",
                    "oak_sign", "spruce_sign", "birch_sign", "jungle_sign", "acacia_sign", "dark_oak_sign", "crimson_sign", "warped_sign", "azalea",
                    "flowering_azalea", "oak_leaves", "spruce_leaves", "jungle_leaves", "acacia_leaves", "dark_oak_leaves", "azalea_leaves", "flowering_azalea_leaves", "vine",
                    "moss_carpet", "moss_block", "small_dripleaf", "big_dripleaf", "poppy", "dandelion", "cornflower", "azure_bluet", "lily_of_the_valley",
                    "oxeye_daisy", "blue_orchid", "allium", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip", "sunflower", "lilac",
                    "rose_bush", "peony", "wither_rose", "spore_blossom", "oak_sapling", "spruce_sapling", "birch_sapling", "jungle_sapling", "acacia_sapling",
                    "dark_oak_sapling", "brown_mushroom", "red_mushroom", "crimson_fungus", "warped_fungus", "grass", "tall_grass", "fern", "large_fern",
                    "sweet_berries", "sugar_cane", "bamboo", "dead_bush", "cactus", "lily_pad", "seagrass", "kelp", "string",
                    "cobweb", "glow_lichen", "hanging_roots", "crimson_roots", "weeping_vines", "nether_sprouts", "warped_roots", "twisting_vines", "chorus_plant",
                    "chorus_flower", "bookshelf", "note_block", "jukebox", "lectern", "loom", "composter", "barrel", "cartography_table",
                    "fletching_table", "cauldron", "brewing_stand", "smoker", "stonecutter", "grindstone", "smithing_table", "blast_furnace", "bell",
                    "enchanting_table", "anvil", "chipped_anvil", "damaged_anvil", "ender_chest", "lodestone", "respawn_anchor", "end_portal_frame", "end_crystal",
                    "snow", "small_amethyst_bud", "medium_amethyst_bud", "large_amethyst_bud", "amethyst_cluster", "pointed_dripstone", "turtle_egg", "bee_nest", "beehive",
                    "glass_pane", "white_stained_glass_pane", "orange_stained_glass_pane", "magenta_stained_glass_pane", "light_blue_stained_glass_pane", "yellow_stained_glass_pane", "lime_stained_glass_pane", "pink_stained_glass_pane", "gray_stained_glass_pane",
                    "light_gray_stained_glass_pane", "cyan_stained_glass_pane", "purple_stained_glass_pane", "blue_stained_glass_pane", "brown_stained_glass_pane", "green_stained_glass_pane", "red_stained_glass_pane", "black_stained_glass_pane", "candle",
                    "white_candle", "orange_candle", "magenta_candle", "light_blue_candle", "yellow_candle", "lime_candle", "pink_candle", "gray_candle", "light_gray_candle",
                    "cyan_candle", "purple_candle", "blue_candle", "brown_candle", "green_candle", "red_candle", "black_candle", "white_carpet", "orange_carpet",
                    "magenta_carpet", "light_blue_carpet", "yellow_carpet", "lime_carpet", "pink_carpet", "gray_carpet", "light_gray_carpet", "cyan_carpet", "purple_carpet",
                    "blue_carpet", "brown_carpet", "green_carpet", "red_carpet", "black_carpet", "white_bed", "orange_bed", "magenta_bed", "light_blue_bed",
                    "yellow_bed", "lime_bed", "pink_bed", "gray_bed", "light_gray_bed", "cyan_bed", "purple_bed", "blue_bed", "brown_bed",
                    "green_bed", "red_bed", "black_bed", "white_banner", "orange_banner", "magenta_banner", "light_blue_banner", "yellow_banner", "lime_banner",
                    "pink_banner", "gray_banner", "light_gray_banner", "cyan_banner", "purple_banner", "blue_banner", "brown_banner", "green_banner", "red_banner",
                    "black_banner", "shulker_box", "white_shulker_box", "orange_shulker_box", "magenta_shulker_box", "light_blue_shulker_box", "yellow_shulker_box", "lime_shulker_box", "pink_shulker_box",
                    "gray_shulker_box", "light_gray_shulker_box", "cyan_shulker_box", "purple_shulker_box", "blue_shulker_box", "brown_shulker_box", "green_shulker_box", "red_shulker_box", "black_shulker_box",
                    "skeleton_skull", "wither_skeleton_skull", "player_head", "zombie_head", "creeper_head", "dragon_head", "tube_coral", "brain_coral", "bubble_coral",
                    "fire_coral", "horn_coral", "dead_tube_coral", "dead_brain_coral", "dead_bubble_coral", "dead_fire_coral", "dead_horn_coral", "tube_coral_fan", "brain_coral_fan",
                    "bubble_coral_fan", "fire_coral_fan", "horn_coral_fan", "dead_tube_coral_fan", "dead_brain_coral_fan", "dead_bubble_coral_fan", "dead_fire_coral_fan", "dead_horn_coral_fan", "barrier",
                    "light"
            ).map(Examples::toItemStack).collect(Collectors.toList());
        });
        map.put(ItemGroup.REDSTONE, () -> {
            return Stream.of(
                    "redstone", "redstone_torch", "repeater", "comparator", "lever", "redstone_block", "daylight_detector", "redstone_lamp", "command_block",
                    "oak_trapdoor", "spruce_trapdoor", "birch_trapdoor", "jungle_trapdoor", "acacia_trapdoor", "dark_oak_trapdoor", "crimson_trapdoor", "warped_trapdoor", "iron_trapdoor",
                    "oak_door", "spruce_door", "birch_door", "jungle_door", "acacia_door", "dark_oak_door", "crimson_door", "warped_door", "iron_door",
                    "oak_button", "spruce_button", "birch_button", "jungle_button", "acacia_button", "dark_oak_button", "crimson_button", "warped_button", "stone_button",
                    "oak_pressure_plate", "spruce_pressure_plate", "birch_pressure_plate", "jungle_pressure_plate", "acacia_pressure_plate", "dark_oak_pressure_plate", "crimson_pressure_plate", "warped_pressure_plate", "stone_pressure_plate",
                    "polished_blackstone_button", "polished_blackstone_pressure_plate", "light_weighted_pressure_plate", "heavy_weighted_pressure_plate", "piston", "sticky_piston", "slime_block", "honey_block", "observer",
                    "target", "hopper", "dispenser", "dropper", "trapped_chest", "lightning_rod", "tripwire_hook", "string", "lectern",
                    "note_block", "tnt", "oak_fence_gate", "spruce_fence_gate", "birch_fence_gate", "jungle_fence_gate", "acacia_fence_gate", "dark_oak_fence_gate", "crimson_fence_gate",
                    "warped_fence_gate", "sculk_sensor", "spawner", "repeating_command_block", "chain_command_block", "structure_block", "jigsaw", "structure_void", "knowledge_book",
                    "debug_stick", "command_block_minecart"
            ).map(Examples::toItemStack).collect(Collectors.toList());
        });
        map.put(ItemGroup.TRANSPORTATION, () -> {
            List<ItemStack> transportation = Stream.of(
                    "rail", "powered_rail", "detector_rail", "activator_rail", "minecart", "chest_minecart", "furnace_minecart", "tnt_minecart", "hopper_minecart",
                    "oak_boat", "spruce_boat", "birch_boat", "jungle_boat", "acacia_boat", "dark_oak_boat", "elytra"
            ).map(Examples::toItemStack).collect(Collectors.toCollection(ArrayList::new));
            transportation.add(getEnchantedItem(Items.ELYTRA, Enchantments.UNBREAKING, Enchantments.MENDING));
            transportation.addAll(Stream.of(
                    "firework_rocket",
                    "saddle", "carrot_on_a_stick", "warped_fungus_on_a_stick", "leather_horse_armor", "iron_horse_armor", "golden_horse_armor", "diamond_horse_armor", "lead", "command_block_minecart",
                    "ender_pearl", "ender_eye", "ladder", "scaffolding"
            ).map(Examples::toItemStack).collect(Collectors.toList()));
            return transportation;
        });
        map.put(ItemGroup.MISC, () -> {
            return Stream.of(
                    "bucket", "water_bucket", "lava_bucket", "powder_snow_bucket", "string", "bone_meal", "map", "writable_book", "spawner",
                    "white_dye", "orange_dye", "magenta_dye", "light_blue_dye", "yellow_dye", "lime_dye", "pink_dye", "gray_dye", "light_gray_dye",
                    "cyan_dye", "purple_dye", "blue_dye", "brown_dye", "green_dye", "red_dye", "black_dye", "flower_banner_pattern", "creeper_banner_pattern",
                    "skull_banner_pattern", "mojang_banner_pattern", "globe_banner_pattern", "piglin_banner_pattern", "firework_star", "firework_rocket", "villager_spawn_egg", "wandering_trader_spawn_egg", "chicken_spawn_egg",
                    "sheep_spawn_egg", "pig_spawn_egg", "cow_spawn_egg", "horse_spawn_egg", "donkey_spawn_egg", "mule_spawn_egg", "skeleton_horse_spawn_egg", "zombie_horse_spawn_egg", "llama_spawn_egg",
                    "trader_llama_spawn_egg", "goat_spawn_egg", "rabbit_spawn_egg", "wolf_spawn_egg", "cat_spawn_egg", "ocelot_spawn_egg", "bee_spawn_egg", "fox_spawn_egg", "panda_spawn_egg",
                    "parrot_spawn_egg", "polar_bear_spawn_egg", "mooshroom_spawn_egg", "turtle_spawn_egg", "salmon_spawn_egg", "cod_spawn_egg", "tropical_fish_spawn_egg", "pufferfish_spawn_egg", "squid_spawn_egg",
                    "glow_squid_spawn_egg", "dolphin_spawn_egg", "axolotl_spawn_egg", "bat_spawn_egg", "strider_spawn_egg", "salmon_bucket", "cod_bucket", "tropical_fish_bucket", "pufferfish_bucket",
                    "axolotl_bucket", "zombie_spawn_egg", "zombie_villager_spawn_egg", "husk_spawn_egg", "drowned_spawn_egg", "skeleton_spawn_egg", "stray_spawn_egg", "creeper_spawn_egg", "spider_spawn_egg",
                    "cave_spider_spawn_egg", "silverfish_spawn_egg", "enderman_spawn_egg", "endermite_spawn_egg", "phantom_spawn_egg", "slime_spawn_egg", "witch_spawn_egg", "pillager_spawn_egg", "vindicator_spawn_egg",
                    "evoker_spawn_egg", "vex_spawn_egg", "ravager_spawn_egg", "guardian_spawn_egg", "elder_guardian_spawn_egg", "magma_cube_spawn_egg", "ghast_spawn_egg", "piglin_spawn_egg", "piglin_brute_spawn_egg",
                    "zombified_piglin_spawn_egg", "hoglin_spawn_egg", "zoglin_spawn_egg", "blaze_spawn_egg", "wither_skeleton_spawn_egg", "shulker_spawn_egg", "stick", "bowl", "splash_potion",
                    "wheat_seeds", "wheat", "beetroot_seeds", "pumpkin_seeds", "melon_seeds", "cocoa_beans", "nether_wart", "popped_chorus_fruit", "sugar",
                    "paper", "book", "coal", "charcoal", "iron_nugget", "raw_iron", "iron_ingot", "raw_copper", "copper_ingot",
                    "gold_nugget", "raw_gold", "gold_ingot", "emerald", "lapis_lazuli", "diamond", "netherite_scrap", "netherite_ingot", "quartz",
                    "amethyst_shard", "flint", "snowball", "clay_ball", "brick", "nether_brick", "prismarine_shard", "prismarine_crystals", "glowstone_dust",
                    "feather", "milk_bucket", "egg", "leather", "rabbit_hide", "rabbit_foot", "ink_sac", "glow_ink_sac", "honeycomb",
                    "scute", "turtle_egg", "bone", "gunpowder", "spider_eye", "ender_pearl", "ender_eye", "phantom_membrane", "slime_ball",
                    "magma_cream", "blaze_rod", "blaze_powder", "fire_charge", "ghast_tear", "dragon_egg", "shulker_shell", "nether_star", "music_disc_13",
                    "music_disc_cat", "music_disc_blocks", "music_disc_chirp", "music_disc_far", "music_disc_mall", "music_disc_mellohi", "music_disc_stal", "music_disc_strad", "music_disc_ward",
                    "music_disc_11", "music_disc_wait", "music_disc_pigstep", "nautilus_shell", "heart_of_the_sea", "experience_bottle", "bundle", "debug_stick", "knowledge_book"
            ).map(Examples::toItemStack).collect(Collectors.toList());
        });
        map.put(ItemGroup.FOOD, () -> {
            return Stream.of(
                    "apple", "golden_apple", "enchanted_golden_apple", "bread", "potato", "poisonous_potato", "baked_potato", "carrot", "golden_carrot",
                    "beetroot", "melon_slice", "sweet_berries", "glow_berries", "chorus_fruit", "dried_kelp", "chicken", "cooked_chicken", "rabbit",
                    "cooked_rabbit", "porkchop", "cooked_porkchop", "mutton", "cooked_mutton", "beef", "cooked_beef", "cod", "cooked_cod",
                    "salmon", "tropical_fish", "cooked_salmon", "pufferfish", "honey_bottle", "milk_bucket", "pumpkin_pie", "cookie", "cake",
                    "beetroot_soup", "mushroom_stew", "rabbit_stew", "suspicious_stew", "rotten_flesh", "spider_eye"
            ).map(Examples::toItemStack).collect(Collectors.toList());
        });
        map.put(ItemGroup.TOOLS, () -> {
            List<ItemStack> tools = Lists.newArrayList(Items.FLINT_AND_STEEL.getDefaultStack(), Items.SHEARS.getDefaultStack());
            tools.add(getEnchantedItem(Items.SHEARS, Enchantments.EFFICIENCY, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.addAll(List.of(Items.LEAD.getDefaultStack(), Items.NAME_TAG.getDefaultStack(), Items.FISHING_ROD.getDefaultStack()));
            tools.add(getEnchantedItem(Items.FISHING_ROD, Enchantments.LURE, Enchantments.LUCK_OF_THE_SEA, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.addAll(List.of(Items.SPYGLASS.getDefaultStack(), Items.COMPASS.getDefaultStack(), Items.CLOCK.getDefaultStack()));
            tools.add(getEnchantedItem(Items.NETHERITE_SHOVEL, Enchantments.EFFICIENCY, Enchantments.SILK_TOUCH, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.add(getEnchantedItem(Items.NETHERITE_SHOVEL, Enchantments.EFFICIENCY, Enchantments.FORTUNE, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.add(getEnchantedItem(Items.NETHERITE_PICKAXE, Enchantments.EFFICIENCY, Enchantments.SILK_TOUCH, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.add(getEnchantedItem(Items.NETHERITE_PICKAXE, Enchantments.EFFICIENCY, Enchantments.FORTUNE, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.add(getEnchantedItem(Items.NETHERITE_AXE, Enchantments.EFFICIENCY, Enchantments.SHARPNESS, Enchantments.SILK_TOUCH, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.add(getEnchantedItem(Items.NETHERITE_AXE, Enchantments.EFFICIENCY, Enchantments.SHARPNESS, Enchantments.FORTUNE, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.add(getEnchantedItem(Items.NETHERITE_HOE, Enchantments.EFFICIENCY, Enchantments.SILK_TOUCH, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.add(getEnchantedItem(Items.NETHERITE_HOE, Enchantments.EFFICIENCY, Enchantments.FORTUNE, Enchantments.UNBREAKING, Enchantments.MENDING));
            tools.addAll(Stream.of(
                    "wooden_shovel",
                    "wooden_pickaxe", "wooden_axe", "wooden_hoe", "stone_shovel", "stone_pickaxe", "stone_axe", "stone_hoe", "golden_shovel", "golden_pickaxe",
                    "golden_axe", "golden_hoe", "iron_shovel", "iron_pickaxe", "iron_axe", "iron_hoe", "diamond_shovel", "diamond_pickaxe", "diamond_axe",
                    "diamond_hoe", "netherite_shovel", "netherite_pickaxe", "netherite_axe", "netherite_hoe"
            ).map(Examples::toItemStack).collect(Collectors.toList()));
            tools.addAll(Stream.of(
                    Enchantments.UNBREAKING, Enchantments.MENDING, Enchantments.EFFICIENCY, Enchantments.SILK_TOUCH, Enchantments.FORTUNE, Enchantments.LURE, Enchantments.LUCK_OF_THE_SEA,
                    Enchantments.VANISHING_CURSE
            ).map(Examples::toEnchantBook).collect(Collectors.toList()));
            return tools;
        });
        map.put(ItemGroup.COMBAT, () -> {
            List<ItemStack> combat = Lists.newArrayList();
            combat.add(getEnchantedItem(Items.NETHERITE_SWORD, Enchantments.SHARPNESS, Enchantments.SWEEPING, Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT, Enchantments.LOOTING, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.TRIDENT, Enchantments.IMPALING, Enchantments.LOYALTY, Enchantments.CHANNELING, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.TRIDENT, Enchantments.IMPALING, Enchantments.RIPTIDE, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.BOW, Enchantments.POWER, Enchantments.PUNCH, Enchantments.FLAME, Enchantments.INFINITY, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.CROSSBOW, Enchantments.QUICK_CHARGE, Enchantments.PIERCING, Enchantments.MULTISHOT, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.SHIELD, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(Items.TOTEM_OF_UNDYING.getDefaultStack());
            combat.add(getEnchantedItem(Items.NETHERITE_HELMET, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.RESPIRATION, Enchantments.AQUA_AFFINITY, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.NETHERITE_CHESTPLATE, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.NETHERITE_LEGGINGS, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.add(getEnchantedItem(Items.NETHERITE_BOOTS, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.FEATHER_FALLING, Enchantments.SOUL_SPEED, Enchantments.DEPTH_STRIDER, Enchantments.FROST_WALKER, Enchantments.UNBREAKING, Enchantments.MENDING));
            combat.addAll(Stream.of(
                    "wooden_sword", "stone_sword", "golden_sword", "iron_sword", "diamond_sword", "netherite_sword", "trident",
                    "bow", "crossbow", "turtle_helmet", "leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots", "chainmail_helmet", "chainmail_chestplate",
                    "chainmail_leggings", "chainmail_boots", "iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots", "diamond_helmet", "diamond_chestplate", "diamond_leggings",
                    "diamond_boots", "golden_helmet", "golden_chestplate", "golden_leggings", "golden_boots", "netherite_helmet", "netherite_chestplate", "netherite_leggings", "netherite_boots"
            ).map(Examples::toItemStack).collect(Collectors.toList()));
            combat.addAll(Stream.of(
                    Enchantments.UNBREAKING, Enchantments.MENDING, Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.FEATHER_FALLING,
                    Enchantments.THORNS, Enchantments.RESPIRATION, Enchantments.AQUA_AFFINITY, Enchantments.SOUL_SPEED, Enchantments.DEPTH_STRIDER, Enchantments.FROST_WALKER, Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS,
                    Enchantments.SWEEPING, Enchantments.KNOCKBACK, Enchantments.FIRE_ASPECT, Enchantments.LOOTING, Enchantments.IMPALING, Enchantments.CHANNELING, Enchantments.LOYALTY, Enchantments.RIPTIDE, Enchantments.POWER,
                    Enchantments.PUNCH, Enchantments.FLAME, Enchantments.INFINITY, Enchantments.QUICK_CHARGE, Enchantments.PIERCING, Enchantments.MULTISHOT, Enchantments.VANISHING_CURSE, Enchantments.BINDING_CURSE
            ).map(Examples::toEnchantBook).collect(Collectors.toList()));
            combat.add(Items.ARROW.getDefaultStack());
            combat.add(Items.SPECTRAL_ARROW.getDefaultStack());
            for (Potion potion : Registry.POTION) {
                if (!potion.getEffects().isEmpty()) {
                    combat.add(toTippedArrow(potion));
                }
            }
            return combat;
        });
        map.put(ItemGroup.BREWING, () -> {
            List<ItemStack> brewing = Stream.of(
                    "brewing_stand", "glass_bottle", "potion", "cauldron", "water_bucket", "blaze_powder", "nether_wart", "gunpowder", "redstone",
                    "glowstone_dust", "dragon_breath", "spider_eye", "fermented_spider_eye", "magma_cream", "pufferfish", "golden_carrot", "glistering_melon_slice", "sugar",
                    "rabbit_foot", "ghast_tear", "turtle_helmet", "phantom_membrane"
            ).map(Examples::toItemStack).collect(Collectors.toCollection(ArrayList::new));
            for (Potion potion : Registry.POTION) {
                if (!potion.getEffects().isEmpty()) {
                    brewing.add(PotionUtil.setPotion(Items.POTION.getDefaultStack(), potion));
                }
            }
            for (Potion potion : Registry.POTION) {
                if (!potion.getEffects().isEmpty()) {
                    brewing.add(PotionUtil.setPotion(Items.SPLASH_POTION.getDefaultStack(), potion));
                }
            }
            for (Potion potion : Registry.POTION) {
                if (!potion.getEffects().isEmpty()) {
                    brewing.add(PotionUtil.setPotion(Items.LINGERING_POTION.getDefaultStack(), potion));
                }
            }
            return brewing;
        });
    }
}
