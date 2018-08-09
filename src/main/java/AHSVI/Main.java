package AHSVI;

import ilog.concert.IloException;
import POMDPProblem.POMDPFileReader;
import POMDPProblem.POMDPProblem;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting POMDP solver");

        String RESOURCES_FOLDER_NAME = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        String POMDP_EXT = ".POMDP";
        String POMDP_1D = "1d";
        String POMDP_1S = "1s";

        // =======================================
        // =          S E T T I N G S
        String pomdpFileName = POMDP_1D;
        double epsilon = 0.00001;

        String pomdpFilePathStr = RESOURCES_FOLDER_NAME + pomdpFileName + POMDP_EXT;
        POMDPFileReader pomdpFileReader = new POMDPFileReader(pomdpFilePathStr, true);
        pomdpFileReader.loadProblem();

        POMDPProblem pomdpProblem = pomdpFileReader.getPomdpProblem();
        System.out.println(pomdpProblem);
        HSVIAlgorithm hsviAlgorithm = new HSVIAlgorithm(pomdpProblem, epsilon);
        try {
            hsviAlgorithm.solve();
        } catch (IloException e) {
            e.printStackTrace();
            System.exit(2);
        }

        System.out.println("Final utility LB: " + hsviAlgorithm.finalUtilityLB);
        System.out.println("Final utility UB: " + hsviAlgorithm.finalUtilityUB);
    }
}
