package hsvi;

import hsvi.HSVIAlgorithm;
import networkproblem.NetworkFileReader;
import pomdpproblem.POMDPFileReader;
import pomdpproblem.POMDPProblem;

import java.io.File;

public class AHSVIMain {
    public static void main(String[] args) {
        System.out.println("Starting POMDP solver");

        String RESOURCES_FOLDER_NAME =
                "src" + File.separator + "main" + File.separator + "resources" + File.separator + "networks"+ File.separator;
        String POMDP_EXT = ".network";

        String DATA1 = "data1";

        // =======================================
        // =          S E T T I N G S            =

        String networksFileName = DATA1;
        double epsilon = 1e-10;

        // =======================================

        String pomdpFilePathStr = RESOURCES_FOLDER_NAME + networksFileName + POMDP_EXT;

        NetworkFileReader networkFileReader = new NetworkFileReader(pomdpFilePathStr);
        networkFileReader.loadNetwork();
        POMDPProblem pomdpProblem = networkFileReader.getPomdpProblem();

        // =======================================
        // =               A H S V I             =

        //AHSVIAlgorithm ahsviAlgorithm = new AHSVIAlgorithm(pomdpProblem, epsilon);
        //ahsviAlgorithm.solve();

    }
}
