package dev.tr7zw.exordium.components.vanilla;

import dev.tr7zw.exordium.components.BufferComponent;
import dev.tr7zw.util.NMSHelper;
import lombok.Getter;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//#if MC >= 12104
import net.minecraft.core.component.DataComponents;
//#endif

public class HotbarComponent implements BufferComponent<Void> {

    private static final Minecraft minecraft = Minecraft.getInstance();
    @Getter
    private static final ResourceLocation id = NMSHelper.getResourceLocation("minecraft", "hotbar");

    private float lastAttackState = 0;
    private final Object[] hotbarModels = new Object[10];
    private final int[] itemPopAnimation = new int[10];
    private final int[] itemAmount = new int[10];
    private final int[] itemDurability = new int[10];
    private int selectedSlot = 0;
    private boolean hasEnchantedItem = false;
    private boolean cooldownActive = false;

    @Override
    public void captureState(Void context) {
        hasEnchantedItem = false;
        cooldownActive = false;
        lastAttackState = minecraft.player.getAttackStrengthScale(0.0F);
        Player player = getCameraPlayer();
        if (player == null)
            return;
        for (int m = 0; m < 9; m++) {
            store((player.getInventory()).items.get(m), m, player);
        }
        store(player.getOffhandItem(), 9, player);
        selectedSlot = player.getInventory().selected;
    }

    @Override
    public boolean hasChanged(Void context) {
        if (minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            float g = minecraft.player.getAttackStrengthScale(0.0F);
            if (g != lastAttackState) {
                return true;
            }
        }
        if (hasEnchantedItem || cooldownActive) {
            return true;
        }
        Player player = getCameraPlayer();
        if (player == null)
            return true;
        if (selectedSlot != player.getInventory().selected) {
            return true;
        }
        for (int m = 0; m < 9; m++) {
            ItemStack item = (player.getInventory()).items.get(m);
            if (hasChanged(item, m, player)) {
                return true;
            }
        }
        if (hasChanged(player.getOffhandItem(), 9, player)) {
            return true;
        }

        return false;
    }

    private void store(ItemStack item, int id, Player player) {
        if (item != null && !item.isEmpty()) {
            hotbarModels[id] = getModel(item, player);
            itemPopAnimation[id] = item.getPopTime();
            itemAmount[id] = item.getCount();
            itemDurability[id] = item.getDamageValue();
            if (item.isEnchanted()) {
                this.hasEnchantedItem = true;
            }
            //#if MC >= 12102
            if (player.getCooldowns().isOnCooldown(item)) {
                //#else
                //$$if (player.getCooldowns().isOnCooldown(item.getItem())) {
                //#endif
                this.cooldownActive = true;
            }
        } else {
            hotbarModels[id] = null;
            itemPopAnimation[id] = 0;
            itemAmount[id] = 0;
            itemDurability[id] = -1;
        }
    }

    private boolean hasChanged(ItemStack item, int id, Player player) {
        if (item != null && !item.isEmpty()) {
            if (itemAmount[id] != item.getCount()) {
                return true;
            }
            if (itemPopAnimation[id] != item.getPopTime()) {
                return true;
            }
            if (itemDurability[id] != item.getDamageValue()) {
                return true;
            }
            if (getModel(item, player) != hotbarModels[id]) {
                return true;
            }
        } else if (hotbarModels[id] != null) {
            return true;
        }
        return false;
    }

    private Player getCameraPlayer() {
        Entity var2 = minecraft.getCameraEntity();
        return var2 instanceof Player player ? player : null;
    }

    private Object getModel(ItemStack itemStack, Player player) {
        //#if MC >= 12104
        //        return Math.random(); // FIXME The new ItemModel class/ItemStackRenderState logic is incompatible with all of this
        ResourceLocation resourceLocation = (ResourceLocation) itemStack.get(DataComponents.ITEM_MODEL);
        if (resourceLocation != null) {
            return minecraft.getModelManager().getItemModel(resourceLocation);
        } else {
            return null;
        }
        //#else
        //$$ return minecraft.getItemRenderer().getModel(itemStack, player.level(), player, 0);
        //#endif
    }

}
