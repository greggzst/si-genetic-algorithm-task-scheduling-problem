import msrcpsp.scheduling.Schedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by GreggJakubiak on 09.03.2017.
 */
public class GA {

    private Population population;
    private int crossoverRate;
    private int mutationRate;

    public GA(Population population,int crossoverRate, int mutationRate){
        this.population = population;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    public Schedule[] roulette(){
        List<Schedule> selectedIndividuals = new ArrayList<>();
        int populationSize = population.getPopulationSize();
        Schedule[] individuals = population.getIndividuals();
        double[] fitnesses = population.getIndividualFitnesses();
        Random random = new Random();
        for(int i = 0; i < populationSize; i++){
            if(random.nextDouble() >= fitnesses[i]){
                selectedIndividuals.add(individuals[i]);
            }
        }
        return (Schedule[]) selectedIndividuals.toArray();
    }
}
