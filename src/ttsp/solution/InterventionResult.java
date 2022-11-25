package ttsp.solution;

import ttsp.data.Intervention;
import ttsp.data.TTSPData;

public record InterventionResult(int number, int team, int day, int time) {

    public int getStartTime() {
        return (day - 1) * 120 + time;
    }

    public int getEndTime(TTSPData data){
        for (Intervention i : data.interventions()){
            if (i.number() == number){
                return getStartTime() + i.duration();
            }
        }
        return 0;
    }

    public Intervention getIntervention(TTSPData data){
        for (Intervention i : data.interventions()){
            if (i.number() == number){
                return i;
            }
        }
        return null;
    }
    @Override
    public String toString(){
        return "#" + this.number + " day " + this.day + " -> starts at time " + this.time + " / executed by team #" + this.team + "\n";
    }
}
