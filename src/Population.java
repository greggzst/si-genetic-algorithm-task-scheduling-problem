import msrcpsp.evaluation.DurationEvaluator;
import msrcpsp.io.MSRCPSPIO;
import msrcpsp.scheduling.BaseIndividual;
import msrcpsp.scheduling.Resource;
import msrcpsp.scheduling.Schedule;
import msrcpsp.scheduling.Task;
import msrcpsp.scheduling.greedy.Greedy;

import java.util.List;
import java.util.Random;

/**
 * Created by GreggJakubiak on 09.03.2017.
 */
public class Population {
    private Schedule[] individuals;
    private int[] individualDurations;
    private double[] individualFitnesses;
    private int populationSize;
    private MSRCPSPIO reader = new MSRCPSPIO();

    //constructor for creating population of a given size and using given def file
    public Population(int popSize, String fileName){
        populationSize = popSize;
        individuals = new Schedule[popSize];
        individualDurations = new int[popSize];
        individualFitnesses = new double[popSize];
        for(int i = 0; i < individuals.length; i++) {
            individuals[i] = reader.readDefinition(fileName);
            individuals[i].setEvaluator(new DurationEvaluator(individuals[i]));
        }
    }
    //population from schedule array
    public Population(Schedule[] schedules){
        populationSize = schedules.length;
        individuals = schedules;
        individualDurations = new int[individuals.length];
        individualFitnesses = new double[individuals.length];
        for (int i = 0; i < individuals.length; i++){
            individualDurations[i] = calculateIndividualDuration(individuals[i]);
        }
        calculateFitnesses();
    }

    //initializing population at random and setting task times
    public void initializeRandomPopulation(){
        for(int i = 0; i < individuals.length; i++){
            initializeRandomIndividual(individuals[i]);
            initializeTaskTime(individuals[i]);
            individualDurations[i] = calculateIndividualDuration(individuals[i]);
            individuals[i].fix();
        }
        calculateFitnesses();
    }

    public Schedule getFittest(){
        int index = 0;
        double fittest = individualFitnesses[index];
        for(int i = 1; i < populationSize; i++){
            if(fittest < individualFitnesses[i]){
                index = i;
                fittest = individualFitnesses[index];
            }
        }
        return individuals[index];

    }

    //initializing particular individual taking into account task constraints
    private void initializeRandomIndividual(Schedule schedule){
        List<Resource> capableResources;
        Random random = new Random(System.currentTimeMillis());
        int[] upperBounds = schedule.getUpperBounds(schedule.getTasks().length);
        Task[] tasks = schedule.getTasks();
        for(int i = 0; i < tasks.length; i++){
            capableResources = schedule.getCapableResources(tasks[i]);
            schedule.assign(tasks[i], capableResources.get((int)(random.nextDouble() * upperBounds[i])));
        }
    }

    private void initializeTaskTime(Schedule schedule){
        Greedy greedy = new Greedy(schedule.getSuccesors());
        greedy.buildTimestamps(schedule);
    }

    //calcualte duration for individual schedule using BaseIndividual
    private int calculateIndividualDuration(Schedule schedule){
        BaseIndividual baseIndividual = new BaseIndividual(schedule, schedule.getEvaluator());
        baseIndividual.setDurationAndCost();
        return baseIndividual.getDuration();
    }

    public Schedule[] getIndividuals(){
        return individuals;
    }

    public int[] getIndividualDurations(){
        return individualDurations;
    }

    public double[] getIndividualFitnesses() { return individualFitnesses; }

    public int getPopulationSize(){ return populationSize; }

    public int getSumOfDurations(){
        int sum = 0;
        for(int i = 0; i < individualDurations.length; i++){
            sum += individualDurations[i];
        }
        return sum;
    }

    private void calculateFitnesses(){
        int sumOfDurations = getSumOfDurations();
        for (int i = 0; i < individualDurations.length; i++){
            individualFitnesses[i] = (double) individualDurations[i] / sumOfDurations;
        }
    }
}
