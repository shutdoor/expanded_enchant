package com.shutdoor.expandedminer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static com.shutdoor.expandedminer.Expanded.MODID;

@Mod(MODID)
public class Expanded {

    public static final String MODID = "expanded";

    public Expanded() {
        IEventBus modEventBus  = FMLJavaModLoadingContext.get().getModEventBus(), forgeEventBus = MinecraftForge.EVENT_BUS;
        EnchantmentReg.ENCHANTMENTS.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }
}
