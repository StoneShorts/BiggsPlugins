package net.runelite.client.live.inDevelopment.biggs.SoundManager;

import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.Collections.Widgets;
import net.runelite.client.live.polarbot.util.PolarAPI.plugins.PolarAPIPlugin.PolarAPI;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.client.callback.ClientThread;
import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

@PluginDescriptor(
        name = "Polar Sound Manager",
        description = "Plays custom sounds based on in-game actions.",
        tags = {"biggs", "polar", "utility"},
        enabledByDefault = true
)
public class SoundEffectPlugin extends Plugin {
//836
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    private static final String SOUND_DIR = System.getProperty("user.home") + "\\.runelite\\polarsoundmanager";
    private static final String ATTACK = "attack.wav";  // Default file name
    private static final String HEAL_FEROX = "heal_ferox.wav";  // Default file name
    private static final String DAMAGE = "damage_taken.wav";  // Default file name
    private static final String BANK_OPEN_SOUND = "bank_open.wav";
    private static final String DEATH = "jev.wav";
    private static final String DADDY_SOUND = "daddy.wav";  // Add the new sound file
    private boolean wasWidgetVisible = false;  // Track previous state of the widget
    private boolean wasBankOpen = false;
    private int lastHP;

    @Override
    protected void startUp() {
        ensureSoundDirectoryExists();
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        boolean isBankNowOpen = PolarAPI.isBankOpen();
        if (!wasBankOpen && isBankNowOpen) {
            playSound(BANK_OPEN_SOUND);
        }

        int currentHP = client.getBoostedSkillLevel(Skill.HITPOINTS);
        if (currentHP < lastHP) {
            playSound(DAMAGE);
        }
        lastHP = currentHP;

        boolean isWidgetNowVisible = Widgets.search().withId(15138822).first().isPresent();
        if (!wasWidgetVisible && isWidgetNowVisible) {
            playSound(DADDY_SOUND);
        }

        wasWidgetVisible = isWidgetNowVisible;

        // Update the bank open state for the next tick
        wasBankOpen = isBankNowOpen;
    }

    public boolean isBankOpen() {
        Widget bankWidget = client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER);
        return bankWidget != null && !bankWidget.isHidden();
    }


    private void ensureSoundDirectoryExists() {
        File soundDirectory = new File(SOUND_DIR);
        if (!soundDirectory.exists()) {
            if (soundDirectory.mkdirs()) {
                System.out.println("Created directory: " + SOUND_DIR);
            } else {
                System.out.println("Failed to create directory: " + SOUND_DIR);
            }
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event) {
        if (event.getActor() instanceof Player) {
            Player player = (Player) event.getActor();
            if (player.equals(client.getLocalPlayer()) && isAttackAnimation(player.getAnimation())) {
                playSound(ATTACK);
            }
            if (player.equals(client.getLocalPlayer()) && isFeroxPoolAnimation(player.getAnimation())) {
                playSound(HEAL_FEROX);

            }
            if (player.equals(client.getLocalPlayer()) && isDeathAnimation(player.getAnimation())) {
                playSound(DEATH);

            }
        }
    }

    private boolean isAttackAnimation(int animationId) {
        return animationId == 422 || animationId == 376;
    }

    private boolean isDeathAnimation(int animationId) {
        return animationId == 836; // Replace with correct attack animation IDs
    }

    private boolean isFeroxPoolAnimation(int animationId) {
        return animationId == 7305; // Replace with correct attack animation IDs
    }

    private void playSound(String fileName) {
        new Thread(() -> {
            try {
                File soundFile = new File(SOUND_DIR, fileName);
                if (!soundFile.exists()) {
                    System.out.println("Sound file not found: " + soundFile.getAbsolutePath());
                    return;
                }

                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
