public class Technician {
    int name;
    int[] domainsLevels;
    int[] unavailability;

    public Technician(int name, int[] domainsLevels, int[] unavailability){
        this.name = name;
        this.domainsLevels = domainsLevels;
        this.unavailability = unavailability;
    }

    public void print(){
        System.out.println("-> Tech #" + this.name);
        System.out.print("Skills (mastered level per domain) -> ");
        for (int i : this.domainsLevels){
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.print("Not available on day(s) -> ");
        for (int i : this.unavailability){
            System.out.print(i + " ");
        }
        System.out.println();
    }
}
