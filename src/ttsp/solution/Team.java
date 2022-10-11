package ttsp.solution;

import ttsp.data.TTSPData;
import ttsp.data.Technician;

public class Team {
    private final int day;
    private final int teamNb;
    private final int[] technicians;

    public Team(int day, int teamNb, int[] technicians){
        this.day = day;
        this.teamNb = teamNb;
        this.technicians = technicians;
    }
    public int getTeamNb() {
        return teamNb;
    }

    public int[] getTechnicians() {
        return this.technicians;
    }

    public int getDay() {
        return day;
    }

    public int nbTechniciansOfLevelInDomain(TTSPData data, int level, int domain){
        int result = 0;
        for (int t : this.technicians){
            Technician tech = data.getTechnicianFromNumber(t);
            if (tech.getLevelInDomain(domain) >= level){
                result++;
            }
        }
        return result;
    }

    public void print(){
        System.out.print("#" + teamNb + " -> ");
        for (int t : this.technicians){
            System.out.print(t + " ");
        }
        System.out.println();
    }
}
