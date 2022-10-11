package ttsp;

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

    public void print(){
        System.out.print("#" + teamNb + " -> ");
        for (int t : this.technicians){
            System.out.print(t + " ");
        }
        System.out.println();
    }
}
