package code;

public class Team {
    private int day;
    private int teamNb;
    private Technician[] technicians;

    public int getTeamNb() {
        return teamNb;
    }

    public void setTeamNb(int teamNb) {
        if (teamNb < 0){
            System.out.println("Invalid team number");
        }else{
            this.teamNb = teamNb;
        }
    }

    public Technician[] getTechnicians() {
        return technicians;
    }

    public void setTechnicians(Technician[] technicians) {
        this.technicians = technicians;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }



    public Team(int day, int teamNb, Technician[] technicians){
        this.day = day;
        this.teamNb = teamNb;
        this.technicians = technicians;
    }

    public void print(){
        System.out.print("#" + teamNb + " -> ");
        for (Technician t : technicians){
            System.out.print(t.getName() + " ");
        }
        System.out.println();
    }
}
