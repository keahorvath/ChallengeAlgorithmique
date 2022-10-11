package ttsp;

import java.util.ArrayList;

public class TTSPSolution {

    private InterventionResult[] interventionsResults;
    private Team[] teams;
    private int nbDays;

    public TTSPSolution(InterventionResult[] interventionsResults, Team[] teams, int nbDays){
        this.interventionsResults = interventionsResults;
        this.teams = teams;
        this.nbDays = nbDays;
    }

    public InterventionResult[] getInterventionsResults() {
        return interventionsResults;
    }

    public Team[] getTeams() {
        return teams;
    }

    public int getNbDays() {
        return nbDays;
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

    public ArrayList getTeamsOfTechnician(int name, int day){
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
