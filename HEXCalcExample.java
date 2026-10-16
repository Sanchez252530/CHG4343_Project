public class HEXCalcExample {
    public static void main(String[] args){

        boolean isCountercurrent = true;
        // countercurrent true = countercurrent; false = parallel
        boolean waterInAnnulus = true;
        // waterInAnnulus true = waterInAnnulus; false = process in annulus
        double processFlow = 2.5; // kg/s
        double waterFlow = 2; // kg/s
        double processInletTemp = 25; // °C
        double waterInletTemp = 90; // °C

        HEXCalcStatic.HEXSolver(processFlow, waterFlow, processInletTemp, waterInletTemp, waterInAnnulus, isCountercurrent);
        // this is a void method, gives no return
    }
}
