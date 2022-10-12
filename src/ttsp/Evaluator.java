package ttsp;

import ttsp.data.Intervention;
import ttsp.data.TTSPData;
import ttsp.solution.InterventionResult;
import ttsp.solution.TTSPSolution;

import java.io.File;
import java.io.FileNotFoundException;

public class Evaluator {

    public static int evaluate(TTSPData data, TTSPSolution solution){
        int c1 =0, c2=0, c3=0, c=0;
        for (Intervention i : data.interventions()){
            InterventionResult iResult = solution.getInterventionResult(i.number());
            int endTime = iResult.getStartTime() + i.duration();
            int prio = i.prio();
            if (c < endTime){
                c = endTime;
            }
            if (prio == 1 && c1 < endTime){
                c1 = endTime;
            }
            else if (prio == 2 && c2 < endTime){
                c2 = endTime;
            }
            else if (prio == 3 && c3 < endTime){
                c3 = endTime;
            }
        }
        print(c1, c2, c3, c);
        return 28*c1 + 14*c2 + 4*c3 + c;
    }

    public static void print(int c1, int c2, int c3, int c){
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
        System.out.println("-> TOTAL COST = " + value);
        System.out.println();
    }
}
