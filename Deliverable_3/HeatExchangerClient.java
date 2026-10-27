public class HeatExchangerClient {
    private HeatExchanger HEX;

    public HeatExchangerClient(HeatExchanger HEX) {
        if (HEX == null) System.exit(0);
        this.HEX = new HeatExchanger(HEX);
    }

    public HeatExchangerClient(HeatExchangerClient source) {
        if (source == null) System.exit(0);
        this.HEX = new HeatExchanger(source.HEX);
    }

    @Override // unsure if this is needed
    public HeatExchangerClient clone() {
        return new HeatExchangerClient(this);
    }

    @Override
    public boolean equals(Object comparator) {
        if (comparator == null) return false;
        if (comparator.getClass() != this.getClass()) return false;
        HeatExchangerClient obj = (HeatExchangerClient) comparator;
        if (!this.HEX.equals(obj.HEX)) return false;
        return true;
    }

    // getter and setter
    public HeatExchanger getHEX(){return new HeatExchanger(this.HEX);}
    public boolean setHEX(HeatExchanger HEX){
        if (HEX == null) return false;
        this.HEX = new HeatExchanger(HEX);
        return true;
    }

    // start of all the calculation methods

    public double innerVelo(){
        double innerPipeArea = Math.PI * HEX.getDInnerInside() * HEX.getDInnerInside() * 0.25;
        return HEX.getInnerFluid().getFlowRate() / (HEX.getInnerFluid().getDensity() * innerPipeArea);
    }

    public double annulusVelo(){
        double annulusArea = Math.PI * (Math.pow(HEX.getDOuterInside(),2) - Math.pow(HEX.getDInnerOutside(),2)) * 0.25;
        return HEX.getAnnulusFluid().getFlowRate() / (annulusArea * HEX.getAnnulusFluid().getDensity());
    }

    public double innerRe(){
        return (this.innerVelo() * HEX.getDInnerInside() * HEX.getInnerFluid().getDensity()) / HEX.getInnerFluid().getDynViscosity();
    }

    public double annulusRe(){
        double annulusHydroD = HEX.getDOuterInside() - HEX.getDInnerOutside();
        return HEX.getAnnulusFluid().getDensity() * annulusHydroD * annulusVelo() / HEX.getAnnulusFluid().getDynViscosity();
    }

    public double prandtl(Fluid fluid){
        return fluid.getDynViscosity() * fluid.getSpecHeatCap() / fluid.getThermCond();
    }

    public String streamCheck(double re, double pr){
        String regime = "";
        if (re <= 2300) {
            regime = "Laminar";
        }
        else if (re >= 10000) {
            regime = "Turbulent";
        }
        else {
            System.out.println("Process Reynolds between 2300 and 10000");
            System.out.println("No correlation for process flow regime");
            System.exit(0);
        }
        if (pr < 0.5 || pr > 2000){
            System.out.println("Water Prandtl not between 0.5 and 2000");
            System.out.println("No correlation for water flow regime");
            System.exit(0);
        }
        return regime;
    }

    public double innerNu(){
        double nu;
        double re = innerRe();
        double pr = prandtl(HEX.getInnerFluid());
        String regime = streamCheck(re, pr);
        if(regime.equals("Turbulent")){
            double fricFactor = Math.pow(-1.8 * Math.log10(6.9/re), -2);
            nu =((fricFactor/8)*(re - 1000)*pr) / (1 + 12.7 * Math.sqrt(fricFactor/8)*(Math.pow(pr,2.0/3.0)-1));
        }else{
            double totalLeg = HEX.getLegLength() * 2 * HEX.getNoOfHairpins();
            double graetz = re * pr * HEX.getDInnerInside() / totalLeg;
            nu = 3.66 + ((0.0668 * graetz) / (1 + 0.04 * Math.pow(graetz,2.0/3.0)));
        }
        return nu;
    }

    public double annulusNu(){
        double re = annulusRe();
        double pr = prandtl(HEX.getAnnulusFluid());
        String regime = streamCheck(re, pr);
        double totalLeg = HEX.getLegLength() * 2 * HEX.getNoOfHairpins();
        double annulusHydroD = HEX.getDOuterInside() - HEX.getDInnerOutside();
        double dRatio = HEX.getDInnerOutside() / HEX.getDOuterInside();
        double nu = 0;
        if (regime.equals("Laminar")){
            if (dRatio < 0.05){
                System.out.println("Diameter ratio out of range");
                System.out.println("No correlation available for annulus Nusselt");
                System.exit(0);
            }
            else if (dRatio >= 0.05 && dRatio < 0.10){
                nu = 17.46 + (11.56 - 17.46)*((dRatio - 0.05)/(0.10 - 0.05));
            }
            else if (dRatio >= 0.10 && dRatio < 0.25){
                nu = 11.56 + (7.37 - 11.56)*((dRatio - 0.10)/(0.25 - 0.10));
            }
            else if (dRatio >= 0.25 && dRatio < 0.50){
                nu = 7.37 + (5.74 - 7.37)*((dRatio - 0.25)/(0.50 - 0.25));
            }
            else if (dRatio >= 0.50 && dRatio < 1.00){
                nu = 5.74 + (4.86 - 5.74)*((dRatio - 0.50)/(1.00 - 0.50));
            }
            else {
                System.out.println("Diameter ratio out of range");
                System.out.println("No correlation available for annulus Nusselt");
                System.exit(0);
            }
        }
        else {
            double reModified = re * (((1 + Math.pow(dRatio,2))*Math.log(dRatio) + (1 - Math.pow(dRatio,2))) / (Math.pow(1 - dRatio,2) * Math.log(dRatio)));
            double fricFactor = Math.pow(1.8 * Math.log10(reModified) - 1.5, -2);
            double correction = 1.07 + (900 / re) - (0.63 / (1 + 10 * pr));
            double geometryFactor = 0.75 * Math.pow(dRatio,-0.17);
            double lengthCorr = 1 + Math.pow(annulusHydroD / totalLeg,0.66666666666666667);
            nu = lengthCorr * geometryFactor * (fricFactor/8) * re * pr / (correction + 12.7 * Math.sqrt(fricFactor/8) * (Math.pow(pr,0.66666666666666667) - 1));
        }
        return nu;
    }

    public double innerPressureDrop(){
        double re = innerRe();
        double pr = prandtl(HEX.getInnerFluid());
        String regime = streamCheck(re, pr);
        double d_h = HEX.getDInnerInside();
        double totalLeg = HEX.getLegLength() * 2 * HEX.getNoOfHairpins();
        double minorLossCoeff = 1.5 * (1 + HEX.getNoOfHairpins()) + 2 * Math.max(0, HEX.getNoOfHairpins() - 1);
        double fricFactor=0;
        if (regime.equals("Laminar")){
            fricFactor = 64 / re;
        }
        else {
            fricFactor = Math.pow(-1.8 * Math.log10(Math.pow((HEX.getAbsRoughness() /(3.7 * d_h)),1.11) + (6.9 / re)),-2);
        }
        return (fricFactor * (totalLeg / d_h) + minorLossCoeff) * (0.5 * HEX.getInnerFluid().getDensity() * innerVelo() * innerVelo());
    }

    public double annulusPressureDrop(){
        double re = annulusRe();
        double pr = prandtl(HEX.getAnnulusFluid());
        String regime = streamCheck(re, pr);
        double d_h = HEX.getDOuterInside() - HEX.getDInnerOutside();
        double totalLeg = HEX.getLegLength() * 2 * HEX.getNoOfHairpins();
        double minorLossCoeff = 1.5 * (1 + HEX.getNoOfHairpins()) + 2 * Math.max(0, HEX.getNoOfHairpins() - 1);
        double fricFactor=0;
        if (regime.equals("Laminar")){
            double dRatio = HEX.getDInnerOutside() / HEX.getDOuterInside();
            fricFactor = (64 / re) * ((Math.pow(1 - dRatio,2)) / (1 + (dRatio * dRatio) + ((1 - Math.pow(dRatio,2)) / Math.log(dRatio))));
        }
        else {
            fricFactor = Math.pow(-1.8 * Math.log10(Math.pow((HEX.getAbsRoughness() /(3.7 * d_h)),1.11) + (6.9 / re)),-2);
        }
        return (fricFactor * (totalLeg / d_h) + minorLossCoeff) * (0.5 * HEX.getAnnulusFluid().getDensity() * annulusVelo() * annulusVelo());
    }

    public double heatTransferRate(){
        double totalLeg = HEX.getLegLength() * 2 * HEX.getNoOfHairpins();
        double annulusHydroD = HEX.getDOuterInside() - HEX.getDInnerOutside();
        double overallHeatTransCoeff = 0;

        double innerHeatTransCoeff = innerNu() * HEX.getInnerFluid().getThermCond() / HEX.getDInnerInside();
        double annulusHeatTransCoeff = annulusNu() * HEX.getAnnulusFluid().getThermCond() / annulusHydroD;

        double innerPipeConvRes = HEX.getDInnerOutside() / (HEX.getDInnerInside() * innerHeatTransCoeff);
        double innerPipeFoulRes = HEX.getDInnerOutside() * HEX.getInnerFluid().getFoulRes() / HEX.getDInnerInside();
        double pipeWallCondRes = (HEX.getDInnerOutside() * Math.log(HEX.getDInnerOutside() /HEX.getDInnerInside())) / (2 * HEX.getPipeWallCond());
        double annulusFoulRes = HEX.getAnnulusFluid().getFoulRes();
        double annulusConvRes = 1 / annulusHeatTransCoeff;
        double totalThermalRes = innerPipeConvRes + innerPipeFoulRes + pipeWallCondRes + annulusFoulRes + annulusConvRes;
        overallHeatTransCoeff = 1 / totalThermalRes;

        double heatTransArea = Math.PI * HEX.getDInnerOutside() * totalLeg;

        double innerHeatCapRate = HEX.getInnerFluid().getSpecHeatCap() * HEX.getInnerFluid().getFlowRate();
        double annulusHeatCapRate = HEX.getAnnulusFluid().getSpecHeatCap() * HEX.getAnnulusFluid().getFlowRate();
        double minHeatCapRate = Math.min(innerHeatCapRate,annulusHeatCapRate);
        double maxHeatCapRate = Math.max(innerHeatCapRate,annulusHeatCapRate);

        double heatCapRatio = minHeatCapRate / maxHeatCapRate;
        double NTU = overallHeatTransCoeff * heatTransArea / minHeatCapRate;
        double effectiveness = 0;

        if (HEX.getIsCountercurrent() == true){
            if (heatCapRatio == 1.0){
                effectiveness = NTU / (1 + NTU);
            }
            else {
                effectiveness = (1 - Math.exp(-1 * NTU * (1 - heatCapRatio))) / (1 - heatCapRatio * Math.exp(-1 * NTU * (1 - heatCapRatio)));
            }
        }
        else {
            effectiveness = (1 - Math.exp(-1 * NTU * (1 + heatCapRatio))) / (1 + heatCapRatio);
        }

        double highTemp = Math.max(HEX.getInnerFluid().getInletTemp(), HEX.getAnnulusFluid().getInletTemp());
        double lowTemp = Math.min(HEX.getInnerFluid().getInletTemp(), HEX.getAnnulusFluid().getInletTemp());

        return (effectiveness * minHeatCapRate * (highTemp - lowTemp));
    } // returns the heat transfer rate

    public double outletTemp(Fluid fluid){
        double heatCapRate = fluid.getSpecHeatCap() * fluid.getFlowRate();
        if (fluid.getInletTemp() >= HEX.getInnerFluid().getInletTemp() && fluid.getInletTemp() >= HEX.getAnnulusFluid().getInletTemp()){
            return fluid.getInletTemp() - (heatTransferRate() / heatCapRate);
        }
        else {
            return fluid.getInletTemp() + (heatTransferRate() / heatCapRate);
        }
    }

    public double minApproachTemp(){
        double deltaTempMin = 0;
        double hotInletTemp;
        double coldInletTemp;
        double hotOutletTemp;
        double coldOutletTemp;

        if (HEX.getInnerFluid().getInletTemp() >= HEX.getAnnulusFluid().getInletTemp()){
            hotInletTemp = HEX.getInnerFluid().getInletTemp();
            coldInletTemp = HEX.getAnnulusFluid().getInletTemp();
            hotOutletTemp = outletTemp(HEX.getInnerFluid());
            coldOutletTemp = outletTemp(HEX.getAnnulusFluid());
        }
        else{
            hotInletTemp = HEX.getAnnulusFluid().getInletTemp();
            coldInletTemp = HEX.getInnerFluid().getInletTemp();
            hotOutletTemp = outletTemp(HEX.getAnnulusFluid());
            coldOutletTemp = outletTemp(HEX.getInnerFluid());
        }

        //Parallel flow
        if(HEX.getIsCountercurrent() == false){
            deltaTempMin=Math.min(hotInletTemp-coldInletTemp,hotOutletTemp-coldOutletTemp);
        }
        //Countercurrent flow
        else
        {
            deltaTempMin=Math.min(hotInletTemp-coldOutletTemp,hotOutletTemp-coldInletTemp);
        }
        return deltaTempMin;
    }
}
