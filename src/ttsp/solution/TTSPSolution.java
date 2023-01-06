package ttsp.solution;

import ttsp.data.TTSPData;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TTSPSolution {

    private final ArrayList<InterventionResult> interventionsResults;
    private final ArrayList<Team> teams;
    private final int nbDays;
    private final int nbInterventions;

    public TTSPSolution(ArrayList<InterventionResult> interventionsResults, ArrayList<Team> teams, int nbDays, int nbInterventions){
        this.interventionsResults = interventionsResults;
        this.teams = teams;
        this.nbDays = nbDays;
        this.nbInterventions = nbInterventions;
    }

    public TTSPSolution(ArrayList<InterventionResult> interventionsResults, ArrayList<Team> teams, int nbDays){
        this.interventionsResults = interventionsResults;
        this.teams = teams;
        this.nbDays = nbDays;
        this.nbInterventions = interventionsResults.size();
    }

    public TTSPSolution(TTSPData data, int[][][] x, int[][][] y, int[] o, int[] b){
        ArrayList<Team> teams = new ArrayList<>();
        for (int k = 0; k < data.nbInterventions(); k++) {
            int nbTeamsOnDay = 0;
            for (int g = 0; g < data.nbTechs(); g++) {
                ArrayList<Integer> technicians = new ArrayList<>();
                for (int t = 0; t < data.nbTechs(); t++) {
                    if (x[t][g][k] == 1){
                        technicians.add(t+1);
                    }
                }
                int size = technicians.size();
                if (size != 0){
                    int[] techniciansCopy = new int[size];
                    for (int a = 0; a < size; a++) {
                        techniciansCopy[a] = technicians.get(a);
                    }
                    teams.add(new Team(k+1, g, techniciansCopy));
                    nbTeamsOnDay++;
                }
            }
            if (nbTeamsOnDay <= 1){
                teams.remove(teams.size()-1);
            }
        }
        this.teams = teams;

        int nbDays = 0;
        for (Team t : this.teams){
            if (t.day() > nbDays){
                nbDays =t.day();
            }
        }
        this.nbDays = nbDays;

        ArrayList<InterventionResult> interventionsResults = new ArrayList<>();
        for (int i = 0; i < data.nbInterventions(); i++) {
            if (o[i] == 0){
                for (int g = 0; g < data.nbTechs(); g++) {
                    for (int k = 0; k < data.nbInterventions(); k++) {
                        if (y[i][g][k] == 1){
                            InterventionResult res = new InterventionResult(i+1, g, b[i]/120 + 1, b[i]%120);
                            interventionsResults.add(res);
                        }
                    }
                }
            }
        }

        this.interventionsResults = interventionsResults;
        this.nbInterventions = data.nbInterventions();
    }

    public ArrayList<InterventionResult> getInterventionsResults() {
        return interventionsResults;
    }

    public int getNbDays() {
        return nbDays;
    }

    public InterventionResult getInterventionResult(int number){
        for (InterventionResult i : this.interventionsResults){
            if (i.number() == number){
                return i;
            }
        }
        //Intervention #number is outsourced
        return null;
    }

    public boolean hasTeam(int day, int teamNb){
        for (Team t : teams){
            if (t.day() == day && t.teamNb() == teamNb){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Team> getTeamsOfDay(int day){
        ArrayList<Team> teams = new ArrayList<>();
        for (Team t : this.teams){
            if (t.day() == day){
                teams.add(t);
            }
        }
        return teams;
    }

    public Team getTeamOfDayWithNumber(int nb, int day){
        for (Team t : this.teams){
            if (t.day() == day && t.teamNb() == nb){
                return t;
            }
        }
        return null;
    }


    public ArrayList<Integer> getTeamsOfTechnician(int name, int day){
        ArrayList<Team> teams = getTeamsOfDay(day);
        ArrayList<Integer> teamsAssigned= new ArrayList<>();
        for (Team t : teams){
            for (int tech: t.technicians()){
                if (tech == name){
                    teamsAssigned.add(t.teamNb());
                }
            }
        }
        return teamsAssigned;
    }

    public ArrayList<InterventionResult> getInterventionsCompletedOnDay(int day){
        ArrayList<InterventionResult> interventionsCompleted = new ArrayList<>();
        for (InterventionResult i : interventionsResults){
            if (i.day() == day){
                interventionsCompleted.add(i);
            }
        }
        return interventionsCompleted;
    }
    public ArrayList<InterventionResult> getInterventionsCompletedOnDayByTeam(int day, int teamNb){
        ArrayList<InterventionResult> interventionsCompleted = new ArrayList<>();
        for (InterventionResult i : interventionsResults){
            if (i.day() == day && i.team() == teamNb){
                interventionsCompleted.add(i);
            }
        }
        return interventionsCompleted;
    }

    public void export(String path) throws IOException {
        File intervFile = new File(path + "/interv_dates");
        StringBuilder string1 = new StringBuilder();
        for (InterventionResult i : interventionsResults){
            string1.append(i.number()).append(" ").append(i.day()).append(" ").append(i.time()).append(" ").append(i.team()).append("\n");
        }
        FileWriter writer1 = new FileWriter(intervFile);
        writer1.write(string1.toString());
        writer1.close();

        File techFile = new File(path + "/tech_teams");
        StringBuilder string2 = new StringBuilder();
        for (int k = 1; k < nbDays+1; k++) {
            string2.append(k);
            ArrayList<Team> teams = getTeamsOfDay(k);
            for (Team g : teams){
                string2.append(" [ ");
                for (int t : g.technicians()){
                    string2.append(t).append(" ");
                }
                string2.append("]");
            }
            string2.append("\n");
        }
        FileWriter writer2 = new FileWriter(techFile);
        writer2.write(string2.toString());
        writer2.close();

    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("///////////// Solution ////////////\n");
        s.append("----------------------------------\n");
        s.append("----- INTERVENTION SCHEDULE ------\n");
        s.append("----------------------------------\n");
        for (InterventionResult i : this.interventionsResults){
            s.append(i);
        }
        s.append("\nOutsourced interventions: ");
        for (int i = 1; i < this.nbInterventions+1; i++) {
            if (getInterventionResult(i) == null){
                s.append(i).append(" ");
            }
        }

        s.append("\n----------------------------------\n");
        s.append("------- TECHNICIAN TEAMS ---------\n");
        s.append("----------------------------------\n");
        for (int day = 1; day < this.nbDays+1; day++) {
            s.append("Teams of day ").append(day).append("\n");
            for (Team t : getTeamsOfDay(day)){
                s.append(t).append("\n");
            }
        }
        return s.toString();
    }
}
