package POMDPProblem;

import POMDPProblem.POMDPDummyProblems.POMDPDummyProblems;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class POMDPFileReader {

    private String pathToPOMDPFile;
    private boolean dummy;

    private POMDPProblem pomdpProblem;

    public POMDPFileReader(String fileName) {
        this(fileName, false);
    }

    public POMDPFileReader(String pathToPOMDPFile, boolean dummy) {
        this.pathToPOMDPFile = pathToPOMDPFile;
        this.dummy = dummy;
        pomdpProblem = null;
    }

    public void loadProblem() {
        System.out.println("Loading " + pathToPOMDPFile);
        if (dummy) {
            loadDummyPOMDP(pathToPOMDPFile);
        } else {
            loadPOMDPFile(pathToPOMDPFile);
        }
        System.out.println("Loading done");
    }

    public POMDPProblem getPomdpProblem() {
        return pomdpProblem;
    }

    private void loadPOMDPFile(String fileName) {
        System.out.println("NOPE");
        System.exit(10);
    }

    private void loadDummyPOMDP(String pathToPOMDPFile) {
        pomdpProblem = new POMDPDummyProblems(FilenameUtils.getBaseName(pathToPOMDPFile)).load();
    }
}
