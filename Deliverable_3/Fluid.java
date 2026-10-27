public class Fluid {
    private double density;
    private double dynViscosity;
    private double specHeatCap;
    private double thermCond;
    private double foulRes;
    private double inletTemp;
    private double flowRate;

    public Fluid(double density, double dynViscosity, double specHeatCap, double thermCond, double foulRes, double inletTemp, double flowRate){
        if (density < 0.0) System.exit(0);
        if (dynViscosity < 0.0) System.exit(0);
        if (specHeatCap < 0.0) System.exit(0);
        if (thermCond < 0.0) System.exit(0);
        if (foulRes < 0.0) System.exit(0);
        if (inletTemp < -273.15) System.exit(0); // abs 0 in celcius
        if (flowRate < 0.0) System.exit(0);

        this.density = density;
        this.dynViscosity = dynViscosity;
        this.specHeatCap = specHeatCap;
        this.thermCond = thermCond;
        this.foulRes = foulRes;
        this.inletTemp = inletTemp;
        this.flowRate = flowRate;
    } // constructor

    public Fluid(Fluid source){
        if (source == null) System.exit(0);
        this.density = source.density;
        this.dynViscosity = source.dynViscosity;
        this.specHeatCap = source.specHeatCap;
        this.thermCond = source.thermCond;
        this.foulRes = source.foulRes;
        this.inletTemp = source.inletTemp;
        this.flowRate = source.flowRate;
    } // copy constructor

    @Override // unsure if this is needed
    public Fluid clone(){
        return new Fluid(this);
    }

    @Override
    public boolean equals(Object comparator){
        if (comparator == null) return false;
        if (comparator.getClass() != this.getClass()) return false;
        Fluid obj = (Fluid)comparator;
        if (obj.density != this.density) return false;
        if (obj.dynViscosity != this.dynViscosity) return false;
        if (obj.specHeatCap != this.specHeatCap) return false;
        if (obj.thermCond != this.thermCond) return false;
        if (obj.foulRes != this.foulRes) return false;
        if (obj.inletTemp != this.inletTemp) return false;
        if (obj.flowRate != this.flowRate) return false;
        return true;
    } // overridden equals

    // all getters
    public double getDensity(){return this.density;}
    public double getDynViscosity(){return this.dynViscosity;}
    public double getSpecHeatCap(){return this.specHeatCap;}
    public double getThermCond(){return this.thermCond;}
    public double getFoulRes(){return this.foulRes;}
    public double getInletTemp(){return this.inletTemp;}
    public double getFlowRate(){return this.flowRate;}

    // all setters
    public boolean setDensity(double density){
        if (density < 0.0) return false;
        this.density = density;
        return true;
    }
    public boolean setDynViscosity(double dynViscosity){
        if (dynViscosity < 0.0) return false;
        this.dynViscosity = dynViscosity;
        return true;
    }
    public boolean setSpecHeatCap(double specHeatCap){
        if (specHeatCap < 0.0) return false;
        this.specHeatCap = specHeatCap;
        return true;
    }
    public boolean setThermCond(double thermCond){
        if (thermCond < 0.0) return false;
        this.thermCond = thermCond;
        return true;
    }
    public boolean setFoulRes(double foulRes){
        if (foulRes < 0.0) return false;
        this.foulRes = foulRes;
        return true;
    }
    public boolean setInletTemp(double inletTemp){
        if (inletTemp < -273.15) return false;
        this.inletTemp = inletTemp;
        return true;
    }
    public boolean setFlowRate(double flowRate){
        if (flowRate < 0.0) return false;
        this.flowRate = flowRate;
        return true;
    }
}
