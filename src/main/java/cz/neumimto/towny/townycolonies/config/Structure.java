package cz.neumimto.towny.townycolonies.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.Conversion;
import com.electronwill.nightconfig.core.conversion.Converter;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.conversion.Path;
import cz.neumimto.towny.townycolonies.TownyColonies;
import cz.neumimto.towny.townycolonies.mechanics.MechanicService;
import cz.neumimto.towny.townycolonies.mechanics.Mechanic;
import org.bukkit.Material;

import java.util.*;


public class Structure {

    @Path("Id")
    public String id;

    @Path("Name")
    public String name;

    @Path("Description")
    public List<String> description;

    @Path("Period")
    public long period;

    @Path("Material")
    @Conversion(MaterialConversion.class)
    public Material material;

    @Path("CustomModelData")
    public Integer customModelData;

    @Path("MaxCount")
    public Integer maxCount;

    @Path("AreaRadius")
    @Conversion(AreaConversion.class)
    public Area area;

    @Path("BuyRequirements")
    @Conversion(BuyReq.class)
    public List<LoadedPair<Mechanic<?>, ?>> buyRequirements;

    @Path("PlaceRequirements")
    @Conversion(BuyReq.class)
    public List<LoadedPair<Mechanic<?>, ?>> placeRequirements;

    @Path("BuildRequirements")
    @Conversion(BuyReq.class)
    public List<LoadedPair<Mechanic<?>, ?>> buildRequirements;

    @Path("Upkeep")
    @Conversion(Upkeep.class)
    public List<LoadedPair<Mechanic<Object>, Object>> upkeep;

    @Path("Blocks")
    @Conversion(Blocks.class)
    public Map<String, Integer> blocks;

    @Path("SaveEachNTicks")
    public int saveEachNTicks;

    @Path("Production")
    @Conversion(Production.class)
    public List<LoadedPair<Mechanic<Object>,Object>> production;

    public static class Area {
        public final int x;
        public final int z;
        public final int y;

        public Area(int x, int z, int y) {
            this.x = x;
            this.z = z;
            this.y = y;
        }
    }

    public static class LoadedPair<M, C> {
        public final C configValue;
        public final Mechanic<C> mechanic;

        public LoadedPair(C configValue, Mechanic<C> mechanic) {
            this.configValue = configValue;
            this.mechanic = mechanic;
        }
    }

    public static class Production extends ConfiguredMechanic {
        @Override
        protected Optional<Mechanic> mechanic(MechanicService service, String name) {
            return service.prodMech(name);
        }
    }


    public static class Upkeep extends ConfiguredMechanic {
        @Override
        protected Optional<Mechanic> mechanic(MechanicService service, String name) {
            return service.prodReq(name);
        }
    }

    public static class BuyReq extends ConfiguredMechanic {
        @Override
        protected Optional<Mechanic> mechanic(MechanicService service, String name) {
            return service.buyReq(name);
        }
    }

    public static class BuildReq extends ConfiguredMechanic {
        @Override
        protected Optional<Mechanic> mechanic(MechanicService service, String name) {
            return service.placeReq(name);
        }
    }

    public static abstract class ConfiguredMechanic implements Converter<List<?>, List<Config>> {

        protected abstract Optional<Mechanic> mechanic(MechanicService service, String name);

        @Override
        public List convertToField(List<Config> value) {
            List mechs = new ArrayList();
            if (value == null) {
                return mechs;
            }
            var registry = TownyColonies.injector.getInstance(MechanicService.class);

            for (Config config : value) {
                String mechanic = config.get("Mechanic");
                Optional<Mechanic> mech = mechanic(registry, mechanic);
                if (mech.isPresent()) {
                    Mechanic m = mech.get();
                    Object aNew = m.getNew();
                    new ObjectConverter().toObject(config, aNew);
                    mechs.add(new LoadedPair<>(aNew, m));
                }
            }

            return mechs;
        }

        @Override
        public List convertFromField(List value) {
            return null;
        }
    }

    public static class AreaConversion implements Converter<Area, String> {
        @Override
        public Area convertToField(String value) {
            String[] a = value.split("x");
            return new Area(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
        }

        @Override
        public String convertFromField(Area value) {
            return null;
        }
    }

    public static class Blocks implements Converter<Map, Config> {

        @Override
        public Map convertToField(Config value) {
            Map map = new HashMap();

            if (value != null) {
                Map<String, Object> stringObjectMap = value.valueMap();
                for (Map.Entry<String, Object> e : stringObjectMap.entrySet()) {
                    map.put(e.getKey(), Integer.parseInt(e.getValue().toString()));
                }
            }

            return map;
        }

        @Override
        public Config convertFromField(Map value) {
            return null;
        }
    }

    public static class MaterialConversion implements Converter<Material, String> {
        @Override
        public Material convertToField(String value) {
            return Material.matchMaterial(value);
        }

        @Override
        public String convertFromField(Material value) {
            return value.name();
        }
    }

}

