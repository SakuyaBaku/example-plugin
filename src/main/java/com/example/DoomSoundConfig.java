Java
package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("doomsound")
public interface DoomSoundConfig extends Config
{
    @ConfigItem(
        keyName = "enableSound",
        name = "Enable Spawn Sound",
        description = "Play a custom sound when Doom of Mokhaiotl spawns."
    )
    default boolean enableSound()
    {
        return true;
    }

    @Range(min = 0, max = 100)
    @ConfigItem(
        keyName = "soundVolume",
        name = "Volume (%)",
        description = "Adjust the playback volume of the spawn sound."
    )
    default int soundVolume()
    {
        return 80;
    }
}
