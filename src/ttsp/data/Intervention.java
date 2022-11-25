package ttsp.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public record Intervention(int number, int duration, int[] preds, int prio, int cost, int[][] domains) implements Comparable<Intervention>{

    public int minNbOfTechnicians(){
        int maxNbPerDomain = 0;
        for (int d = 0; d < domains.length; d++) {
            for (int l = 0; l < domains[d].length; l++) {
                if (domains[d][l] > maxNbPerDomain){
                    maxNbPerDomain = domains[d][l];
                }
            }
        }
        return maxNbPerDomain;
    }

    public int totalLevelsNeeded(){
        int sum = 0;
        for (int d = 0; d < domains.length; d++) {
            for (int l = 0; l < domains[d].length; l++) {
                sum += domains[d][l];
            }
        }
        return sum;
    }
    public boolean isDoable(ArrayList<Technician> technicians){
        if (technicians.size() < this.minNbOfTechnicians()){
            return false;
        }
        int[][] domains = new int[this.domains().length][this.domains()[0].length];
        for (int d = 0; d < domains.length; d++) {
            for (int l = 0; l < domains[0].length; l++) {
                domains[d][l] = this.domains()[d][l];
            }
        }
        int[][] zeros = new int[domains.length][domains[0].length];
        for (Technician t : technicians){
            for (int d = 1; d < domains.length+1; d++) {
                for (int l = 1; l < domains[0].length+1; l++) {
                    if (domains[d-1][l-1] != 0 && t.isQualified(d, l)){
                        domains[d-1][l-1]--;
                    }
                }
            }
        }
        return Arrays.deepEquals(domains, zeros);
    }
    @Override
    public int compareTo(Intervention i){
        return Comparator.comparing(Intervention::prio)
                .thenComparing(Intervention::duration, Comparator.reverseOrder())
                .thenComparing(Intervention::minNbOfTechnicians, Comparator.reverseOrder())
                .thenComparing(Intervention::totalLevelsNeeded, Comparator.reverseOrder())
                .compare(this, i);
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("-> Interv #").append(this.number).append("\n");
        s.append("Time = ").append(this.duration).append(" Priority = ").append(this.prio).append(" Cost = ").append(this.cost).append("\n");
        for (int i = 0; i < this.domains.length; i++) {
            s.append("Number of technicians required for domain ").append(i + 1).append(" -> ");
            for (int j = 0; j < this.domains[i].length; j++) {
                s.append(this.domains[i][j]).append(" ");
            }
            if (i != this.domains.length - 1) {
                s.append("\n");
            }
        }
        s.append("\nPredecessors = ");
        for (int p : preds) {
            s.append(p).append(" ");
        }
        s.append("\n");
        return s.toString();
    }
}
