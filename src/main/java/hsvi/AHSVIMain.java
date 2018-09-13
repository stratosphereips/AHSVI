package hsvi;

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
import networkproblem.NetworkDistrubitionToPOMDPConverter;
import org.apache.commons.cli.*;
import pomdpproblem.POMDPProblem;

import java.util.Arrays;

public class AHSVIMain {

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
        System.out.println("Starting AHSVI");
        System.out.println("Input arguments: " + Arrays.toString(args));
        CommandLine cmd = parseCommandLine(args);

        // =======================================
        // =          S E T T I N G S            =

        String networksFileName = cmd.getOptionValue("nf");
        String portsValuesFileName = cmd.getOptionValue("pvf", null);
        String portsSuccessfulAttacksProbsFileName = cmd.getOptionValue("spf", null);
        int honeypotsCountLb = Integer.parseInt(cmd.getOptionValue("hcl", "1"));
        int honeypotsCountUb = Integer.parseInt(cmd.getOptionValue("hcu", "1"));
        double probeSuccessProbability = Double.parseDouble(cmd.getOptionValue("psp", "0.5"));
        double probeCost = Double.parseDouble(cmd.getOptionValue("pc", "0.0"));
        double discount = Double.parseDouble(cmd.getOptionValue("d", "0.95"));


        int maxNumberOfDetectedAttacksAllowed = 0;
        double attackOnHoneyPotCost = -1.0;

        double epsilon = Config.ZERO;

        // =======================================

        NetworkDistrubitionToPOMDPConverter networkFileReader =
                new NetworkDistrubitionToPOMDPConverter(networksFileName)
                        .setDiscount(discount)
                        .setHoneypotsCountsRange(honeypotsCountLb, honeypotsCountUb)
                        .setMaxNumberOfDetectedAttacksAllowed(maxNumberOfDetectedAttacksAllowed)
                        .setProbeSuccessProbability(probeSuccessProbability)
                        .setProbeCost(probeCost)
                        .loadPortsValues(portsValuesFileName)
                        .loadPortsSuccessfulAttackProbs(portsSuccessfulAttacksProbsFileName);
        POMDPProblem pomdpProblem = networkFileReader.getPomdpProblem();

        AHSVIMinValueFinder minValueFinder =
                new AHSVIMinValueFinder(networkFileReader.getStatesGroupsIds(), networkFileReader.getGroupsProbabilities());

        PreSolveMethod preSolveMethod = new AHSVIPreSolveMethod(minValueFinder);
        InSolveMethod inSolveMethod = new AHSVIInSolveMethod(minValueFinder);
        PostSolveMethod postSolveMethod = new AHSVIPostSolveMethod(minValueFinder, networkFileReader.getInfoSets());
        SolveTerminator solveTerminator = new AHSVISolveTerminatorAbsoluteDiff();
        ExploreTerminator exploreTerminator = new HSVIExploreTerminatorClassic();

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
