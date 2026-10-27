import java.util.Scanner;
import java.text.DecimalFormat;

public class Driver {
    public static void main(String[] args){
        Scanner scan = new Scanner (System.in);
        DecimalFormat df = new DecimalFormat("0.00");

        double densityProcess = 1040; // kg/m3
        double dynViscosityProcess = 0.00164; // Pa s
        double specHeatCapProcess = 3600; // J/kg K
        double thermCondProcess = 0.44; // W / mK
        double foulResProcess = 0.0002; // m2K/W

        double processInletTemp = 25; // °C
        double processFlowRate = 2.5; // kg/s

        Fluid processFluid = new Fluid(densityProcess, dynViscosityProcess, specHeatCapProcess, thermCondProcess, foulResProcess, processInletTemp, processFlowRate);

        double densityWater = 975; // kg/m3
        double dynViscosityWater = 0.00038; // Pa s
        double specHeatCapWater = 4200; // J/kg K
        double thermCondWater = 0.66; // W / mK
        double foulResWater = 0.0001; // m2K/W

        double waterInletTemp = 90; // °C
        double waterFlowRate = 2; // kg/s

        Fluid hotWater = new Fluid(densityWater, dynViscosityWater, specHeatCapWater, thermCondWater, foulResWater, waterInletTemp, waterFlowRate);

        double legLength = 6; //m
        double pipeWallCond = 45; // W / mK
        double absRoughness = 0.000045; // m
        double dOuterInside = 0.102; // m
        double dInnerOutside = 0.060; // m
        double dInnerInside = 0.053; // m
        int noOfHairpins = 3;
        boolean isCounterCurrent = true;

        HeatExchanger HEX = new HeatExchanger(processFluid, hotWater, legLength, pipeWallCond, absRoughness, dOuterInside, dInnerOutside, dInnerInside, noOfHairpins, isCounterCurrent);
        HeatExchangerClient Client = new HeatExchangerClient(HEX);

        double pumpEff = 0.7;
        double annOperatingTime = 8000; // h/yr
        double annualizationFactor = 0.18;
        double elecPrice = 0.12; // CAD/kWh
        double installedCostPerModule = 11000; // CAD

        CostCenter Costing = new CostCenter(Client, pumpEff, annOperatingTime, annualizationFactor, elecPrice, installedCostPerModule);

        System.out.println("Heat transfer rate is "+df.format(Client.heatTransferRate())+" W");
        System.out.println("Hot water outlet temp is "+df.format(Client.outletTemp(Client.getHEX().getAnnulusFluid()))+" C");
        System.out.println("Process fluid outlet temp is "+df.format(Client.outletTemp(Client.getHEX().getInnerFluid()))+" C");
        System.out.println("");
        System.out.println("Hot water pumping cost is "+df.format(Costing.annulusPumpingCost())+" CAD/year");
        System.out.println("Process pumping cost is "+df.format(Costing.innerPumpingCost())+" CAD/year");
        System.out.println("Annualized cost is "+df.format(Costing.annualizedCapCost())+" CAD/year");
        System.out.println("");
        System.out.println("Total cost is "+df.format(Costing.HEXCost())+" CAD/year");
    }
}
