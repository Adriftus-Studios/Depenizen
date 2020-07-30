package com.denizenscript.depenizen.bukkit.events.mythicmobs;

import com.denizenscript.denizencore.objects.*;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.depenizen.bukkit.objects.mythicmobs.MythicMobsMobTag;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizen.events.BukkitScriptEvent;
import com.denizenscript.denizen.objects.EntityTag;
import com.denizenscript.denizen.objects.ItemTag;
import com.denizenscript.denizencore.scripts.ScriptEntryData;
import com.denizenscript.denizencore.utilities.CoreUtilities;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MythicMobsDeathEvent extends BukkitScriptEvent implements Listener {

    // <--[event]
    // @Events
    // mythicmob mob dies (by <entity>) (in <area>)
    // mythicmob mob death (by <entity>) (in <area>)
    // mythicmob mob killed (by <entity>) (in <area>)
    // mythicmob <mob> dies (by <entity>) (in <area>)
    // mythicmob <mob> death (by <entity>) (in <area>)
    // mythicmob <mob> killed (by <entity>) (in <area>)

    //
    // @Regex ^on mythicmob [^\s]+ (dies|death|killed)( by [^\s]+)?( in ((notable (cuboid|ellipsoid))|([^\s]+)))?$
    //
    // @Switch in:<area> to only process the event if it occurred within a specified area.
    //
    // @Triggers when a MythicMob dies.
    //
    // @Context
    // <context.mob> Returns the MythicMob that has been killed.
    // <context.entity> Returns the EntityTag for the MythicMob.
    // <context.killer> returns the EntityTag that killed the MythicMob (if available).
    // <context.level> Returns the level of the MythicMob.
    // <context.drops> Returns a list of items dropped.
    //
    // @Determine
    // ListTag(ItemTag) to specify new items to be dropped.
    //
    // @Plugin Depenizen, MythicMobs
    //
    // @Group Depenizen
    //
    // -->

    public MythicMobsDeathEvent() {
        instance = this;
    }

    public static MythicMobsDeathEvent instance;
    public MythicMobDeathEvent event;
    public MythicMobsMobTag mob;
    public EntityTag entity;
    public EntityTag killer;
    public ElementTag level;
    public ElementTag experience;
    public ElementTag currency;
    public List<ItemStack> newDrops;

    @Override
    public boolean couldMatch(ScriptPath path) {
        String cmd = path.eventArgLowerAt(2);
        return path.eventLower.startsWith("mythicmob")
                && (cmd.equals("death") || cmd.equals("dies") || cmd.equals("killed"));
    }

    @Override
    public boolean matches(ScriptPath path) {
        String mob = path.eventArgLowerAt(1);

        if (!mob.equals("mob")
                && !mob.equals(CoreUtilities.toLowerCase(this.mob.getMobType().getInternalName()))) {
            return false;
        }

        // TODO: Remove the stupid from this...
        if ((path.eventArgLowerAt(3).equals("by") || path.eventArgLowerAt(4).equals("by"))
                && !tryEntity(killer, path.eventArgLowerAt(4))
                && !tryEntity(killer, path.eventArgLowerAt(5))) {
            return false;
        }

        if (!runInCheck(path, entity.getLocation())
                && !runInCheck(path, killer.getLocation())) {
            return false;
        }

        return super.matches(path);
    }

    @Override
    public String getName() {
        return "MythicMobsDeath";
    }

    @Override
    public boolean applyDetermination(ScriptPath path, ObjectTag determinationObj) {
        if (isDefaultDetermination(determinationObj)) {
            String determination = determinationObj.toString();
            Argument arg = new Argument(determination);
            if (Argument.valueOf(determination).matchesArgumentList(ItemTag.class)) {
                if (newDrops == null) {
                    newDrops = new ArrayList<>();
                }
                List<ItemTag> items = ListTag.valueOf(determination, getTagContext(path)).filter(ItemTag.class, path.container, true);
                for (ItemTag i : items) {
                    newDrops.add(i.getItemStack());
                }
                if (newDrops != null && !newDrops.isEmpty()) {
                    event.setDrops(newDrops);
                }
                return true;
            }
        }
        return super.applyDetermination(path, determinationObj);
    }

    @Override
    public ScriptEntryData getScriptEntryData() {
        return new BukkitScriptEntryData(killer.isPlayer() ? killer.getDenizenPlayer() : null, killer.isNPC() ? killer.getDenizenNPC() : null);
    }

    @Override
    public ObjectTag getContext(String name) {
        if (name.equals("mob")) {
            return mob;
        }
        else if (name.equals("killer")) {
            return killer;
        }
        else if (name.equals("entity")) {
            return entity;
        }
        else if (name.equals("xp")) {
            return experience;
        }
        else if (name.equals("currency")) {
            return currency;
        }
        else if (name.equals("drops")) {
            ListTag oldDrops = new ListTag();
            for (ItemStack i : event.getDrops()) {
                oldDrops.addObject(new ItemTag(i));
            }
            return oldDrops;
        }
        else if (name.equals("level")) {
            return level;
        }
        return super.getContext(name);
    }

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent event) {
        mob = new MythicMobsMobTag(event.getMob());
        entity = new EntityTag(event.getEntity());
        EntityTag.rememberEntity(entity.getBukkitEntity());
        killer = new EntityTag(event.getKiller());
        level = new ElementTag(event.getMobLevel());
        newDrops = null;
        this.event = event;
        fire(event);
        EntityTag.forgetEntity(entity.getBukkitEntity());
    }
}
