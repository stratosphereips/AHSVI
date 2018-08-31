package hsvi;

import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.AHSVIInSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.InSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.AHSVIPostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.HSVIPostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.PostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.AHSVIPreSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.PreSolveMethod;
import hsvi.hsvicontrollers.terminators.exploreterminators.AHSVIExploreTerminatorClassic;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminator;
import hsvi.hsvicontrollers.terminators.exploreterminators.HSVIExploreTerminatorClassic;
import hsvi.hsvicontrollers.terminators.solveterminators.AHSVISolveTerminatorAbsoluteDiff;
import hsvi.hsvicontrollers.terminators.solveterminators.HSVISolveTerminatorClassic;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import networkproblem.NetworkDistrubitionToPOMDPConverter;
import pomdpproblem.POMDPProblem;

import java.io.File;

public class AHSVIMain {
    public static void main(String[] args) {
        System.out.println("Starting POMDP solver");

        String RESOURCES_FOLDER_NAME =
                "src" + File.separator + "main" + File.separator + "resources" + File.separator + "networks" + File.separator;
        String POMDP_EXT = ".network";

        String DATA1 = "data1";

        // =======================================
        // =          S E T T I N G S            =

        String networksFileName = DATA1;

        double discount = 0.9;

        int honeypotsCount = 1;

        int maxNumberOfDetectedAttacksAllowed = 0;
        double successfulAttackReward = 1.0;
        double probeSuccessProbability = 0.2;
        double probeCost = -0.5;

        double epsilon = 1e-10;

        // =======================================

        String pomdpFilePathStr = RESOURCES_FOLDER_NAME + networksFileName + POMDP_EXT;

        NetworkDistrubitionToPOMDPConverter networkFileReader =
                new NetworkDistrubitionToPOMDPConverter(pomdpFilePathStr)
                        .setDiscount(discount)
                        .setHoneypotsCount(honeypotsCount)
                        .setMaxNumberOfDetectedAttacksAllowed(maxNumberOfDetectedAttacksAllowed)
                        .setSuccessfulAttackReward(successfulAttackReward)
                        .setProbeSuccessProbability(probeSuccessProbability)
                        .setProbeCost(probeCost)
                        .loadNetwork();
        POMDPProblem pomdpProblem = networkFileReader.getPomdpProblem();

        AHSVIMinValueFinder minValueFinder =
                new AHSVIMinValueFinder(networkFileReader.getStatesGroupsIds(), networkFileReader.getGroupsProbabilities());

        PreSolveMethod preSolveMethod = new AHSVIPreSolveMethod(minValueFinder);
        InSolveMethod inSolveMethod = new AHSVIInSolveMethod(minValueFinder);
        PostSolveMethod postSolveMethod = new AHSVIPostSolveMethod();
        SolveTerminator solveTerminator = new AHSVISolveTerminatorAbsoluteDiff(minValueFinder);
        ExploreTerminator exploreTerminator = new AHSVIExploreTerminatorClassic();

        // =======================================
        // =               A H S V I             =

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
    }
}
