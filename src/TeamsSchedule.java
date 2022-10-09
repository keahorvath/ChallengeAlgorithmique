public class TeamsSchedule {
    TeamsDay[] days;

    public TeamsSchedule(TeamsDay[] days){
        this.days = days;
    }

    public void print(){
        System.out.println("----------------------------------");
        System.out.println("------- TECHNICIAN TEAMS ---------");
        System.out.println("----------------------------------");
        for (TeamsDay d : days){
            d.print();
        }
    }
}
