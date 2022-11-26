package ttsp;

import gurobi.GRBException;
import ttsp.data.Intervention;
import ttsp.data.TTSPData;
import ttsp.data.Technician;
import ttsp.solution.InterventionResult;
import ttsp.solution.TTSPSolution;
import ttsp.solution.Team;

import java.io.File;
import java.io.IOException;
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
                }
                interventionsToDo.clear();
                continue;
            }

            //Reset teams and available technicians
            ArrayList<Team> teamsOfCurrentDay = new ArrayList<>();
            ArrayList<Integer> stopTimeOfTeams = new ArrayList<>();
            ArrayList<Technician> availableTechnicians = getAndSortTechnicians(currentDay, data);

            //Create the teams of currentDay
            while(availableTechnicians.size() != 0 && interventionsToDo.size() != 0){
                Intervention currentIntervention = getNextDoableIntervention(120 * (currentDay - 1), data, interventionsToDo, interventionResults, availableTechnicians);
                if (currentIntervention == null){
                    //It means that no more interventions can be done on the current day
                    break;
                }
                Team bestTeam = getBestTeam(currentIntervention, availableTechnicians, currentDay, teamsOfCurrentDay.size()+1);
                if (bestTeam == null){
                    //It means that the current intervention can't be done with the remaining technicians
                    //Should never happen if code is correct
                    System.err.println("No team found for intervention");
                    continue;
                }
                for (int t : bestTeam.technicians()){
                    availableTechnicians.remove(data.getTechnicianFromNumber(t));
                }
                teamsOfCurrentDay.add(bestTeam);
                stopTimeOfTeams.add(currentIntervention.duration());
                interventionResults.add(new InterventionResult(currentIntervention.number(), teamsOfCurrentDay.size(), currentDay, 0));
                interventionsToDo.remove(currentIntervention);
            }

            //Do as many interventions as possible with current teams
            for (Team g : teamsOfCurrentDay){
                int currentStopTime = stopTimeOfTeams.get(g.teamNb()-1);
                ArrayList<Technician> techniciansOfTeam = new ArrayList<>();
                for (int t : g.technicians()){
                    techniciansOfTeam.add(data.getTechnicianFromNumber(t));
                }
                Intervention currentIntervention = getNextDoableIntervention(120 * (currentDay - 1) + currentStopTime, data, interventionsToDo, interventionResults, techniciansOfTeam);
                //While interventions can still be done by team
                while (currentIntervention != null && currentStopTime != 120){
                    interventionResults.add(new InterventionResult(currentIntervention.number(), g.teamNb(), currentDay, currentStopTime));
                    interventionsToDo.remove(currentIntervention);
                    currentStopTime += currentIntervention.duration();
                    currentIntervention = getNextDoableIntervention(120 * (currentDay - 1) + currentStopTime, data, interventionsToDo, interventionResults, techniciansOfTeam);
                }
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
     * and that is doable with the remaining available technicians
     */
    public static Intervention getNextDoableIntervention(int currentTime, TTSPData data, ArrayList<Intervention> interventions, ArrayList<InterventionResult> results, ArrayList<Technician> availableTechnicians){
        for (Intervention i : interventions){
            if ((currentTime%120) + i.duration() > 120){
                continue;
            }
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
            if (!arePredsDone){
                continue;
            }
            if (i.isDoable(availableTechnicians)){
                return i;
            }
        }
        return null;
    }

    /**
     *Returns the team with the least amount of technicians and least amount of overqualified technicians
     */
    public static Team getBestTeam(Intervention intervention, ArrayList<Technician> technicians, int day, int teamNb){
        int[][] domains = intervention.getImprovedDomains();
        int[][] zeros = new int[domains.length][domains[0].length];
        for (int i = 0; i < domains.length; i++) {
            Arrays.fill(zeros[i], 0);
        }
        ArrayList<Technician> techniciansCopy = new ArrayList<>(technicians);
        ArrayList<Technician> techniciansUsed = new ArrayList<>();

        while (!Arrays.deepEquals(domains, zeros)){
            if (techniciansCopy.size() == 0) {
                return null;
            }
            ArrayList<Technician> usableTechnicians = new ArrayList<>();
            ArrayList<Integer> nbUses = new ArrayList<>();
            for (int d = 1; d < domains.length+1; d++) {
                for (int l = 1; l < domains[0].length+1; l++) {
                    if (domains[d-1][l-1] == 0){
                        continue;
                    }
                    for (Technician t : techniciansCopy){
                        if (t.isQualified(d, l)){
                            if (usableTechnicians.contains(t)){
                                int prevValue = nbUses.get(usableTechnicians.indexOf(t));
                                nbUses.set(usableTechnicians.indexOf(t), prevValue+1);
                            }else{
                                usableTechnicians.add(t);
                                nbUses.add(1);
                            }
                        }
                    }
                }
            }
            //Get all technicians that has the max amount of uses
            ArrayList<Technician> bestTechnicians = new ArrayList<>();
            int bestValue = Collections.max(nbUses);
            for (int i = 0; i < usableTechnicians.size(); i++){
                if (nbUses.get(i) == bestValue){
                    bestTechnicians.add(usableTechnicians.get(i));
                }
            }
            Technician bestTechnician = bestTechnicians.get(0);
            for (Technician t : bestTechnicians){
                if (t.amountUnusedLevels(domains) < bestTechnician.amountUnusedLevels(domains)){
                    bestTechnician = t;
                }
            }

            //Update values
            techniciansUsed.add(bestTechnician);
            techniciansCopy.remove(bestTechnician);
            for (int d = 1; d < domains.length+1; d++) {
                for (int l = 1; l < domains[0].length+1; l++) {
                    if (domains[d-1][l-1] != 0 && bestTechnician.isQualified(d, l)){
                        domains[d-1][l-1]--;
                    }
                }
            }
            intervention.getImprovedDomains(domains);
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

    public static void main(String[] args) throws IOException, GRBException {
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
            System.out.println("-> TOTAL COST = " + Evaluator.evaluate(data, solution));
            solution.export(args[0]);
        }else{
            System.out.println("Solution is not feasible");
        }
    }
}
