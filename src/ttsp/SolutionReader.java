package ttsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SolutionReader {

    public static InterventionResult[] readInterventionsResults(File file) throws FileNotFoundException {
        Scanner intervDatesReader = new Scanner(file);
        ArrayList<InterventionResult> interventionsResults = new ArrayList<>();
        while (intervDatesReader.hasNextLine()){
            String str = intervDatesReader.nextLine();
            String[] data = str.split(" ", 0);
            InterventionResult interventionResult = new InterventionResult(Integer.parseInt(data[0]), Integer.parseInt(data[3]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            interventionsResults.add(interventionResult);
        }
        InterventionResult[] interventionsResultsCopy = new InterventionResult[interventionsResults.size()];
        for (int i = 0; i < interventionsResults.size(); i++){
            interventionsResultsCopy[i] = interventionsResults.get(i);
        }
        return interventionsResultsCopy;
    }

    public static Team[] readTeamsSchedule(File file) throws FileNotFoundException {
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
                ArrayList<Technician> technicians = new ArrayList<>();
                while (!data[i].equals("]")){
                    technicians.add(new Technician(Integer.parseInt(data[i++])));
                }
                Technician[] techs = new Technician[technicians.size()];
                for (int j = 0; j < technicians.size(); j++) {
                    techs[j] = technicians.get(j);
                }
                teams.add(new Team(day, currentTeam, techs));
            }
        }
        Team[] teamsCopy = new Team[teams.size()];
        for (int i = 0; i < teamsCopy.length; i++) {
            teamsCopy[i] = teams.get(i);
        }
        return teamsCopy;
    }

    public static TTSPSolution solutionReader(File intervDatesFile, File techTeamsFile) throws FileNotFoundException {
        InterventionResult[] interventionsResults = readInterventionsResults(intervDatesFile);
        Team[] teams = readTeamsSchedule(techTeamsFile);
        TTSPSolution ttspSolution = new TTSPSolution(interventionsResults, teams, teams[teams.length-1].getDay());
        ttspSolution.print();
        return ttspSolution;
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

        solutionReader(intervDatesFile, techTeamsFile);
    }
}
