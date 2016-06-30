package kihira.tails.client.gui.controls;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class NumberInput extends Gui {

    private int xPos;
    private int yPos;

    private final int width = 35;
    private final int height = 20;
    private final int btnWidth = 10;

    private GuiTextField numInput;

    public NumberInput(int x, int y) {
        this.xPos = x;
        this.yPos = y;

        numInput = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, xPos, yPos+1, width-btnWidth-2, height-2);

    }

    public void draw(int mouseX, int mouseY) {
        numInput.drawTextBox();
        drawRect(xPos+numInput.width, yPos, xPos+numInput.width+btnWidth, yPos+height/2, 0xFFFFFFFF);
        drawRect(xPos+numInput.width, yPos+height/2, xPos+numInput.width+btnWidth, yPos+height, 0xAAAAAAFF);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        numInput.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void keyTyped(char key, int keycode) {
        numInput.textboxKeyTyped(key, keycode);
    }
}
