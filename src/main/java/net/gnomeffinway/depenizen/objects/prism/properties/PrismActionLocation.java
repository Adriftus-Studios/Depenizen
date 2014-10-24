package net.gnomeffinway.depenizen.objects.prism.properties;

import me.botsko.prism.actions.Handler;
import net.aufdemrand.denizen.objects.Mechanism;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dObject;
import net.aufdemrand.denizen.objects.properties.Property;
import net.aufdemrand.denizen.tags.Attribute;
import net.gnomeffinway.depenizen.objects.prism.PrismAction;
import org.bukkit.Bukkit;

public class PrismActionLocation implements Property {

    public static boolean describes(dObject obj) {
        return obj instanceof PrismAction;
    }

    public static PrismActionLocation getFrom(dObject action) {
        if (!describes(action)) return null;
        else return new PrismActionLocation((PrismAction) action);
    }

    ///////////////////
    // Instance Fields and Methods
    /////////////

    PrismAction prismAction;
    Handler action;

    private PrismActionLocation(PrismAction action) {
        this.prismAction = action;
        this.action = action.getAction();
    }

    @Override
    public String getPropertyString() {
        return new dLocation(Bukkit.getServer().getWorld(action.getWorldName()),
                action.getX(), action.getY(), action.getZ()).identifySimple();
    }

    @Override
    public String getPropertyId() {
        return "location";
    }

    @Override
    public String getAttribute(Attribute attribute) {

        // <--[tag]
        // @attribute <prism@action.location>
        // @returns dLocation
        // @description
        // Returns the location this action occurred at.
        // @plugin Prism
        // -->
        if (attribute.startsWith("location")) {
            return new dLocation(Bukkit.getServer().getWorld(action.getWorldName()),
                    action.getX(), action.getY(), action.getZ()).getAttribute(attribute.fulfill(1));
        }

        return null;

    }

    @Override
    public void adjust(Mechanism mechanism) {

        // No documentation, internal only
        if (mechanism.matches("location") && mechanism.requireObject(dLocation.class)) {
            prismAction.setLocation(action.getWorldName(), action.getX(), action.getY(), action.getZ());
        }

    }

}
