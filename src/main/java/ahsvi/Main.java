package ahsvi;

import hsvi.HSVIAlgorithm;
import pomdpproblem.POMDPFileReader;
import pomdpproblem.POMDPProblem;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting POMDP solver");

        String RESOURCES_FOLDER_NAME = "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        String POMDP_EXT = ".POMDP";

        // some POMDP problems that can be loaded
        String POMDP_1S = "1s";
        String POMDP_2S = "2s";
        String POMDP_1D = "1d";
        String POMDP_TIGER = "tiger";
        String POMDP_TIGERGRID = "tiger-grid";

        // =======================================
        // =          S E T T I N G S            =

        //String pomdpFileName = POMDP_1S;
        //String pomdpFileName = POMDP_2S;
        String pomdpFileName = POMDP_1D;
        //String pomdpFileName = POMDP_TIGER;
        //String pomdpFileName = POMDP_TIGERGRID;
        double epsilon = 0.1;

        // =======================================

        String pomdpFilePathStr = RESOURCES_FOLDER_NAME + pomdpFileName + POMDP_EXT;
        POMDPFileReader pomdpFileReader = new POMDPFileReader(pomdpFilePathStr, true);
        pomdpFileReader.loadProblem();
        POMDPProblem pomdpProblem = pomdpFileReader.getPomdpProblem();

        // =======================================
        // =               A H S V I             =

        /*
        pomdpProblem.initBelief = new double[pomdpProblem.getNumberOfStates()];
        for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
            pomdpProblem.initBelief[s] = 1/pomdpProblem.getNumberOfStates();
        }
        AHSVIAlgorithm ahsviAlgorithm = new AHSVIAlgorithm(pomdpProblem, epsilon);
        ahsviAlgorithm.solve();
        */
        // System.out.println(pomdpProblem);


        // =======================================
        // =                H S V I              =

        HSVIAlgorithm hsviAlgorithm = new HSVIAlgorithm(pomdpProblem, epsilon);
        hsviAlgorithm.solve();

        System.out.println("Final utility LB: " + hsviAlgorithm.getLBValueInInitBelief());
        System.out.println("Final utility UB: " + hsviAlgorithm.getUBValueInInitBelief());

    }
}
