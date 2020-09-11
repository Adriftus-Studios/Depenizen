package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.DenizenAPI;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.objects.core.MapTag;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.denizenscript.depenizen.bukkit.commands.vivecraft.VivePoseCommand;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.vivecraft.VSE;
import org.vivecraft.VivePlayer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

public class VivecraftBridge extends Bridge {

    public LocationTag convertVector(String vectorString) {
        String[] list = vectorString.substring(1, vectorString.length()-1).split(", ");
        Vector vector = new Vector(Double.parseDouble(list[0]), Double.parseDouble(list[1]), Double.parseDouble(list[2]));
        return new LocationTag(vector);
    }

    public static Map<UUID, VivePlayer> getVivePlayers() {
        return VSE.vivePlayers;
    }

    public static byte[] getByteArray(LocationTag loc, LocationTag vector) {
        ByteBuf payload = Unpooled.buffer();
        payload.writeBoolean(false);
        payload.writeFloat((float)loc.getX());
        payload.writeFloat((float)loc.getY());
        payload.writeFloat((float)loc.getZ());
        payload.writeFloat(0);
        payload.writeFloat((float)vector.getX());
        payload.writeFloat((float)vector.getY());
        payload.writeFloat((float)vector.getZ());
        return new byte[payload.readableBytes()];
    }

    public static byte[] getByteArray(LocationTag loc) {
        ByteBuf payload = Unpooled.buffer();
        payload.writeBoolean(false);
        payload.writeFloat((float) loc.getX());
        payload.writeFloat((float) loc.getY());
        payload.writeFloat((float) loc.getZ());
        payload.writeFloat((float) 0.75);
        payload.writeFloat((float) loc.toVector().getX());
        payload.writeFloat((float) loc.toVector().getY());
        payload.writeFloat((float) loc.toVector().getZ());
        return new byte[payload.readableBytes()];
    }

