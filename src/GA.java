import msrcpsp.scheduling.Schedule;

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
}
