package ttsp;

import ttsp.data.Intervention;
import ttsp.data.TTSPData;
import ttsp.data.Technician;
import ttsp.solution.InterventionResult;
import ttsp.solution.TTSPSolution;
import ttsp.solution.Team;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Checker {

    public static boolean check(TTSPData data, TTSPSolution solution){
        boolean check1 = checkTechniciansConstraints(data, solution);
        boolean check2 = checkPredecessorsConstraints(data, solution);
        boolean check3 = checkBudgetConstraint(data, solution);
        boolean check4 = checkTeamConstraints(data, solution);
        boolean check5 = checkDomainsLevelsConstraints(data, solution);
        return check1 && check2 && check3 && check4 && check5;
    }

    public static boolean checkTechniciansConstraints(TTSPData data, TTSPSolution solution){
        boolean noIssue = true;
        for (int day = 1; day < solution.getNbDays()+1; day++) {
            for (Technician tech: data.getTechnicians()) {
                ArrayList<Integer> teamsAssigned = solution.getTeamsOfTechnician(tech.getName(), day);
                if (teamsAssigned.size() == 0){
                    System.out.println("[issue] Technician #" + tech.getName() + " is not assigned to a team on day " + day);
                    noIssue = false;
                }
                if (teamsAssigned.size() > 1){
                    System.out.print("[issue] Technician #" + tech.getName() + " is assigned to multiple teams on day " + day + " -> ");
                    for (int i : teamsAssigned){
                        System.out.print(i + " ");
                    }
                    System.out.println();
                    noIssue = false;
                }
                if (data.TechUnavailableOnDay(tech, day) && teamsAssigned.get(0) != 0){
                    System.out.println("[issue] Technician #" + tech.getName() + " is assigned to team " + teamsAssigned.get(0) + " on day " + day + " but should be assigned to team 0 because he/she is unavailable");
                    noIssue = false;
                }
            }
        }
        return noIssue;
    }

    public static boolean checkPredecessorsConstraints(TTSPData data, TTSPSolution solution){
        boolean noIssue = true;
        for(Intervention i : data.getInterventions()){
            InterventionResult iResult = solution.getInterventionResult(i.getNumber());
            //iResult == null means that i is outsourced
            if (iResult == null){
                continue;
            }
            int startTimeI = iResult.getStartTime();
            for (int nb : i.getPreds()){
                Intervention p = data.getInterventionFromNumber(nb);
                InterventionResult pResult = solution.getInterventionResult(nb);
                if (pResult == null){
                    System.out.println("[issue] Intervention #" + i.getNumber() + " is scheduled whereas its predecessor " + p.getNumber() + " is outsourced");
                    noIssue = false;
                    continue;
                }
                int endTimeP = pResult.getStartTime() + p.getDuration();
                if (startTimeI < endTimeP){
                    System.out.println("[issue] Intervention #" + i.getNumber() + " starts before the completion of intervention #" + p.getNumber() + " which is defined as one of its predecessors (day " + iResult.getDay() + " [" + iResult.getTime() + "," + (i.getDuration()+iResult.getTime()) + "] < day " + pResult.getDay() + " [" + pResult.getTime() + "," + (p.getDuration()+pResult.getTime()) + "])");
                    noIssue = false;
                }
            }
        }
        return noIssue;
    }

    public static boolean checkBudgetConstraint(TTSPData data, TTSPSolution solution){
        ArrayList<Intervention> outsourcedInterventions = new ArrayList<>();
        for (Intervention i : data.getInterventions()){
            if (solution.getInterventionResult(i.getNumber()) == null){
                outsourcedInterventions.add(i);
            }
        }
        int cost = 0;
        for (Intervention i : outsourcedInterventions){
            cost += i.getCost();
        }
        if (cost > data.getBudget()){
            System.out.print("[issue] The total cost of all outsourced interventions is larger than the outsourcing budget : " + cost + " > " + data.getBudget() + " (outsourced interventions : ");
            for (Intervention i : outsourcedInterventions){
                System.out.print(i.getNumber());
            }
            System.out.println(")");
            return false;
        }
        return true;
    }

    public static boolean checkTeamConstraints(TTSPData data, TTSPSolution solution){
        boolean noIssue = true;
        for (int day = 1; day < solution.getNbDays() + 1; day++){
            for (InterventionResult i : solution.getInterventionsCompletedOnDay(day)){
                if (i.getTeam() >= solution.getTeamsOfDay(day).length){
                    System.out.println("[issue] Intervention #" + i.getNumber() + " is assigned to team #" + i.getTeam() + " which does not exist on day " + day);
                    noIssue = false;
                }
            }
            for (int teamNb = 1; teamNb < solution.getTeamsOfDay(day).length; teamNb++){
                InterventionResult[] interventionsCompleted = solution.getInterventionsCompletedOnDayByTeam(day, teamNb);
                Arrays.sort(interventionsCompleted, Comparator.comparing(InterventionResult::getTime));
                for (int i = 1; i < interventionsCompleted.length; i++){
                    int prevStartTime = interventionsCompleted[i-1].getTime();
                    int prevEndTime = data.getInterventionFromNumber(interventionsCompleted[i-1].getNumber()).getDuration() + interventionsCompleted[i-1].getTime();
                    int currentStartTime =  interventionsCompleted[i].getTime();
                    int currentEndTime = data.getInterventionFromNumber(interventionsCompleted[i].getNumber()).getDuration() + interventionsCompleted[i].getTime();
                    if (prevEndTime > currentStartTime){
                        System.out.println("[issue] Interventions #" + interventionsCompleted[i-1].getNumber() + " and #" + interventionsCompleted[i].getNumber() + " overlap on day " + day + " and are executed by the same team #" + teamNb + " ([" + prevStartTime + "," + prevEndTime + "] and [" + currentStartTime + "," + currentEndTime + "])");
                        noIssue = false;
                    }
                }

            }


        }

        return noIssue;
    }

    public static boolean checkDomainsLevelsConstraints(TTSPData data, TTSPSolution solution){
        boolean noIssue = true;
        for (InterventionResult i : solution.getInterventionsResults()){
            Team team = solution.getTeamOfDayWithNumber(i.getTeam(), i.getDay());
            if (team == null){
                continue;
            }
            for (int d = 1; d < data.getNbDomains() + 1; d++) {
                for (int l = 1; l < data.getNbLevels() + 1; l++) {
                    int nbTechnicians = team.nbTechniciansOfLevelInDomain(data, l, d);
                    int nbTechniciansRequired = data.getInterventionFromNumber(i.getNumber()).getDomains()[d-1][l-1];
                    if  (nbTechnicians < nbTechniciansRequired){
                        System.out.print("[issue] Team #" + team.getTeamNb() + " ( ");
                        for (int t : team.getTechnicians()){
                            System.out.print(t + " ");
                        }
                        System.out.println(") on day " + i.getDay() + " is not skilled enough to perform intervention #" + i.getNumber() + " (domain:" + d + " / level:" + l + " -> " + nbTechnicians + " < " + nbTechniciansRequired + " )");
                        noIssue = false;
                    }
                }
            }
        }
        return noIssue;
    }

    public static void main(String[] args) throws Exception {
        File instanceFile = new File(args[0] + "/instance");
        File intervListFile = new File(args[0] + "/interv_list");
        File techListFile = new File(args[0] + "/tech_list");
        File intervDatesFile = new File(args[0] + "/interv_dates");
        File techTeamsFile = new File(args[0] + "/tech_teams");

        TTSPData data = InstanceReader.instanceReader(instanceFile, intervListFile, techListFile);
        TTSPSolution solution = SolutionReader.solutionReader(intervDatesFile, techTeamsFile);
        System.out.println("----------------------------------");
        System.out.println("------- CHECK CONSTRAINTS --------");
        System.out.println("----------------------------------");
        boolean isFeasible = check(data, solution);
        System.out.println("-> FEASIBLE = " + isFeasible);
        System.out.println();
    }
}
