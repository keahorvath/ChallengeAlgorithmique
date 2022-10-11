package ttsp.data;

public record TTSPData(String name, int nbDomains, int nbLevels, int nbTechs, int nbInterventions, int budget,
                       Intervention[] interventions, Technician[] technicians) {

    public Intervention getInterventionFromNumber(int number) {
        for (Intervention i : this.interventions) {
            if (i.number() == number) {
                return i;
            }
        }
        System.out.println("getInterventionFromNumber method was called with an invalid number");
        System.exit(1);
        return null;
    }

    public Technician getTechnicianFromNumber(int number) {
        for (Technician t : this.technicians) {
            if (t.name() == number) {
                return t;
            }
        }
        System.out.println("getTechnicianFromNumber method was called with an invalid number");
        System.exit(1);
        return null;
    }

    public boolean TechUnavailableOnDay(Technician technician, int day) {
        for (int d : technician.unavailability()) {
            if (d == day) {
                return true;
            }
        }
        return false;
    }

    public void print() {

        System.out.println("///////////// Instance " + this.name + " ////////////");
        System.out.println("#Interventions = " + this.nbInterventions);
        System.out.println("#Technicians = " + this.nbTechs);
        System.out.println("#Domains / #Levels = " + this.nbDomains + " / " + this.nbLevels);
        System.out.println("Outsourcing budget = " + this.budget);
        System.out.println();

        System.out.println("----------------------------------");
        System.out.println("--------- INTERVENTIONS ----------");
        System.out.println("----------------------------------");
        for (Intervention i : this.interventions) {
            i.printInfo();
        }
        System.out.println();

        System.out.println("----------------------------------");
        System.out.println("---------- TECHNICIANS -----------");
        System.out.println("----------------------------------");
        for (Technician t : this.technicians) {
            t.print();
        }
        System.out.println();
    }
}
