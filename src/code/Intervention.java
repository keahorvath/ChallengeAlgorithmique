package code;

public class Intervention {
    final int UNASSIGNED = -1;

    public int number;
    public int duration;
    public int[] preds;
    public int prio;
    public int cost;
    public int[][] domains;

    public boolean completed = false;
    public boolean hired = false;
    public int team = UNASSIGNED;
    public int day = UNASSIGNED;
    public int time = UNASSIGNED;

    public Intervention(int number){
        this.number = number;
    }

    public void fillInfo(int duration, int[] preds, int prio, int cost, int[][] domains){
        this.duration = duration;
        this.preds = preds;
        this.prio = prio;
        this.cost = cost;
        this.domains = domains;
    }

    public void fillResults(int team, int day, int time){
        this.completed = true;
        this.team = team;
        this.day = day;
        this.time = time;
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
            System.out.println(p + " ");
        }
        System.out.println();
    }

    public void printResult(){
        if (this.completed){
            System.out.println("#" + this.number + " day " + this.day + " -> starts at time " + this.time + " / executed by team #" + this.team);
        }
    }
}
