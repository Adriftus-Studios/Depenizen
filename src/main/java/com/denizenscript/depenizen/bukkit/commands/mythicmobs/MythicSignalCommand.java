package com.denizenscript.depenizen.bukkit.commands.mythicmobs;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.depenizen.bukkit.objects.mythicmobs.MythicMobsMobTag;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;

import java.util.List;

public class MythicSignalCommand extends AbstractCommand {

    public MythicSignalCommand() {
        setName("mythicsignal");
        setSyntax("mythicsignal [<mythicmob>|...] [<signal>] (source:<entity>)");
        setRequiredArguments(2, 3);
    }

    // <--[command]
    // @Name MythicSignal
    // @Syntax mythicsignal [<mythicmob>|...] [<signal>] (source:<entity>)
    // @Group Depenizen
    // @Plugin Depenizen, MythicMobs
    // @Required 2
    // @Maximum 3
    // @Short Sends a signal trigger to the target MythicMobs.
    //
    // @Description
    // This allows you to send a signal trigger to multiple MythicMobs.
    // If those mobs have any triggers configured for that signal, they will fire.
    // Optionally, specify an entity that acts as the sender.
    // NOTE: signals are case sensitive.
    //
    // @Usage
    // Used to trigger the player's target signal "attack".
    // - mythicsignal <player.target.mythicmob> attack
    //
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        for (Argument arg : scriptEntry.getProcessedArgs()) {
            if (!scriptEntry.hasObject("targets")
                    && arg.matchesArgumentList(MythicMobsMobTag.class)) {
                scriptEntry.addObject("targets", arg.asType(ListTag.class).filter(MythicMobsMobTag.class, scriptEntry));
            }
            else if (!scriptEntry.hasObject("source")
                    && arg.matchesPrefix("source")
                    && arg.matchesArgumentType(EntityTag.class)) {
                scriptEntry.addObject("source", arg.asType(EntityTag.class));
            }
            else if (!scriptEntry.hasObject("signal")) {
                scriptEntry.addObject("signal", arg.asType(ElementTag.class));
            }
            else {
                arg.reportUnhandled();
            }
        }
        if (!scriptEntry.hasObject("targets")) {
            throw new InvalidArgumentsException("Must specify MythicMobs to send the signal to.");
        }
        if (!scriptEntry.hasObject("signal")) {
            throw new InvalidArgumentsException("Must specify a signal to send.");
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {

        EntityTag source = scriptEntry.getObjectTag("source");
        List<MythicMobsMobTag> targets = (List<MythicMobsMobTag>) scriptEntry.getObject("targets");
        ElementTag signal = scriptEntry.getElement("signal");

        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(), ArgumentHelper.debugList("mythicmobs", targets)
                    + signal.debug()
                    + (source == null ? "" : source.debug()));
        }

        for (MythicMobsMobTag mob : targets) {
            mob.getMob().signalMob((source == null ? null : BukkitAdapter.adapt(source.getBukkitEntity())), signal.asString());
        }
    }
}
