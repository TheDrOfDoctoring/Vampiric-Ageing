package com.doctor.vampiricageing.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.items.BloodTesterItem;
import com.doctor.vampiricageing.items.TaintedBloodBottleItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VampiricAgeing.MODID);

    public static final RegistryObject<BloodTesterItem> BLOOD_TESTER = ITEMS.register("blood_tester", () -> new BloodTesterItem(new Item.Properties()));
    public static final RegistryObject<TaintedBloodBottleItem> TAINTED_BLOOD_BOTTLE_ITEM = ITEMS.register("tainted_blood_bottle", () -> new TaintedBloodBottleItem(new Item.Properties().defaultDurability(5).setNoRepair()));
    public static void creativeTabItems(BuildCreativeModeTabContentsEvent event) {
        event.accept(BLOOD_TESTER);
        event.accept(TAINTED_BLOOD_BOTTLE_ITEM);
    }
}
