package code;

public class TTSPData {
    private Instance instance;
    private Intervention[] interventions;
    private Technician[] technicians;

    public TTSPData(Instance instance, Intervention[] interventions, Technician[] technicians){
        this.instance = instance;
        this.interventions = interventions;
        this.technicians = technicians;
    }

    public Instance getInstance() {
        return instance;
    }

    public Intervention[] getInterventions() {
        return interventions;
    }

    public Technician[] getTechnicians() {
        return technicians;
    }

    public void print(){
        this.instance.print();
        System.out.println("----------------------------------");
        System.out.println("--------- INTERVENTIONS ----------");
        System.out.println("----------------------------------");
        for (Intervention i : this.interventions){
            i.printInfo();
        }
        System.out.println();
        System.out.println("----------------------------------");
        System.out.println("---------- TECHNICIANS -----------");
        System.out.println("----------------------------------");
        for (Technician t : this.technicians){
            t.print();
        }
        System.out.println();
    }
}
