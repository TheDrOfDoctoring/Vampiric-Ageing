package com.doctor.vampiricageing;

import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import com.doctor.vampiricageing.capabilities.AgeingCapability;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.client.init.ClientRegistryHandler;
import com.doctor.vampiricageing.command.VampiricAgeingCommands;
import com.doctor.vampiricageing.config.ClientConfig;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import com.doctor.vampiricageing.init.ModItems;
import com.doctor.vampiricageing.networking.ClientProxy;
import com.doctor.vampiricageing.networking.IProxy;
import com.doctor.vampiricageing.networking.Networking;
import com.doctor.vampiricageing.networking.ServerProxy;
import com.doctor.vampiricageing.skills.VampiricAgeingSkills;
import com.mojang.logging.LogUtils;
import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.network.ISyncable;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

@Mod(VampiricAgeing.MODID)
public class VampiricAgeing
{
    public static final String MODID = "vampiricageing";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);


    public VampiricAgeing()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        VampiricAgeingActions.register(modEventBus);
        VampiricAgeingSkills.register(modEventBus);
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientRegistryHandler::init);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::gatherData);
        ModItems.ITEMS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::onCommandsRegister);
    }
    public void onCommandsRegister(@NotNull RegisterCommandsEvent event) {
        VampiricAgeingCommands.registerCommands(event.getDispatcher(), event.getBuildContext());
    }
    public void setup(final FMLCommonSetupEvent event) {
        Networking.registerMessages();
    }
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        EntityTypeTagProvider entityTypeTagProvider = new EntityTypeTagProvider(generator, MODID, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), entityTypeTagProvider);
    }


}
