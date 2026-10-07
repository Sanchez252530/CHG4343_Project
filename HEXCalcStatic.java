import java.text.DecimalFormat;

public class HEXCalcStatic {
    public static void HEXSolver(double processFlow, double waterFlow, double processInletTemp, double waterInletTemp, boolean waterInAnnulus, boolean countercurrent){
        DecimalFormat df = new DecimalFormat("0.00");
        double heatTransRate = HEXCalcStatic.HeatTransferRate(processFlow, waterFlow, processInletTemp, waterInletTemp, waterInAnnulus, countercurrent);
        System.out.println("Overall heat transfer rate is: "+df.format(heatTransRate)+" W");

        double processOutletTemp = HEXCalcStatic.ProcessOutletTemp(processFlow, processInletTemp, heatTransRate);
        System.out.println("Process fluid outlet temp is: "+df.format(processOutletTemp)+" °C");

        double waterOutletTemp = HEXCalcStatic.WaterOutletTemp(waterFlow, waterInletTemp, heatTransRate);
        System.out.println("Hot water outlet temp is: "+df.format(waterOutletTemp)+" °C");

        double totalCost = HEXCalcStatic.HEXCost(processFlow, waterFlow, waterInAnnulus);
        System.out.println("Total annual cost is: "+df.format(totalCost)+" CAD/year");
    }

