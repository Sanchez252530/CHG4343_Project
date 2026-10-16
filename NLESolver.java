import java.text.DecimalFormat;


public class NLESolver {

    // Problem inputs (same case as the Task 1 main method)
    public static final double ProcessFlow = 2.5;          // kg/s
    public static final double ProcessInletTemp = 25;      // °C
    public static final double WaterInletTemp = 90;        // °C
    public static final boolean WaterInAnnulus = true;
    public static final boolean Countercurrent = true;
    public static final double TargetProcessOutletTemp = 45; // °C

    // Solver settings
    public static final double WaterFlowLower = 0.5;       // kg/s
    public static final double WaterFlowUpper = 8.0;       // kg/s
    public static final double SearchStep = 0.1;           // kg/s, incremental search step
    public static final double Tolerance = 0.001;          // °C, absolute residual
    public static final int MaxIterations = 100;

    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("0.0000");

        double[] bounds = IncrementalSearch(WaterFlowLower, WaterFlowUpper, SearchStep);
        if (bounds == null) {
            System.out.println("No root found between " + WaterFlowLower + " and " + WaterFlowUpper + " kg/s");
            return;
        }
        System.out.println("Incremental search bracketed the root between "
                + df.format(bounds[0]) + " and " + df.format(bounds[1]) + " kg/s");

        double waterFlow = Ridders(bounds[0], bounds[1], Tolerance, MaxIterations);

        System.out.println();
        System.out.println("Hot water flow rate required: " + df.format(waterFlow) + " kg/s");
        System.out.println("Process outlet temperature at this flow: "
                + df.format(ProcessOutletTempFor(waterFlow)) + " °C");
        System.out.println();
        System.out.println("Heat exchanger performance at this flow rate:");
        HEXCalcStatic.HEXSolver(ProcessFlow, waterFlow, ProcessInletTemp, WaterInletTemp, WaterInAnnulus, Countercurrent);
    }

    // Process outlet temperature (degC) for a given hot water flow, using the Task 1 methods
    public static double ProcessOutletTempFor(double waterFlow) {
        double heatTransRate = HEXCalcStatic.HeatTransferRate(ProcessFlow, waterFlow, ProcessInletTemp,
                WaterInletTemp, WaterInAnnulus, Countercurrent);
        return HEXCalcStatic.ProcessOutletTemp(ProcessFlow, ProcessInletTemp, heatTransRate);
    }

    // Residual of the nonlinear equation: f(m_w) = T_out(m_w) - T_target
    public static double Residual(double waterFlow) {
        return ProcessOutletTempFor(waterFlow) - TargetProcessOutletTemp;
    }

    // {where the residual changes sign. Returns Null
    public static double[] IncrementalSearch(double lower, double upper, double step) {
        double x1 = lower;
        double f1 = Residual(x1);
        if (f1 == 0) {
            return new double[]{x1, x1};
        }
        while (x1 < upper) {
            double x2 = Math.min(x1 + step, upper);
            double f2 = Residual(x2);
            if (f1 * f2 <= 0) {
                return new double[]{x1, x2};
            }
            x1 = x2;
            f1 = f2;
        }
        return null;
    }

    // Ridders' method s
    public static double Ridders(double x1, double x2, double tol, int maxIter) {
        DecimalFormat df = new DecimalFormat("0.000000");
        double f1 = Residual(x1);
        double f2 = Residual(x2);

        if (Math.abs(f1) <= tol) {
            return x1;
        }
        if (Math.abs(f2) <= tol) {
            return x2;
        }

        double x4 = x1;
        System.out.println();
        System.out.println("Riders' method iterations:");
        System.out.println("Iter    m_w (kg/s)      Residual (°C)");

        for (int i = 1; i <= maxIter; i++) {
            double x3 = 0.5 * (x1 + x2);
            double f3 = Residual(x3);
            double s = Math.sqrt(f3 * f3 - f1 * f2);
            if (s == 0) {
                return x3;
            }
            x4 = x3 + (x3 - x1) * Math.signum(f1 - f2) * f3 / s;
            double f4 = Residual(x4);
            System.out.println(i + "       " + df.format(x4) + "        " + df.format(f4));

            if (Math.abs(f4) <= tol) {
                System.out.println("Converged in " + i + " iteration(s)");
                return x4;
            }

            // Keep the root bracketed for the next iteration
            if (Math.signum(f3) != Math.signum(f4)) {
                x1 = x3;
                f1 = f3;
                x2 = x4;
                f2 = f4;
            }
            else if (Math.signum(f1) != Math.signum(f4)) {
                x2 = x4;
                f2 = f4;
            }
            else {
                x1 = x4;
                f1 = f4;
            }
        }
        System.out.println("Warning: maximum of " + maxIter + " iterations reached without convergence");
        return x4;
    }
}
