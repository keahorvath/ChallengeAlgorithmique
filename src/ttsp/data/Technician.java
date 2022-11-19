package ttsp.data;

public record Technician(int name, int[] domainsLevels, int[] unavailability) {

    public int getLevelInDomain(int domain) {
        return this.domainsLevels[domain - 1];
    }

    public boolean isTechnicianQualified(int domain, int level){
        return this.domainsLevels[domain] > level;
    }

    public void print() {
        System.out.println("-> Tech #" + this.name);
        System.out.print("Skills (mastered level per domain) -> ");
        for (int i : this.domainsLevels) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.print("Not available on day(s) -> ");
        for (int i : this.unavailability) {
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
