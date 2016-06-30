package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiSlider;
import kihira.foxlib.client.gui.ISliderCallback;
import kihira.tails.client.gui.controls.NumberInput;
import net.minecraft.client.resources.I18n;

import java.io.IOException;
import java.util.function.Predicate;

public class TransformPanel extends Panel<GuiEditor> implements ISliderCallback {

    private GuiSlider rotXSlider;
    private GuiSlider rotYSlider;
    private GuiSlider rotZSlider;
    private NumberInput xPosInput;

    private final int spacing = 25;
    private final int topOffset = 10;

    private final Predicate<String> validNumber = input -> {
        if (input == null) return true;
        try {
            Integer.valueOf(input);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    };

    TransformPanel(GuiEditor parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {

        buttonList.add(rotXSlider = new GuiSlider(this, 0, 0, topOffset, 100, -180, 180, parent.originalPartInfo.rot[0], "X Rotation"));
        buttonList.add(rotYSlider = new GuiSlider(this, 1, 0, topOffset+spacing, 100, -180, 180, parent.originalPartInfo.rot[1], "Y Rotation"));
        buttonList.add(rotZSlider = new GuiSlider(this, 2, 0, topOffset+spacing*2, 100, -180, 180, parent.originalPartInfo.rot[2], "Z Rotation"));

        xPosInput = new NumberInput(0, topOffset+spacing*4);
//        xInput = new GuiTextField(3, fontRendererObj, 0, topOffset+spacing*4, 30, 18);
//        xInput.setMaxStringLength(3);
//        xInput.setValidator(validNumber::test);
//        xInput.setText(String.valueOf(parent.getEditingPartInfo().pos[0]));
//        yInput = new GuiTextField(4, fontRendererObj, 35, topOffset+spacing*4, 30, 18);
//        yInput.setMaxStringLength(3);
//        yInput.setValidator(validNumber::test);
//        yInput.setText(String.valueOf(parent.getEditingPartInfo().pos[1]));
//        zInput = new GuiTextField(5, fontRendererObj, 70, topOffset+spacing*4, 30, 18);
//        zInput.setMaxStringLength(3);
//        zInput.setValidator(validNumber::test);
//        zInput.setText(String.valueOf(parent.getEditingPartInfo().pos[2]));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        zLevel = -100;
        drawGradientRect(0, 0, width, height, 0xCC000000, 0xCC000000);
        zLevel = 0;

        xPosInput.draw(mouseX, mouseY);

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

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char key, int keycode) {
        xPosInput.keyTyped(key, keycode);

//        // todo need to get the values seperately?
//        setPos(Integer.valueOf(xInput.getText()), Integer.valueOf(yInput.getText()), Integer.valueOf(zInput.getText()));

        super.keyTyped(key, keycode);
    }

    @Override
    public boolean onValueChange(GuiSlider slider, float oldValue, float newValue) {
        if (slider == rotXSlider) parent.getEditingPartInfo().rot[0] = newValue;
        else if (slider == rotYSlider) parent.getEditingPartInfo().rot[1] = newValue;
        else if (slider == rotZSlider) parent.getEditingPartInfo().rot[2] = newValue;

        parent.setPartsInfo(parent.getEditingPartInfo());
        return true;
    }

    private void setPos(float x, float y, float z) {
        parent.getEditingPartInfo().pos[0] = x;
        parent.getEditingPartInfo().pos[1] = y;
        parent.getEditingPartInfo().pos[2] = z;

        parent.setPartsInfo(parent.getEditingPartInfo());
    }
}
