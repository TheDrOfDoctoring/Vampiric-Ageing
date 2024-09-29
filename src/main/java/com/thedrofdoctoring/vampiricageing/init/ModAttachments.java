package com.thedrofdoctoring.vampiricageing.init;

import com.thedrofdoctoring.vampiricageing.VampiricAgeing;
import com.thedrofdoctoring.vampiricageing.capabilities.AgeingManager;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, VampiricAgeing.MODID);
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<AgeingManager>> AGEING_MANAGER = ATTACHMENT_TYPES.register(AgeingManager.AGEING_KEY.getPath(), () -> AttachmentType.builder(new AgeingManager.Factory()).serialize(new AgeingManager.Serializer()).copyOnDeath().build());

}
