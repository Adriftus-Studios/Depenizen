package com.denizenscript.depenizen.bukkit.commands.mythicmobs;

import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.utilities.Utilities;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.depenizen.bukkit.bridges.MythicMobsBridge;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MythicSkillCommand extends AbstractCommand {

    public MythicSkillCommand() {
        setName("mythicskill");
        setSyntax("mythicskill [<skillname>] [<location>|.../<entity>|...] (power:<#.#>) (casters:<entity>|...)");
        setRequiredArguments(2, 5);
    }

    // <--[command]
    // @Name MythicSkill
    // @Syntax mythicskill [<skillname>] [<location>|.../<entity>|...] (power:<#.#>) (casters:<entity>|...)
    // @Group Depenizen
    // @Plugin Depenizen, MythicMobs
    // @Required 2
    // @Maximum 5
    // @Short Cast a MythicMob skill from an entity.
    //
    // @Description
    // This will cast a MythicMob skill from any specified entity.
    // Optionally, you can have multiple entities cast the skill.
    // You may also specify multiple EntityTag(s) or LocationTag(s) for targets.
    // You may not have both the Entity and Location targets, one or the other.
    // The MythicMob configuration must be configured to account for this.
    //
    // @Usage
    // Used to make the player use the MythicMob skill frostbolt on their target.
    // - mythicskill <player> frostbolt <player.target>
    //
    // -->

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {

        for (Argument arg : scriptEntry.getProcessedArgs()) {
            if (!scriptEntry.hasObject("casters")
                    && arg.matchesPrefix("casters")
                    && arg.matchesArgumentList(EntityTag.class)) {
                scriptEntry.addObject("casters", arg.asType(ListTag.class).filter(EntityTag.class, scriptEntry));
            }
            else if (!scriptEntry.hasObject("skill")
                    && MythicMobsBridge.skillExists(arg.asElement().asString())) {
                scriptEntry.addObject("skill", arg.asType(ElementTag.class));
            }
            else if (!scriptEntry.hasObject("entity_targets")
                    && arg.matchesArgumentList(EntityTag.class)) {
                scriptEntry.addObject("entity_targets", arg.asType(ListTag.class).filter(EntityTag.class, scriptEntry));
            }
            else if (!scriptEntry.hasObject("location_targets")
                    && arg.matchesArgumentList(LocationTag.class)) {
                scriptEntry.addObject("location_targets", arg.asType(ListTag.class).filter(LocationTag.class, scriptEntry));
            }
            else if (!scriptEntry.hasObject("power")
                    && arg.matchesFloat()) {
                scriptEntry.addObject("power", arg.asType(ElementTag.class));
            }
            else {
                arg.reportUnhandled();
            }
        }
        if (!scriptEntry.hasObject("skill")) {
            throw new InvalidArgumentsException("Must specify a valid skill.");
        }
        if (scriptEntry.hasObject("entity_targets") && scriptEntry.hasObject("location_targets")) {
            throw new InvalidArgumentsException("Cannot have both entity and location targets.");
        }
        if (!scriptEntry.hasObject("casters")) {
            scriptEntry.defaultObject("casters", (Utilities.entryHasPlayer(scriptEntry) ? Arrays.asList(Utilities.getEntryPlayer(scriptEntry).getDenizenEntity()) : null),
                    (Utilities.entryHasNPC(scriptEntry) ? Arrays.asList(Utilities.getEntryNPC(scriptEntry).getDenizenEntity()) : null));
        }
        scriptEntry.defaultObject("power", new ElementTag(0));
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        List<EntityTag> casters = (List<EntityTag>) scriptEntry.getObject("casters");
        ElementTag skill = scriptEntry.getObjectTag("skill");
        List<EntityTag> entity_targets = (List<EntityTag>) scriptEntry.getObject("entity_targets");
        List<LocationTag> location_targets = (List<LocationTag>) scriptEntry.getObject("location_targets");
        ElementTag power = scriptEntry.getObjectTag("power");
        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(), ArgumentHelper.debugList("casters", casters)
                    + skill.debug()
                    + (location_targets == null ? "" : ArgumentHelper.debugList("location_Targets", location_targets))
                    + (entity_targets == null ? "" : ArgumentHelper.debugList("entity_targets", entity_targets))
                    + (power == null ? "" : power.debug()));
        }
        HashSet<Entity> entityTargets = null;
        HashSet<Location> locationTargets = null;
        if (entity_targets != null) {
            entityTargets = new HashSet<>();
            for (EntityTag entity : entity_targets) {
                entityTargets.add(entity.getBukkitEntity());
            }
        }
        else {
            locationTargets = new HashSet<>(location_targets);
        }
        for (EntityTag caster : casters) {
            MythicMobsBridge.getAPI().castSkill(caster.getBukkitEntity(), skill.asString(), caster.getBukkitEntity().getLocation(), entityTargets, locationTargets, power.asFloat());
        }
    }
}
