package mekanism.common.util;

import mekanism.api.IHeatTransfer;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class HeatUtils {

    public static double[] simulate(IHeatTransfer source) {
        double[] heatTransferred = new double[]{0, 0};
        for (Direction side : Direction.values()) {
            IHeatTransfer sink = source.getAdjacent(side);
            if (sink != null) {
                double invConduction = sink.getInverseConductionCoefficient() + source.getInverseConductionCoefficient();
                double heatToTransfer = source.getTemp() / invConduction;
                source.transferHeatTo(-heatToTransfer);
                sink.transferHeatTo(heatToTransfer);
                if (!(sink instanceof ICapabilityProvider && ((ICapabilityProvider) sink).getCapability(Capabilities.GRID_TRANSMITTER_CAPABILITY, side.getOpposite()).isPresent())) {
                    heatTransferred[0] += heatToTransfer;
                }
                continue;
            }

            //Transfer to air otherwise
            double invConduction = IHeatTransfer.AIR_INVERSE_COEFFICIENT + source.getInsulationCoefficient(side) + source.getInverseConductionCoefficient();
            double heatToTransfer = source.getTemp() / invConduction;
            source.transferHeatTo(-heatToTransfer);
            heatTransferred[1] += heatToTransfer;
        }
        return heatTransferred;
    }
}