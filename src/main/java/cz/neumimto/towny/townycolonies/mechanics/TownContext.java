package cz.neumimto.towny.townycolonies.mechanics;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import cz.neumimto.towny.townycolonies.config.Structure;
import cz.neumimto.towny.townycolonies.model.LoadedStructure;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TownContext {
    public Town town;
    public Resident resident;

    public Player player;

    public Structure structure;
    public Location structureCenterLocation;

    public LoadedStructure loadedStructure;
}
