package de.maxhenkel.corpse.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.maxhenkel.corelib.inventory.ScreenBase;
import de.maxhenkel.corpse.Main;
import de.maxhenkel.corpse.entities.CorpseEntity;
import de.maxhenkel.corpse.net.MessageTransferItems;
import de.maxhenkel.corpse.net.MessageSwitchInventoryPage;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;

public class CorpseScreen extends ScreenBase<CorpseContainer> {

    public static final ResourceLocation CORPSE_GUI_TEXTURE = new ResourceLocation(Main.MODID, "textures/gui/gui_corpse.png");

    private static final int PADDING = 7;

    private PlayerInventory playerInventory;
    private CorpseEntity corpse;

    private Button previous;
    private Button next;

    private int page;

    public CorpseScreen(CorpseEntity corpse, PlayerInventory playerInventory, CorpseContainer container, ITextComponent title) {
        super(CORPSE_GUI_TEXTURE, container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.corpse = corpse;
        this.page = 0;

        xSize = 176;
        ySize = 248;
    }

    @Override
    protected void func_231160_c_() {
        super.func_231160_c_();

        field_230710_m_.clear();
        int left = (field_230708_k_ - xSize) / 2;
        int buttonWidth = 50;
        int buttonHeight = 20;
        previous = func_230480_a_(new Button(left + PADDING, guiTop + 149 - buttonHeight, buttonWidth, buttonHeight, new TranslationTextComponent("button.corpse.previous"), button -> {
            page--;
            if (page < 0) {
                page = 0;
            }
            Main.SIMPLE_CHANNEL.sendToServer(new MessageSwitchInventoryPage(page));
        }));
        next = func_230480_a_(new Button(left + xSize - buttonWidth - PADDING, guiTop + 149 - buttonHeight, buttonWidth, buttonHeight, new TranslationTextComponent("button.corpse.next"), button -> {
            page++;
            if (page >= getPages()) {
                page = getPages() - 1;
            }
            Main.SIMPLE_CHANNEL.sendToServer(new MessageSwitchInventoryPage(page));
        }));

        func_230480_a_(new TransferItemsButton(left + xSize - TransferItemsButton.WIDTH - 9, guiTop + 5, button -> {
            Main.SIMPLE_CHANNEL.sendToServer(new MessageTransferItems());
        }));
    }

    @Override
    public void func_231023_e_() {
        super.func_231023_e_();
        if (page <= 0) {
            previous.field_230693_o_ = false;
        } else {
            previous.field_230693_o_ = true;
        }

        if (page >= getPages() - 1) {
            next.field_230693_o_ = false;
        } else {
            next.field_230693_o_ = true;
        }
    }

    private int getPages() {
        return corpse.getSizeInventory() / 54;
    }

    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);

        field_230712_o_.func_238421_b_(matrixStack, corpse.getDisplayName().getString(), guiLeft + 7, guiTop + 7, FONT_COLOR);
        field_230712_o_.func_238421_b_(matrixStack, playerInventory.getDisplayName().getString(), guiLeft + 7, guiTop + ySize - 96 + 2, FONT_COLOR);

        String pageName = new TranslationTextComponent("gui.corpse.page", page + 1, getPages()).getString();
        int pageWidth = field_230712_o_.getStringWidth(pageName);
        field_230712_o_.func_238421_b_(matrixStack, pageName, guiLeft + xSize / 2 - pageWidth / 2, guiTop + ySize - 113, FONT_COLOR);
    }

    @Override
    protected void func_230451_b_(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.func_230451_b_(matrixStack, mouseX, mouseY);

        if (mouseX >= guiLeft + xSize - TransferItemsButton.WIDTH - 9 && mouseX < guiLeft + xSize - 9 && mouseY >= guiTop + 5 && mouseY < guiTop + 5 + TransferItemsButton.HEIGHT) {
            func_238654_b_(matrixStack, Collections.singletonList(new TranslationTextComponent("tooltip.corpse.transfer_items").func_241878_f()), mouseX - guiLeft, mouseY - guiTop);
        }
    }
}
