Java
package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
    name = "Doom Spawn Sound",
    description = "Plays a custom sound when Doom of Mokhaiotl spawns.",
    tags = {"doom", "mokhaiotl", "boss", "sound", "audio"}
)
public class DoomSoundPlugin extends Plugin
{
    private static final String BOSS_NAME = "Doom of Mokhaiotl";
    private static final String SOUND_FILE = "/doom_spawn.wav";

    @Inject
    private DoomSoundConfig config;

    @Provides
    DoomSoundConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(DoomSoundConfig.class);
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event)
    {
        if (!config.enableSound())
        {
            return;
        }

        NPC npc = event.getNpc();
        if (npc.getName() != null && npc.getName().equalsIgnoreCase(BOSS_NAME))
        {
            playSound();
        }
    }

    private void playSound()
    {
        try
        {
            InputStream audioSrc = getClass().getResourceAsStream(SOUND_FILE);
            if (audioSrc == null)
            {
                log.warn("Sound file {} not found in resources!", SOUND_FILE);
                return;
            }

            // Wrap in BufferedInputStream for clip mark/reset support
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Set Volume
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
            {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volumePercent = config.soundVolume() / 100.0f;
                
                // Convert linear 0.0-1.0 to decibels (dB)
                float dB = (float) (Math.log10(Math.max(volumePercent, 0.0001f)) * 20.0);
                gainControl.setValue(dB);
            }

            clip.start();
        }
        catch (Exception e)
        {
            log.error("Failed to play custom sound for Doom of Mokhaiotl", e);
        }
    }
}
