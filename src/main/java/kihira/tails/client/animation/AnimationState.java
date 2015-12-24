package kihira.tails.client.animation;

import kihira.tails.common.ModelData;

public class AnimationState {

    public final String name;

    private final boolean loops;

    public AnimationState(String name, boolean loops) {
        this.name = name;
        this.loops = loops;
    }

    public void update(ModelData model, float partialTicks) {

    }
}
