package com.denizenscript.depenizen.bukkit.bridges;

import com.denizenscript.depenizen.bukkit.properties.factions.FactionsPlayerNPCProperties;
import com.denizenscript.depenizen.bukkit.objects.factions.FactionTag;
import com.denizenscript.depenizen.bukkit.Bridge;
import com.denizenscript.depenizen.bukkit.utilities.BridgeLoadException;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.denizenscript.depenizen.bukkit.properties.factions.FactionsLocationProperties;
import com.denizenscript.denizen.objects.LocationTag;
import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizencore.objects.ObjectFetcher;
import com.denizenscript.denizencore.objects.TagRunnable;
import com.denizenscript.denizencore.objects.properties.PropertyParser;
import com.denizenscript.denizencore.tags.ReplaceableTagEvent;
import com.denizenscript.denizencore.objects.core.ListTag;
import com.denizenscript.denizencore.tags.Attribute;
import com.denizenscript.denizencore.tags.TagManager;

public class FactionsBridge extends Bridge {

    @Override
    public void init() {
        if (plugin.getDescription().getVersion().startsWith("1.")) {
            throw new BridgeLoadException("Only Factions 1.x.x is supported.");
        }
        ObjectFetcher.registerWithObjectFetcher(FactionTag.class);
        PropertyParser.registerProperty(FactionsPlayerNPCProperties.class, NPCTag.class);
        PropertyParser.registerProperty(FactionsPlayerNPCProperties.class, PlayerTag.class);
        PropertyParser.registerProperty(FactionsLocationProperties.class, LocationTag.class);
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                factionTagEvent(event);
            }
        }, "faction");
        TagManager.registerTagHandler(new TagRunnable.RootForm() {
            @Override
            public void run(ReplaceableTagEvent event) {
                tagEvent(event);
            }
        }, "factions");
    }

    public void factionTagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes().fulfill(1);
        // <--[tag]
        // @attribute <faction[<name>]>
        // @returns FactionTag
        // @description
        // Returns the faction for the input name.
        // @Plugin Depenizen, Factions
        // -->
        String nameOrId = attribute.getContext(1);
        Faction f = FactionColl.get().getByName(nameOrId);
        if (f == null && FactionColl.get().containsId(nameOrId)) {
            f = FactionColl.get().get(nameOrId);
        }
        if (f != null) {
            event.setReplacedObject(new FactionTag(f).getObjectAttribute(attribute.fulfill(1)));
        }

    }

    public void tagEvent(ReplaceableTagEvent event) {
        Attribute attribute = event.getAttributes().fulfill(1);

        // <--[tag]
        // @attribute <factions.list_factions>
        // @returns ListTag(dFaction)
        // @description
        // Returns a list of all current factions.
        // @Plugin Depenizen, Factions
        // -->
        if (attribute.startsWith("list_factions")) {
            ListTag factions = new ListTag();
            for (Faction f : FactionColl.get().getAll()) {
                factions.addObject(new FactionTag(f));
            }
            event.setReplacedObject(factions.getObjectAttribute(attribute.fulfill(1)));
        }

    }
}
