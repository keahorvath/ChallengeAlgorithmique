package ttsp;

import gurobi.GRBException;
import ttsp.data.TTSPData;
import ttsp.solution.TTSPSolution;

import java.io.File;
import java.io.IOException;

import static ttsp.Algorithm.algorithm;
import static ttsp.InstanceReader.instanceReader;

public class Results {
    public static void main(String[] args) throws IOException, GRBException {
        if (args.length != 1){
            System.exit(1);
        }
        for (int i = 1; i <= 10; i++) {
            String path = args[0] + "/data" + i;
            File instanceFile = new File(path + "/instance");
            File intervListFile = new File(path + "/interv_list");
            File techListFile = new File(path + "/tech_list");
            TTSPData data = instanceReader(instanceFile, intervListFile, techListFile);
            TTSPSolution solution = algorithm(data);
            boolean check = Checker.check(data, solution);
            System.out.println("data" + i + ":");
            if (check){
                System.out.println("Solution is feasible");
                System.out.println("-> TOTAL COST = " + Evaluator.evaluate(data, solution));
            }else{
                System.out.println("Solution is not feasible");
            }
            System.out.println();
        }
    }
}
