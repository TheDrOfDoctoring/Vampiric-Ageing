package com.doctor.vampiricageing.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.items.BloodTesterItem;
import com.doctor.vampiricageing.items.TaintedBloodBottleItem;
import com.doctor.vampiricageing.items.TaintedElixirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VampiricAgeing.MODID);

    public static final RegistryObject<BloodTesterItem> BLOOD_TESTER = ITEMS.register("blood_tester", () -> new BloodTesterItem(new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<TaintedBloodBottleItem> TAINTED_BLOOD_BOTTLE_ITEM = ITEMS.register("tainted_blood_bottle", () -> new TaintedBloodBottleItem(new Item.Properties().defaultDurability(5).setNoRepair().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<TaintedElixirItem> TAINTED_ELIXIR_ITEM = ITEMS.register("tainted_elixir", () -> new TaintedElixirItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<Item> TAINTED_CONCENTRATE_ITEM = ITEMS.register("tainted_concentrate", () -> new Item(new Item.Properties().tab(ItemGroup.TAB_MISC)));
}
