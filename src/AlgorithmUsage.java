import msrcpsp.io.MSRCPSPIO;
import msrcpsp.scheduling.Schedule;
import msrcpsp.validation.BaseValidator;
import msrcpsp.validation.CompleteValidator;

import java.io.IOException;

/**
 * Created by GreggJakubiak on 09.03.2017.
 */
public class AlgorithmUsage {

    private static final String definitionFile = "assets/def_small/10_3_5_3.def";
    //private static final String definitionFile = "assets/def_small/10_5_8_5.def";
    //private static final String definitionFile = "assets/def_small/10_7_10_7.def";
    //private static final String definitionFile = "assets/def_small/15_3_5_3.def";
    //private static final String definitionFile = "assets/def_small/15_6_10_6.def";
    //private static final String definitionFile = "assets/def_small/15_9_12_9.def";
    //private static final String definitionFile = "assets/def_small/100_5_22_15.def";

    private static final String writeFile = "assets/solutions_small/10_3_5_3.sol";
    //private static final String writeFile = "assets/solutions_small/10_5_8_5.sol";
    //private static final String writeFile = "assets/solutions_small/10_7_10_7.sol";
    //private static final String writeFile = "assets/solutions_small/15_3_5_3.sol";
    //private static final String writeFile = "assets/solutions_small/15_6_10_6.sol";
    //private static final String writeFile = "assets/solutions_small/15_9_12_9.sol";
    //private static final String writeFile = "assets/def_small/100_5_22_15.sol";

    public static void main(String args[]){
        MSRCPSPIO reader = new MSRCPSPIO();
        Population pop = new Population(100, definitionFile);
        pop.initializeRandomPopulation();
        pop.getSumOfInverseDurations();

        GA ga = new GA(pop, 100, 0.1,0.01);
        Schedule schedule = ga.start();
        BaseValidator validator = new CompleteValidator();
        System.out.println(validator.validate(schedule));
        System.out.println(validator.getErrorMessages());
        // save to a file
        try {
            reader.write(schedule, writeFile);
        } catch (IOException e) {
            System.out.print("Writing to a file failed");
        }


    }
}
