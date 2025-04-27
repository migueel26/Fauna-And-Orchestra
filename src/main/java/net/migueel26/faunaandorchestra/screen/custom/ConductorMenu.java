package net.migueel26.faunaandorchestra.screen.custom;

import net.migueel26.faunaandorchestra.entity.custom.ConductorEntity;
import net.migueel26.faunaandorchestra.mixins.client.accessors.ClientLevelAccessor;
import net.migueel26.faunaandorchestra.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.UUID;

public class ConductorMenu extends AbstractContainerMenu {
    public final ConductorEntity conductor;
    private final Level level;
    public static ConductorMenu create(int id, Inventory inventory, FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        ConductorEntity conductor = (ConductorEntity) ((ClientLevelAccessor) inventory.player.level()).callGetEntities().get(uuid);
        return new ConductorMenu(id, inventory, conductor);
    }

    public ConductorMenu(int id, Inventory inventory, ConductorEntity conductor) {
        super(ModMenuTypes.CONDUCTOR_MENU.get(), id);
        this.conductor = conductor;
        this.level = inventory.player.level();

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addSlot(new SlotItemHandler(this.conductor.inventory, 0, 116, 30));
    }
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.canInteractWithEntity(conductor, 4.0);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
