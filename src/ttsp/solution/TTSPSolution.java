package ttsp.solution;

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

    public boolean isOutsourced(int number){
        for (InterventionResult i : this.interventionsResults){
            if (i.getNumber() == number){
                return false;
            }
        }
        return true;
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
