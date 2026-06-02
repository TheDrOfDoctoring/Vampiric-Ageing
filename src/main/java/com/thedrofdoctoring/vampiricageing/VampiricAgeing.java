package com.thedrofdoctoring.vampiricageing;

import com.mojang.logging.LogUtils;
import com.thedrofdoctoring.vampiricageing.actions.VampiricAgeingActions;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.AgeingRegistry;
import com.thedrofdoctoring.vampiricageing.capabilities.ageing.methods.*;
import com.thedrofdoctoring.vampiricageing.capabilities.handlers.WerewolfAgeingHandler;
import com.thedrofdoctoring.vampiricageing.client.init.ClientRegistryHandler;
import com.thedrofdoctoring.vampiricageing.command.VampiricAgeingCommands;
import com.thedrofdoctoring.vampiricageing.config.ClientConfig;
import com.thedrofdoctoring.vampiricageing.config.CommonConfig;
import com.thedrofdoctoring.vampiricageing.config.HunterAgeingConfig;
import com.thedrofdoctoring.vampiricageing.config.WerewolvesAgeingConfig;
import com.thedrofdoctoring.vampiricageing.data.AgeingBlockTagsProvider;
import com.thedrofdoctoring.vampiricageing.data.AgeingDataComponents;
import com.thedrofdoctoring.vampiricageing.data.EntityTypeTagProvider;
import com.thedrofdoctoring.vampiricageing.data.ItemTagProvider;
import com.thedrofdoctoring.vampiricageing.data.datamaps.AgeingDatamaps;
import com.thedrofdoctoring.vampiricageing.data.datamaps.AgeingDatamapsProvider;
import com.thedrofdoctoring.vampiricageing.init.ModAttachments;
import com.thedrofdoctoring.vampiricageing.init.ModEffects;
import com.thedrofdoctoring.vampiricageing.init.ModItems;
import com.thedrofdoctoring.vampiricageing.init.ModOils;
import com.thedrofdoctoring.vampiricageing.skills.VampiricAgeingSkills;
import com.thedrofdoctoring.vampiricageing.skills.WerewolfAgeingSkills;
import de.teamlapen.lib.HelperRegistry;
import de.teamlapen.lib.lib.storage.IAttachedSyncable;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

@Mod(VampiricAgeing.MODID)
public class VampiricAgeing
{
    public static final String MODID = "vampiricageing";
    public static final String WEREWOLVES_MODID = "werewolves";
    public static final Logger LOGGER = LogUtils.getLogger();

    public VampiricAgeing(IEventBus modEventBus, ModContainer container)
    {

        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::registerDataMapTypes);
        if(FMLEnvironment.dist == Dist.CLIENT) {
            container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            ClientRegistryHandler.init(modEventBus);

        }
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        VampiricAgeingActions.register(modEventBus);
        VampiricAgeingSkills.register(modEventBus);
        ModItems.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModEffects.EFFECTS.register(modEventBus);
        ModOils.OILS.register(modEventBus);
        AgeingDataComponents.COMPONENTS.register(modEventBus);

        AgeingRegistry.registerAgeType(AgeingReference.VAMP);
        AgeingRegistry.registerAgeType(AgeingReference.HUNTER);


        AgeingRegistry.registerAgeMethod(new BitingMethod());
        AgeingRegistry.registerAgeMethod(new TimeMethod());
        AgeingRegistry.registerAgeMethod(new VampHuntingMethod());
        AgeingRegistry.registerAgeMethod(new HunterHuntingMethod());
        AgeingRegistry.registerAgeMethod(new DrinkBloodMethod());


        container.registerConfig(ModConfig.Type.COMMON, HunterAgeingConfig.HUNTER_AGEING_CONFIG, MODID+"-hunterAgeing.toml");
        if(ModList.get().isLoaded(WEREWOLVES_MODID)) {
            AgeingRegistry.registerAgeType(AgeingReference.WEREWOLF);
            AgeingRegistry.registerAgeMethod(new DevourMethod());

            container.registerConfig(ModConfig.Type.COMMON, WerewolvesAgeingConfig.WEREWOLF_AGEING_CONFIG,MODID+"-werewolfAgeing.toml");
            NeoForge.EVENT_BUS.register(new WerewolfAgeingHandler());
            WerewolfAgeingSkills.register(modEventBus);
        }
        NeoForge.EVENT_BUS.addListener(this::onCommandsRegister);
    }
    public void onCommandsRegister(@NotNull RegisterCommandsEvent event) {
        VampiricAgeingCommands.registerCommands(event.getDispatcher(), event.getBuildContext());
    }

    private void gatherData(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        EntityTypeTagProvider entityTypeTagProvider = new EntityTypeTagProvider(packOutput, lookupProvider, existingFileHelper);
        AgeingBlockTagsProvider provider = new AgeingBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        ItemTagProvider itemTagProvider = new ItemTagProvider(packOutput, lookupProvider, provider, existingFileHelper);
        generator.addProvider(event.includeServer(), new AgeingDatamapsProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), provider);
        generator.addProvider(event.includeServer(), itemTagProvider);
        generator.addProvider(event.includeServer(), entityTypeTagProvider);
    }
    private void enqueueIMC(final InterModEnqueueEvent event) {
        HelperRegistry.registerSyncablePlayerCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.AGEING_MANAGER.get(), AgeingManager.class);
        HelperRegistry.registerSyncableEntityCapability((AttachmentType<IAttachedSyncable>) (Object) ModAttachments.AGEING_MANAGER.get(), AgeingManager.class);
    }

    public void registerDataMapTypes(final RegisterDataMapTypesEvent event) {
        event.register(AgeingDatamaps.AGE_ITEM_RESTRICTION);
    }


    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(VampiricAgeing.MODID, path);
    }
}
