package com.example.addon.modules;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoWither extends Module {
    public int ticks;
    private final BlockPos.Mutable pPos = new BlockPos.Mutable();
    private final BlockPos.Mutable[] placePositions = new BlockPos.Mutable[15];

    public AutoWither(String name, String description) {
        super(Categories.Misc, name, description);
        for (int i = 0; i < placePositions.length; i++) {
            placePositions[i] = new BlockPos.Mutable();
        }
    }

    private boolean checkPos() {
        boolean isAble = true;
        for (BlockPos.Mutable placePosition : placePositions) {
            if (!BlockUtils.canPlace(placePosition)) isAble = false;
            if (!this.mc.world.getBlockState(placePosition).isAir()) isAble = false;
        };
        return isAble;
    }
    private void placeBlocks() {
        this.pPos.set(this.mc.player.getBlockPos());
        int[][] symbolOffsets = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 1, 1},
            {0, 1, -1},
            {0, 2, 0},
            {0, 2, 1},
            {0, 2, -1},
        };

        Direction facing = this.mc.player.getHorizontalFacing();

        int baseX = this.pPos.getX();
        int baseY = this.pPos.getY();
        int baseZ = this.pPos.getZ();

        switch (facing) {
            case EAST -> baseX += 2;  // +X
            case WEST -> baseX -= 2;  // -X
            case NORTH -> baseZ -= 2; // -Z
            case SOUTH -> baseZ += 2; // +Z
        }

        for (int i = 0; i < symbolOffsets.length; i++) {
            int xOffset = symbolOffsets[i][0];
            int yOffset = symbolOffsets[i][1];
            int zOffset = symbolOffsets[i][2];

            switch (facing) {
                case EAST, WEST:
                    if (facing == Direction.WEST) {
                        xOffset = -xOffset;
                    }
                    break;
                case NORTH, SOUTH:
                    int temp = xOffset;
                    xOffset = (facing == Direction.NORTH) ? -zOffset : zOffset;
                    zOffset = temp;
                    break;
            }

            this.placePositions[i].set(
                baseX + xOffset,
                baseY + yOffset,
                baseZ + zOffset
            );
        }
    }
    public void onActivate() {
        if (this.mc.world != null && this.mc.player != null) {
            FindItemResult result0 = this.getInvBlock(Items.SOUL_SAND);
            FindItemResult result1 = this.getInvBlock(Items.WITHER_SKELETON_SKULL);
            if (result0 != null && result1 != null) {
                    placeBlocks();
                this.ticks = 0;
            }
        } else {
            this.toggle();
        }
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (!(event.entity instanceof WitherEntity)) return;
        if (InvUtils.findInHotbar(Items.NAME_TAG) == null) {
            this.toggle();
            return;
        }
        //ItemStack currentStack = mc.player.getMainHandStack();
        InvUtils.swap(InvUtils.findInHotbar(Items.NAME_TAG).slot(), true);
        mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(event.entity, mc.player.isSneaking(), Hand.MAIN_HAND));
        InvUtils.swapBack();
        this.toggle();

    }
    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (this.ticks == 0) {
            boolean isAble = checkPos();
            if (!isAble) {
                for (BlockPos.Mutable placePosition : placePositions) {
                    placePosition.set(placePosition.getX(), placePosition.getY()+1, placePosition.getZ());
                };
                isAble = checkPos();
                if (!isAble){
                    this.toggle();
                    return;
                }
            }
            BlockUtils.place(this.placePositions[0], this.getInvBlock(Items.SOUL_SAND), false, 100, true);
            ++this.ticks;
        } else if (this.ticks == 1) {
            if (!this.mc.world.getBlockState(this.placePositions[0]).isAir()) {
                BlockUtils.place(this.placePositions[1], this.getInvBlock(Items.SOUL_SAND), false, 100, true);
                ++this.ticks;
            } else {
                --this.ticks;
            }

        } else if (this.ticks == 2) {
            if (!this.mc.world.getBlockState(this.placePositions[1]).isAir()) {
                BlockUtils.place(this.placePositions[2], this.getInvBlock(Items.SOUL_SAND), false, 100, true);
                ++this.ticks;
            } else {
                --this.ticks;
            }

        } else if (this.ticks == 3) {
            if (!this.mc.world.getBlockState(this.placePositions[2]).isAir()) {
                BlockUtils.place(this.placePositions[3], this.getInvBlock(Items.SOUL_SAND), false, 100, true);
                ++this.ticks;
            } else {
                --this.ticks;
            }

        } else if (this.ticks == 4) {
            if (!this.mc.world.getBlockState(this.placePositions[3]).isAir()) {
                BlockUtils.place(this.placePositions[4], this.getInvBlock(Items.WITHER_SKELETON_SKULL), false, 100, true);
                ++this.ticks;
            } else {
                --this.ticks;
            }

        } else if (this.ticks == 5) {
            if (!this.mc.world.getBlockState(this.placePositions[4]).isAir()) {
                BlockUtils.place(this.placePositions[5], this.getInvBlock(Items.WITHER_SKELETON_SKULL), false, 100, true);
                ++this.ticks;
            } else {
                --this.ticks;
            }

        } else if (this.ticks == 6) {
            if (!this.mc.world.getBlockState(this.placePositions[5]).isAir()) {
                BlockUtils.place(this.placePositions[6], this.getInvBlock(Items.WITHER_SKELETON_SKULL), false, 100, true);
                ++this.ticks;
            } else {
                --this.ticks;
            }
        } else if (this.ticks == 7) {
            if (!this.mc.world.getBlockState(this.placePositions[6]).isAir()) {
                //this.toggle();
            } else {
                --this.ticks;
            }
        }
    }

    private FindItemResult getInvBlock(Item item) {
        return InvUtils.findInHotbar(item);
    }
}