    public static VivePlayer getIdlePlayer(Player npc) {
        VivePlayer returnable = new VivePlayer(npc);
        ByteBuf payload = Unpooled.buffer();
        payload.writeBoolean(false);
        payload.writeFloat((float) 0);
        payload.writeFloat((float) 1);
        payload.writeFloat((float) 0);
        payload.writeFloat((float) 0.96);
        payload.writeFloat((float) 0.25);
        payload.writeFloat((float) 0);
        payload.writeFloat((float) 0);
        returnable.hmdData = new byte[payload.readableBytes()];
        payload = Unpooled.buffer();
        payload.writeBoolean(false);
        payload.writeFloat((float) 0.5193569);
        payload.writeFloat((float) 0.69866323);
        payload.writeFloat((float) 0.044001184);
        payload.writeFloat((float) 0.58673733);
        payload.writeFloat((float) -0.7875683);
        payload.writeFloat((float) 0.1693283);
        payload.writeFloat((float) 0.082498305);
        returnable.controller0data = new byte[payload.readableBytes()];
        payload = Unpooled.buffer();
        payload.writeBoolean(false);
        payload.writeFloat((float) -0.3381468);
        payload.writeFloat((float) 0.6491368);
        payload.writeFloat((float) -0.25372127);
        payload.writeFloat((float) -0.47935414);
        payload.writeFloat((float) 0.7218389);
        payload.writeFloat((float) 0.3276589);
        payload.writeFloat((float) 0.37657538);
        returnable.controller1data = new byte[payload.readableBytes()];
        returnable.heightScale = (float) 0.93;
        returnable.worldScale = (float) 1.1;
        return returnable;

    }
    @Override
    public void init() {

        DenizenAPI.getCurrentInstance().getCommandRegistry().registerCommand(VivePoseCommand.class);

        try {
            VivePlayer.class.getDeclaredField("isTeleportMode").setAccessible(true);
            VivePlayer.class.getDeclaredField("isReverseHands").setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        // This is used instead of a property class because
        // property classes can only return strings and not tags
        PlayerTag.registerTag("vivecraft", (attribute, player) -> {

            // <--[tag]
            // @attribute <PlayerTag.vivecraft>
            // @returns ObjectTag
            // @plugin Depenizen, ViveCraft
            // @description
            // Returns null if player is not using ViveCraft
            // -->
            if (attribute.startsWith("vivecraft")) {
                attribute = attribute.fulfill(1);
                if (!VSE.isVive(player.getPlayerEntity())) {
                    return null;
                } else {
                    VivePlayer vp = VivecraftBridge.getVivePlayers().get(player.getPlayerEntity().getUniqueId());

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.is_vr>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns whether the player is using Vivecraft
                    // -->

                    if (attribute.startsWith("is_vr")) {
                        return new ElementTag(vp.isVR());
                    }

                    if (attribute.startsWith("test")) {
                        MapTag map = new MapTag();
                        ListTag list1 = new ListTag();
                        ByteArrayInputStream byin1 = new ByteArrayInputStream(vp.hmdData);
                        DataInputStream da1 = new DataInputStream(byin1);
                        try {
                            list1.addObject(new ElementTag(String.valueOf(da1.readBoolean())));
                            list1.addObject(new ElementTag(String.valueOf(da1.readFloat())));
                            list1.addObject(new ElementTag(String.valueOf(da1.readFloat())));
                            list1.addObject(new ElementTag(String.valueOf(da1.readFloat())));
                            list1.addObject(new ElementTag(String.valueOf(da1.readFloat())));
                            list1.addObject(new ElementTag(String.valueOf(da1.readFloat())));
                            list1.addObject(new ElementTag(String.valueOf(da1.readFloat())));
                            list1.addObject(new ElementTag(String.valueOf(da1.readFloat())));
                            map.putObject("HMD", list1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ListTag list2 = new ListTag();
                        ByteArrayInputStream byin2 = new ByteArrayInputStream(vp.controller0data);
                        DataInputStream da2 = new DataInputStream(byin2);
                        try {
                            list2.addObject(new ElementTag(String.valueOf(da2.readBoolean())));
                            list2.addObject(new ElementTag(String.valueOf(da2.readFloat())));
                            list2.addObject(new ElementTag(String.valueOf(da2.readFloat())));
                            list2.addObject(new ElementTag(String.valueOf(da2.readFloat())));
                            list2.addObject(new ElementTag(String.valueOf(da2.readFloat())));
                            list2.addObject(new ElementTag(String.valueOf(da2.readFloat())));
                            list2.addObject(new ElementTag(String.valueOf(da2.readFloat())));
                            list2.addObject(new ElementTag(String.valueOf(da2.readFloat())));
                            map.putObject("controller0", list2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ListTag list = new ListTag();
                        ByteArrayInputStream byin = new ByteArrayInputStream(vp.controller1data);
                        DataInputStream da = new DataInputStream(byin);
                        try {
                            list.addObject(new ElementTag(String.valueOf(da.readBoolean())));
                            list.addObject(new ElementTag(String.valueOf(da.readFloat())));
                            list.addObject(new ElementTag(String.valueOf(da.readFloat())));
                            list.addObject(new ElementTag(String.valueOf(da.readFloat())));
                            list.addObject(new ElementTag(String.valueOf(da.readFloat())));
                            list.addObject(new ElementTag(String.valueOf(da.readFloat())));
                            list.addObject(new ElementTag(String.valueOf(da.readFloat())));
                            list.addObject(new ElementTag(String.valueOf(da.readFloat())));
                            map.putObject("controller1", list);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        map.putObject("height", new ElementTag(vp.heightScale));
                        map.putObject("world", new ElementTag(vp.worldScale));
                        return map;
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.height>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns the player's calibrated height
                    // --

                    if (attribute.startsWith("height")) {
                        return new ElementTag(player.getPlayerEntity().getMetadata("height").get(0).asFloat());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.activehand>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns which hand (left or right) last performed some
                    // actions. Currently throwing projectiles such as snowballs
                    // --

                    if (attribute.startsWith("activehand")) {
                        return new ElementTag(player.getPlayerEntity().getMetadata("activehand").get(0).asString());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.is_seated>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns whether the player is seated
                    // --

                    if (attribute.startsWith("is_seated")) {
                        return new ElementTag(player.getPlayerEntity().getMetadata("seated").get(0).asBoolean());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.is_crawling>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns whether the player is crawling
                    // --

                    if (attribute.startsWith("is_crawling")) {
                        return new ElementTag(vp.crawling);
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.headset_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns the LocationTag for the VR headset
                    // -->

                    if (attribute.startsWith("headset_location")) {
                        String aim = player.getPlayerEntity().getMetadata("head.aim").get(0).asString();
                        return new LocationTag(((Location) player.getPlayerEntity().getMetadata("head.pos").get(0).value()).setDirection(convertVector(aim).toVector()));
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.righthand_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns the LocationTag for the righthand controller
                    // -->

                    if (attribute.startsWith("righthand_location")) {
                        String aim = player.getPlayerEntity().getMetadata("righthand.aim").get(0).asString();
                        return new LocationTag(((Location) player.getPlayerEntity().getMetadata("righthand.pos").get(0).value()).setDirection(convertVector(aim).toVector()));
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.lefthand_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns the LocationTag for the lefthand controller
                    // -->

                    if (attribute.startsWith("lefthand_location")) {
                        String aim = player.getPlayerEntity().getMetadata("lefthand.aim").get(0).asString();
                        return new LocationTag(((Location) player.getPlayerEntity().getMetadata("lefthand.pos").get(0).value()).setDirection(convertVector(aim).toVector()));
                    }

                }
            }
            return null;
        });
    }
}
