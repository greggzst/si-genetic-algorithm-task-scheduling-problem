import msrcpsp.scheduling.Resource;
import msrcpsp.scheduling.Schedule;
import msrcpsp.scheduling.Task;
import msrcpsp.scheduling.greedy.Greedy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by GreggJakubiak on 09.03.2017.
 */
public class GA {

    private Population population;
    private double crossoverRate;
    private double mutationRate;
    private int generations;
    private int tournamentSize;

    public GA(Population population, int generations,double crossoverRate, double mutationRate){
        this.population = population;
        this.generations = generations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = 0;
    }

    public GA(Population population, int generations,double crossoverRate, double mutationRate,int tournamentSize){
        this.population = population;
        this.generations = generations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.tournamentSize = tournamentSize;
    }


    //roulette operation
    private Schedule roulette(){
        Schedule schedule = null;
        int populationSize = population.getPopulationSize();
        Schedule[] individuals = population.getIndividuals();
        double[] fitnesses = population.getIndividualFitnesses();
        Random random = new Random();
        double roulettePointer = random.nextDouble();
        double rangeUpperBound = 0;
        //loop through all population and rotate roulette if you get the number within approprate
        //fitness return the individual
        for(int i = 0; i < populationSize; i++){
            rangeUpperBound += fitnesses[i];
            if (roulettePointer < rangeUpperBound){
                schedule = individuals[i];
                break;
            }
        }

        return schedule != null ? new Schedule(schedule) : new Schedule(individuals[populationSize - 1]);
    }

    private Schedule tournament(){
        int numberOfIndividuals = 0;
        Random random = new Random();
        int[] indices = new int[tournamentSize];
        while(numberOfIndividuals < tournamentSize){
            indices[numberOfIndividuals++] = (int) (random.nextDouble() * population.getPopulationSize());
        }

        int index = indices[0];
        int duration = population.getIndividualDurations()[indices[0]];
        for(Integer i : indices){
            if(duration > population.getIndividualDurations()[i]){
                index = i;
                duration = population.getIndividualDurations()[i];
            }
        }

        return new Schedule(population.getIndividuals()[index]);
    }

    //this does all the work
    public Schedule start(){
        int genNum = 0;
        int populationSize = population.getPopulationSize();

        System.out.println("Początkowa populacja: ");
        System.out.print(population.getBestDuration() + " ");
        System.out.print(population.getWorstDuration() + " ");
        System.out.print(population.getAverageDuration());
        System.out.println();

        //loop until we reach number of given generations
        while(genNum < generations){

            Schedule[] newPopulation = new Schedule[populationSize];
            int popSize = 0;

            while(popSize < populationSize){

                Schedule parent1 = null;
                Schedule parent2 = null;

                if(tournamentSize == 0){
                    parent1 = roulette();
                    parent2 = roulette();
                }else{
                    parent1 = tournament();
                    parent2 = tournament();
                }

                crossover(parent1,parent2);
                mutate(parent1);
                mutate(parent2);

                newPopulation[popSize++] = parent1;
                newPopulation[popSize++] = parent2;
            }

            population = new Population(newPopulation);

            //System.out.println("Pokolenie nr: " + (genNum + 1));
            System.out.print(population.getBestDuration() + " ");
            System.out.print(population.getWorstDuration() + " ");
            System.out.print(population.getAverageDuration());
            System.out.println();

            genNum++;
        }

        return population.getBest();
    }

    //crossover operation which uses some helper methods
    private void crossover(Schedule firstParent, Schedule secondParent){
        Random random = new Random();
        if(random.nextDouble() <= crossoverRate) {
            Task[] firstParentTasks = firstParent.getTasks();
            Task[] secondParentTasks = secondParent.getTasks();
            //get crossover point
            int crossoverPoint = crossoverPoint(firstParentTasks.length);
            //get tasks before and after crossover point
            List<Task> firstParentTaskBeforeCrossOverPoint = getTasksBeforeCrossoverPoint(firstParentTasks, crossoverPoint);
            List<Task> firstParentTaskAfterCrossOverPoint = getTasksAfterCrossoverPoint(firstParentTasks, crossoverPoint);
            List<Task> secondParentTaskBeforeCrossOverPoint = getTasksBeforeCrossoverPoint(secondParentTasks, crossoverPoint);
            List<Task> secondParentTaskAfterCrossOverPoint = getTasksAfterCrossoverPoint(secondParentTasks, crossoverPoint);
            //swap resources before crossover point
            swapResources(firstParentTaskBeforeCrossOverPoint, secondParentTaskBeforeCrossOverPoint);

            //connect swapped parts with the unchanged one
            //parts after crossover do not change so they are just being added at the end
            firstParentTaskBeforeCrossOverPoint.addAll(firstParentTaskAfterCrossOverPoint);
            secondParentTaskBeforeCrossOverPoint.addAll(secondParentTaskAfterCrossOverPoint);
            //convert into arrays and set as schedule tasks
            Task[] firstAfterCrossover = firstParentTaskBeforeCrossOverPoint.toArray(new Task[firstParentTaskBeforeCrossOverPoint.size()]);
            Task[] secondAfterCrossover = secondParentTaskBeforeCrossOverPoint.toArray(new Task[secondParentTaskBeforeCrossOverPoint.size()]);

            firstParent.setTasks(firstAfterCrossover);
            secondParent.setTasks(secondAfterCrossover);
        }

    }

    //swap resources id
    private void swapResources(List<Task> tasks1, List<Task> tasks2){
        for(int i = 0; i < tasks1.size(); i++){
            int firstTaskResourceId = tasks1.get(i).getResourceId();
            int secondTaskResourceId = tasks2.get(i).getResourceId();
            tasks1.get(i).setResourceId(secondTaskResourceId);
            tasks2.get(i).setResourceId(firstTaskResourceId);
        }
    }

    //mutating tasks in schedule according to mutation rate
    private void mutate(Schedule schedule){
        Random random = new Random();
        Task[] tasks = schedule.getTasks();
        int[] upperBounds = schedule.getUpperBounds(tasks.length);
        for(int i = 0; i < tasks.length; i++){
            if(random.nextDouble() <= mutationRate){
                List<Resource> capableResources = schedule.getCapableResources(tasks[i]);
                tasks[i].setResourceId(capableResources.get((int)(random.nextDouble() * upperBounds[i])).getId());
            }
        }
    }

    //find crossover point
    private int crossoverPoint(int numberOfTasks){
        Random random = new Random();
        int point = (int) (random.nextDouble() * numberOfTasks);
        return point;
    }

    private List<Task> getTasksBeforeCrossoverPoint(Task[] tasks,int crossoverPoint){
        List<Task> tasksBefore = new ArrayList<>();
        for(int i = 0; i <= crossoverPoint; i++){
            tasksBefore.add(tasks[i]);
        }
        return tasksBefore;
    }

    private List<Task> getTasksAfterCrossoverPoint(Task[] tasks, int crossoverPoint){
        List<Task> tasksAfter = new ArrayList<>();
        for(int i = crossoverPoint + 1; i < tasks.length; i++){
            tasksAfter.add(tasks[i]);
        }
        return  tasksAfter;
    }
}
