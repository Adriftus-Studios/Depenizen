package com.denizenscript.depenizen.bukkit.events.bungee;

import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class BungeePlayerQuitsScriptEvent extends BukkitScriptEvent {

    // <--[event]
    // @Events
    // bungee player leaves network
    //
    // @Regex ^on bungee player leaves network$
    //
    // @Triggers when a player leaves the Bungee network.
    //
    // @Context
    // <context.name> returns the leaving player's name.
    // <context.uuid> returns the leaving player's UUID.
    //
    // @Plugin Depenizen, BungeeCord
    //
    // @Player when the player has been on this specific server before.
    //
    // @Group Depenizen
    //
    // -->

    public BungeePlayerQuitsScriptEvent() {
        instance = this;
    }

    public static BungeePlayerQuitsScriptEvent instance;

    public String name;

    public UUID uuid;

    @Override
    public boolean couldMatch(ScriptPath path) {
        return path.eventLower.startsWith("bungee player leaves network");
    }

    @Override
    public String getName() {
        return "BungeePlayerQuits";
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        OfflinePlayer player = null;
        try {
            player = Bukkit.getOfflinePlayer(uuid);
            if (!player.hasPlayedBefore()) {
                player = null;
            }
        }
        catch (IllegalArgumentException ex) {
            // Ignore.
        }
        return new BukkitScriptEntryData(player == null ? null : new PlayerTag(player), null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("name")) {
            return new ElementTag(this.name);
        }
        else if (name.equals("uuid")) {
            return new ElementTag(uuid.toString());
        }
        return super.getContext(name);
    }
}
