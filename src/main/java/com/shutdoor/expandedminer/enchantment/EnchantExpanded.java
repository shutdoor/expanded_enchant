package com.shutdoor.expandedminer.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.ShovelItem;
import net.minecraftforge.common.ToolType;

public class EnchantExpanded extends Enchantment
{
    public EnchantExpanded() {
        super(Rarity.VERY_RARE, EnchantmentType.DIGGER, new EquipmentSlotType[]{
                EquipmentSlotType.MAINHAND
        });
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 10 + 20 * (enchantmentLevel - 1);
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return super.getMinCost(enchantmentLevel) + 50;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        if(stack.getItem() instanceof PickaxeItem || stack.getToolTypes().contains(ToolType.PICKAXE) ||
                stack.getItem() instanceof ShovelItem || stack.getToolTypes().contains(ToolType.SHOVEL))
        {
            return (stack.getItem() instanceof PickaxeItem || stack.getToolTypes().contains(ToolType.PICKAXE) ||
                    stack.getItem() instanceof ShovelItem || stack.getToolTypes().contains(ToolType.SHOVEL));
        }
        return false;
    }

}


