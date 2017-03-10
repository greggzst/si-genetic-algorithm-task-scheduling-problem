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
    private int crossoverRate;
    private int mutationRate;

    public GA(Population population,int crossoverRate, int mutationRate){
        this.population = population;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
    }

    //roulette operation
    public Schedule roulette(){
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

    //crossover operation which uses some helper methods
    public void crossover(Schedule firstParent, Schedule secondParent){
        Task[] firstParentTasks = firstParent.getTasks();
        Task[] secondParentTasks = secondParent.getTasks();

        int crossoverPoint = crossoverPoint(firstParentTasks.length);
        List<Task> firstParentTaskBeforeCrossOverPoint = getTasksBeforeCrossoverPoint(firstParentTasks,crossoverPoint);
        List<Task> firstParentTaskAfterCrossOverPoint = getTasksAfterCrossoverPoint(firstParentTasks,crossoverPoint);
        List<Task> secondParentTaskBeforeCrossOverPoint = getTasksBeforeCrossoverPoint(secondParentTasks,crossoverPoint);
        List<Task> secondParentTaskAfterCrossOverPoint = getTasksAfterCrossoverPoint(secondParentTasks,crossoverPoint);

        firstParentTaskBeforeCrossOverPoint.addAll(secondParentTaskAfterCrossOverPoint);
        secondParentTaskBeforeCrossOverPoint.addAll(firstParentTaskAfterCrossOverPoint);

        Task[] firstAfterCrossover = (Task[]) firstParentTaskBeforeCrossOverPoint.toArray();
        Task[] secondAfterCrossover = (Task[]) secondParentTaskBeforeCrossOverPoint.toArray();

        firstParent.setTasks(firstAfterCrossover);
        secondParent.setTasks(secondAfterCrossover);

    }

    //mutating tasks in schedule according to mutation rate
    public void mutate(Schedule schedule){
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
        int point = 0;
        for (int i = 0; i < numberOfTasks; i++){
            if(random.nextDouble() <= crossoverRate){
                point = i;
                break;
            }
        }

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
