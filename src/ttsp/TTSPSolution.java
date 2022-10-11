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
        for (int day = 0; day < this.nbDays; day++) {
            System.out.println("Teams of day " + day);
            for (Team t : getTeamsOfDay(day)){
                t.print();
            }
        }
        System.out.println();
    }
}
