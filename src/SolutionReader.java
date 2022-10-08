import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class SolutionReader {
    public static void main(String[] args) throws FileNotFoundException {
        File intervDatesFile = new File(args[0] + "/interv_dates");
        File techTeamsFile = new File(args[0] + "/tech_teams");

        Scanner intervDatesReader = new Scanner(intervDatesFile);
        ArrayList<Intervention> intervs = new ArrayList<>();
        while (intervDatesReader.hasNextLine()){
            String str = intervDatesReader.nextLine();
            String[] data = str.split(" ", 0);
            Intervention intervention = new Intervention(Integer.parseInt(data[0]));
            intervention.fillResults(Integer.parseInt(data[3]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
            intervs.add(intervention);
        }
        Intervention[] interventions = new Intervention[intervs.size()];
        for (int i = 0; i < intervs.size(); i++){
            interventions[i] = intervs.get(i);
        }
        InterventionsSchedule interventionsSchedule = new InterventionsSchedule(interventions);
        interventionsSchedule.print();


    }
}
