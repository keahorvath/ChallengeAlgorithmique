package ttsp;

import ttsp.solution.InterventionResult;
import ttsp.solution.TTSPSolution;
import ttsp.solution.Team;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SolutionReader {

    public static ArrayList<InterventionResult> readInterventionsResults(File file) throws FileNotFoundException {
        Scanner intervDatesReader = new Scanner(file);
        ArrayList<InterventionResult> interventionsResults = new ArrayList<>();
        while (intervDatesReader.hasNextLine()){
            String str = intervDatesReader.nextLine();
            String[] data = str.split(" ", 0);
            InterventionResult interventionResult = new InterventionResult(Integer.parseInt(data[0]), Integer.parseInt(data[3]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            interventionsResults.add(interventionResult);
        }
        return interventionsResults;
    }

    public static ArrayList<Team> readTeamsSchedule(File file) throws FileNotFoundException {
        Scanner teamsReader = new Scanner(file);
        ArrayList<Team> teams = new ArrayList<>();
        while (teamsReader.hasNextLine()){
            String str = teamsReader.nextLine();
            String[] data = str.split(" ", 0);
            int day = Integer.parseInt(data[0]);
            int currentTeam = -1;
            for (int i = 1; i < data.length; i++) {
                if (data[i].equals("[")){
                    currentTeam++;
                    continue;
                }
                ArrayList<Integer> technicians = new ArrayList<>();
                while (!data[i].equals("]")){
                    technicians.add(Integer.parseInt(data[i++]));
                }
                int[] techniciansCopy = new int[technicians.size()];
                for (int j = 0; j < technicians.size(); j++) {
                    techniciansCopy[j] = technicians.get(j);
                }
                teams.add(new Team(day, currentTeam, techniciansCopy));
            }
        }
        return teams;
    }

    public static TTSPSolution solutionReader(File intervDatesFile, File techTeamsFile) throws FileNotFoundException {
        ArrayList<InterventionResult> interventionsResults = readInterventionsResults(intervDatesFile);
        ArrayList<Team> teams = readTeamsSchedule(techTeamsFile);
        return new TTSPSolution(interventionsResults, teams, teams.get(teams.size()-1).day());
    }

    public static String usage(){
        return "usage: java -jar solutionReader.jar <absolutePathToFolder>";
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1){
            System.out.println(usage());
            System.exit(1);
        }
        File intervDatesFile = new File(args[0] + "/interv_dates");
        File techTeamsFile = new File(args[0] + "/tech_teams");

        TTSPSolution solution = solutionReader(intervDatesFile, techTeamsFile);
        System.out.println(solution);
    }
}
