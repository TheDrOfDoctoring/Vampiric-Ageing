package com.doctor.vampiricageing.init;

import com.doctor.vampiricageing.VampiricAgeing;
import com.doctor.vampiricageing.items.BloodTesterItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, VampiricAgeing.MODID);

    public static final RegistryObject<BloodTesterItem> BLOOD_TESTER = ITEMS.register("blood_tester", () -> new BloodTesterItem(new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
}
