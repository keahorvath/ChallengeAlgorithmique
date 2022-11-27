package ttsp.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public record Intervention(int number, int duration, int[] preds, int prio, int cost, int[][] domains) implements Comparable<Intervention>{

    //To make sure we can't change the values of domains
    public int[][] getDomains(){
        int[][] domainsCopy = new int[domains.length][domains[0].length];
        for (int i = 0; i < domains.length; i++) {
            System.arraycopy(domains[i], 0, domainsCopy[i], 0, domains[0].length);
        }
        return domainsCopy;
    }

    public int minNbOfTechnicians(){
        int maxNbPerDomain = 0;
        for (int[] domain : domains) {
            for (int i : domain) {
                if (i > maxNbPerDomain) {
                    maxNbPerDomain = i;
                }
            }
        }
        return maxNbPerDomain;
    }

    public int totalLevelsNeeded(){
        int sum = 0;
        for (int[] domain : domains) {
            for (int level : domain) {
                sum += level;
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
            System.arraycopy(this.domains()[d], 0, domains[d], 0, domains[0].length);
        }
        int[][] zeros = new int[domains.length][domains[0].length];
        for (int i = 0; i < domains.length; i++) {
            Arrays.fill(zeros[i], 0);
        }

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

    public int[][] getImprovedDomains(){
        int[][] domainsImproved = getDomains();
        for (int d = 0; d < domains.length; d++) {
            int highestLevel = 0;
            int value = 0;
            for (int l = domains[0].length-1; l >= 1; l--){
                if (domains[d][l] != 0){
                    highestLevel = l;
                    value = domains[d][l];
                    break;
                }
            }
            if (value == 0){
                continue;
            }
            for (int l = highestLevel-1; l >= 0; l--) {
                if (domainsImproved[d][l] <= value){
                    domainsImproved[d][l] = 0;
                }
            }
        }
        return domainsImproved;
    }

    public void getImprovedDomains(int[][] domains){
        for (int d = 0; d < domains.length; d++) {
            int highestLevel = 0;
            int value = 0;
            for (int l = domains[0].length-1; l >= 1; l--){
                if (domains[d][l] != 0){
                    highestLevel = l;
                    value = domains[d][l];
                    break;
                }
            }
            if (value == 0){
                continue;
            }
            for (int l = highestLevel-1; l >= 0; l--) {
                if (domains[d][l] <= value){
                    domains[d][l] = 0;
                }
            }
        }
    }

    @Override
    public int compareTo(Intervention i){
        return Comparator.comparing(Intervention::prio)
                .thenComparing(Intervention::duration, Comparator.reverseOrder())
                //.thenComparing(Intervention::minNbOfTechnicians, Comparator.reverseOrder())
                .thenComparing(Intervention::totalLevelsNeeded, Comparator.reverseOrder())
                .thenComparing(Intervention::minNbOfTechnicians, Comparator.reverseOrder())
                //.thenComparing(Intervention::duration, Comparator.reverseOrder())
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
