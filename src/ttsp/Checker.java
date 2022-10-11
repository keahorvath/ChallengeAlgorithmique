package ttsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Checker {

    public static boolean check(TTSPData data, TTSPSolution solution){
        return checkTechnicians(data, solution);
    }

    public static boolean checkTechnicians(TTSPData data, TTSPSolution solution){
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

    public static boolean checkInterventions(TTSPData data, TTSPSolution solution){

        return true;
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
