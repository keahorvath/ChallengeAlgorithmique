package ttsp.data;

import java.util.Comparator;

public record Technician(int name, int[] domainsLevels, int[] unavailability) implements Comparable<Technician>{

    public int getLevelInDomain(int domain) {
        return this.domainsLevels[domain-1];
    }

    public boolean isQualified(int domain, int level){
        return this.domainsLevels[domain-1] >= level;
    }


    public int amountUnusedLevels(int[][] domains){
        int amount = 0;
        for (int d = 0; d < domains.length; d++) {
            int technicianLevel = getLevelInDomain(d+1);
            if (technicianLevel == 0){
                continue;
            }
            if (domains[d][technicianLevel-1] != 0){
                continue;
            }

            for (int l = technicianLevel-1; l >= 0; l--) {
                if (domains[d][l] != 0){
                    amount += technicianLevel - 1 - l;
                    break;
                }
                if (l == 0){
                    amount += technicianLevel;
                }
            }
        }
        return amount;
    }

    public boolean isAvailable(int day){
        for(int k : unavailability) {
            if (k == day)
                return false;
        }
        return true;
    }

    public int sumOfLevels(){
        int sum = 0;
        for (int domainsLevel : domainsLevels) {
            sum += domainsLevel;
        }
        return sum;
    }

    @Override
    public int compareTo(Technician t){
        return Comparator.comparing(Technician::sumOfLevels)
                .compare(this, t);
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("-> Tech #").append(this.name).append("\n");
        s.append("Skills (mastered level per domain) -> ");
        for (int i : this.domainsLevels) {
            s.append(i).append(" ");
        }
        s.append("\n");
        s.append("Not available on day(s) -> ");
        for (int i : this.unavailability) {
            s.append(i).append(" ");
        }
        s.append("\n");
        return s.toString();
    }
}
