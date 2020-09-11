package com.denizenscript.depenizen.bukkit.commands.vivecraft;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.exceptions.InvalidArgumentsException;
import com.denizenscript.denizencore.objects.Argument;
import com.denizenscript.denizencore.objects.ArgumentHelper;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.commands.AbstractCommand;
import com.denizenscript.denizencore.utilities.debugging.Debug;
import com.denizenscript.depenizen.bukkit.bridges.VivecraftBridge;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.vivecraft.VSE;
import org.vivecraft.VivePlayer;

import java.util.List;
import java.util.UUID;

public class VivePoseCommand extends AbstractCommand {

    public VivePoseCommand() {
        setName("vivepose");
        setSyntax("vivepose [<npc>] (rightarm:<location>) (leftarm:<location>) (head:<location>) (mirror:<player>) (targets:<player>|...)");
        setRequiredArguments(2, 5);
    }

    @Override
    public void parseArgs(ScriptEntry scriptEntry) throws InvalidArgumentsException {
        for (Argument arg : scriptEntry.getProcessedArgs()) {
            if (!scriptEntry.hasObject("npcs")
                    && arg.matchesArgumentList(NPCTag.class)) {
                scriptEntry.addObject("npcs", arg.asType(ListTag.class).filter(NPCTag.class, scriptEntry));
            } else if (!scriptEntry.hasObject("rightarm")
                    && arg.matchesArgumentType(LocationTag.class)
                    && arg.matchesPrefix("rightarm")) {
                scriptEntry.addObject("rightarm", arg.asType(LocationTag.class));
            } else if (!scriptEntry.hasObject("leftarm")
                    && arg.matchesArgumentType(LocationTag.class)
                    && arg.matchesPrefix("leftarm")) {
                scriptEntry.addObject("leftarm", arg.asType(LocationTag.class));
            } else if (!scriptEntry.hasObject("mirror")
                    && arg.matchesArgumentType(PlayerTag.class)
                    && arg.matchesPrefix("mirror")) {
                scriptEntry.addObject("mirror", arg.asType(PlayerTag.class));
            } else if (!scriptEntry.hasObject("head")
                    && arg.matchesArgumentType(LocationTag.class)
                    && arg.matchesPrefix("head")) {
                scriptEntry.addObject("head", arg.asType(LocationTag.class));
            } else if (!scriptEntry.hasObject("targets")
                    && arg.matchesArgumentList(PlayerTag.class)
                    && arg.matchesPrefix("targets")) {
                    scriptEntry.addObject("targets", arg.asType(ListTag.class).filter(PlayerTag.class, scriptEntry));
            } else {
                arg.reportUnhandled();
            }
        }
    }

    @Override
    public void execute(ScriptEntry scriptEntry) {
        List<NPCTag> npcs = (List<NPCTag>) scriptEntry.getObject("npcs");
        List<PlayerTag> targets = (List<PlayerTag>) scriptEntry.getObject("targets");
        PlayerTag mirror = scriptEntry.getObjectTag("mirror");
        LocationTag rightarm = scriptEntry.getObjectTag("rightarm");
        LocationTag leftarm = scriptEntry.getObjectTag("leftarm");
        LocationTag head = scriptEntry.getObjectTag("head");
        if (scriptEntry.dbCallShouldDebug()) {
            Debug.report(scriptEntry, getName(), ArgumentHelper.debugList("npcs", npcs)
                    + (rightarm == null ? "" : rightarm.debug())
                    + (leftarm == null ? "" : leftarm.debug())
                    + (head == null ? "" : head.debug())
                    + (targets == null ? "" : ArgumentHelper.debugList("targets", targets)));
        }
        VivePlayer vp = VivecraftBridge.getIdlePlayer((Player) npcs.get(0));
        if (mirror != null) {
            VivePlayer copy = VivecraftBridge.getVivePlayers().get(mirror.getPlayerEntity().getUniqueId());
            vp.worldScale = copy.worldScale;
            vp.heightScale = copy.heightScale;
            vp.hmdData = copy.hmdData;
            vp.controller0data = copy.controller0data;
            vp.controller1data = copy.controller1data;
        }
        if (rightarm != null) {
            vp.controller0data = VivecraftBridge.getByteArray(rightarm);
        }
        if (leftarm != null) {
            vp.controller1data = VivecraftBridge.getByteArray(leftarm);
        }
        if (head != null) {
            vp.hmdData = VivecraftBridge.getByteArray(head);
        }
        for (NPCTag npc : npcs) {
            vp.player = (Player) npc.getLivingEntity();
            if (npc.getEntityType() != EntityType.PLAYER) {
                Debug.echoError(npc.debug() + " is not a PLAYER type NPC");
                continue;
            }
            if (targets != null) {
                for (PlayerTag targ : targets) {
                    if (VSE.isVive(targ.getPlayerEntity())) {
                        targ.getPlayerEntity().sendPluginMessage(VSE.getPlugin(VSE.class), VSE.CHANNEL, vp.getUberPacket());
                    }
                }
            }
            else {
                for (UUID targ : VivecraftBridge.getVivePlayers().keySet()) {
                    Bukkit.getPlayer(targ).sendPluginMessage(VSE.getPlugin(VSE.class), VSE.CHANNEL, vp.getUberPacket());
                }
            }
        }
    }
}
