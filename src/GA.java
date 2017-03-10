import msrcpsp.scheduling.Resource;
import msrcpsp.scheduling.Schedule;
import msrcpsp.scheduling.Task;

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

    public GA(Population population, int generations,double crossoverRate, double mutationRate){
        this.population = population;
        this.generations = generations;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    //roulette operation
    private Schedule roulette(){
        Schedule schedule = null;
        int populationSize = population.getPopulationSize();
        Schedule[] individuals = population.getIndividuals();
        double[] fitnesses = population.getIndividualFitnesses();
        Random random = new Random();

        //loop through all population and rotate roulette if you get the number within approprate
        //fitness return the individual
        for(int i = 0; i < populationSize; i++){
            if(random.nextDouble() >= fitnesses[i]){
                schedule = individuals[i];
                break;
            }
        }

        return schedule;
    }

    //this does all the work
    public Schedule start(){
        int genNum = 0;
        int populationSize = population.getPopulationSize();
        //loop until we reach number of given generations
        while(genNum < generations){

            Schedule[] newPopulation = new Schedule[populationSize];
            int popSize = 0;

            while(popSize < populationSize){
                Schedule parent1 = roulette();
                Schedule parent2 = roulette();

                crossover(parent1,parent2);
                mutate(parent1);
                mutate(parent2);

                newPopulation[popSize++] = parent1;
                newPopulation[popSize++] = parent2;
            }

            population = new Population(newPopulation);

            genNum++;
        }

        return population.getFittest();
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
        int point = (int) random.nextDouble() * numberOfTasks;
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
