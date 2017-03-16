import msrcpsp.evaluation.DurationEvaluator;
import msrcpsp.io.MSRCPSPIO;
import msrcpsp.scheduling.BaseIndividual;
import msrcpsp.scheduling.Resource;
import msrcpsp.scheduling.Schedule;
import msrcpsp.scheduling.Task;
import msrcpsp.scheduling.greedy.Greedy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by GreggJakubiak on 09.03.2017.
 */
public class Population {
    private Schedule[] individuals;
    private int[] individualDurations;
    private double[] individualRouletteRange;
    private int populationSize;
    private MSRCPSPIO reader = new MSRCPSPIO();

    //constructor for creating population of a given size and using given def file
    public Population(int popSize, String fileName){
        populationSize = popSize;
        individuals = new Schedule[popSize];
        individualDurations = new int[popSize];
        individualRouletteRange = new double[popSize];
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
        individualRouletteRange = new double[individuals.length];
        for (int i = 0; i < individuals.length; i++){
            individualDurations[i] = calculateIndividualDuration(individuals[i]);
        }
        calculateRouletteRange();
    }

    //initializing population at random and setting task times
    public void initializeRandomPopulation(){
        for(int i = 0; i < individuals.length; i++){
            initializeRandomIndividual(individuals[i]);
            initializeTaskTime(individuals[i]);
            individualDurations[i] = calculateIndividualDuration(individuals[i]);
        }
        calculateRouletteRange();
    }

    public int getBestDuration(){
        int duration = individualDurations[0];
        for(int i = 1; i < populationSize; i++){
            if(duration > individualDurations[i]){
                duration = individualDurations[i];
            }
        }
        return duration;
    }

    public int getWorstDuration(){
        int duration = individualDurations[0];
        for(int i = 1; i < populationSize; i++){
            if(duration < individualDurations[i]){
                duration = individualDurations[i];
            }
        }
        return duration;
    }

    public double getAverageDuration(){
        return (double) (getSumOfDurations() / getPopulationSize());
    }

    //the best schedule has the shortest duration time
    public Schedule getBest(){
        int index = 0;
        int duration = individualDurations[index];
        for(int i = 1; i < populationSize; i++){
            if(duration > individualDurations[i]){
                index = i;
                duration = individualDurations[index];
            }
        }
        return individuals[index];

    }

    //the worst schedule has the longest duration time
    public Schedule getWorst(){
        int index = 0;
        int duration = individualDurations[index];
        for(int i = 1; i < populationSize; i++){
            if(duration < individualDurations[i]){
                index = i;
                duration = individualDurations[index];
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

    private int[] getSortedIndecies(){
        int[] sortedIndecies = new int[populationSize];
        int[] durations = new int[populationSize];
        for(int i = 0; i < populationSize; i++){
            sortedIndecies[i] = i;
            durations[i] = individualDurations[i];
        }
        int tempIndex = 0;
        int tempDuration = 0;
        for(int i = 0; i < populationSize; i++){
            for(int j = 1; j < (populationSize - i); j++){
                int firstDuration = durations[j-1];
                int secondDuration = durations[j];
                if(firstDuration > secondDuration){
                    tempDuration = durations[j-1];
                    durations[j-1] = durations[j];
                    durations[j] = tempDuration;

                    tempIndex = sortedIndecies[j-1];
                    sortedIndecies[j-1] = sortedIndecies[j];
                    sortedIndecies[j] = tempIndex;
                }

            }
        }

        return sortedIndecies;

    }

    public Schedule[] getIndividuals(){
        return individuals;
    }

    public int[] getIndividualDurations(){
        return individualDurations;
    }

    public double[] getIndividualRouletteRange() { return individualRouletteRange; }

    public int getPopulationSize(){ return populationSize; }

    public int getSumOfDurations(){
        int sum = 0;
        for(int i = 0; i < individualDurations.length; i++){
            sum += individualDurations[i];
        }
        return sum;
    }

    public double getSumOfInverseDurations(){
        double sum = 0;
        for(int i = 0; i < individualDurations.length; i++){
            double inverse = (double) 1 / individualDurations[i];
            sum += inverse;
        }
        return sum;
    }

    private void calculateRouletteRange(){
        double sumOfInverseDurations = getSumOfInverseDurations();
        int[] sortedIndecies = getSortedIndecies();
        HashMap<Integer,Double> rangeValueMap = new HashMap<>();

        for(Integer index : sortedIndecies){
            double range = (double) 1 / individualDurations[index];
            range /= sumOfInverseDurations;
            if(!rangeValueMap.containsKey(individualDurations[index])){
                rangeValueMap.put(individualDurations[index], range);
            }else{
                double currentValue = rangeValueMap.get(individualDurations[index]);
                rangeValueMap.put(individualDurations[index],range + currentValue);
            }
        }

        for(Integer index : sortedIndecies){
            individualRouletteRange[index] = rangeValueMap.get(individualDurations[index]);
        }

    }
}
