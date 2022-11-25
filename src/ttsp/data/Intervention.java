package ttsp.data;

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
    @Override
    public int compareTo(Intervention interventionToCompare){
        return interventionToCompare.minNbOfTechnicians() - this.minNbOfTechnicians();
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
