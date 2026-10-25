package com.sakuya.doooom;

import com.google.inject.Provides;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "DOOOOM?",
	description = "Plays separate custom sounds when Doom of Mokhaiotl spawns or kills you.",
	tags = {"doom", "mokhaiotl", "death", "sound", "audio"}
)
public class DoomPlugin extends Plugin
{
	private static final String BOSS_NAME = "Doom of Mokhaiotl";
	private static final String SPAWN_SOUND_FILE = "/doom_spawn.wav";
	private static final String DEATH_SOUND_FILE = "/doom_death.wav";

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private DoomConfig config;

	@Provides
	DoomConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DoomConfig.class);
	}

	@Override
	protected void startUp()
	{
		migrateDeathSetting("enableDeathSound", "enableDeathSound");
		migrateDeathSetting("soundVolume", "deathSoundVolume");
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (config.enableSpawnSound())
		{
			NPC npc = event.getNpc();
			if (npc.getName() != null && npc.getName().equalsIgnoreCase(BOSS_NAME))
			{
				playSound(SPAWN_SOUND_FILE, config.spawnSoundVolume(), "spawn");
			}
		}
	}

	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		if (config.enableDeathSound()
			&& event.getActor() instanceof Player
			&& event.getActor() == client.getLocalPlayer()
			&& isDoomPresent())
		{
			playSound(DEATH_SOUND_FILE, config.deathSoundVolume(), "death");
		}
	}

	private void migrateDeathSetting(String oldKey, String newKey)
	{
		String oldValue = configManager.getConfiguration("doomdeathsound", oldKey);
		if (oldValue != null && configManager.getConfiguration("doomsound", newKey) == null)
		{
			configManager.setConfiguration("doomsound", newKey, oldValue);
		}
	}

	private boolean isDoomPresent()
	{
		for (NPC npc : client.getNpcs())
		{
			if (npc != null && npc.getName() != null
				&& (npc.getName().equalsIgnoreCase(BOSS_NAME)
				|| npc.getName().toLowerCase().contains("mokhaiotl")))
			{
				return true;
			}
		}
		return false;
	}

	private void playSound(String soundFile, int volume, String soundType)
	{
		try
		{
			InputStream audioSrc = getClass().getResourceAsStream(soundFile);
			if (audioSrc == null)
			{
				log.warn("Sound file {} not found in resources!", soundFile);
				return;
			}

			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
			Clip clip = AudioSystem.getClip();
			clip.open(audioStream);

			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
			{
				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				float volumePercent = volume / 100.0f;
				float dB = (float) (Math.log10(Math.max(volumePercent, 0.0001f)) * 20.0);
				gainControl.setValue(dB);
			}

			clip.start();
		}
		catch (Exception e)
		{
			log.error("Failed to play custom Doom {} sound", soundType, e);
		}
	}
}
