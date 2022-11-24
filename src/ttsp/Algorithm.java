package ttsp;

import gurobi.GRBException;
import ttsp.data.Intervention;
import ttsp.data.TTSPData;
import ttsp.data.Technician;
import ttsp.solution.InterventionResult;
import ttsp.solution.TTSPSolution;
import ttsp.solution.Team;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import static ttsp.InstanceReader.instanceReader;

public class Algorithm {

    public static TTSPSolution algorithm(TTSPData data){

        ArrayList<Intervention> interventionsToDo = new ArrayList<>();
        for(Intervention i : data.interventions()){
            interventionsToDo.add(i);
        }
        ArrayList<Integer> outsourcedInterventions = new ArrayList<>();
        ArrayList<InterventionResult> interventionResults = new ArrayList<>();
        ArrayList<Team> teams = new ArrayList<>();

        int currentDay = 1;
        int remainingBudget = data.budget();

        while (interventionsToDo.size() != 0){
            ArrayList<Team> teamsOfCurrentDay = new ArrayList<>();
            ArrayList<Technician> availableTechnicians = new ArrayList<>();
            for (Technician t : data.technicians()){
                if (t.isAvailable(currentDay)){
                    availableTechnicians.add(t);
                }
            }

            //If the remaining interventions can all be outsourced, outsource them
            if (costOfInterventions(interventionsToDo) <= remainingBudget){
                for (Intervention i : interventionsToDo){
                    outsourcedInterventions.add(i.number());
                    interventionsToDo.remove(i);
                }
                continue;
            }

            //while interventions can still be done on currentDay
            while(true){
                Intervention currentIntervention = getNextDoableIntervention(data, interventionsToDo);
            }


        }
        return null;
    }

    public static int costOfInterventions(ArrayList<Intervention> interventions){
        int cost = 0;
        for (Intervention i : interventions){
            cost += i.cost();
        }
        return cost;
    }

    public static Intervention getNextDoableIntervention(TTSPData data, ArrayList<Intervention> interventions){
        for (Intervention i : interventions){
            boolean arePredsDone = true;
            for (int j : i.preds()){
                Intervention pred = data.getInterventionFromNumber(j);
                if (interventions.contains(pred)){
                    arePredsDone = false;
                    break;
                }
            }
            if (arePredsDone){
                return i;
            }
        }
        return null;
    }
    public static String usage(){
        return "usage: java -jar algorithm.jar absolutePathToFolder";
    }

    public static void main(String[] args) throws FileNotFoundException, GRBException {
        if (args.length != 1){
            System.out.println(usage());
            System.exit(1);
        }
        File instanceFile = new File(args[0] + "/instance");
        File intervListFile = new File(args[0] + "/interv_list");
        File techListFile = new File(args[0] + "/tech_list");

        TTSPData data = instanceReader(instanceFile, intervListFile, techListFile);
        System.out.println(data);
    }
}
