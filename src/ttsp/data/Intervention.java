package ttsp.data;

public record Intervention(int number, int duration, int[] preds, int prio, int cost, int[][] domains) {

    public void printInfo() {
        System.out.println("-> Interv #" + this.number);
        System.out.println("Time = " + this.duration + " Priority = " + this.prio + " Cost = " + this.cost);
        for (int i = 0; i < this.domains.length; i++) {
            System.out.print("Number of technicians required for domain " + (i + 1) + " -> ");
            for (int j = 0; j < this.domains[i].length; j++) {
                System.out.print(this.domains[i][j] + " ");
            }
            if (i != this.domains.length - 1) {
                System.out.println();
            }
        }
        System.out.println();
        System.out.print("Predecessors = ");
        for (int p : preds) {
            System.out.print(p + " ");
        }
        System.out.println();
    }
}
