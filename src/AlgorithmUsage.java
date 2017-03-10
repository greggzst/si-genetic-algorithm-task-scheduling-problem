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
    private static final String writeFile = "assets/solutions_small/10_3_5_3.sol";

    public static void main(String args[]){
        MSRCPSPIO reader = new MSRCPSPIO();
        Population pop = new Population(100, definitionFile);
        pop.initializeRandomPopulation();

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
