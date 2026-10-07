public class CHG4343_Project_Deliverable2 {
    public static void main(String[] args){

        boolean countercurrent = true;
        // countercurrent true = countercurrent; false = parallel
        boolean waterInAnnulus = true;
        // waterInAnnulus true = waterInAnnulus; false = process in annulus
        double processFlow = 2.5; // kg/s
        double waterFlow = 2; // kg/s
        double processInletTemp = 25; // °C
        double waterInletTemp = 90; // °C

        HEXCalcStatic.HEXSolver(processFlow, waterFlow, processInletTemp, waterInletTemp, waterInAnnulus, countercurrent);
        // this is a void method, gives no return
    }
}
