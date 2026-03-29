package com.anastas1s12.jujutsu_awakening.client.hud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class KeyframesEditorScreen extends Screen {

    // Shared pose used by the game to animate arms
    public static AnimationPose CURRENT_POSE = new AnimationPose();

    private final List<Keyframe> keyframes = new ArrayList<>();
    private int selectedKeyframe = 0;
    private boolean isPlaying = false;
    private boolean loopEnabled = true;
    private float playbackTime = 0f;
    private long lastFrameTime = 0;

    // Editing values
    private float lPitch = 0, lYaw = 0, lRoll = 0, lTx = 0, lTy = 0, lTz = 0, lPx = 0, lPy = 0, lPz = 0;
    private float rPitch = 0, rYaw = 0, rRoll = 0, rTx = 0, rTy = 0, rTz = 0, rPx = 0, rPy = 0, rPz = 0;

    private static class Keyframe {
        float time;
        AnimationPose pose;

        Keyframe(float time, AnimationPose pose) {
            this.time = time;
            this.pose = pose.copy();
        }
    }

    public static class AnimationPose {
        float lPitch, lYaw, lRoll, lTx, lTy, lTz, lPx, lPy, lPz;
        float rPitch, rYaw, rRoll, rTx, rTy, rTz, rPx, rPy, rPz;

        AnimationPose copy() {
            AnimationPose p = new AnimationPose();
            p.lPitch = lPitch; p.lYaw = lYaw; p.lRoll = lRoll;
            p.lTx = lTx; p.lTy = lTy; p.lTz = lTz;
            p.lPx = lPx; p.lPy = lPy; p.lPz = lPz;
            p.rPitch = rPitch; p.rYaw = rYaw; p.rRoll = rRoll;
            p.rTx = rTx; p.rTy = rTy; p.rTz = rTz;
            p.rPx = rPx; p.rPy = rPy; p.rPz = rPz;
            return p;
        }
    }

    public KeyframesEditorScreen() {
        super(Component.literal("First Person Animation Editor"));
        keyframes.add(new Keyframe(0f, new AnimationPose()));
    }

    @Override
    protected void init() {
        int w = this.width;

        // Top Controls
        this.addRenderableWidget(Button.builder(Component.literal("+ Add KF"), b -> addKeyframe())
                .bounds(20, 25, 90, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("✕ Delete"), b -> deleteKeyframe())
                .bounds(120, 25, 90, 20).build());
        this.addRenderableWidget(Button.builder(Component.literal("💾 Export JSON"), b -> exportToJson())
                .bounds(w - 170, 25, 150, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("▶ Play"), b -> {
            isPlaying = true;
            lastFrameTime = System.currentTimeMillis();
        }).bounds(w/2 - 160, 25, 70, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("■ Stop"), b -> {
            isPlaying = false;
            playbackTime = 0;
        }).bounds(w/2 - 80, 25, 70, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal(loopEnabled ? "🔁 Loop ON" : "🔁 Loop OFF"), b -> {
            loopEnabled = !loopEnabled;
        }).bounds(w/2 + 10, 25, 110, 20).build());

        // LEFT ARM
        guiDrawSectionTitle(30, 75, "LEFT ARM");
        addSlider(30, 100, "L Pitch", lPitch, -180, 180, v -> lPitch = v);
        addSlider(30, 130, "L Yaw",   lYaw,   -180, 180, v -> lYaw = v);
        addSlider(30, 160, "L Roll",  lRoll,  -180, 180, v -> lRoll = v);
        addSlider(30, 190, "L TX",    lTx,    -2, 2, v -> lTx = v);
        addSlider(30, 220, "L TY",    lTy,    -2, 2, v -> lTy = v);
        addSlider(30, 250, "L TZ",    lTz,    -2, 2, v -> lTz = v);
        addSlider(30, 280, "L PivX",  lPx,    -1, 1, v -> lPx = v);
        addSlider(30, 310, "L PivY",  lPy,    -1, 1, v -> lPy = v);
        addSlider(30, 340, "L PivZ",  lPz,    -1, 1, v -> lPz = v);

        // RIGHT ARM
        guiDrawSectionTitle(w - 250, 75, "RIGHT ARM");
        addSlider(w - 240, 100, "R Pitch", rPitch, -180, 180, v -> rPitch = v);
        addSlider(w - 240, 130, "R Yaw",   rYaw,   -180, 180, v -> rYaw = v);
        addSlider(w - 240, 160, "R Roll",  rRoll,  -180, 180, v -> rRoll = v);
        addSlider(w - 240, 190, "R TX",    rTx,    -2, 2, v -> rTx = v);
        addSlider(w - 240, 220, "R TY",    rTy,    -2, 2, v -> rTy = v);
        addSlider(w - 240, 250, "R TZ",    rTz,    -2, 2, v -> rTz = v);
        addSlider(w - 240, 280, "R PivX",  rPx,    -1, 1, v -> rPx = v);
        addSlider(w - 240, 310, "R PivY",  rPy,    -1, 1, v -> rPy = v);
        addSlider(w - 240, 340, "R PivZ",  rPz,    -1, 1, v -> rPz = v);
    }

    private void guiDrawSectionTitle(int x, int y, String text) {
        // Will be drawn in render()
    }

    private void addSlider(int x, int y, String label, float current, float min, float max, ValueConsumer consumer) {
        double initial = (current - min) / (max - min);
        this.addRenderableWidget(new AbstractSliderButton(x, y, 200, 20, Component.literal(label), initial) {
            @Override
            protected void updateMessage() {
                float val = min + (float) (this.value * (max - min));
                setMessage(Component.literal(label + ": " + String.format("%.2f", val)));
            }

            @Override
            protected void applyValue() {
                float val = min + (float) (this.value * (max - min));
                consumer.accept(val);
            }
        });
    }

    private void addKeyframe() {
        AnimationPose pose = new AnimationPose();
        pose.lPitch = lPitch; pose.lYaw = lYaw; pose.lRoll = lRoll;
        pose.lTx = lTx; pose.lTy = lTy; pose.lTz = lTz;
        pose.lPx = lPx; pose.lPy = lPy; pose.lPz = lPz;
        pose.rPitch = rPitch; pose.rYaw = rYaw; pose.rRoll = rRoll;
        pose.rTx = rTx; pose.rTy = rTy; pose.rTz = rTz;
        pose.rPx = rPx; pose.rPy = rPy; pose.rPz = rPz;

        keyframes.add(new Keyframe(playbackTime, pose));
        selectedKeyframe = keyframes.size() - 1;
    }

    private void deleteKeyframe() {
        if (keyframes.size() > 1) {
            keyframes.remove(selectedKeyframe);
            if (selectedKeyframe >= keyframes.size()) selectedKeyframe--;
        }
    }

    private AnimationPose getInterpolatedPose(float time) {
        if (keyframes.size() == 1) return keyframes.get(0).pose.copy();

        for (int i = 0; i < keyframes.size() - 1; i++) {
            Keyframe a = keyframes.get(i);
            Keyframe b = keyframes.get(i + 1);
            if (time >= a.time && time <= b.time) {
                float prog = (time - a.time) / (b.time - a.time);
                AnimationPose p = new AnimationPose();

                p.lPitch = lerp(a.pose.lPitch, b.pose.lPitch, prog);
                p.lYaw   = lerp(a.pose.lYaw,   b.pose.lYaw,   prog);
                p.lRoll  = lerp(a.pose.lRoll,  b.pose.lRoll,  prog);
                p.lTx    = lerp(a.pose.lTx,    b.pose.lTx,    prog);
                p.lTy    = lerp(a.pose.lTy,    b.pose.lTy,    prog);
                p.lTz    = lerp(a.pose.lTz,    b.pose.lTz,    prog);
                p.lPx    = lerp(a.pose.lPx,    b.pose.lPx,    prog);
                p.lPy    = lerp(a.pose.lPy,    b.pose.lPy,    prog);
                p.lPz    = lerp(a.pose.lPz,    b.pose.lPz,    prog);

                p.rPitch = lerp(a.pose.rPitch, b.pose.rPitch, prog);
                p.rYaw   = lerp(a.pose.rYaw,   b.pose.rYaw,   prog);
                p.rRoll  = lerp(a.pose.rRoll,  b.pose.rRoll,  prog);
                p.rTx    = lerp(a.pose.rTx,    b.pose.rTx,    prog);
                p.rTy    = lerp(a.pose.rTy,    b.pose.rTy,    prog);
                p.rTz    = lerp(a.pose.rTz,    b.pose.rTz,    prog);
                p.rPx    = lerp(a.pose.rPx,    b.pose.rPx,    prog);
                p.rPy    = lerp(a.pose.rPy,    b.pose.rPy,    prog);
                p.rPz    = lerp(a.pose.rPz,    b.pose.rPz,    prog);

                return p;
            }
        }
        return keyframes.get(keyframes.size() - 1).pose.copy();
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private void exportToJson() {
        try {
            JsonArray array = new JsonArray();
            for (Keyframe kf : keyframes) {
                JsonObject obj = new JsonObject();
                obj.addProperty("time", kf.time);
                // Left
                obj.addProperty("lPitch", kf.pose.lPitch); obj.addProperty("lYaw", kf.pose.lYaw);
                obj.addProperty("lRoll", kf.pose.lRoll); obj.addProperty("lTx", kf.pose.lTx);
                obj.addProperty("lTy", kf.pose.lTy); obj.addProperty("lTz", kf.pose.lTz);
                obj.addProperty("lPx", kf.pose.lPx); obj.addProperty("lPy", kf.pose.lPy);
                obj.addProperty("lPz", kf.pose.lPz);
                // Right
                obj.addProperty("rPitch", kf.pose.rPitch); obj.addProperty("rYaw", kf.pose.rYaw);
                obj.addProperty("rRoll", kf.pose.rRoll); obj.addProperty("rTx", kf.pose.rTx);
                obj.addProperty("rTy", kf.pose.rTy); obj.addProperty("rTz", kf.pose.rTz);
                obj.addProperty("rPx", kf.pose.rPx); obj.addProperty("rPy", kf.pose.rPy);
                obj.addProperty("rPz", kf.pose.rPz);
                array.add(obj);
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Path path = Path.of("animation.json");
            Files.writeString(path, gson.toJson(array));
            System.out.println("✅ Exported animation to animation.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void render(GuiGraphics gui, int mx, int my, float delta) {
        gui.fill(0, 0, width, height, 0x88000000);

        gui.drawString(font, "First Person Animation Editor", width/2 - 130, 8, 0xFFFFFF, true);

        // Timeline
        gui.fill(20, 55, width - 20, 75, 0xFF222222);
        for (int i = 0; i < keyframes.size(); i++) {
            int x = 40 + (int) (i * (width - 80f) / Math.max(1, keyframes.size() - 1));
            boolean selected = i == selectedKeyframe;
            gui.drawString(font, selected ? "◆" : "◇", x - 5, 58, selected ? 0x44AAFF : 0x888888, true);
        }

        gui.drawString(font, "LEFT ARM", 30, 75, 0x44FFAA, true);
        gui.drawString(font, "RIGHT ARM", width - 250, 75, 0xFF4444, true);

        super.render(gui, mx, my, delta);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        // Timeline click
        if (click.y() > 50 && click.y() < 80) {
            for (int i = 0; i < keyframes.size(); i++) {
                int x = 40 + (int) (i * (width - 80f) / Math.max(1, keyframes.size() - 1));
                if (click.x() > x - 15 && click.x() < x + 15) {
                    selectedKeyframe = i;
                    AnimationPose p = keyframes.get(i).pose;
                    // Load into editor
                    lPitch = p.lPitch; lYaw = p.lYaw; lRoll = p.lRoll;
                    lTx = p.lTx; lTy = p.lTy; lTz = p.lTz;
                    lPx = p.lPx; lPy = p.lPy; lPz = p.lPz;
                    rPitch = p.rPitch; rYaw = p.rYaw; rRoll = p.rRoll;
                    rTx = p.rTx; rTy = p.rTy; rTz = p.rTz;
                    rPx = p.rPx; rPy = p.rPy; rPz = p.rPz;
                    return true;
                }
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public void tick() {
        if (isPlaying) {
            long now = System.currentTimeMillis();
            playbackTime += (now - lastFrameTime) * 0.08f; // speed control
            lastFrameTime = now;

            if (playbackTime >= 100) {
                if (loopEnabled) playbackTime = 0;
                else isPlaying = false;
            }

            CURRENT_POSE = getInterpolatedPose(playbackTime);
        }
    }

    @FunctionalInterface
    private interface ValueConsumer {
        void accept(float value);
    }
}