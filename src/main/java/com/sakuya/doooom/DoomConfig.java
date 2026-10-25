package com.sakuya.doooom;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("doomsound")
public interface DoomConfig extends Config
{
	@ConfigItem(
		keyName = "enableSound",
		name = "Enable Spawn Sound",
		description = "Play a sound when Doom of Mokhaiotl spawns.",
		position = 0
	)
	default boolean enableSpawnSound()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "soundVolume",
		name = "Spawn Sound Volume (%)",
		description = "Set the spawn sound volume.",
		position = 1
	)
	default int spawnSoundVolume()
	{
		return 80;
	}

	@ConfigItem(
		keyName = "enableDeathSound",
		name = "Enable Death Sound",
		description = "Play a sound when you die while Doom of Mokhaiotl is present.",
		position = 2
	)
	default boolean enableDeathSound()
	{
		return true;
	}

	@Range(min = 0, max = 100)
	@ConfigItem(
		keyName = "deathSoundVolume",
		name = "Death Sound Volume (%)",
		description = "Set the death sound volume.",
		position = 3
	)
	default int deathSoundVolume()
	{
		return 80;
	}
}
