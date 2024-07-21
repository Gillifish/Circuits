package org.gillinet;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static void initialize() {}

    public static Item register(Item item, String id) {
        Identifier itemID = Identifier.of(Circuits.MOD_ID, id);
        return Registry.register(Registries.ITEM, itemID, item);
    }
}
