import java.text.DecimalFormat;

public class CostCenter {
    private double pumpEff;
    private double annualOperatingTime;
    private double annualizationFactor;
    private double elecPrice;
    private double installedCostPerModule;
    private HeatExchangerClient HEXClient;

    public CostCenter(HeatExchangerClient HEXClient, double pumpEff, double annualOperatingTime, double annualizationFactor, double elecPrice, double installedCostPerModule){
        if (HEXClient == null) System.exit(0);
        if (pumpEff < 0.0 || pumpEff > 1.0) System.exit(0);
        if (annualOperatingTime < 0.0 || annualOperatingTime > 8760.0) System.exit(0); // hours in a year
        if (annualizationFactor < 0.0) System.exit(0);
        if (elecPrice < 0.0) System.exit(0);
        if (installedCostPerModule < 0.0) System.exit(0);
        this.HEXClient = new HeatExchangerClient(HEXClient);
        this.pumpEff = pumpEff;
        this.annualOperatingTime = annualOperatingTime;
        this.annualizationFactor = annualizationFactor;
        this.elecPrice = elecPrice;
        this.installedCostPerModule = installedCostPerModule;
    }

    public CostCenter(CostCenter source){
        if (source == null) System.exit(0);
        this.HEXClient = new HeatExchangerClient(source.HEXClient);
        this.pumpEff = source.pumpEff;
        this.annualOperatingTime = source.annualOperatingTime;
        this.annualizationFactor = source.annualizationFactor;
        this.elecPrice = source.elecPrice;
        this.installedCostPerModule = source.installedCostPerModule;
    }

    @Override // unsure if this is needed
    public CostCenter clone(){
        return new CostCenter(this);
    }

    @Override
    public boolean equals(Object comparator){
        if (comparator == null) return false;
        if (comparator.getClass() != this.getClass()) return false;
        CostCenter obj = (CostCenter) comparator;
        if(!this.HEXClient.equals(obj.HEXClient)) return false;
        if(this.pumpEff != obj.pumpEff) return false;
        if(this.annualOperatingTime != obj.annualOperatingTime) return false;
        if(this.annualizationFactor != obj.annualizationFactor) return false;
        if(this.elecPrice != obj.elecPrice) return false;
        if(this.installedCostPerModule != obj.installedCostPerModule) return false;
        return true;
    } // equals

    // all getters
    public HeatExchangerClient getHEXClient(){return new HeatExchangerClient(this.HEXClient);}
    public double getPumpEff(){return this.pumpEff;}
    public double getAnnualOperatingTime(){return this.annualOperatingTime;}
    public double getAnnualizationFactor(){return this.annualizationFactor;}
    public double getElecPrice(){return this.elecPrice;}
    public double getInstalledCostPerModule(){return this.installedCostPerModule;}

    //all setters
    public boolean setHEXClient(HeatExchangerClient HEXClient){
        if (HEXClient == null) return false;
        this.HEXClient = new HeatExchangerClient(HEXClient);
        return true;
    }
    public boolean setPumpEff(double pumpEff){
        if (pumpEff < 0.0 || pumpEff > 1.0) return false;
        this.pumpEff = pumpEff;
        return true;
    }
    public boolean setAnnualOperatingTime(double annualOperatingTime){
        if (annualOperatingTime < 0.0 || annualOperatingTime > 8760.0) return false;
        this.annualOperatingTime = annualOperatingTime;
        return true;
    }
    public boolean setAnnualizationFactor(double annualizationFactor){
        if (annualizationFactor < 0.0) return false;
        this.annualizationFactor = annualizationFactor;
        return true;
    }
    public boolean setElecPrice(double elecPrice){
        if (elecPrice < 0.0) return false;
        this.elecPrice = elecPrice;
        return true;
    }
    public boolean setInstalledCostPerModule(double installedCostPerModule){
        if (installedCostPerModule < 0.0) return false;
        this.installedCostPerModule = installedCostPerModule;
        return true;
    }

    // now for the cost calculations
    public double innerPumpingCost(){
        double innerPumpPower = (HEXClient.innerPressureDrop() * (HEXClient.getHEX().getInnerFluid().getFlowRate() / HEXClient.getHEX().getInnerFluid().getDensity())) / this.pumpEff;
        return (innerPumpPower / 1000) * this.annualOperatingTime * this.elecPrice;
    }

    public double annulusPumpingCost(){
        double annulusPumpPower = (HEXClient.annulusPressureDrop() * (HEXClient.getHEX().getAnnulusFluid().getFlowRate() / HEXClient.getHEX().getAnnulusFluid().getDensity())) / this.pumpEff;
        return (annulusPumpPower / 1000) * this.annualOperatingTime * this.elecPrice;
    }

    public double annualizedCapCost(){
        return HEXClient.getHEX().getNoOfHairpins() * this.installedCostPerModule * this.annualizationFactor;
    }

    public double HEXCost(){
        return innerPumpingCost() + annulusPumpingCost() + annualizedCapCost();
    }

}
