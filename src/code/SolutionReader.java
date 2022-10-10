package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SolutionReader {

    //This readInterventionsSchedule method reads an interv_dates file without knowing the interventions' data
    public static InterventionsSchedule readInterventionsSchedule(File file) throws FileNotFoundException {
        Scanner intervDatesReader = new Scanner(file);
        ArrayList<Intervention> intervs = new ArrayList<>();
        while (intervDatesReader.hasNextLine()){
            String str = intervDatesReader.nextLine();
            String[] data = str.split(" ", 0);
            Intervention intervention = new Intervention(Integer.parseInt(data[0]));
            intervention.fillResults(Integer.parseInt(data[3]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            intervs.add(intervention);
        }
        Intervention[] interventions = new Intervention[intervs.size()];
        for (int i = 0; i < intervs.size(); i++){
            interventions[i] = intervs.get(i);
        }
        return new InterventionsSchedule(interventions);
    }
    public static InterventionsSchedule readInterventionsSchedule(File file, TTSPData ttspData) throws FileNotFoundException {
        Scanner intervDatesReader = new Scanner(file);
        int currentIntervention = 0;
        while (intervDatesReader.hasNextLine()){
            String str = intervDatesReader.nextLine();
            String[] data = str.split(" ", 0);
            ttspData.getInterventions()[currentIntervention].fillResults(Integer.parseInt(data[3]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            currentIntervention++;
        }
        return new InterventionsSchedule(ttspData.getInterventions());
    }

    public static TeamsSchedule readTeamsSchedule(File file) throws Exception {
        Scanner teamsReader = new Scanner(file);
        ArrayList<TeamsDay> days = new ArrayList<>();
        while (teamsReader.hasNextLine()){
            String str = teamsReader.nextLine();
            String[] data = str.split(" ", 0);
            int day = Integer.parseInt(data[0]);
            int currentTeam = -1;
            ArrayList<Team> teams = new ArrayList<>();
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
            Team[] teams2 = new Team[teams.size()];
            for (int i = 0; i < teams.size(); i++) {
                teams2[i] = teams.get(i);
            }
            TeamsDay teamsDay = new TeamsDay(teams2);
            days.add(teamsDay);

        }
        TeamsDay[] days2 = new TeamsDay[days.size()];
        for (int i = 0; i < days.size(); i++) {
            days2[i] = days.get(i);
        }
        return new TeamsSchedule(days2);
    }

    public static TTSPSolution solutionReader(File intervDatesFile, File techTeamsFile) throws Exception {
        InterventionsSchedule interventionsSchedule = readInterventionsSchedule(intervDatesFile);
        TeamsSchedule teamsSchedule = readTeamsSchedule(techTeamsFile);
        TTSPSolution ttspSolution = new TTSPSolution(interventionsSchedule, teamsSchedule);
        ttspSolution.print();
        return ttspSolution;
    }

    public static TTSPSolution solutionReader(File intervDatesFile, File techTeamsFile, TTSPData data) throws Exception {
        InterventionsSchedule interventionsSchedule = readInterventionsSchedule(intervDatesFile, data);
        TeamsSchedule teamsSchedule = readTeamsSchedule(techTeamsFile);
        TTSPSolution ttspSolution = new TTSPSolution(interventionsSchedule, teamsSchedule);
        ttspSolution.print();
        return ttspSolution;
    }

    public static void main(String[] args) throws Exception {
        File intervDatesFile = new File(args[0] + "/interv_dates");
        File techTeamsFile = new File(args[0] + "/tech_teams");

        solutionReader(intervDatesFile, techTeamsFile);


    }
}
