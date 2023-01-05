package ttsp;

import gurobi.GRBException;
import ttsp.data.TTSPData;
import ttsp.solution.InterventionResult;
import ttsp.solution.TTSPSolution;

import java.io.File;
import java.io.IOException;

import static ttsp.Algorithm.algorithm;
import static ttsp.InstanceReader.instanceReader;

//Comment line 103 in Algorithm.java before using
public class RandomSort {
    public static String usage(){
        return "usage: java -jar randomSort.jar absolutePathToFolder";
    }

    public static void main(String[] args) throws IOException, GRBException {
        if (args.length != 1){
            System.exit(1);
        }
        int nbInstances = 1000;
        for (int i = 1; i <= 10; i++) {
            String path = args[0] + "/data" + i;
            File instanceFile = new File(path + "/instance");
            File intervListFile = new File(path + "/interv_list");
            File techListFile = new File(path + "/tech_list");
            TTSPData data = instanceReader(instanceFile, intervListFile, techListFile);
            TTSPSolution solution = algorithm(data);
            for (int j = 0; j < nbInstances; j++) {
                TTSPSolution solution2 = algorithm(data);
                if (Evaluator.evaluate(data, solution) > Evaluator.evaluate(data, solution2)){
                    solution = solution2;
                }
            }
            boolean check = Checker.check(data, solution);
            System.out.println("data" + i + ":");
            if (check){
                System.out.println("-> TOTAL COST = " + Evaluator.evaluate(data, solution));

            }else{
                System.out.println("Solution is not feasible");
            }
            System.out.println();
        }
    }
}
