import msrcpsp.io.MSRCPSPIO;
import msrcpsp.scheduling.Resource;
import msrcpsp.scheduling.Schedule;
import msrcpsp.scheduling.Task;

import java.util.List;

/**
 * Created by GreggJakubiak on 09.03.2017.
 */
public class Population {
    private Schedule[] individuals;
    private MSRCPSPIO reader = new MSRCPSPIO();

    //constructor for creating population of a given size and using given def file
    public Population(int popSize, String fileName){
        individuals = new Schedule[popSize];
        for(int i = 0; i < individuals.length; i++){
            individuals[i] = reader.readDefinition(fileName);
        }
    }

    //initializing population at random
    private void initializeRandomPopulation(){
        for(int i = 0; i < individuals.length; i++){

        }
    }

    //initializing particular individual taking into account task constraints
    private void initializeRandomIndividual(Schedule schedule){
        List<Resource> capableResources;
        Task[] tasks = schedule.getTasks();
        for(int i = 0; i < tasks.length; i++){
            capableResources = schedule.getCapableResources(tasks[i]);
        }
    }
}
