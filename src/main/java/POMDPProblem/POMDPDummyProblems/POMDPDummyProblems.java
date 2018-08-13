package POMDPProblem.POMDPDummyProblems;

import POMDPProblem.POMDPProblem;

public class POMDPDummyProblems implements POMDPDummyProblemI {
    private final String pomdpProblemName;

    public POMDPDummyProblems(String pomdpProblemName) {
        this.pomdpProblemName = pomdpProblemName;
    }

    public POMDPProblem load() {
        switch (pomdpProblemName) {
            case "1s":
                return new POMDP1S().load();
            case "2s":
                return new POMDP2S().load();
            case "1d":
                return new POMDP1D().load();
            case "tiger":
                return new POMDPTiger().load();
            case "tiger-grid":
                return new POMDPTigerGrid().load();
            default:
                throw new IllegalArgumentException("No such dummy POMDP");
        }
    }
}
