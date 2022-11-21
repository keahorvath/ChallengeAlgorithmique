package ttsp;

import ttsp.data.TTSPData;

import java.io.File;
import java.io.FileNotFoundException;

import static ttsp.InstanceReader.instanceReader;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRB.DoubleParam;
import gurobi.GRB.IntAttr;
import gurobi.GRB.IntParam;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import ttsp.solution.TTSPSolution;


public class Solver {

    public static TTSPSolution solver(TTSPData data, int timeLimit) throws GRBException {
        GRBEnv env = new GRBEnv();
        env.set(DoubleParam.TimeLimit, timeLimit);
        env.set(IntParam.Threads, 1);
        GRBModel model = new GRBModel(env);


        int maxEndTime = 0;
        for (int i = 0; i < data.nbInterventions(); i++) {
            maxEndTime += data.interventions()[i].duration();
        }

        int maxDay = data.nbInterventions();

        //Variables
        GRBVar[][][] x = new GRBVar[data.nbTechs()][][]; //x(t,g,k)
        for (int t = 0; t < data.nbTechs(); t++) {
            x[t] = new GRBVar[data.nbTechs()][];
            for (int g = 0; g < data.nbTechs(); g++) {
                x[t][g] = new GRBVar[maxDay];
                for (int k = 0; k < maxDay; k++) {
                    x[t][g][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, String.format("x(%s,%s,%s)", t, g, k));
                }
            }
        }
        GRBVar[][][] y = new GRBVar[data.nbInterventions()][][]; //y(i,g,k)
        for (int i = 0; i < data.nbInterventions(); i++) {
            y[i] = new GRBVar[data.nbTechs()][];
            for (int g = 0; g < data.nbTechs(); g++) {
                y[i][g] = new GRBVar[maxDay];
                for (int k = 0; k < maxDay; k++) {
                    y[i][g][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, String.format("y(%s,%s,%s)", i, g, k));
                }
            }
        }

        GRBVar[] o = new GRBVar[data.nbInterventions()]; //o_i
        for (int i= 0; i < data.nbInterventions(); i++) {
            o[i]= model.addVar(0.0, 1.0, 0.0, GRB.BINARY, String.format("o_%s", i));
        }

        GRBVar[][] u = new GRBVar[data.nbInterventions()][]; //u_ii'
        for (int i = 0; i < data.nbInterventions(); i++) {
            u[i] = new GRBVar[data.nbInterventions()];
            for (int j = 0; j < data.nbInterventions(); j++) {
                u[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, String.format("u_%s%s", i, j));
            }
        }

        GRBVar[] b = new GRBVar[data.nbInterventions()]; //b_i
        for (int i= 0; i < data.nbInterventions(); i++) {
            b[i]= model.addVar(0.0, maxEndTime, 0.0, GRB.CONTINUOUS, String.format("b_%s", i));
        }

        GRBVar C = model.addVar(0.0, maxEndTime, 0.0, GRB.CONTINUOUS, "C");
        GRBVar C1 = model.addVar(0.0, maxEndTime, 0.0, GRB.CONTINUOUS, "C1");
        GRBVar C2 = model.addVar(0.0, maxEndTime, 0.0, GRB.CONTINUOUS, "C2");
        GRBVar C3 = model.addVar(0.0, maxEndTime, 0.0, GRB.CONTINUOUS, "C3");


        // Fonction objectif (1)
        GRBLinExpr obj = new GRBLinExpr();
        obj.addTerm(28, C1);
        obj.addTerm(14, C2);
        obj.addTerm(4, C3);
        obj.addTerm(1, C);
        model.setObjective(obj, GRB.MINIMIZE);


        // Contrainte (2)
        for (int t = 0; t < data.nbTechs(); t++) {
            for (int k = 0; k < maxDay; k++) {
                GRBLinExpr expr = new GRBLinExpr();
                for (int g = 0; g < data.nbTechs(); g++) {
                    expr.addTerm(1.0, x[t][g][k]);
                }
                model.addConstr(expr, GRB.EQUAL, 1.0, "Constraint 2");
            }
        }
        // Contrainte (3)
        for (int t = 0; t < data.nbTechs(); t++) {
            for (int k : data.technicians()[t].unavailability()){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, x[t][0][k-1]);
                model.addConstr(expr, GRB.EQUAL, 1.0,"Constraint 3");
            }
        }

