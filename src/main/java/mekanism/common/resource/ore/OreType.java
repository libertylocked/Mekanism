package mekanism.common.resource.ore;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.common.resource.IResource;
import mekanism.common.resource.MiscResource;
import mekanism.common.resource.PrimaryResource;
import mekanism.common.world.height.HeightShape;
import net.minecraft.util.StringRepresentable;

public enum OreType implements StringRepresentable {
    //TODO - 1.18: Adjust default ore rates and add multiple ore features per type
    // also potentially do something similar to what large diamond veins do in relation to having a rarity filter?
    TIN(PrimaryResource.TIN,
          new BaseOreConfig("small", 14, 0, 8, HeightShape.UNIFORM, OreAnchor.aboveBottom(0), OreAnchor.absolute(60))
    ),
    OSMIUM(PrimaryResource.OSMIUM,
          new BaseOreConfig("small", 12, 0, 8, HeightShape.UNIFORM, OreAnchor.aboveBottom(0), OreAnchor.absolute(60))
    ),
    URANIUM(PrimaryResource.URANIUM,
          new BaseOreConfig("small", 8, 0, 8, HeightShape.UNIFORM, OreAnchor.aboveBottom(0), OreAnchor.absolute(60))
    ),
    FLUORITE(MiscResource.FLUORITE, 1, 4,
          new BaseOreConfig("small", 6, 0, 12, HeightShape.UNIFORM, OreAnchor.aboveBottom(0), OreAnchor.absolute(32))
    ),
    LEAD(PrimaryResource.LEAD,
          new BaseOreConfig("small", 8, 0, 8, HeightShape.UNIFORM, OreAnchor.aboveBottom(0), OreAnchor.absolute(48))
    );

    public static Codec<OreType> CODEC = StringRepresentable.fromEnum(OreType::values, OreType::byName);
    private static final Map<String, OreType> NAME_LOOKUP = Arrays.stream(values()).collect(Collectors.toMap(OreType::getSerializedName, oreType -> oreType));

    private final List<BaseOreConfig> baseConfigs;
    private final IResource resource;
    private final int minExp;
    private final int maxExp;

    OreType(IResource resource, BaseOreConfig... configs) {
        this(resource, 0, configs);
    }

    OreType(IResource resource, int exp, BaseOreConfig... configs) {
        this(resource, exp, exp, configs);
    }

    OreType(IResource resource, int minExp, int maxExp, BaseOreConfig... configs) {
        this.resource = resource;
        this.minExp = minExp;
        this.maxExp = maxExp;
        this.baseConfigs = List.of(configs);
    }

    public IResource getResource() {
        return resource;
    }

    public List<BaseOreConfig> getBaseConfigs() {
        return baseConfigs;
    }

    public int getMinExp() {
        return minExp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public static OreType get(IResource resource) {
        for (OreType ore : values()) {
            if (resource == ore.resource) {
                return ore;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public String getSerializedName() {
        return resource.getRegistrySuffix();
    }

    @Nullable
    private static OreType byName(String name) {
        return NAME_LOOKUP.get(name);
    }

    public record OreVeinType(OreType type, int index) {

        public static final Codec<OreVeinType> CODEC = RecordCodecBuilder.create(builder -> builder.group(
              OreType.CODEC.fieldOf("type").forGetter(config -> config.type),
              Codec.INT.fieldOf("index").forGetter(config -> config.index)
        ).apply(builder, OreVeinType::new));

        public OreVeinType {
            if (index < 0 || index >= type.getBaseConfigs().size()) {
                throw new IndexOutOfBoundsException("Vein Type index out of range: " + index);
            }
        }

        public String name() {
            return "ore_" + type.getResource().getRegistrySuffix() + "_" + type.getBaseConfigs().get(index).name();
        }
    }
}