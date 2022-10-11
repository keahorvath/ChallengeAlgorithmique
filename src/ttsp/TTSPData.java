package ttsp;

public class TTSPData {
    private final String name;
    private final int nbDomains;
    private final int nbLevels;
    private final int nbTechs;
    private final int nbInterventions;
    private final int budget;
    private Intervention[] interventions;
    private Technician[] technicians;

    public TTSPData(String name, int nbDomains, int nbLevels, int nbTechs, int nbInterventions, int budget, Intervention[] interventions, Technician[] technicians){
        this.name = name;
        this.nbDomains = nbDomains;
        this.nbLevels = nbLevels;
        this.nbTechs = nbTechs;
        this.nbInterventions = nbInterventions;
        this.budget = budget;
        this.interventions = interventions;
        this.technicians = technicians;
    }


    public Intervention[] getInterventions() {
        return interventions;
    }

    public Technician[] getTechnicians() {
        return technicians;
    }

    public void print(){

        System.out.println("///////////// Instance " + this.name + " ////////////");
        System.out.println("#Interventions = " + this.nbInterventions);
        System.out.println("#Technicians = " + this.nbTechs);
        System.out.println("#Domains / #Levels = " + this.nbDomains + " / " + this.nbLevels);
        System.out.println("Outsourcing budget = " + this.budget);
        System.out.println();

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
