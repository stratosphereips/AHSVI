package hsvi;

import helpers.HelperFunctions;
import hsvi.CustomLogger.CustomLogger;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.AHSVIMinValueFinder;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.AHSVIInSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.insolvemethods.InSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.AHSVIPostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.postsolvemethods.PostSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.AHSVIPreSolveMethod;
import hsvi.hsvicontrollers.hsvioverridablemethods.solvemethods.presolvemethods.PreSolveMethod;
import hsvi.hsvicontrollers.terminators.exploreterminators.ExploreTerminator;
import hsvi.hsvicontrollers.terminators.exploreterminators.HSVIExploreTerminatorClassic;
import hsvi.hsvicontrollers.terminators.solveterminators.AHSVISolveTerminatorAbsoluteDiff;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminator;
import hsvi.hsvicontrollers.terminators.solveterminators.SolveTerminatorInfinite;
import networkproblem.NetworkDistrubitionToPOMDPConverter;
import networkproblem.statesmaker.AllPermutationsMaker;
import networkproblem.statesmaker.SamplePermutationMaker;
import org.apache.commons.cli.*;
import pomdpproblem.POMDPProblem;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class AHSVIAveragedMain {

    private static Logger LOGGER = CustomLogger.getLogger();

    private static Options createOptions() {
        Options options = new Options();
        Option opt = new Option("nf", "netsfile", true, "input file with network and their counts");
        opt.setRequired(true);
        options.addOption(opt);
        options.addOption("pvf", "portsvaluessfile", true, "input file with values of ports");
        options.addOption("spf", "successprobabilitiesfile", true, "input file with probabilities of successful attacks on ports");
        options.addOption("d", "discount", true, "discount used in computing POMDP");
        opt = new Option("hcl", "honepotscountlower", true, "lower bound in range of honeypots counts");
        opt.setRequired(true);
        options.addOption(opt);
        opt = new Option("hcu", "honepotscountupper", true, "upper bound in range of honeypots counts");
        opt.setRequired(true);
        options.addOption(opt);
        options.addOption("psp", "probesuccessprobability", true, "probability of successful action probe");
        options.addOption("pc", "probecost", true, "cost of action probe (should be negative, if you want it to be \"cost\")");
        options.addOption("e", "epsilon", true, "epsilon used in (A)HSVI algorithm");
        return options;
    }

    private static CommandLine parseCommandLine(String[] args) {
        Options options = createOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        return cmd;
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            //
        }
        System.out.println("Starting AHSVI");
        System.out.println("Input arguments: " + Arrays.toString(args));
        CommandLine cmd = parseCommandLine(args);

        // =========================================================
        // =           N E T W O R K S   S E T T I N G S           =

        String networksFileName = cmd.getOptionValue("nf");
        String portsValuesFileName = cmd.getOptionValue("pvf", null);
        String portsSuccessfulAttacksProbsFileName = cmd.getOptionValue("spf", null);
        int honeypotsCountLb = Integer.parseInt(cmd.getOptionValue("hcl", "1"));
        int honeypotsCountUb = Integer.parseInt(cmd.getOptionValue("hcu", "1"));
        double probeSuccessProbability = Double.parseDouble(cmd.getOptionValue("psp", "0.5"));
        double probeCost = Double.parseDouble(cmd.getOptionValue("pc", "-0.0"));
        double discount = Double.parseDouble(cmd.getOptionValue("d", "0.95"));

        Class<?> permutationsMakerClass = SamplePermutationMaker.class;

        // =======================================
        NetworkDistrubitionToPOMDPConverter networkFileReader = null;
        POMDPProblem pomdpProblem= null;
        AHSVIMinValueFinder minValueFinder= null;
        PreSolveMethod preSolveMethod= null;
        InSolveMethod inSolveMethod= null;
        PostSolveMethod postSolveMethod= null;
        SolveTerminator solveTerminator = null;
        ExploreTerminator exploreTerminator = null;
        double epsilon = Config.ZERO;
        HSVIAlgorithm hsviAlgorithm= null;

        double[] beliefsSummed = null;


        int samplingEpochsCount = 1000;
        for (int iter = 0; iter < samplingEpochsCount; ++iter) {
            System.out.println("Epoch: " + iter);

            networkFileReader =
                    new NetworkDistrubitionToPOMDPConverter(networksFileName, permutationsMakerClass)
                            .setDiscount(discount)
                            .setHoneypotsCountsRange(honeypotsCountLb, honeypotsCountUb)
                            .setProbeSuccessProbability(probeSuccessProbability)
                            .setProbeCost(probeCost)
                            .loadPortsValues(portsValuesFileName)
                            .loadPortsSuccessfulAttackProbs(portsSuccessfulAttacksProbsFileName);
            pomdpProblem = networkFileReader.getPomdpProblem();

            minValueFinder =
                    new AHSVIMinValueFinder(networkFileReader.getStatesGroupsIds(), networkFileReader.getGroupsProbabilities());

            if (iter == 0) {
                beliefsSummed = new double[pomdpProblem.getNumberOfStates()];
            }

            // =========================================================
            // =              A H S V I   S E T T I N G S              =

             preSolveMethod = new AHSVIPreSolveMethod(minValueFinder, networkFileReader.getInfoSets());
             inSolveMethod = new AHSVIInSolveMethod(minValueFinder, networkFileReader.getInfoSets());
             postSolveMethod = new AHSVIPostSolveMethod(minValueFinder, networkFileReader.getInfoSets());
             solveTerminator = new AHSVISolveTerminatorAbsoluteDiff();
             exploreTerminator = new HSVIExploreTerminatorClassic();

            // =========================================================
            // =                        A H S V I                      =

            hsviAlgorithm = new HSVIAlgorithm.HSVIAlgorithmBuilder()
                    .setEpsilon(epsilon)
                    .setPomdpProblem(pomdpProblem)
                    .setPreSolveMethod(preSolveMethod)
                    .setInSolveMethod(inSolveMethod)
                    .setPostSolveMethod(postSolveMethod)
                    .setSolveTerminator(solveTerminator)
                    .setExploreTerminator(exploreTerminator)
                    .build();
            hsviAlgorithm.solve();

            HelperFunctions.arrAdd(beliefsSummed, pomdpProblem.getInitBelief());
        }

        HelperFunctions.arrDiv(beliefsSummed, samplingEpochsCount);
        LOGGER.fine("\n\n\n\n\n");
        LOGGER.fine(Arrays.stream(beliefsSummed).sum() + "  " + Arrays.toString(beliefsSummed));

        for (int s = 0; s < pomdpProblem.getNumberOfStates(); ++s) {
            LOGGER.fine(String.format("\t%s  %.4f", pomdpProblem.getStateName(s), beliefsSummed[s]));
        }
    }
}
