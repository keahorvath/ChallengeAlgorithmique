package ttsp;

public class Intervention {

    private final int number;
    private int duration;
    private int[] preds;
    private int prio;
    private int cost;
    private int[][] domains;

    public Intervention(int number, int duration, int[] preds, int prio, int cost, int[][] domains){
        this.number = number;
        this.duration = duration;
        this.preds = preds;
        this.prio = prio;
        this.cost = cost;
        this.domains = domains;
    }

    public int getNumber() {
        return number;
    }

    public int getDuration() {
        return duration;
    }

    public int[] getPreds() {
        return preds;
    }

    public int getPrio() {
        return prio;
    }

    public int getCost() {
        return cost;
    }

    public int[][] getDomains() {
        return domains;
    }


    public void printInfo(){
        System.out.println("-> Interv #" + this.number);
        System.out.println("Time = " + this.duration + " Priority = " + this.prio + " Cost = " + this.cost);
        for (int i = 0; i < domains.length; i++){
            System.out.print("Number of technicians required for domain " + (i+1) + " -> ");
            for (int j = 0; j < domains[i].length; j++){
                System.out.print(domains[i][j] + " ");
            }
            if (i != domains.length-1){
                System.out.println();
            }
        }
        System.out.println();
        System.out.print("Predecessors = ");
        for (int p : preds){
            System.out.print(p + " ");
        }
        System.out.println();
    }
}
