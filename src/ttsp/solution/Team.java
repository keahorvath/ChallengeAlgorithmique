package ttsp.solution;

import ttsp.data.TTSPData;
import ttsp.data.Technician;

public record Team(int day, int teamNb, int[] technicians) {

    public int nbTechniciansOfLevelInDomain(TTSPData data, int level, int domain) {
        int result = 0;
        for (int t : this.technicians) {
            Technician tech = data.getTechnicianFromNumber(t);
            if (tech.getLevelInDomain(domain) >= level) {
                result++;
            }
        }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("#").append(this.teamNb).append(" -> ");
        for (int t : this.technicians) {
            s.append(t).append(" ");
        }
        return s.toString();
    }
}
