package com.shutdoor.expandedminer;

import com.shutdoor.expandedminer.enchantment.EnchantExpanded;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.shutdoor.expandedminer.Expanded.MODID;

public class EnchantmentReg {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MODID);

    public static final RegistryObject<Enchantment> EXPANDED = ENCHANTMENTS.register("expanded", () -> new EnchantExpanded());

}