    public static double HeatTransferRate(double processFlow, double waterFlow, double processInletTemp, double waterInletTemp, boolean waterInAnnulus, boolean countercurrent){
        if (processFlow <= 0){
            System.out.println("Error: Process flow value is invalid");
            System.exit(0);
        }
        if (waterFlow <= 0){
            System.out.println("Error: Hot water flow value is invalid");
            System.exit(0);
        }

        double totalLeg = Initialize.LegLength * 2 * Initialize.NoOfHairpins;
        double innerPipeArea = Math.PI * Initialize.DInnerInside * Initialize.DInnerInside * 0.25;
        double annulusArea = Math.PI * (Math.pow(Initialize.DOuterInside,2) - Math.pow(Initialize.DInnerOutside,2)) * 0.25;
        double annulusHydroD = Initialize.DOuterInside - Initialize.DInnerOutside;
        double overallHeatTransCoeff = 0;

        if (waterInAnnulus == true) {
            // start with the process heat transfer calculations (inner pipe)
            double processVelo = processFlow / (Initialize.DensityProcess * innerPipeArea);
            double processRe = (processVelo * Initialize.DInnerInside * Initialize.DensityProcess) / Initialize.DynViscosityProcess;
            double processPr = (Initialize.SpecHeatCapProcess *Initialize.DynViscosityProcess) / Initialize.ThermCondProcess;

            String processFlowRegime = "";
            if (processRe <= 2300) {
                processFlowRegime = "Laminar";
            }
            else if (processRe >= 10000) {
                processFlowRegime = "Turbulent";
            }
            else {
                System.out.println("Process Reynolds between 2300 and 10000");
                System.out.println("No correlation for process flow regime");
                System.exit(0);
            }

            if (processPr >= 0.5 && processPr <= 2000){
            }
            else {
                System.out.println("Process Prandtl not between 0.5 and 2000");
                System.out.println("No correlation for process flow regime");
                System.exit(0);
            }

            double processGraetz;
            double processNu = 0; // It needs this initialized or the IDE is angry
            double processFricFactor;
            if (processFlowRegime.equals("Turbulent")){
                processFricFactor = Math.pow(-1.8 * Math.log10(6.9/processRe), -2);
                processNu = ((processFricFactor/8)*(processRe - 1000)*processPr) / (1 + 12.7 * Math.sqrt(processFricFactor/8)*(Math.pow(processPr,0.66666666666666667)-1));
            }
            else{
                processGraetz = processRe * processPr * Initialize.DInnerInside / totalLeg;
                processNu = 3.66 + ((0.0668 * processGraetz) / (1 + 0.04 * Math.pow(processGraetz,0.66666666666666667)));
            }

            double processHeatTransCoeff = processNu * Initialize.ThermCondProcess / Initialize.DInnerInside;

            // now for the hot water calcs (annulus)
            double waterVelo = waterFlow / (annulusArea * Initialize.DensityWater);
            double waterRe = Initialize.DensityWater * annulusHydroD * waterVelo / Initialize.DynViscosityWater;
            double waterPr = Initialize.DynViscosityWater * Initialize.SpecHeatCapWater / Initialize.ThermCondWater;

            String waterFlowRegime = "";
            if (waterRe <= 2300) {
                waterFlowRegime = "Laminar";
            }
            else if (waterRe >= 10000) {
                waterFlowRegime = "Turbulent";
            }
            else {
                System.out.println("Water Reynolds between 2300 and 10000");
                System.out.println("No correlation for water flow regime");
                System.exit(0);
            }

            if (waterPr >= 0.5 && waterPr <= 2000){
            }
            else {
                System.out.println("Water Prandtl not between 0.5 and 2000");
                System.out.println("No correlation for water flow regime");
                System.exit(0);
            }

            double waterNu = 0;
            double dRatio = Initialize.DInnerOutside / Initialize.DOuterInside;
            if (waterFlowRegime.equals("Laminar")) {
                // this is the interpolation table
                if (dRatio < 0.05){
                    System.out.println("Diameter ratio out of range");
                    System.out.println("No correlation available for annulus Nusselt");
                    System.exit(0);
                }
                else if (dRatio <= 0.05 && dRatio >= 0.10){
                    waterNu = 17.46 + (11.56 - 17.46)*((dRatio - 0.05)/(0.10 - 0.05));
                }
                else if (dRatio <= 0.10 && dRatio >= 0.25){
                    waterNu = 11.56 + (7.37 - 11.56)*((dRatio - 0.10)/(0.25 - 0.10));
                }
                else if (dRatio <= 0.25 && dRatio >= 0.50){
                    waterNu = 7.37 + (5.74 - 7.37)*((dRatio - 0.25)/(0.50 - 0.25));
                }
                else if (dRatio <= 0.50 && dRatio >= 1.00){
                    waterNu = 5.74 + (4.86 - 5.74)*((dRatio - 0.50)/(1.00 - 0.50));
                }
                else {
                    System.out.println("Diameter ratio out of range");
                    System.out.println("No correlation available for annulus Nusselt");
                    System.exit(0);
                }
            }
            else{
                double waterReModified = waterRe * (((1 + Math.pow(dRatio,2))*Math.log(dRatio) + (1 - Math.pow(dRatio,2))) / (Math.pow(1 - dRatio,2) * Math.log(dRatio)));
                double waterFricFactor = Math.pow(1.8 * Math.log10(waterReModified) - 1.5, -2);
                double waterCorrection = 1.07 + (900 / waterRe) - (0.63 / (1 + 10 * waterPr));
                double waterGeometryFactor = 0.75 * Math.pow(dRatio,-0.17);
                double waterLengthCorr = 1 + Math.pow(annulusHydroD / totalLeg,0.66666666666666667);
                waterNu = waterLengthCorr * waterGeometryFactor * (waterFricFactor/8) * waterRe * waterPr / (waterCorrection + 12.7 * Math.sqrt(waterFricFactor/8) * (Math.pow(waterPr,0.66666666666666667) - 1));
            }

            double waterHeatTransCoeff = waterNu * Initialize.ThermCondWater / annulusHydroD;

            double innerPipeConvRes = Initialize.DInnerOutside / (Initialize.DInnerInside * processHeatTransCoeff);
            double innerPipeFoulRes = Initialize.DInnerOutside * Initialize.FoulResProcess / Initialize.DInnerInside;
            double pipeWallCondRes = (Initialize.DInnerOutside * Math.log(Initialize.DInnerOutside /Initialize.DInnerInside)) / (2 * Initialize.PipeWallCond);
            double annulusFoulRes = Initialize.FoulResWater;
            double annulusConvRes = 1 / waterHeatTransCoeff;
            double totalThermalRes = innerPipeConvRes + innerPipeFoulRes + pipeWallCondRes + annulusFoulRes + annulusConvRes;
            overallHeatTransCoeff = 1 / totalThermalRes;
        }

        // if other arrangement...

        else if (waterInAnnulus == false){
            // now the water is the inner pipe
            double waterVelo = waterFlow / (Initialize.DensityWater * innerPipeArea);
            double waterRe = (waterVelo * Initialize.DInnerInside * Initialize.DensityWater) / Initialize.DynViscosityWater;
            double waterPr = (Initialize.SpecHeatCapWater *Initialize.DynViscosityWater) / Initialize.ThermCondWater;

            String waterFlowRegime = "";
            if (waterRe <= 2300) {
                waterFlowRegime = "Laminar";
            }
            else if (waterRe >= 10000) {
                waterFlowRegime = "Turbulent";
            }
            else {
                System.out.println("Water Reynolds between 2300 and 10000");
                System.out.println("No correlation for water flow regime");
                System.exit(0);
            }

            if (waterPr >= 0.5 && waterPr <= 2000){
            }
            else {
                System.out.println("Water Prandtl not between 0.5 and 2000");
                System.out.println("No correlation for water flow regime");
                System.exit(0);
            }

            double waterGraetz;
            double waterNu = 0; // It needs this initialized or the IDE is angry
            double waterFricFactor;
            if (waterFlowRegime.equals("Turbulent")){
                waterFricFactor = Math.pow(-1.8 * Math.log10(6.9/waterRe), -2);
                waterNu = ((waterFricFactor/8)*(waterRe - 1000)*waterPr) / (1 + 12.7 * Math.sqrt(waterFricFactor/8)*(Math.pow(waterPr,0.66666666666666667)-1));
            }
            else{
                waterGraetz = waterRe * waterPr * Initialize.DInnerInside / totalLeg;
                waterNu = 3.66 + ((0.0668 * waterGraetz) / (1 + 0.04 * Math.pow(waterGraetz,0.66666666666666667)));
            }

            double waterHeatTransCoeff = waterNu * Initialize.ThermCondWater / Initialize.DInnerInside;

            // now for the process calcs for the annulus
            double processVelo = processFlow / (annulusArea * Initialize.DensityProcess);
            double processRe = Initialize.DensityProcess * annulusHydroD * processVelo / Initialize.DynViscosityProcess;
            double processPr = Initialize.DynViscosityProcess * Initialize.SpecHeatCapProcess / Initialize.ThermCondProcess;

            String processFlowRegime = "";
            if (processRe <= 2300) {
                processFlowRegime = "Laminar";
            }
            else if (processRe >= 10000) {
                processFlowRegime = "Turbulent";
            }
            else {
                System.out.println("Process Reynolds between 2300 and 10000");
                System.out.println("No correlation for process flow regime");
                System.exit(0);
            }

            if (processPr >= 0.5 && processPr <= 2000){
            }
            else {
                System.out.println("Process Prandtl not between 0.5 and 2000");
                System.out.println("No correlation for process flow regime");
                System.exit(0);
            }

            double processNu = 0;
            double dRatio = Initialize.DInnerOutside / Initialize.DOuterInside;
            if (processFlowRegime.equals("Laminar")) {
                // this is the interpolation table
                if (dRatio < 0.05){
                    System.out.println("Diameter ratio out of range");
                    System.out.println("No correlation available for annulus Nusselt");
                    System.exit(0);
                }
                else if (dRatio <= 0.05 && dRatio >= 0.10){
                    processNu = 17.46 + (11.56 - 17.46)*((dRatio - 0.05)/(0.10 - 0.05));
                }
                else if (dRatio <= 0.10 && dRatio >= 0.25){
                    processNu = 11.56 + (7.37 - 11.56)*((dRatio - 0.10)/(0.25 - 0.10));
                }
                else if (dRatio <= 0.25 && dRatio >= 0.50){
                    processNu = 7.37 + (5.74 - 7.37)*((dRatio - 0.25)/(0.50 - 0.25));
                }
                else if (dRatio <= 0.50 && dRatio >= 1.00){
                    processNu = 5.74 + (4.86 - 5.74)*((dRatio - 0.50)/(1.00 - 0.50));
                }
                else {
                    System.out.println("Diameter ratio out of range");
                    System.out.println("No correlation available for annulus Nusselt");
                    System.exit(0);
                }
            }
            else{
                double processReModified = processRe * (((1 + Math.pow(dRatio,2))*Math.log(dRatio) + (1 - Math.pow(dRatio,2))) / (Math.pow(1 - dRatio,2) * Math.log(dRatio)));
                double processFricFactor = Math.pow(1.8 * Math.log10(processReModified) - 1.5, -2);
                double processCorrection = 1.07 + (900 / processRe) - (0.63 / (1 + 10 * processPr));
                double processGeometryFactor = 0.75 * Math.pow(dRatio,-0.17);
                double processLengthCorr = 1 + Math.pow(annulusHydroD / totalLeg,0.66666666666666667);
                processNu = processLengthCorr * processGeometryFactor * (processFricFactor/8) * processRe * processPr / (processCorrection + 12.7 * Math.sqrt(processFricFactor/8) * (Math.pow(processPr,0.66666666666666667) - 1));
            }

            double processHeatTransCoeff = processNu * Initialize.ThermCondProcess / annulusHydroD;

            double innerPipeConvRes = Initialize.DInnerOutside / (Initialize.DInnerInside * waterHeatTransCoeff);
            double innerPipeFoulRes = Initialize.DInnerOutside * Initialize.FoulResWater / Initialize.DInnerInside;
            double pipeWallCondRes = (Initialize.DInnerOutside * Math.log(Initialize.DInnerOutside /Initialize.DInnerInside)) / (2 * Initialize.PipeWallCond);
            double annulusFoulRes = Initialize.FoulResProcess;
            double annulusConvRes = 1 / processHeatTransCoeff;
            double totalThermalRes = innerPipeConvRes + innerPipeFoulRes + pipeWallCondRes + annulusFoulRes + annulusConvRes;
            overallHeatTransCoeff = 1 / totalThermalRes;
        }

        if (processFlow <= 0){
            System.out.println("Error: Process flow value is invalid");
            System.exit(0);
        }
        if (waterFlow <= 0){
            System.out.println("Error: Hot water flow value is invalid");
            System.exit(0);
        }

        double heatTransArea = Math.PI * Initialize.DInnerOutside * totalLeg;

        double processHeatCapRate = Initialize.SpecHeatCapProcess * processFlow;
        double waterHeatCapRate = Initialize.SpecHeatCapWater * waterFlow;
        double minHeatCapRate = 0;
        double maxHeatCapRate = 0;

        if (processHeatCapRate >= waterHeatCapRate){
            minHeatCapRate = waterHeatCapRate;
            maxHeatCapRate = processHeatCapRate;
        }
        else {
            minHeatCapRate = processHeatCapRate;
            maxHeatCapRate = waterHeatCapRate;
        }

        double heatCapRatio = minHeatCapRate / maxHeatCapRate;
        double NTU = overallHeatTransCoeff * heatTransArea / minHeatCapRate;
        double effectiveness = 0;

        if (countercurrent == true){
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

        return (effectiveness * minHeatCapRate * (waterInletTemp - processInletTemp));
    } // returns the heat transfer rate

    public static double ProcessOutletTemp(double processFlow, double processInletTemp, double heatTransRate){
        if (processFlow <= 0){
            System.out.println("Error: Process flow value is invalid");
            System.exit(0);
        }

        double processHeatCapRate = Initialize.SpecHeatCapProcess * processFlow;
        return processInletTemp + (heatTransRate / processHeatCapRate);
    }

    public static double WaterOutletTemp(double waterFlow, double waterInletTemp, double heatTransRate){
        if (waterFlow <= 0){
            System.out.println("Error: Hot water flow value is invalid");
            System.exit(0);
        }
        double waterHeatCapRate = Initialize.SpecHeatCapWater * waterFlow;
        return waterInletTemp - (heatTransRate / waterHeatCapRate);
    }

    public static double HEXCost(double processFlow, double waterFlow, boolean waterInAnnulus){
        // this function also prints the pressure drops since they're not really useful anywhere else
        // so we need the DecimalFormat object since we only return the annual cost

        if (processFlow <= 0){
            System.out.println("Error: Process flow value is invalid");
            System.exit(0);
        }
        if (waterFlow <= 0){
            System.out.println("Error: Hot water flow value is invalid");
            System.exit(0);
        }

        DecimalFormat df = new DecimalFormat("0.00");
        double totalLeg = Initialize.LegLength * 2 * Initialize.NoOfHairpins;
        double innerPipeArea = Math.PI * Initialize.DInnerInside * Initialize.DInnerInside * 0.25;
        double annulusArea = Math.PI * (Math.pow(Initialize.DOuterInside,2) - Math.pow(Initialize.DInnerOutside,2)) * 0.25;
        double annulusHydroD = Initialize.DOuterInside - Initialize.DInnerOutside;
        double minorLossCoeff = 1.5 * (1 + Initialize.NoOfHairpins) + 2 * Math.max(0, Initialize.NoOfHairpins - 1);

        double processVelo;
        double processRe;
        double waterVelo;
        double waterRe;
        double processD_h;
        double waterD_h;

        if (waterInAnnulus == true){
            processVelo = processFlow / (Initialize.DensityProcess * innerPipeArea);
            processRe = (processVelo * Initialize.DInnerInside * Initialize.DensityProcess) / Initialize.DynViscosityProcess;
            waterVelo = waterFlow / (annulusArea * Initialize.DensityWater);
            waterRe = Initialize.DensityWater * annulusHydroD * waterVelo / Initialize.DynViscosityWater;
            processD_h = Initialize.DInnerInside;
            waterD_h = annulusHydroD;
        }
        else{
            processVelo = processFlow / (annulusArea * Initialize.DensityProcess);
            processRe = Initialize.DensityProcess * annulusHydroD * processVelo / Initialize.DynViscosityProcess;
            waterVelo = waterFlow / (Initialize.DensityWater * innerPipeArea);
            waterRe = (waterVelo * Initialize.DInnerInside * Initialize.DensityWater) / Initialize.DynViscosityWater;
            processD_h = annulusHydroD;
            waterD_h = Initialize.DInnerInside;
        }

        // process flow pressure drop
        String processFlowRegime = "";
        if (processRe <= 2300) {
            processFlowRegime = "Laminar";
        }
        else if (processRe >= 10000) {
            processFlowRegime = "Turbulent";
        }
        else {
            System.out.println("Process Reynolds between 2300 and 10000");
            System.out.println("No correlation for process flow regime");
            System.exit(0);
        }

        double processFricFactor;
        if (processFlowRegime.equals("Laminar") && waterInAnnulus == true){
            processFricFactor = 64 / processRe;
        }
        else if (processFlowRegime.equals("Laminar") && waterInAnnulus == false){
            double dRatio = Initialize.DInnerOutside / Initialize.DOuterInside;
            processFricFactor = (64 / processRe) * ((Math.pow(1 - dRatio,2)) / (1 + (dRatio * dRatio) + ((1 - Math.pow(dRatio,2)) / Math.log(dRatio))));
        }
        else {
            processFricFactor = Math.pow(-1.8 * Math.log10(Math.pow((Initialize.AbsRoughness /(3.7 * processD_h)),1.11) + (6.9 / processRe)),-2);
        }

        double processPressureDrop = (processFricFactor * (totalLeg / processD_h) + minorLossCoeff) * (0.5 * Initialize.DensityProcess * processVelo * processVelo);
        System.out.println("The process fluid pressure drop is "+df.format(processPressureDrop)+" Pa");

        // now for the water pressure drop
        String waterFlowRegime = "";
        if (waterRe <= 2300) {
            waterFlowRegime = "Laminar";
        }
        else if (waterRe >= 10000) {
            waterFlowRegime = "Turbulent";
        }
        else {
            System.out.println("Water Reynolds between 2300 and 10000");
            System.out.println("No correlation for water flow regime");
            System.exit(0);
        }

        double waterFricFactor;
        if (waterFlowRegime.equals("Laminar") && waterInAnnulus == true){
            waterFricFactor = 64 / waterRe;
        }
        else if (waterFlowRegime.equals("Laminar") && waterInAnnulus == false){
            double dRatio = Initialize.DInnerOutside / Initialize.DOuterInside;
            waterFricFactor = (64 / waterRe) * ((Math.pow(1 - dRatio,2)) / (1 + (dRatio * dRatio) + ((1 - Math.pow(dRatio,2)) / Math.log(dRatio))));
        }
        else {
            waterFricFactor = Math.pow(-1.8 * Math.log10(Math.pow((Initialize.AbsRoughness /(3.7 * waterD_h)),1.11) + (6.9 / waterRe)),-2);
        }

        double waterPressureDrop = (waterFricFactor * (totalLeg / waterD_h) + minorLossCoeff) * (0.5 * Initialize.DensityWater * waterVelo * waterVelo);
        System.out.println("The hot water pressure drop is "+df.format(waterPressureDrop)+" Pa");

        // now for the actual costing stuff
        double processPumpPower = (processPressureDrop * (processFlow / Initialize.DensityProcess)) / Initialize.PumpEff;
        double processPumpCost = (processPumpPower / 1000) * Initialize.AnnOperatingTime * Initialize.ElecPrice;
        double waterPumpPower = (waterPressureDrop * (waterFlow / Initialize.DensityWater)) / Initialize.PumpEff;
        double waterPumpCost = (waterPumpPower / 1000) * Initialize.AnnOperatingTime * Initialize.ElecPrice;
        double annualizedCapCost = Initialize.NoOfHairpins * Initialize.InstalledCostPerModule * Initialize.AnnualizationFactor;

        return (processPumpCost + waterPumpCost + annualizedCapCost);
        // returns the total annual cost
    }

}
