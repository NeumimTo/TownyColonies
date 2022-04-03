package cz.neumimto.towny.townycolonies;

import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.WorldCoord;
import cz.neumimto.towny.townycolonies.config.ConfigurationService;
import cz.neumimto.towny.townycolonies.config.Structure;
import cz.neumimto.towny.townycolonies.model.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Singleton
public class SubclaimService {

    @Inject
    private ConfigurationService configurationService;

    private Set<Region> subclaims = new HashSet<>();

    public Optional<Region> createRegion(StructureMetadata.LoadedStructure structure) {
        return createRegion(structure.id, structure.center);
    }

    public Optional<Region> createRegion(String id, Location center) {
        Optional<Structure> structureById = configurationService.findStructureById(id);
        if (structureById.isPresent()) {
            Structure def = structureById.get();
            BoundingBox of = BoundingBox.of(center, def.area.x, def.area.y, def.area.z);
            return  Optional.of(new Region(id, of, center.getWorld().getName()));
        }
        return Optional.empty();
    }

    public void registerRegion(Region value) {
        subclaims.add(value);
    }

    public Optional<Region> overlaps(Region region) {
        for (Region subclaim : subclaims) {
            if (subclaim.overlaps(region)) {
                return Optional.of(subclaim);
            }
        }
        return Optional.empty();
    }

    public boolean isOutsideTownClaim(Region region, Town town) {

        BoundingBox bb = region.boundingBox;
        double minX = bb.getMinX();
        double maxX = bb.getMaxX();

        double minZ = bb.getMinZ();
        double maxZ = bb.getMaxZ();

        World world = Bukkit.getWorld(region.world);
        Location location = new Location(world, minX, 0, minZ);

        Location location2 = new Location(world, minX, 0, maxZ);

        Location location3 = new Location(world, maxX, 0, minZ);

        Location location4 = new Location(world, maxX, 0, maxZ);

        return isOutsideTownClaim(location, town) ||
                isOutsideTownClaim(location2, town) ||
                isOutsideTownClaim(location3, town) ||
                isOutsideTownClaim(location4, town);
    }

    public boolean isOutsideTownClaim(Location location, Town town) {
        WorldCoord worldCoord = new WorldCoord(location.getWorld().getName(), Coord.parseCoord(location));

        TownBlock townBlock = worldCoord.getTownBlockOrNull();
        if (townBlock == null) {
            return true;
        }
        if (!townBlock.hasTown()) {
            return true;
        }
        Town townAtLoc = townBlock.getTownOrNull();
        if (town != townAtLoc) {
            return true;
        }
        return false;
    }
}