package hsvi;

import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.AHSVIInSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.InSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.AHSVIPostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.PostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.AHSVIPreSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.PreSolveMethod;
import hsvi.hsvicontrollers.terminators.exploreterminators.AHSVIExploreTerminatorClassic;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminator;
import hsvi.hsvicontrollers.terminators.exploreterminators.HSVIExploreTerminatorClassic;
import hsvi.hsvicontrollers.terminators.solveterminators.AHSVISolveTerminatorAbsoluteDiff;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import networkproblem.NetworkDistrubitionToPOMDPConverter;
import pomdpproblem.POMDPProblem;

import java.io.File;
import java.util.TreeSet;

public class AHSVIMain {
    public static void main(String[] args) {
        System.out.println("Starting POMDP solver");

        String RESOURCES_FOLDER_NAME =
                "src" + File.separator + "main" + File.separator + "resources" + File.separator;
        String NETWORKS_FOLDER_NAME = RESOURCES_FOLDER_NAME + "networks" + File.separator;
        String PORTS_INFO_FOLDER_NAME = RESOURCES_FOLDER_NAME + "ports_info" + File.separator;
        String POMDP_EXT = ".network";
        String TXT_EXT = ".txt";

        String DATA1 = "data1";
        String DATA2 = "data2";
        String DATA3 = "data3";

        String PORTS_VALUES_SMALL = "ports_values_small";
        String PORTS_SUCCESSFUL_ATTACK_PROBS_SMALL = "ports_successful_attack_probs_small";

        // =======================================
        // =          S E T T I N G S            =

        String networksFileName = DATA2;

        String portsValuesFileName = PORTS_VALUES_SMALL;
        String portsSuccessfulAttacksProbsFileName = PORTS_SUCCESSFUL_ATTACK_PROBS_SMALL;

        double discount = 0.9;

        int honeypotsCount = 2;

        int maxNumberOfDetectedAttacksAllowed = 0;
        double probeSuccessProbability = 0.2;
        double probeCost = 0.0;
        double attackOnHoneyPotCost = -1.0;

        double epsilon = Config.ZERO;

        // =======================================

        String pomdpFilePathStr = NETWORKS_FOLDER_NAME + networksFileName + POMDP_EXT;
        String portsValuesFilePathStr = PORTS_INFO_FOLDER_NAME + portsValuesFileName + TXT_EXT;
        String portsSuccessfullAttackProbsFilePathStr = PORTS_INFO_FOLDER_NAME + portsSuccessfulAttacksProbsFileName + TXT_EXT;

        NetworkDistrubitionToPOMDPConverter networkFileReader =
                new NetworkDistrubitionToPOMDPConverter(pomdpFilePathStr)
                        .setDiscount(discount)
                        .setHoneypotsCount(honeypotsCount)
                        .setMaxNumberOfDetectedAttacksAllowed(maxNumberOfDetectedAttacksAllowed)
                        .setProbeSuccessProbability(probeSuccessProbability)
                        .setProbeCost(probeCost)
                        .loadPortsValues(portsValuesFilePathStr)
                        .loadPortsSuccessfulAttackProbs(portsSuccessfullAttackProbsFilePathStr);
        POMDPProblem pomdpProblem = networkFileReader.getPomdpProblem();

        AHSVIMinValueFinder minValueFinder =
                new AHSVIMinValueFinder(networkFileReader.getStatesGroupsIds(), networkFileReader.getGroupsProbabilities());

        PreSolveMethod preSolveMethod = new AHSVIPreSolveMethod(minValueFinder);
        InSolveMethod inSolveMethod = new AHSVIInSolveMethod(minValueFinder);
        PostSolveMethod postSolveMethod = new AHSVIPostSolveMethod(minValueFinder, networkFileReader.getInfoSets());
        SolveTerminator solveTerminator = new AHSVISolveTerminatorAbsoluteDiff();
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
