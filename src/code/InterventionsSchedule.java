package code;

public class InterventionsSchedule {
    Intervention[] interventions;

    public InterventionsSchedule(Intervention[] interventions){
        this.interventions = interventions;
    }

    public void print(){
        System.out.println("----------------------------------");
        System.out.println("----- INTERVENTION SCHEDULE ------");
        System.out.println("----------------------------------");
        for (Intervention i : interventions){
            i.printResult();
        }
        System.out.println();
    }
}
