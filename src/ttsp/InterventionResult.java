package ttsp;

public class InterventionResult {

    private final int number;
    private final int team;
    private final int day;
    private final int time;

    public InterventionResult(int number, int team, int day, int time){
        this.number = number;
        this.team = team;
        this.day = day;
        this.time = time;
    }

    public void print(){
        System.out.println("#" + this.number + " day " + this.day + " -> starts at time " + this.time + " / executed by team #" + this.team);
    }
}