        // Contrainte 3 bis
        // Une intervention ne peut pas être affectée à l'équipe 0
        for (int i = 0; i < data.nbInterventions(); i++) {
            for (int k = 0; k < maxDay; k++) {
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, y[i][0][k]);
                model.addConstr(expr, GRB.EQUAL, 0,"Constraint 3 bis");
            }
        }

        // Contrainte (3bis-bis)
        // Si aucune intervention est réalisée sur un jour k donné,
        // Alors tous les techniciens sont dans l'équipe 0
        for (int k = 0; k < data.nbInterventions(); k++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int t = 0; t < data.nbTechs(); t++) {
                expr.addTerm(1, x[t][0][k]);
            }
            for (int i = 0; i < data.nbInterventions(); i++) {
                for (int g = 0; g < data.nbTechs(); g++) {
                    expr.addTerm(data.nbTechs(), y[i][g][k]);
                }
            }
            model.addConstr(expr, GRB.GREATER_EQUAL, data.nbTechs(), "Constraint 3 bis-bis");
        }

        // Contrainte (4)
        for (int i = 0; i < data.nbInterventions(); i++) {
            for (int d = 0; d < data.nbDomains(); d++) {
                for (int l = 0; l < data.nbLevels(); l++) {
                    for (int g = 0; g < data.nbTechs(); g++) {
                        for (int k = 0; k < maxDay; k++) {
                            GRBLinExpr expr = new GRBLinExpr();
                            for (int t = 0; t < data.nbTechs(); t++) {
                                expr.addTerm(data.technicians()[t].isTechnicianQualified(d,l) ? 1 : 0, x[t][g][k]);
                            }
                            expr.addTerm(-data.interventions()[i].domains()[d][l], y[i][g][k]);
                            model.addConstr(expr, GRB.GREATER_EQUAL, 0,"Constraint 4");
                        }
                    }

                }
            }
        }

        // Contrainte (5)
        for (int i = 0; i < data.nbInterventions(); i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int g = 0; g < data.nbTechs(); g++) {
                for (int k = 0; k < maxDay; k++) {
                    expr.addTerm((k)*120, y[i][g][k]);
                }
            }
            expr.addTerm(-1, b[i]);
            model.addConstr(0, GRB.GREATER_EQUAL, expr,"Constraint 5");
        }

        // Contrainte (6)
        for (int i = 0; i < data.nbInterventions(); i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int g = 0; g < data.nbTechs(); g++) {
                for (int k = 0; k < maxDay; k++) {
                    expr.addTerm((k+1)*120, y[i][g][k]);
                }
            }
            for (int g = 0; g < data.nbTechs(); g++) {
                for (int k = 0; k < maxDay; k++) {
                    expr.addTerm(-data.interventions()[i].duration(), y[i][g][k]);
                }
            }
            expr.addTerm(-1, b[i]);
            model.addConstr(0, GRB.LESS_EQUAL, expr,"Constraint 6");
        }

        // Contrainte (7)
        for (int i = 0; i < data.nbInterventions(); i++) {
            for (int j = 0; j < data.nbInterventions(); j++) {
                if (i != j){
                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm(1, b[i]);
                    expr.addTerm(-1, b[j]);
                    expr.addTerm(maxEndTime, u[i][j]);
                    model.addConstr(expr, GRB.LESS_EQUAL, -data.interventions()[i].duration() + maxEndTime,"Constraint 7");
                }
            }
        }

        // Contrainte (8)
        for (int i = 0; i < data.nbInterventions(); i++) {
            for (int j = 0; j < data.nbInterventions(); j++) {
                if (i != j){
                    for (int g = 0; g < data.nbTechs(); g++) {
                        for (int k = 0; k < maxDay; k++) {
                            GRBLinExpr expr = new GRBLinExpr();
                            expr.addTerm(1, u[i][j]);
                            expr.addTerm(1, u[j][i]);
                            expr.addTerm(-1, y[i][g][k]);
                            expr.addTerm(-1, y[j][g][k]);
                            model.addConstr(expr, GRB.GREATER_EQUAL, -1,"Constraint 8");
                        }
                    }
                }
            }
        }

        // Contrainte (9)
        for (int i = 0; i < data.nbInterventions(); i++) {
            GRBLinExpr expr = new GRBLinExpr();
            for (int g = 0; g < data.nbTechs(); g++) {
                for (int k = 0; k < maxDay; k++) {
                    expr.addTerm(1, y[i][g][k]);
                }
            }
            expr.addTerm(1, o[i]);
            model.addConstr(expr, GRB.EQUAL, 1,"Constraint 9");
        }

        // Contrainte (10)
        for (int i = 0; i < data.nbInterventions(); i++) {
            for (int j : data.interventions()[i].preds()){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, b[j-1]);
                expr.addTerm(-1, b[i]);
                expr.addTerm(-maxEndTime, o[i]);
                model.addConstr(expr, GRB.LESS_EQUAL, -data.interventions()[j-1].duration(),"Constraint 10");
            }
        }

        // Contrainte (11)
        for (int i = 0; i < data.nbInterventions(); i++) {
            for (int j : data.interventions()[i].preds()) {
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, o[j]);
                expr.addTerm(-1, o[i]);
                model.addConstr(expr, GRB.LESS_EQUAL, 0,"Constraint 11");
            }
        }

        // Contrainte (12)
        GRBLinExpr expr12 = new GRBLinExpr();
        for (int i = 0; i < data.nbInterventions(); i++) {
            expr12.addTerm(data.interventions()[i].cost(), o[i]);
        }
        model.addConstr(expr12, GRB.LESS_EQUAL, data.budget(),"Constraint 12");

        // Contrainte (13)
        for (int i = 0; i < data.nbInterventions(); i++) {
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1, C);
            expr.addTerm(-1, b[i]);
            for (int g = 0; g < data.nbTechs(); g++) {
                for (int k = 0; k < maxDay; k++) {
                    expr.addTerm(-data.interventions()[i].duration(), y[i][g][k]);
                }
            }
            model.addConstr(expr, GRB.GREATER_EQUAL, 0,"Constraint 13");
        }

        // Contrainte (14)
        for (int i = 0; i < data.nbInterventions(); i++) {
            if (data.interventions()[i].prio() == 1){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, C1);
                expr.addTerm(-1, b[i]);
                for (int g = 0; g < data.nbTechs(); g++) {
                    for (int k = 0; k < maxDay; k++) {
                        expr.addTerm(-data.interventions()[i].duration(), y[i][g][k]);
                    }
                }
                model.addConstr(expr, GRB.GREATER_EQUAL, 0,"Constraint 14");
            }
        }

        // Contrainte (15)
        for (int i = 0; i < data.nbInterventions(); i++) {
            if (data.interventions()[i].prio() == 2){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, C2);
                expr.addTerm(-1, b[i]);
                for (int g = 0; g < data.nbTechs(); g++) {
                    for (int k = 0; k < maxDay; k++) {
                        expr.addTerm(-data.interventions()[i].duration(), y[i][g][k]);
                    }
                }
                model.addConstr(expr, GRB.GREATER_EQUAL, 0,"Constraint 15");
            }
        }

        // Contrainte (16)
        for (int i = 0; i < data.nbInterventions(); i++) {
            if (data.interventions()[i].prio() == 3){
                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1, C3);
                expr.addTerm(-1, b[i]);
                for (int g = 0; g < data.nbTechs(); g++) {
                    for (int k = 0; k < maxDay; k++) {
                        expr.addTerm(-data.interventions()[i].duration(), y[i][g][k]);
                    }
                }
                model.addConstr(expr, GRB.GREATER_EQUAL, 0,"Constraint 16");
            }
        }

        model.optimize();

        if (model.get(IntAttr.SolCount) > 0) {
            System.out.println("Success! (Status: " + model.get(GRB.IntAttr.Status) + ")");
            System.out.println("Runtime : " + Math.round(model.get(DoubleAttr.Runtime) * 1000) / 1000.0 + " seconds");
            System.out.println("--> Printing results ");
            System.out.println("Objective value = " + Math.round((float)model.get(DoubleAttr.ObjVal)));

        } else {
            System.err.println("Fail! (Status: " + model.get(GRB.IntAttr.Status) + ")");
            return null;
        }

        int[][][] xCopy = new int[data.nbTechs()][data.nbTechs()][data.nbInterventions()];
        for (int t = 0; t < data.nbTechs(); t++) {
            for (int g = 0; g < data.nbTechs(); g++) {
                for (int k = 0; k < data.nbInterventions(); k++) {
                    xCopy[t][g][k] = (int)(x[t][g][k].get(GRB.DoubleAttr.X));
                }
            }
        }

        int[][][] yCopy = new int[data.nbInterventions()][data.nbTechs()][data.nbInterventions()];
        for (int i = 0; i < data.nbInterventions(); i++) {
            for (int g = 0; g < data.nbTechs(); g++) {
                for (int k = 0; k < maxDay; k++) {
                    yCopy[i][g][k] = (int)y[i][g][k].get(DoubleAttr.X);
                }
            }
        }

        int[] oCopy = new int[data.nbInterventions()];
        for (int i = 0; i < data.nbInterventions(); i++) {
            oCopy[i] = (int)o[i].get(DoubleAttr.X);
        }

        int[] bCopy = new int[data.nbInterventions()];
        for (int i = 0; i < data.nbInterventions(); i++) {
            bCopy[i] = Math.round((float)b[i].get(DoubleAttr.X));
        }

        return new TTSPSolution(data, xCopy, yCopy, oCopy, bCopy);
    }

    public static String usage(){
        return "usage: java -jar solver.jar absolutePathToFolder timeLimit";
    }

    public static void main(String[] args) throws FileNotFoundException, GRBException {
        if (args.length != 2){
            System.out.println(usage());
            System.exit(1);
        }
        File instanceFile = new File(args[0] + "/instance");
        File intervListFile = new File(args[0] + "/interv_list");
        File techListFile = new File(args[0] + "/tech_list");

        TTSPData data = instanceReader(instanceFile, intervListFile, techListFile);
        System.out.println(data);
        TTSPSolution sol = solver(data, Integer.parseInt(args[1]));
        if (sol == null){
            System.out.println("Solver couldn't find a solution within the time limit");
        }else{
            System.out.println(sol);
            System.out.println("Checking solution...");
            boolean check = Checker.check(data, sol);
            if (check){
                System.out.println("Solution is feasible");
            }else{
                System.out.println("Solution is not feasible");
            }
        }
    }
}
