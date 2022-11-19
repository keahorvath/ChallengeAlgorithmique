package ttsp.solution;

import ttsp.Evaluator;
import ttsp.Solver;
import ttsp.data.TTSPData;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.ArrayList;

public class TTSPSolution {

    private final InterventionResult[] interventionsResults;
    private final Team[] teams;
    private final int nbDays;

    public TTSPSolution(InterventionResult[] interventionsResults, Team[] teams, int nbDays){
        this.interventionsResults = interventionsResults;
        this.teams = teams;
        this.nbDays = nbDays;
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
        int size = teams.size();
        Team[] teamsCopy = new Team[size];
        for (int a = 0; a < size; a++) {
            teamsCopy[a] = teams.get(a);
        }

        this.teams = teamsCopy;

        int nbDays = 0;
        for (Team t : this.teams){
            if (t.getDay() > nbDays){
                nbDays =t.getDay();
            }
        }
        this.nbDays = nbDays;

        ArrayList<InterventionResult> interventionsResults = new ArrayList<>();
        for (int i = 0; i < data.nbInterventions(); i++) {
            if (o[i] == 0){
                for (int g = 0; g < data.nbTechs(); g++) {
                    for (int k = 0; k < data.nbInterventions(); k++) {
                        if (y[i][g][k] == 1){
                            System.out.println("intervention" + (i+1) + " : b=" + b[i] + " end=" + (b[i]+data.interventions()[i].duration()));
                            InterventionResult res = new InterventionResult(i+1, g, b[i]/120 + 1, b[i]%120);
                            interventionsResults.add(res);
                        }
                    }
                }
            }
        }
        int size2 = interventionsResults.size();
        InterventionResult[] interventionsResultsCopy = new InterventionResult[size2];
        for (int a = 0; a < size2; a++) {
            interventionsResultsCopy[a] = interventionsResults.get(a);
        }

        this.interventionsResults = interventionsResultsCopy;

    }

    public InterventionResult[] getInterventionsResults() {
        return interventionsResults;
    }

    public int getNbDays() {
        return nbDays;
    }

    public InterventionResult getInterventionResult(int number){
        for (InterventionResult i : this.interventionsResults){
            if (i.getNumber() == number){
                return i;
            }
        }
        //Intervention #number is outsourced
        return null;
    }

    public boolean hasTeam(int day, int teamNb){
        for (Team t : teams){
            if (t.getDay() == day && t.getTeamNb() == teamNb){
                return true;
            }
        }
        return false;
    }
    public Team[] getTeamsOfDay(int day){
        ArrayList<Team> teams = new ArrayList<>();
        for (Team t : this.teams){
            if (t.getDay() == day){
                teams.add(t);
            }
        }
        Team[] teamsCopy = new Team[teams.size()];
        for (int i = 0; i < teamsCopy.length; i++) {
            teamsCopy[i] = teams.get(i);
        }
        return teamsCopy;
    }

    public Team getTeamOfDayWithNumber(int nb, int day){
        for (Team t : this.teams){
            if (t.getDay() == day && t.getTeamNb() == nb){
                return t;
            }
        }
        return null;
    }

    public ArrayList<Integer> getTeamsOfTechnician(int name, int day){
        Team[] teams = getTeamsOfDay(day);
        ArrayList<Integer> teamsAssigned= new ArrayList<>();
        for (Team t : teams){
            for (int tech: t.getTechnicians()){
                if (tech == name){
                    teamsAssigned.add(t.getTeamNb());
                }
            }
        }
        return teamsAssigned;
    }

    public InterventionResult[] getInterventionsCompletedOnDay(int day){
        ArrayList<InterventionResult> interventionsCompleted = new ArrayList<>();
        for (InterventionResult i : interventionsResults){
            if (i.getDay() == day){
                interventionsCompleted.add(i);
            }
        }
        InterventionResult[] interventionsCompletedCopy = new InterventionResult[interventionsCompleted.size()];
        for (int i = 0; i < interventionsCompletedCopy.length; i++) {
            interventionsCompletedCopy[i] = interventionsCompleted.get(i);
        }
        return interventionsCompletedCopy;
    }
    public InterventionResult[] getInterventionsCompletedOnDayByTeam(int day, int teamNb){
        ArrayList<InterventionResult> interventionsCompleted = new ArrayList<>();
        for (InterventionResult i : interventionsResults){
            if (i.getDay() == day && i.getTeam() == teamNb){
                interventionsCompleted.add(i);
            }
        }
        InterventionResult[] interventionsCompletedCopy = new InterventionResult[interventionsCompleted.size()];
        for (int i = 0; i < interventionsCompletedCopy.length; i++) {
            interventionsCompletedCopy[i] = interventionsCompleted.get(i);
        }
        return interventionsCompletedCopy;
    }
    public void print(){
        System.out.println("///////////// Solution ////////////");
        System.out.println("----------------------------------");
        System.out.println("----- INTERVENTION SCHEDULE ------");
        System.out.println("----------------------------------");
        for (InterventionResult i : this.interventionsResults){
            i.print();
        }
        System.out.println();
        System.out.println("----------------------------------");
        System.out.println("------- TECHNICIAN TEAMS ---------");
        System.out.println("----------------------------------");
        for (int day = 1; day < this.nbDays+1; day++) {
            System.out.println("Teams of day " + day);
            for (Team t : getTeamsOfDay(day)){
                t.print();
            }
        }
        System.out.println();
    }
}
