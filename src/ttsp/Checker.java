package ttsp;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class Checker {

    public static boolean check(TTSPData data, TTSPSolution solution){
        return checkTechnicians(data, solution);
    }

    public static boolean checkTechnicians(TTSPData data, TTSPSolution solution){
        /*
        for (TeamsDay day : solution.getTeamsSchedule().getDays()) {
            ArrayList<Integer> workingTechnicians = new ArrayList<>();
            ArrayList<Integer> notWorkingTechnicians = new ArrayList<>();
            for (Team t : day.getTeams()){
                for (Technician tech : t.getTechnicians()){
                    if (t.getTeamNb() == 0){
                        notWorkingTechnicians.add(tech.getName());
                    }else{
                        workingTechnicians.add(tech.getName());
                    }
                }
            }
            for (Technician tech : data.getTechnicians()){
                for (int u: tech.getUnavailability()){
                    if (u == day.getDay() && workingTechnicians.contains(tech.getName())){
                        System.out.println("[issue] Technician #" + tech.getName() + " was assigned to a team on day " + day.getDay() + " but should be assigned to team 0 because he/she is unavailable");
                        return false;
                    }
                }
                int occurrences1 = Collections.frequency(workingTechnicians, tech.getName());
                int occurrences2 = Collections.frequency(notWorkingTechnicians, tech.getName());
                if (occurrences1 + occurrences2 == 0){
                    System.out.println("[issue] Technician #" + tech.getName() + " is not assigned to a team on day " + day.getDay());
                    return false;
                }else if (occurrences1 + occurrences2 > 1){
                    System.out.println("[issue] Technician #" + tech.getName() + " is assigned to multiple teams on day " + day.getDay());
                    return false;
                }
            }
        }
        return true;

         */
        return true;
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