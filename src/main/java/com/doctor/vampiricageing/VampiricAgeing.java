package com.doctor.vampiricageing;

import com.doctor.vampiricageing.actions.VampiricAgeingActions;
import com.doctor.vampiricageing.capabilities.VampiricAgeingCapabilityManager;
import com.doctor.vampiricageing.capabilities.WerewolfAgeingManager;
import com.doctor.vampiricageing.client.init.ClientRegistryHandler;
import com.doctor.vampiricageing.command.VampiricAgeingCommands;
import com.doctor.vampiricageing.config.ClientConfig;
import com.doctor.vampiricageing.config.CommonConfig;
import com.doctor.vampiricageing.config.HunterAgeingConfig;
import com.doctor.vampiricageing.config.WerewolvesAgeingConfig;
import com.doctor.vampiricageing.data.EntityTypeTagProvider;
import com.doctor.vampiricageing.data.ItemTagProvider;
import com.doctor.vampiricageing.init.ModEffects;
import com.doctor.vampiricageing.init.ModItems;
import com.doctor.vampiricageing.init.ModOils;
import com.doctor.vampiricageing.networking.ClientProxy;
import com.doctor.vampiricageing.networking.IProxy;
import com.doctor.vampiricageing.networking.Networking;
import com.doctor.vampiricageing.networking.ServerProxy;
import com.doctor.vampiricageing.skills.VampiricAgeingSkills;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VampiricAgeing.MODID)
public class VampiricAgeing
{
    public static final String MODID = "vampiricageing";
    public static final String WEREWOLVES_MODID = "werewolves";
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
        modEventBus.addListener(this::processIMC);
        ModItems.ITEMS.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModOils.OILS.register(modEventBus);
        MinecraftForge.EVENT_BUS.addListener(this::onCommandsRegister);
        ModConfig hunterAgeingConfig = new ModConfig(ModConfig.Type.COMMON, HunterAgeingConfig.HUNTER_AGEING_CONFIG, ModLoadingContext.get().getActiveContainer(), MODID+"-hunterAgeing.toml");
        ModLoadingContext.get().getActiveContainer().addConfig(hunterAgeingConfig);
        if(ModList.get().isLoaded(WEREWOLVES_MODID)) {
            ModConfig werewolfAgeingConfig = new ModConfig(ModConfig.Type.COMMON, WerewolvesAgeingConfig.WEREWOLF_AGEING_CONFIG, ModLoadingContext.get().getActiveContainer(), MODID+"-werewolfAgeing.toml");
            ModLoadingContext.get().getActiveContainer().addConfig(werewolfAgeingConfig);
        }
    }
    public void onCommandsRegister(RegisterCommandsEvent event) {
        VampiricAgeingCommands.registerCommands(event.getDispatcher());
    }
        public void setup(final FMLCommonSetupEvent event) {
        Networking.registerMessages();
    }
    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        EntityTypeTagProvider entityTypeTagProvider = new EntityTypeTagProvider(generator, MODID, event.getExistingFileHelper());
        generator.addProvider(new ItemTagProvider(generator, new BlockTagsProvider(generator, MODID, event.getExistingFileHelper()), MODID, event.getExistingFileHelper()));
        generator.addProvider(entityTypeTagProvider);
    }
    private void processIMC(final InterModProcessEvent event) {
        if(ModList.get().isLoaded(WEREWOLVES_MODID)) {
            MinecraftForge.EVENT_BUS.register(new WerewolfAgeingManager());
        }
        VampiricAgeingCapabilityManager.registerCapability();
    }


}
