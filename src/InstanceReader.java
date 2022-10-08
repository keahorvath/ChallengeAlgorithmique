import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class InstanceReader {

    public static void main(String[] args) throws IOException {
        File instanceFile = new File(args[0] + "/instance");
        File intervListFile = new File(args[0] + "/interv_list");
        File techListFile = new File(args[0] + "/tech_list");

        //Creation of instance
        Scanner instanceReader = new Scanner(instanceFile);
        instanceReader.nextLine();
        String str = instanceReader.nextLine();
        String[] data1 = str.split(" ", 0);
        Instance instance = new Instance(data1[0], Integer.parseInt(data1[1]), Integer.parseInt(data1[2]), Integer.parseInt(data1[3]), Integer.parseInt(data1[4]), Integer.parseInt(data1[5]));

        //Creation of interventions
        Scanner intervReader = new Scanner(intervListFile);
        intervReader.nextLine();
        Intervention[] interventions = new Intervention[instance.nbInterventions];
        for (int i = 0; i < instance.nbInterventions; i++){
            str = intervReader.nextLine();
            String[] data2 = str.split(" ", 0);
            int number = Integer.parseInt(data2[0]);
            int time = Integer.parseInt(data2[1]);
            int currentI = 3;
            ArrayList<Integer> preds1 = new ArrayList<>();
            while (!data2[currentI].equals("]")){
                preds1.add(Integer.parseInt(data2[currentI++]));
            }
            int[] preds2 = new int[preds1.size()];
            for (int a = 0; a < preds1.size(); a++){
                preds2[a] = preds1.get(a);
            }
            int prio = Integer.parseInt(data2[++currentI]);
            int cost = Integer.parseInt(data2[++currentI]);
            int[][] domainsLevels = new int[instance.nbDomains][instance.nbLevels];
            for (int j = 0; j < instance.nbDomains; j++){
                for (int k = 0; k < instance.nbLevels; k++){
                    domainsLevels[j][k] = Integer.parseInt(data2[++currentI]);
                }
            }
            interventions[i] = new Intervention(number);
            interventions[i].fillInfo(time, preds2, prio, cost, domainsLevels);
        }

        //Creation of technicians
        Scanner techsReader = new Scanner(techListFile);
        techsReader.nextLine();
        Technician[] technicians = new Technician[instance.nbTechs];
        for (int i = 0; i < instance.nbTechs; i++) {
            str = techsReader.nextLine();
            String[] data3 = str.split(" ", 0);
            int name = Integer.parseInt(data3[0]);
            int[] domainsLevels = new int[instance.nbDomains];
            for (int j = 0; j < instance.nbDomains; j++) {
                domainsLevels[j] = Integer.parseInt(data3[j + 1]);
            }
            int currentI = instance.nbDomains + 2;
            ArrayList<Integer> unavail1 = new ArrayList<>();
            while (!data3[currentI].equals("]")) {
                unavail1.add(Integer.parseInt(data3[currentI++]));
            }
            int[] unavail2 = new int[unavail1.size()];
            for (int a = 0; a < unavail1.size(); a++) {
                unavail2[a] = unavail1.get(a);
            }
            technicians[i] = new Technician(name, domainsLevels, unavail2);
        }

        //Creation of TTSPData
        TTSPData ttspData = new TTSPData(instance, interventions, technicians);
        ttspData.print();
    }

}
