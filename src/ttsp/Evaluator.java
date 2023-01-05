package ttsp;

import ttsp.data.Intervention;
import ttsp.data.TTSPData;
import ttsp.solution.InterventionResult;
import ttsp.solution.TTSPSolution;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;

public class Evaluator {

    public static int getPriorityCost(int priority, TTSPData data, TTSPSolution solution){
        int c = 0;
        for (Intervention i : data.interventions()){
            InterventionResult iResult = solution.getInterventionResult(i.number());
            if (iResult == null){
                continue;
            }
            int endTime = iResult.getStartTime() + i.duration();
            int prio = i.prio();
            if (prio == priority && c < endTime){
                c = endTime;
            }
        }
        return c;
    }

    public static int getCost(TTSPData data, TTSPSolution solution){
        int c = 0;
        for (Intervention i : data.interventions()){
            InterventionResult iResult = solution.getInterventionResult(i.number());
            if (iResult == null){
                continue;
            }
            int endTime = iResult.getStartTime() + i.duration();
            if (c < endTime){
                c = endTime;
            }
        }
        return c;
    }

    public static int evaluate(TTSPData data, TTSPSolution solution){
        int c1 = getPriorityCost(1, data, solution);
        int c2 = getPriorityCost(2, data, solution);
        int c3 = getPriorityCost(3, data, solution);
        int c = getCost(data, solution);
        return 28*c1 + 14*c2 + 4*c3 + c;
    }

    public static void print(TTSPData data, TTSPSolution solution){
        int c1 = getPriorityCost(1, data, solution);
        int c2 = getPriorityCost(2, data, solution);
        int c3 = getPriorityCost(3, data, solution);
        int c = getCost(data, solution);
        System.out.println("----------------------------------");
        System.out.println("--------- COMPUTE COST -----------");
        System.out.println("----------------------------------");
        System.out.println("Cost for interventions of priority 1 = " + 28*c1 + " (latest completion time = " + c1 + ")");
        System.out.println("Cost for interventions of priority 2 = " + 14*c2 + " (latest completion time = " + c2 + ")");
        System.out.println("Cost for interventions of priority 3 = " + 4*c3 + " (latest completion time = " + c3 + ")");
        System.out.println("Schedule cost = " + c + "  (latest completion time = " + c + ")");

    }

    public static String usage(){
        return "usage: java -jar evaluator.jar <absolutePathToFolder>";
    }

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 1){
            System.out.println(usage());
            System.exit(1);
        }
        File instanceFile = new File(args[0] + "/instance");
        File intervListFile = new File(args[0] + "/interv_list");
        File techListFile = new File(args[0] + "/tech_list");
        File intervDatesFile = new File(args[0] + "/interv_dates");
        File techTeamsFile = new File(args[0] + "/tech_teams");

        TTSPData data = InstanceReader.instanceReader(instanceFile, intervListFile, techListFile);
        TTSPSolution solution = SolutionReader.solutionReader(intervDatesFile, techTeamsFile);
        int value = evaluate(data, solution);
        print(data, solution);
        System.out.println("-> TOTAL COST = " + value);
        System.out.println();
    }
}
