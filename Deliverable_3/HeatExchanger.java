public class HeatExchanger {
    private double legLength;
    private double pipeWallCond;
    private double absRoughness;
    private double dOuterInside;
    private double dInnerOutside;
    private double dInnerInside;
    private int noOfHairpins;

    private Fluid innerFluid;
    private Fluid annulusFluid;
    private boolean isCountercurrent;

    public HeatExchanger(Fluid innerFluid, Fluid annulusFluid, double legLength, double pipeWallCond, double absRoughness, double dOuterInside, double dInnerOutside, double dInnerInside, int noOfHairpins, boolean isCountercurrent){
        if (innerFluid == null || annulusFluid == null) System.exit(0);
        if (legLength < 0.0) System.exit(0);
        if (pipeWallCond < 0.0) System.exit(0);
        if (absRoughness < 0.0) System.exit(0);
        if (dOuterInside < 0.0) System.exit(0);
        if (dInnerOutside < 0.0) System.exit(0);
        if (dInnerInside < 0.0) System.exit(0);
        if (noOfHairpins < 0) System.exit(0);

        this.innerFluid = new Fluid(innerFluid);
        this.annulusFluid = new Fluid(annulusFluid);
        this.legLength = legLength;
        this.pipeWallCond = pipeWallCond;
        this.absRoughness = absRoughness;
        this.dOuterInside = dOuterInside;
        this.dInnerOutside = dInnerOutside;
        this.dInnerInside = dInnerInside;
        this.noOfHairpins = noOfHairpins;
        this.isCountercurrent = isCountercurrent;
    } // constructor

    public HeatExchanger(HeatExchanger source){
        if (source == null) System.exit(0);
        this.innerFluid = new Fluid(source.innerFluid);
        this.annulusFluid = new Fluid(source.annulusFluid);
        this.legLength = source.legLength;
        this.pipeWallCond = source.pipeWallCond;
        this.absRoughness = source.absRoughness;
        this.dOuterInside = source.dOuterInside;
        this.dInnerOutside = source.dInnerOutside;
        this.dInnerInside = source.dInnerInside;
        this.noOfHairpins = source.noOfHairpins;
        this.isCountercurrent = source.isCountercurrent;
    } // copy constructor

    @Override // unsure if this is needed
    public HeatExchanger clone(){
        return new HeatExchanger(this);
    }

    @Override
    public boolean equals(Object comparator){
        if (comparator == null) return false;
        if (comparator.getClass() != this.getClass()) return false;
        HeatExchanger obj = (HeatExchanger) comparator;
        if(!this.innerFluid.equals(obj.innerFluid)) return false;
        if(!this.annulusFluid.equals(obj.annulusFluid)) return false;
        if(this.legLength != obj.legLength) return false;
        if(this.pipeWallCond != obj.pipeWallCond) return false;
        if(this.absRoughness != obj.absRoughness) return false;
        if(this.dOuterInside != obj.dOuterInside) return false;
        if(this.dInnerOutside != obj.dInnerOutside) return false;
        if(this.dInnerInside != obj.dInnerInside) return false;
        if(this.noOfHairpins != obj.noOfHairpins) return false;
        if(this.isCountercurrent != obj.isCountercurrent) return false;
        return true;
    } // equals

    // all getters
    public Fluid getInnerFluid(){return new Fluid(this.innerFluid);}
    public Fluid getAnnulusFluid(){return new Fluid(this.annulusFluid);}
    public double getLegLength(){return this.legLength;}
    public double getPipeWallCond(){return this.pipeWallCond;}
    public double getAbsRoughness(){return this.absRoughness;}
    public double getDOuterInside(){return this.dOuterInside;}
    public double getDInnerOutside(){return this.dInnerOutside;}
    public double getDInnerInside(){return this.dInnerInside;}
    public int getNoOfHairpins(){return this.noOfHairpins;}
    public boolean getIsCountercurrent(){return this.isCountercurrent;}

    // all setters
    public boolean setInnerFluid(Fluid innerFluid){
        if (innerFluid == null) return false;
        this.innerFluid = new Fluid(innerFluid);
        return true;
    }
    public boolean setAnnulusFluid(Fluid annulusFluid){
        if (annulusFluid == null) return false;
        this.annulusFluid = new Fluid(annulusFluid);
        return true;
    }
    public boolean setLegLength(double legLength){
        if (legLength < 0.0) return false;
        this.legLength = legLength;
        return true;
    }
    public boolean setPipeWallCond(double pipeWallCond){
        if (pipeWallCond < 0.0) return false;
        this.pipeWallCond = pipeWallCond;
        return true;
    }
    public boolean setAbsRoughness(double absRoughness){
        if (absRoughness < 0.0) return false;
        this.absRoughness = absRoughness;
        return true;
    }
    public boolean setDOuterInside(double dOuterInside){
        if (dOuterInside < 0.0) return false;
        this.dOuterInside = dOuterInside;
        return true;
    }
    public boolean setDInnerOutside(double dInnerOutside){
        if (dInnerOutside < 0.0) return false;
        this.dInnerOutside = dInnerOutside;
        return true;
    }
    public boolean setDInnerInside(double dInnerInside){
        if (dInnerInside < 0.0) return false;
        this.dInnerInside = dInnerInside;
        return true;
    }
    public boolean setNoOfHairpins(int noOfHairpins){
        if (noOfHairpins < 0) return false;
        this.noOfHairpins = noOfHairpins;
        return true;
    }
    public boolean setIsCountercurrent(boolean isCountercurrent){
        this.isCountercurrent = isCountercurrent;
        return true;
    }
}
