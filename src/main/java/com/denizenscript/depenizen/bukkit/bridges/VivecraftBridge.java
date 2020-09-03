package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.denizen.nms.NMSHandler;
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
                if (VSE.isVive(player.getPlayerEntity())) {
                    VivePlayer vp = new VivePlayer(player.getPlayerEntity());

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.is_vr>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns whether the player is using Vivecraft
                    // -->

                    if (attribute.startsWith("crawling")) {
                        return new ElementTag(vp.isVR());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.crawling>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // Returns whether the player is crawling
                    // -->

                    if (attribute.startsWith("crawling")) {
                        return new ElementTag(vp.crawling);
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.headset_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("headset_location")) {
                        LocationTag pos = new LocationTag(((Location)player.getPlayerEntity().getMetadata("head.pos").get(0).value()));
                        LocationTag offset = new LocationTag(((Location)player.getPlayerEntity().getMetadata("head.pos").get(0).value())).add(convertVector(player.getPlayerEntity().getMetadata("head.aim").get(0).asString()));
                        return new LocationTag(NMSHandler.getEntityHelper().faceLocation(pos, offset));
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.righthand_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("righthand_location")) {
                        LocationTag pos = new LocationTag(((Location)player.getPlayerEntity().getMetadata("righthand.pos").get(0).value()));
                        LocationTag offset = new LocationTag(((Location)player.getPlayerEntity().getMetadata("righthand.pos").get(0).value())).add(convertVector(player.getPlayerEntity().getMetadata("righthand.aim").get(0).asString()));
                        return new LocationTag(NMSHandler.getEntityHelper().faceLocation(pos, offset));
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.lefthand_location>
                    // @returns LocationTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("lefthand_location")) {
                        LocationTag pos = new LocationTag(((Location)player.getPlayerEntity().getMetadata("lefthand.pos").get(0).value()));
                        LocationTag offset = new LocationTag(((Location)player.getPlayerEntity().getMetadata("lefthand.pos").get(0).value())).add(convertVector(player.getPlayerEntity().getMetadata("lefthand.aim").get(0).asString()));
                        return new LocationTag(NMSHandler.getEntityHelper().faceLocation(pos, offset));
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.headset_rotation_x>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("headset_rotation_x")) {
                        return convertVector(player.getPlayerEntity().getMetadata("head.aim").get(0).asString());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.righthand_rotation_x>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("righthand_rotation_x")) {
                        return convertVector(player.getPlayerEntity().getMetadata("righthand.aim").get(0).asString());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.righthand_aim>
                    // @returns LocationTag(Vector)
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->
                    if (attribute.startsWith("righthand_aim")) {
                        return convertVector(player.getPlayerEntity().getMetadata("righthand.aim").get(0).asString());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.lefthand_aim>
                    // @returns LocationTag(Vector)
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->
                    if (attribute.startsWith("lefthand_aim")) {
                        return convertVector(player.getPlayerEntity().getMetadata("lefthand.aim").get(0).asString());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.lefthand_rotation_x>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("lefthand_rotation_x")) {
                        return new ElementTag(((Vector)player.getPlayerEntity().getMetadata("lefthand.aim").get(0).value()).getX());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.headset_rotation_y>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("headset_rotation_y")) {
                        return new ElementTag(((Vector)player.getPlayerEntity().getMetadata("head.aim").get(0).value()).getY());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.righthand_rotation_y>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("righthand_rotation_y")) {
                        return new ElementTag(((Vector)player.getPlayerEntity().getMetadata("righthand.aim").get(0).value()).getY());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.lefthand_rotation_y>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("lefthand_rotation_y")) {
                        return new ElementTag(((Vector)player.getPlayerEntity().getMetadata("lefthand.aim").get(0).value()).getY());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.headset_rotation_z>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("headset_rotation_z")) {
                        return new ElementTag(((Vector)player.getPlayerEntity().getMetadata("head.aim").get(0).value()).getZ());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.righthand_rotation_z>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("righthand_rotation_z")) {
                        return new ElementTag(((Vector)player.getPlayerEntity().getMetadata("righthand.aim").get(0).value()).getZ());
                    }

                    // <--[tag]
                    // @attribute <PlayerTag.vivecraft.lefthand_rotation_z>
                    // @returns ElementTag
                    // @plugin Depenizen, ViveCraft
                    // @description
                    // TODO
                    // -->

                    if (attribute.startsWith("lefthand_rotation_z")) {
                        return new ElementTag(((Vector)player.getPlayerEntity().getMetadata("lefthand.aim").get(0).value()).getZ());
                    }

                } else {
                    return null;
                }
            }
            return null;
        });
    }
}
