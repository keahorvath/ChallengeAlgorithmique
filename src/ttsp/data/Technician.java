package ttsp.data;

public record Technician(int name, int[] domainsLevels, int[] unavailability) {

    public int getLevelInDomain(int domain) {
        return this.domainsLevels[domain - 1];
    }

    public boolean isTechnicianQualified(int domain, int level){
        return this.domainsLevels[domain] > level;
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
