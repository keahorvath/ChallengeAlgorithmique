public class TTSPSolution {
    InterventionsSchedule interventionsSchedule;
    TeamsSchedule teamsSchedule;

    public TTSPSolution(InterventionsSchedule interventionsSchedule, TeamsSchedule teamsSchedule){
        this.interventionsSchedule = interventionsSchedule;
        this.teamsSchedule = teamsSchedule;
    }

    public void print(){
        System.out.println("///////////// Solution ////////////");
        interventionsSchedule.print();
        teamsSchedule.print();

    }
}
