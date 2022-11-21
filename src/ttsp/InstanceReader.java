package ttsp;

import ttsp.data.Intervention;
import ttsp.data.TTSPData;
import ttsp.data.Technician;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class InstanceReader {

    public static Intervention[] readInterventions(File file, int nbInterventions, int nbLevels, int nbDomains) throws FileNotFoundException {
        Scanner intervReader = new Scanner(file);
        intervReader.nextLine();
        Intervention[] interventions = new Intervention[nbInterventions];
        String str;
        for (int i = 0; i < nbInterventions; i++){
            str = intervReader.nextLine();
            String[] data2 = str.split(" ", 0);
            int number = Integer.parseInt(data2[0]);
            int time = Integer.parseInt(data2[1]);
            int currentI = 3;
            ArrayList<Integer> preds = new ArrayList<>();
            while (!data2[currentI].equals("]")){
                preds.add(Integer.parseInt(data2[currentI++]));
            }
            int[] predsCopy = new int[preds.size()];
            for (int a = 0; a < preds.size(); a++){
                predsCopy[a] = preds.get(a);
            }
            int prio = Integer.parseInt(data2[++currentI]);
            int cost = Integer.parseInt(data2[++currentI]);
            int[][] domainsLevels = new int[nbDomains][nbLevels];
            for (int j = 0; j < nbDomains; j++){
                for (int k = 0; k < nbLevels; k++){
                    domainsLevels[j][k] = Integer.parseInt(data2[++currentI]);
                }
            }
            interventions[i] = new Intervention(number, time, predsCopy, prio, cost, domainsLevels);
        }
        return interventions;
    }

    public static Technician[] readTechnicians(File file, int nbTechs, int nbDomains) throws FileNotFoundException {
        Scanner techsReader = new Scanner(file);
        techsReader.nextLine();
        Technician[] technicians = new Technician[nbTechs];
        String str;
        for (int i = 0; i < nbTechs; i++) {
            str = techsReader.nextLine();
            String[] data3 = str.split(" ", 0);
            int name = Integer.parseInt(data3[0]);
            int[] domainsLevels = new int[nbDomains];
            for (int j = 0; j < nbDomains; j++) {
                domainsLevels[j] = Integer.parseInt(data3[j + 1]);
            }
            int currentI = nbDomains + 2;
            ArrayList<Integer> unavailability = new ArrayList<>();
            while (!data3[currentI].equals("]")) {
                unavailability.add(Integer.parseInt(data3[currentI++]));
            }
            int[] unavailabilityCopy = new int[unavailability.size()];
            for (int a = 0; a < unavailability.size(); a++) {
                unavailabilityCopy[a] = unavailability.get(a);
            }
            technicians[i] = new Technician(name, domainsLevels, unavailabilityCopy);
        }
        return technicians;
    }

    public static TTSPData instanceReader(File instanceFile, File intervListFile, File techListFile) throws FileNotFoundException {
        Scanner instanceReader = new Scanner(instanceFile);
        instanceReader.nextLine();
        String str = instanceReader.nextLine();
        String[] data1 = str.split(" ", 0);

        String name = data1[0];
        int nbDomains = Integer.parseInt(data1[1]);
        int nbLevels = Integer.parseInt(data1[2]);
        int nbTechs = Integer.parseInt(data1[3]);
        int nbInterventions = Integer.parseInt(data1[4]);
        int budget = Integer.parseInt(data1[5]);
        Intervention[] interventions = readInterventions(intervListFile, nbInterventions, nbLevels, nbDomains);
        Technician[] technicians = readTechnicians(techListFile, nbTechs, nbDomains);

        return new TTSPData(name, nbDomains, nbLevels, nbTechs, nbInterventions, budget, interventions, technicians);
    }

    public static String usage(){
        return "usage: java -jar instanceReader.jar <absolutePathToFolder>";
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1){
            System.out.println(usage());
            System.exit(1);
        }
        File instanceFile = new File(args[0] + "/instance");
        File intervListFile = new File(args[0] + "/interv_list");
        File techListFile = new File(args[0] + "/tech_list");

        TTSPData data = instanceReader(instanceFile, intervListFile, techListFile);
        System.out.println(data);
    }

}
