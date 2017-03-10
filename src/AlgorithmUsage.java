/**
 * Created by GreggJakubiak on 09.03.2017.
 */
public class AlgorithmUsage {

    private static final String definitionFile = "assets/def_small/10_3_5_3.def";
    private static final String writeFile = "assets/solutions_small/10_3_5_3.sol";

    public static void main(String args[]){
        Population pop = new Population(100, definitionFile);
        pop.initializeRandomPopulation();

        GA ga = new GA(pop, 100, 0.1,0.01);


    }
}
