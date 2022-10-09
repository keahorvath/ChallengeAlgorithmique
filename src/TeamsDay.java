public class TeamsDay {

    private Team[] teams;
    private int day;

    public TeamsDay(Team[] teams) throws Exception {
        this.teams = teams;
        this.day = teams[0].getDay();
        for (int i = 1; i < teams.length; i++) {
            if (teams[i].getDay() != this.day){
                throw new Exception("All teams of a TeamsDay must have the same day");
            }
        }

    }

    public void print(){
        System.out.println("Teams of day " + this.day);
        for (Team t : teams){
            t.print();
        }
    }
}
