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


    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("///////////// Instance ").append(this.name).append(" ////////////").append("\n");
        s.append("#Interventions = ").append(this.nbInterventions).append("\n");
        s.append("#Technicians = ").append(this.nbTechs).append("\n");
        s.append("#Domains / #Levels = ").append(this.nbDomains).append(" / ").append(this.nbLevels).append("\n");
        s.append("Outsourcing budget = ").append(this.budget).append("\n \n");

        s.append("----------------------------------\n");
        s.append("--------- INTERVENTIONS ----------\n");
        s.append("----------------------------------\n");
        for (Intervention i : this.interventions) {
            s.append(i);
        }

        s.append("\n----------------------------------\n");
        s.append("---------- TECHNICIANS -----------\n");
        s.append("----------------------------------\n");
        for (Technician t : this.technicians) {
            s.append(t);
        }
        return s.toString();
    }
}
