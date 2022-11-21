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
            for (Technician tech: data.technicians()) {
                ArrayList<Integer> teamsAssigned = solution.getTeamsOfTechnician(tech.name(), day);
                if (teamsAssigned.size() == 0){
                    System.out.println("[issue] Technician #" + tech.name() + " is not assigned to a team on day " + day);
                    noIssue = false;
                }
                if (teamsAssigned.size() > 1){
                    System.out.print("[issue] Technician #" + tech.name() + " is assigned to multiple teams on day " + day + " -> ");
                    for (int i : teamsAssigned){
                        System.out.print(i + " ");
                    }
                    System.out.println();
                    noIssue = false;
                }
                if (data.TechUnavailableOnDay(tech, day) && teamsAssigned.get(0) != 0){
                    System.out.println("[issue] Technician #" + tech.name() + " is assigned to team " + teamsAssigned.get(0) + " on day " + day + " but should be assigned to team 0 because he/she is unavailable");
                    noIssue = false;
                }
            }
        }
        return noIssue;
    }

    public static boolean checkPredecessorsConstraints(TTSPData data, TTSPSolution solution){
        boolean noIssue = true;
        for(Intervention i : data.interventions()){
            InterventionResult iResult = solution.getInterventionResult(i.number());
            //iResult == null means that intervention is outsourced
            if (iResult == null){
                continue;
            }
            int startTimeI = iResult.getStartTime();
            for (int nb : i.preds()){
                Intervention p = data.getInterventionFromNumber(nb);
                InterventionResult pResult = solution.getInterventionResult(nb);
                if (pResult == null){
                    System.out.println("[issue] Intervention #" + i.number() + " is scheduled whereas its predecessor #" + p.number() + " is outsourced");
                    noIssue = false;
                    continue;
                }
                int endTimeP = pResult.getStartTime() + p.duration();
                if (startTimeI < endTimeP){
                    System.out.println("[issue] Intervention #" + i.number() + " starts before the completion of intervention #" + p.number() + " which is defined as one of its predecessors (day " + iResult.day() + " [" + iResult.time() + "," + (i.duration()+iResult.time()) + "] < day " + pResult.day() + " [" + pResult.time() + "," + (p.duration()+pResult.time()) + "])");
                    noIssue = false;
                }
            }
        }
        return noIssue;
    }

    public static boolean checkBudgetConstraint(TTSPData data, TTSPSolution solution){
        ArrayList<Intervention> outsourcedInterventions = new ArrayList<>();
        for (Intervention i : data.interventions()){
            if (solution.getInterventionResult(i.number()) == null){
                outsourcedInterventions.add(i);
            }
        }
        int cost = 0;
        for (Intervention i : outsourcedInterventions){
            cost += i.cost();
        }
        if (cost > data.budget()){
            System.out.print("[issue] The total cost of all outsourced interventions is larger than the outsourcing budget : " + cost + " > " + data.budget() + " (outsourced interventions : ");
            for (Intervention i : outsourcedInterventions){
                System.out.print(i.number());
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
                if (!solution.hasTeam(day, i.team())){
                    System.out.println("[issue] Intervention #" + i.number() + " is assigned to team #" + i.team() + " which does not exist on day " + day);
                    noIssue = false;
                }
            }
            for (int teamNb = 1; teamNb < solution.getTeamsOfDay(day).length; teamNb++){
                InterventionResult[] interventionsCompleted = solution.getInterventionsCompletedOnDayByTeam(day, teamNb);
                Arrays.sort(interventionsCompleted, Comparator.comparing(InterventionResult::time));
                for (int i = 1; i < interventionsCompleted.length; i++){
                    int prevStartTime = interventionsCompleted[i-1].time();
                    int prevEndTime = data.getInterventionFromNumber(interventionsCompleted[i-1].number()).duration() + interventionsCompleted[i-1].time();
                    int currentStartTime =  interventionsCompleted[i].time();
                    int currentEndTime = data.getInterventionFromNumber(interventionsCompleted[i].number()).duration() + interventionsCompleted[i].time();
                    if (prevEndTime > currentStartTime){
                        System.out.println("[issue] Interventions #" + interventionsCompleted[i-1].number() + " and #" + interventionsCompleted[i].number() + " overlap on day " + day + " and are executed by the same team #" + teamNb + " ([" + prevStartTime + "," + prevEndTime + "] and [" + currentStartTime + "," + currentEndTime + "])");
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
            Team team = solution.getTeamOfDayWithNumber(i.team(), i.day());
            if (team == null){
                continue;
            }
            for (int d = 1; d < data.nbDomains() + 1; d++) {
                for (int l = 1; l < data.nbLevels() ; l++) {
                    int nbTechnicians = team.nbTechniciansOfLevelInDomain(data, l, d);
                    int nbTechniciansRequired = data.getInterventionFromNumber(i.number()).domains()[d-1][l-1];
                    if  (nbTechnicians < nbTechniciansRequired){
                        System.out.print("[issue] Team #" + team.teamNb() + " ( ");
                        for (int t : team.technicians()){
                            System.out.print(t + " ");
                        }
                        System.out.println(") on day " + i.day() + " is not skilled enough to perform intervention #" + i.number() + " (domain:" + d + " / level:" + l + " -> " + nbTechnicians + " < " + nbTechniciansRequired + " )");
                        noIssue = false;
                    }
                }
            }
        }
        return noIssue;
    }

    public static String usage(){
        return "usage: java -jar checker.jar <absolutePathToFolder>";
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1){
            System.out.println(usage());
            System.exit(1);
        }
        File instanceFile = new File(args[0] + "/instance");
        File intervListFile = new File(args[0] + "/interv_list");
        File techListFile = new File(args[0] + "/tech_list");
        File intervDatesFile = new File(args[0] + "/interv_dates");
        File techTeamsFile = new File(args[0] + "/tech_teams");

        TTSPData data = InstanceReader.instanceReader(instanceFile, intervListFile, techListFile);
        System.out.println(data);
        TTSPSolution solution = SolutionReader.solutionReader(intervDatesFile, techTeamsFile);
        System.out.println(solution);
        System.out.println("----------------------------------");
        System.out.println("------- CHECK CONSTRAINTS --------");
        System.out.println("----------------------------------");
        boolean isFeasible = check(data, solution);
        System.out.println("-> FEASIBLE = " + isFeasible);
        System.out.println();
    }
}
