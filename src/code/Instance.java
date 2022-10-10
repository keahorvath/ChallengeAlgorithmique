package code;

public class Instance {
    String name;
    int nbDomains;
    int nbLevels;
    int nbTechs;
    int nbInterventions;
    int budget;

    public Instance(String name, int nbDomains, int nbLevels, int nbTechs, int nbInterventions, int budget){
        this.name = name;
        this.nbDomains = nbDomains;
        this.nbLevels = nbLevels;
        this.nbTechs = nbTechs;
        this.nbInterventions = nbInterventions;
        this.budget = budget;
    }

    public void print(){
        System.out.println("///////////// Instance " + this.name + " ////////////");
        System.out.println("#Interventions = " + this.nbInterventions);
        System.out.println("#Technicians = " + this.nbTechs);
        System.out.println("#Domains / #Levels = " + this.nbDomains + " / " + this.nbLevels);
        System.out.println("Outsourcing budget = " + this.budget);
        System.out.println();
    }

}
