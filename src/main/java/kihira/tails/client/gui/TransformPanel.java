package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiSlider;
import kihira.foxlib.client.gui.IControl;
import kihira.foxlib.client.gui.IControlCallback;
import kihira.tails.client.gui.controls.NumberInput;
import net.minecraft.client.resources.I18n;

import java.io.IOException;

public class TransformPanel extends Panel<GuiEditor> implements IControlCallback<IControl<Float>, Float>{

    private GuiSlider rotXSlider;
    private GuiSlider rotYSlider;
    private GuiSlider rotZSlider;
    private NumberInput xPosInput;
    private NumberInput yPosInput;
    private NumberInput zPosInput;

    private final int spacing = 25;
    private final int topOffset = 10;

    TransformPanel(GuiEditor parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {
        buttonList.add(rotXSlider = new GuiSlider(this, 0, 0, topOffset, 100, -180, 180, parent.originalPartInfo.rot[0], "X Rotation"));
        buttonList.add(rotYSlider = new GuiSlider(this, 1, 0, topOffset+spacing, 100, -180, 180, parent.originalPartInfo.rot[1], "Y Rotation"));
        buttonList.add(rotZSlider = new GuiSlider(this, 2, 0, topOffset+spacing*2, 100, -180, 180, parent.originalPartInfo.rot[2], "Z Rotation"));

        xPosInput = new NumberInput(0, topOffset+spacing*4, -2, 2, 0.1f, this);
        yPosInput = new NumberInput(0, topOffset+spacing*5, -2, 2, 0.1f);
        zPosInput = new NumberInput(0, topOffset+spacing*6, -2, 2, 0.1f);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        zLevel = -100;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);
        zLevel = 0;

        xPosInput.draw(mouseX, mouseY);
        yPosInput.draw(mouseX, mouseY);
        zPosInput.draw(mouseX, mouseY);

        // Labels
        int topOffset = this.topOffset;
        for (int label = 0; label < 3; label++) {
            String s = I18n.format("gui.label." + label, label);
            fontRendererObj.drawString(s, 5, topOffset, 0xFFFFFF);
            topOffset += spacing;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        xPosInput.mouseClicked(mouseX, mouseY, mouseButton);
        yPosInput.mouseClicked(mouseX, mouseY, mouseButton);
        zPosInput.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char key, int keycode) {
        xPosInput.keyTyped(key, keycode);
        yPosInput.keyTyped(key, keycode);
        zPosInput.keyTyped(key, keycode);

        super.keyTyped(key, keycode);
    }

    @Override
    public boolean onValueChange(IControl<Float> control, Float oldValue, Float newValue) {
        // Rot
        if (control == rotXSlider) parent.getEditingPartInfo().rot[0] = newValue;
        else if (control == rotYSlider) parent.getEditingPartInfo().rot[1] = newValue;
        else if (control == rotZSlider) parent.getEditingPartInfo().rot[2] = newValue;
        // Pos
        else if (control == xPosInput) parent.getEditingPartInfo().pos[0] = newValue;
        else if (control == yPosInput) parent.getEditingPartInfo().pos[1] = newValue;
        else if (control == zPosInput) parent.getEditingPartInfo().pos[2] = newValue;

        parent.setPartsInfo(parent.getEditingPartInfo());
        return true;
    }
}
