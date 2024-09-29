package com.thedrofdoctoring.vampiricageing.init;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.items.BloodTesterItem;
import com.thedrofdoctoring.vampiricageing.items.TaintedBloodBottleItem;
import com.thedrofdoctoring.vampiricageing.items.TaintedElixirItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ModItems {
    private static final Set<DeferredHolder<Item, ? extends Item>> creativeTabItems = new HashSet<>();
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, VampiricAgeing.MODID);

    public static final DeferredHolder<Item, BloodTesterItem> BLOOD_TESTER = register("blood_tester", () -> new BloodTesterItem(new Item.Properties()));
    public static final DeferredHolder<Item, TaintedBloodBottleItem> TAINTED_BLOOD_BOTTLE_ITEM = register("tainted_blood_bottle", () -> new TaintedBloodBottleItem(new Item.Properties().durability(5).setNoRepair()));
    public static final DeferredHolder<Item, TaintedElixirItem> TAINTED_ELIXIR_ITEM = register("tainted_elixir", () -> new TaintedElixirItem(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> TAINTED_CONCENTRATE_ITEM = register("tainted_concentrate", () -> new Item(new Item.Properties()));

    public static final ResourceKey<CreativeModeTab> CREATIVE_MODE_TABS = ResourceKey.create(Registries.CREATIVE_MODE_TAB, VampiricAgeing.rl("vampiric_ageing_tab"));
    public static final DeferredRegister<CreativeModeTab> AGEING_CREATIVE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, VampiricAgeing.MODID);

    private static <T extends Item> DeferredHolder<Item, T> register(final String id, final Supplier<? extends T> itemSupplier) {
        DeferredHolder<Item, T> item = ITEMS.register(id, itemSupplier);
        creativeTabItems.add(item);
        return item;
    }
    public static void register(IEventBus bus) {
        ITEMS.register(bus);
        AGEING_CREATIVE_TAB.register(bus);
    }
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AGEING_TAB = AGEING_CREATIVE_TAB.register(CREATIVE_MODE_TABS.location().getPath(), () -> CreativeModeTab.builder().displayItems(
                    (pParameters, pOutput) -> creativeTabItems.forEach(item -> pOutput.accept(item.get())))
            .title(Component.translatable("itemGroup." + VampiricAgeing.MODID))
            .icon(() -> new ItemStack(TAINTED_ELIXIR_ITEM.get()))
            .build()
    );
}
