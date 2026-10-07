public class Initialize {

    // Assumed Values
    public static final double LegLength = 6; //m
    public static final double DensityProcess = 1040; // kg/m3
    public static final double DensityWater = 975; // kg/m3
    public static final double DynViscosityProcess = 0.00164; // Pa s
    public static final double DynViscosityWater = 0.00038; // Pa s
    public static final double SpecHeatCapProcess = 3600; // J/kg K
    public static final double SpecHeatCapWater = 4200; // J/kg K
    public static final double ThermCondProcess = 0.44; // W / mK
    public static final double ThermCondWater = 0.66; // W / mK
    public static final double FoulResProcess = 0.0002; // m2K/W
    public static final double FoulResWater = 0.0001; // m2K/W
    public static final double PipeWallCond = 45; // W / mK
    public static final double AbsRoughness = 0.000045; // m
    public static final double PumpEff = 0.7;
    public static final double AnnOperatingTime = 8000; // h/yr
    public static final double AnnualizationFactor = 0.18;
    public static final double ElecPrice = 0.12; // CAD/kWh

    // Prescribed Design Metrics
    public static final double DOuterInside = 0.102; // m
    public static final double DInnerOutside = 0.060; // m
    public static final double DInnerInside = 0.053; // m
    public static final double NoOfHairpins = 3;
    public static final double InstalledCostPerModule = 11000; // CAD

}
