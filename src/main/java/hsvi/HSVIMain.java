package hsvi;

import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.InSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.PostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.PreSolveMethod;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminator;
import hsvi.hsvicontrollers.terminators.exploreterminators.HSVIExploreTerminatorClassic;
import hsvi.hsvicontrollers.terminators.solveterminators.HSVISolveTerminatorClassic;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import pomdpproblem.POMDPFileReader;
import pomdpproblem.POMDPProblem;

import java.io.File;

public class HSVIMain {
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

        double epsilon = 0.000001;

        PreSolveMethod preSolveMethod = new PreSolveMethod();
        InSolveMethod inSolveMethod = new InSolveMethod();
        PostSolveMethod postSolveMethod = new PostSolveMethod();
        SolveTerminator solveTerminator = new HSVISolveTerminatorClassic();
        ExploreTerminator exploreTerminator = new HSVIExploreTerminatorClassic();

        // =======================================

        String pomdpFilePathStr = RESOURCES_FOLDER_NAME + pomdpFileName + POMDP_EXT;
        POMDPFileReader pomdpFileReader = new POMDPFileReader(pomdpFilePathStr, true);
        pomdpFileReader.loadProblem();
        POMDPProblem pomdpProblem = pomdpFileReader.getPomdpProblem();


        // =======================================
        // =                H S V I              =


        HSVIAlgorithm hsviAlgorithm = new HSVIAlgorithm.HSVIAlgorithmBuilder()
                .setEpsilon(epsilon)
                .setPomdpProblem(pomdpProblem)
                .setPreSolveMethod(preSolveMethod)
                .setInSolveMethod(inSolveMethod)
                .setPostSolveMethod(postSolveMethod)
                .setSolveTerminator(solveTerminator)
                .setExploreTerminator(exploreTerminator)
                .build();
        hsviAlgorithm.solve();

        System.out.println("Final utility LB: " + hsviAlgorithm.getLBValueInInitBelief());
        System.out.println("Final utility UB: " + hsviAlgorithm.getUBValueInInitBelief());

    }
}
