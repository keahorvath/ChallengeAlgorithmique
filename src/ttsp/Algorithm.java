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
import java.util.Collections;

import static ttsp.InstanceReader.instanceReader;

public class Algorithm {

    public static TTSPSolution algorithm(TTSPData data){

        ArrayList<Intervention> interventionsToDo = getAndSortInterventions(data);
        ArrayList<Integer> outsourcedInterventions = new ArrayList<>();
        ArrayList<InterventionResult> interventionResults = new ArrayList<>();
        ArrayList<Team> teams = new ArrayList<>();

        int currentDay = 1;
        int remainingBudget = data.budget();

        while (interventionsToDo.size() != 0){
            //If the remaining interventions can all be outsourced, outsource them
            if (costOfInterventions(interventionsToDo) <= remainingBudget){
                for (Intervention i : interventionsToDo){
                    outsourcedInterventions.add(i.number());
                    interventionsToDo.remove(i);
                }
                continue;
            }

            //Reset teams and available technicians
            ArrayList<Team> teamsOfCurrentDay = new ArrayList<>();
            ArrayList<Technician> availableTechnicians = getAndSortTechnicians(currentDay, data);

            //Create the teams of currentDay
            //TODO Issue: infinite loop when bestTeam == null -> in getNextDoableIntervention check if i can be done with the remaining technicians
            while(availableTechnicians.size() != 0 && interventionsToDo.size() != 0){
                Intervention currentIntervention = getNextDoableIntervention(120 * (currentDay - 1), data, interventionsToDo, interventionResults);
                if (currentIntervention == null){
                    //It means that no more interventions can be done on the current day
                    break;
                }
                Team bestTeam = getBestTeam(currentIntervention, availableTechnicians, currentDay, teamsOfCurrentDay.size()+1);
                if (bestTeam == null){
                    //It means that the current intervention can't be done with the remaining technicians
                    continue;
                }
                for (int t : bestTeam.technicians()){
                    availableTechnicians.remove(data.getTechnicianFromNumber(t));
                }
                teamsOfCurrentDay.add(bestTeam);
                interventionResults.add(new InterventionResult(currentIntervention.number(), teamsOfCurrentDay.size(), currentDay, 0));
                interventionsToDo.remove(currentIntervention);
            }
            teams.add(getTeamZero(currentDay, data, availableTechnicians));
            teams.addAll(teamsOfCurrentDay);
            currentDay++;

        }
        InterventionResult[] interventionResultsCopy = new InterventionResult[interventionResults.size()];
        for (int i = 0; i < interventionResultsCopy.length; i++) {
            interventionResultsCopy[i] = interventionResults.get(i);
        }
        Team[] teamsCopy = new Team[teams.size()];
        for (int i = 0; i < teamsCopy.length; i++) {
            teamsCopy[i] = teams.get(i);
        }
        return new TTSPSolution(interventionResultsCopy, teamsCopy, currentDay-1);
    }

    //TODO: also compare by complexity (nb of domains needed)
    public static ArrayList<Intervention> getAndSortInterventions(TTSPData data){
        ArrayList<Intervention> interventionsToDo = new ArrayList<>();
        Collections.addAll(interventionsToDo, data.interventions());
        Collections.sort(interventionsToDo);
        return interventionsToDo;
    }

    public static ArrayList<Technician> getAndSortTechnicians(int day, TTSPData data){
        ArrayList<Technician> availableTechnicians = new ArrayList<>();
        for (Technician t : data.technicians()){
            if (t.isAvailable(day)){
                availableTechnicians.add(t);
            }
        }
        Collections.sort(availableTechnicians);
        return availableTechnicians;
    }

    public static Team getTeamZero(int day, TTSPData data, ArrayList<Technician> nonWorkingTechnicians){
        ArrayList<Technician> unavailableTechnicians = new ArrayList<>();
        for (Technician t : data.technicians()){
            if (!t.isAvailable(day)){
                unavailableTechnicians.add(t);
            }
        }
        int[] techniciansTeamZero = new int[unavailableTechnicians.size() + nonWorkingTechnicians.size()];
        for (int i = 0; i < unavailableTechnicians.size(); i++) {
            techniciansTeamZero[i] = unavailableTechnicians.get(i).name();
        }
        for (int i = 0; i < nonWorkingTechnicians.size(); i++) {
            techniciansTeamZero[i+unavailableTechnicians.size()] = nonWorkingTechnicians.get(i).name();
        }
        return new Team(day, 0, techniciansTeamZero);
    }

    public static int costOfInterventions(ArrayList<Intervention> interventions){
        int cost = 0;
        for (Intervention i : interventions){
            cost += i.cost();
        }
        return cost;
    }

    /**
     *Returns the next intervention that doesn't have any uncompleted predecessors
     */
    public static Intervention getNextDoableIntervention(int currentTime, TTSPData data, ArrayList<Intervention> interventions, ArrayList<InterventionResult> results){
        for (Intervention i : interventions){
            boolean arePredsDone = true;
            for (int j : i.preds()){
                Intervention pred = data.getInterventionFromNumber(j);
                if (interventions.contains(pred)){
                    arePredsDone = false;
                    break;
                }
                //Get the result for intervention j:
                for (InterventionResult k : results){
                    if (k.number() == j && k.getEndTime(data) > currentTime){
                        arePredsDone = false;
                        break;
                    }
                }
            }
            if (arePredsDone){
                return i;
            }
        }
        return null;
    }

    /**
     * TODO
     * Removes the used technicians
     *Returns the team with the least amount of technicians and least amount of overqualified technicians
     */
    public static Team getBestTeam(Intervention intervention, ArrayList<Technician> technicians, int day, int teamNb){
        int[][] domains = intervention.domains();
        int[][] zeros = new int[domains.length][domains[0].length];
        ArrayList<Technician> techniciansCopy = new ArrayList<>(technicians);
        ArrayList<Technician> techniciansUsed = new ArrayList<>();

        while (!Arrays.deepEquals(domains, zeros)){
            if (techniciansCopy.size() == 0){
                return null;
            }
            Technician t = techniciansCopy.get(0);
            techniciansUsed.add(t);
            techniciansCopy.remove(t);
            for (int d = 1; d < domains.length+1; d++) {
                for (int l = 1; l < domains[0].length+1; l++) {
                    if (domains[d-1][l-1] != 0 && t.isQualified(d, l)){
                        domains[d-1][l-1]--;
                    }
                }
            }
        }
        int[] techniciansUsedCopy = new int[techniciansUsed.size()];
        for (int i = 0; i < techniciansUsed.size(); i++) {
            techniciansUsedCopy[i] = techniciansUsed.get(i).name();
        }
        return new Team(day, teamNb, techniciansUsedCopy);
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
        TTSPSolution solution = algorithm(data);
        System.out.println(solution);

        boolean check = Checker.check(data, solution);
        if (check){
            System.out.println("Solution is feasible");
        }else{
            System.out.println("Solution is not feasible");
        }
    }
}