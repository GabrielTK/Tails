package kihira.tails.client.gui.controls;

import com.google.common.base.Strings;
import kihira.foxlib.client.gui.GuiBaseScreen;
import kihira.foxlib.client.gui.IControl;
import kihira.foxlib.client.gui.IControlCallback;
import kihira.foxlib.client.gui.ITooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// todo tooltips only work for guibuttons
@SideOnly(Side.CLIENT)
public class NumberInput extends Gui implements IControl<Float>, ITooltip {
    private int xPos;
    private int yPos;
    private final int width = 55;
    private final int height = 18;

    private final float min;
    private final float max;
    private final float increment;
    private final float shiftMod = 10f;
    private final float ctrlMod = 0.1f;

    private final int btnWidth = 10;
    private final int btnXPos;
    private final int btnHeight;

    private static final char[] validChars = new char[]{'-', '.', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'}; //todo might need to sort first, need to check
    private final DecimalFormat df = new DecimalFormat("###.##");
    private final GuiTextField numInput;
    private float num = 0f;
    private IControlCallback<IControl<Float>, Float> callback;

    static {
        Arrays.sort(validChars);
    }

    public NumberInput(int x, int y, float min, float max, float increment, @Nullable IControlCallback<IControl<Float>, Float> callback) {
        this.xPos = x;
        this.yPos = y;
        this.min = min;
        this.max = max;
        this.increment = increment;
        this.callback = callback;
        df.setRoundingMode(RoundingMode.FLOOR);

        numInput = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, xPos, yPos+1, width-btnWidth-2, height-2);
        numInput.setValidator(input -> {
            if (input == null) return true;
            int dotCount = 0;
            char[] in = input.toCharArray();
            for (int i = 0; i < in.length; i++) {
                char c = in[i];
                if (c == '-' && i != 0) {
                    return false;
                }
                else if (c == '.') {
                    dotCount++;
                    if (dotCount > 1) return false;
                }
                else if (Arrays.binarySearch(validChars, c) < 0) {
                    return false;
                }
            }
            return true;
        });
        setValue(0f);

        this.btnXPos = xPos+numInput.width+2;
        this.btnHeight = height/2;
    }

    public void draw(int mouseX, int mouseY) {
        numInput.drawTextBox();
        drawRect(btnXPos, yPos, btnXPos+btnWidth, yPos+btnHeight, 0xFFFFFFFF);
        drawRect(btnXPos, yPos+btnHeight, btnXPos+btnWidth, yPos+height, 0xAAAAAAFF);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean focused = numInput.isFocused();
        numInput.mouseClicked(mouseX, mouseY, mouseButton);
        // Only update num when input loses focus
        if (focused && !numInput.isFocused()) {
            if (!Strings.isNullOrEmpty(numInput.getText())) setValue(Float.valueOf(numInput.getText()));
            else setValue(0f);
            numInput.setCursorPosition(0);
        }

        float inc = increment;
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) inc *= shiftMod;
        else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) inc *= ctrlMod;

        // Increase
        if (GuiBaseScreen.isMouseOver(mouseX, mouseY, xPos+numInput.width, yPos, btnWidth, btnHeight)) {
            setValue(num + inc);
        }
        // Decrease
        else if (GuiBaseScreen.isMouseOver(mouseX, mouseY, xPos+numInput.width, yPos+btnHeight, btnWidth, btnHeight)) {
            setValue(num - inc);
        }
    }

    public void keyTyped(char key, int keycode) {
        numInput.textboxKeyTyped(key, keycode);
    }

    @Override
    public void setValue(Float newValue) {
        newValue = MathHelper.clamp_float(newValue, min, max);
        if (callback != null && !callback.onValueChange(this, num, newValue)) return;

        num = newValue;
        numInput.setText(df.format(num));
    }

    @Override
    public Float getValue() {
        return num;
    }

    @Override
    public List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
        if (mouseIdleTime < 7f) return new ArrayList<>();
        List<String> tooltip = new ArrayList<>();
        tooltip.add(String.format("Click arrows to change by %s", increment));
        tooltip.add(String.format("Hold SHIFT to change by %s, CTRL to change by %s", increment*10f, increment*0.1f));
        return tooltip; //todo optimise
    }
}
