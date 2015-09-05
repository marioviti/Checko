package Support;

/**
 * Created by marioviti on 05/09/15.
 */
public class BMRCalculation {

    public static float HarrisBenedictMaleCal(int Hcm, int Wkg, int A) {
        return (float)(66.473 + (13.7156*(float)Wkg) + (5.033*(float)Hcm) - (6.775*(float)A));
    }

    public static float HarrisBenedictFemaleCal(int Hcm, int Wkg, int A) {
        return (float)(655.095 + (9.5634*(float)Wkg) + (1.849*(float)Hcm) - (4.6756*(float)A));
    }
}
