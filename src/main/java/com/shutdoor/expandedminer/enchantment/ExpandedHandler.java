package com.shutdoor.expandedminer.enchantment;

import com.shutdoor.expandedminer.EnchantmentReg;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ExpandedHandler {

    @SubscribeEvent()
    public static void ExpandedBreaking(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!(player instanceof ServerPlayerEntity)) return;

        World world = event.getPlayer().getCommandSenderWorld();

        if (!world.isClientSide) {
            if (player != null) {
                ItemStack tool = player.getMainHandItem();

                int enchantmentLevel = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentReg.EXPANDED.get(), tool);
                int lvlFort = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
                int lvlSilk = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool);

                if (enchantmentLevel > 0) {
                    if (player.swingingArm == null) return;
                    if (tool.getItem() instanceof PickaxeItem || tool.getToolTypes().contains(ToolType.PICKAXE)) {
                        pickAxeEnchantHandler(player, enchantmentLevel, lvlFort, lvlSilk, tool, event, world);
                    } else if (tool.getItem() instanceof ShovelItem || tool.getToolTypes().contains(ToolType.SHOVEL)) {
                        shovelEnchantHandler(player, enchantmentLevel, lvlFort, lvlSilk, tool, event, world);
                    }
                }
            }
        }
    }

    private static Direction blockRay(PlayerEntity player){
        Direction facing = Direction.UP;

        RayTraceResult blockRay = player.pick(5, 0, false);
        if (blockRay.getType() == RayTraceResult.Type.BLOCK) {
            ItemUseContext context = new ItemUseContext(player, player.getUsedItemHand(), ((BlockRayTraceResult) blockRay));
            BlockRayTraceResult res = new BlockRayTraceResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), false);
            facing = res.getDirection();
        }

        return facing;
    }

    private static void pickAxeEnchantHandler(PlayerEntity player, int enchantmentLevel, int lvlFort, int lvlSilk, ItemStack tool, BlockEvent.BreakEvent event, World world) {
        ToolItem toolItem = (ToolItem) tool.getItem();
        Direction facing = blockRay(player);
        ToolType toolType = ToolType.PICKAXE;

        int xMin = 0;
        int xMax = 0;
        int zMin = 0;
        int zMax = 0;
        int yMin = 0;
        int yMax = 0;

        if (facing.equals(Direction.UP) || facing.equals(Direction.DOWN)) {
            zMin = -enchantmentLevel;
            zMax = +enchantmentLevel;
            xMin = -enchantmentLevel;
            xMax = +enchantmentLevel;
            yMin = 0;
            yMax = 0;
        } else if (facing.equals(Direction.SOUTH) || facing.equals(Direction.NORTH)) {
            zMin = 0;
            zMax = 0;
            xMin = -enchantmentLevel;
            xMax = +enchantmentLevel;
            yMin = -1;
            yMax = ((2 * enchantmentLevel) - 1);
        } else if (facing.equals(Direction.WEST) || facing.equals(Direction.EAST)) {
            zMin = -enchantmentLevel;
            zMax = +enchantmentLevel;
            xMin = 0;
            xMax = 0;
            yMin = -1;
            yMax = ((2 * enchantmentLevel) - 1);
        }

        List<BlockPos> blockBreakQueue = new ArrayList<>();
        BlockPos pos = new BlockPos(event.getPos());

//        System.out.println(world.getBlockState(pos).getHarvestLevel() + "~~~~");

        if (toolItem.isCorrectToolForDrops(world.getBlockState(pos))) {
            for (int c = zMin; c <= zMax; c++) {
                for (int a = xMin; a <= xMax; a++) {
                    for (int b = yMin; b <= yMax; b++) {
                        blockBreakQueue.add(pos.offset(a, b, c));
                    }
                }
            }

            for (int i = 0; i < blockBreakQueue.size(); i++) {
                if (world.getBlockEntity(blockBreakQueue.get(i)) instanceof TileEntity) continue;

                BlockState blockToBreak = world.getBlockState(blockBreakQueue.get(i));
                if (!blockToBreak.getBlock().isAir(blockToBreak, world, blockBreakQueue.get(i))) {
//                    System.out.println(blockToBreak.getHarvestTool().getName().toString());
                    if (blockToBreak.getHarvestLevel() != -1) {
                        if (blockToBreak.canHarvestBlock(world, blockBreakQueue.get(i), player)) {
                            if (toolItem.isCorrectToolForDrops(blockToBreak)) {
                                if (((ServerPlayerEntity) player).gameMode.isSurvival()) {
                                    blockToBreak.getBlock().playerDestroy(world, player, blockBreakQueue.get(i), blockToBreak, null, tool);
                                    world.destroyBlock(blockBreakQueue.get(i), false);

                                    int expDrop = blockToBreak.getBlock().getExpDrop(blockToBreak, world, blockBreakQueue.get(i), lvlFort, lvlSilk);
                                    player.giveExperiencePoints(expDrop);
                                } else {
                                    world.destroyBlock(blockBreakQueue.get(i), false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void shovelEnchantHandler(PlayerEntity player, int enchantmentLevel, int lvlFort, int lvlSilk, ItemStack tool, BlockEvent.BreakEvent event, World world) {
        ToolItem toolItem = (ToolItem) tool.getItem();
        Direction facing = blockRay(player);
        ToolType toolType = ToolType.SHOVEL;

        int xMin = 0;
        int xMax = 0;
        int zMin = 0;
        int zMax = 0;
        int yMin = 0;
        int yMax = 0;

        if (facing.equals(Direction.UP) || facing.equals(Direction.DOWN)) {
            zMin = -enchantmentLevel;
            zMax = +enchantmentLevel;
            xMin = -enchantmentLevel;
            xMax = +enchantmentLevel;
            yMin = 0;
            yMax = 0;
        } else if (facing.equals(Direction.SOUTH) || facing.equals(Direction.NORTH)) {
            zMin = 0;
            zMax = 0;
            xMin = -enchantmentLevel;
            xMax = +enchantmentLevel;
            yMin = -1;
            yMax = ((2 * enchantmentLevel) - 1);
        } else if (facing.equals(Direction.WEST) || facing.equals(Direction.EAST)) {
            zMin = -enchantmentLevel;
            zMax = +enchantmentLevel;
            xMin = 0;
            xMax = 0;
            yMin = -1;
            yMax = ((2 * enchantmentLevel) - 1);
        }

        List<BlockPos> blockBreakQueue = new ArrayList<>();
        BlockPos pos = new BlockPos(event.getPos());
        if (world.getBlockState(pos).isToolEffective(toolType)) {
            for (int c = zMin; c <= zMax; c++) {
                for (int a = xMin; a <= xMax; a++) {
                    for (int b = yMin; b <= yMax; b++) {
                        blockBreakQueue.add(pos.offset(a, b, c));
                    }
                }
            }

            for (int i = 0; i < blockBreakQueue.size(); i++) {
                if (world.getBlockEntity(blockBreakQueue.get(i)) instanceof TileEntity) continue;

                BlockState blockToBreak = world.getBlockState(blockBreakQueue.get(i));
                if (!blockToBreak.getBlock().isAir(blockToBreak, world, blockBreakQueue.get(i))) {
                    if (blockToBreak.isToolEffective(toolType)) {
                        if (blockToBreak.canHarvestBlock(world, blockBreakQueue.get(i), player)) {
                                if (((ServerPlayerEntity) player).gameMode.isSurvival()) {
                                    blockToBreak.getBlock().playerDestroy(world, player, blockBreakQueue.get(i), blockToBreak, null, tool);
                                    world.destroyBlock(blockBreakQueue.get(i), false);

                                    int expDrop = blockToBreak.getBlock().getExpDrop(blockToBreak, world, blockBreakQueue.get(i), lvlFort, lvlSilk);
                                    player.giveExperiencePoints(expDrop);
                                } else {
                                    world.destroyBlock(blockBreakQueue.get(i), false);
                                }

                        }
                    }
                }
            }
        }
    }
}
