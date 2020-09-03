package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.core.ElementTag;
import com.denizenscript.depenizen.bukkit.Bridge;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.vivecraft.VSE;
import org.vivecraft.VivePlayer;

public class VivecraftBridge extends Bridge {

    public LocationTag convertVector(String vectorString) {
        String[] list = vectorString.substring(1, vectorString.length()-1).split(", ");
        Vector vector = new Vector(Double.parseDouble(list[0]), Double.parseDouble(list[1]), Double.parseDouble(list[2]));
        return new LocationTag(vector);
    }

    @Override
    public void init() {
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
                    VivePlayer vp = new VivePlayer(player.getPlayerEntity());

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
                    // @attribute <PlayerTag.vivecraft.is_teleport_mode>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns whether the player is in teleport mode
                    // --

                    if (attribute.startsWith("is_teleport_mode")) {
                        try {
                            VivePlayer.class.getDeclaredField("isTeleportMode").setAccessible(true);
                            return new ElementTag(String.valueOf((Boolean) vp.getClass().getDeclaredField("isTeleportMode").get(vp)));
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.is_reverse_hands>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns whether the player is in reverse hands mode
                    // --

                    if (attribute.startsWith("is_reverse_hands")) {
                        try {
                            VivePlayer.class.getDeclaredField("isReverseHands").setAccessible(true);
                            return new ElementTag(String.valueOf((Boolean) vp.getClass().getDeclaredField("isReverseHands").get(vp)));
                        } catch (IllegalAccessException | NoSuchFieldException e) {
                            e.printStackTrace();
                        }
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
                        String s = player.getPlayerEntity().getMetadata("head.aim").get(0).asString();
                        String[] list = s.substring(1, s.length()-1).split(", ");
                        return new LocationTag(((Location) player.getPlayerEntity().getMetadata("head.pos").get(0).value()).setDirection(new Vector(Double.parseDouble(list[0]), Double.parseDouble(list[1]), Double.parseDouble(list[2]))));
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.righthand_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns the LocationTag for the righthand controller
                    // -->

                    if (attribute.startsWith("righthand_location")) {
                        String s = player.getPlayerEntity().getMetadata("righthand.aim").get(0).asString();
                        String[] list = s.substring(1, s.length()-1).split(", ");
                        return new LocationTag(((Location) player.getPlayerEntity().getMetadata("righthand.pos").get(0).value()).setDirection(new Vector(Double.parseDouble(list[0]), Double.parseDouble(list[1]), Double.parseDouble(list[2]))));
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.lefthand_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns the LocationTag for the lefthand controller
                    // -->

                    if (attribute.startsWith("lefthand_location")) {
                        String s = player.getPlayerEntity().getMetadata("lefthand.aim").get(0).asString();
                        String[] list = s.substring(1, s.length()-1).split(", ");
                        return new LocationTag(((Location) player.getPlayerEntity().getMetadata("lefthand.pos").get(0).value()).setDirection(new Vector(Double.parseDouble(list[0]), Double.parseDouble(list[1]), Double.parseDouble(list[2]))));
                    }
                }
            }
            return null;
        });
    }
}