package ttsp;

public class Team {
    private final int day;
    private final int teamNb;
    private final Technician[] technicians;

    public Team(int day, int teamNb, Technician[] technicians){
        this.day = day;
        this.teamNb = teamNb;
        this.technicians = technicians;
    }
    public int getTeamNb() {
        return teamNb;
    }

    public Technician[] getTechnicians() {
        return technicians;
    }

    public int getDay() {
        return day;
    }

    public void print(){
        System.out.print("#" + teamNb + " -> ");
        for (Technician t : technicians){
            System.out.print(t.getName() + " ");
        }
        System.out.println();
    }
}
