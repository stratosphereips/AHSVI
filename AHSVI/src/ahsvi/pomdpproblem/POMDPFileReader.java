package ahsvi.pomdpproblem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author dansm
 */
public class POMDPFileReader {

    private static final HashSet<String> pomdpFileSpecKeyWords = new HashSet<String>(Arrays.asList("discount:",
            "values:", "states:", "actions:", "observations:", "start:", "T:", "O:", "R:"));

    private final Scanner sc;
    private String lastSpecKeyWord;

    private final POMDPProblem hsviProblem;

    public POMDPFileReader(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }

    public POMDPFileReader(File file) throws FileNotFoundException {
        hsviProblem = new POMDPProblem();
        sc = new Scanner(file);
        lastSpecKeyWord = null;
    }

    public void loadPOMDP() {
        ArrayList<String> specs;
        while (sc.hasNext()) {
            specs = getNextSpec();
        }
    }

    private boolean isCurrentLineComment(String token) {
        return token.startsWith("#");
    }

    private void ignoreComment() {
        sc.nextLine();
    }

    private boolean isCurrentTokenSpecKeyWord(String token) {
        return pomdpFileSpecKeyWords.contains(token);
    }

    private ArrayList<String> getNextSpec() {
        ArrayList<String> specs = new ArrayList<>();
        if (lastSpecKeyWord != null) {
            specs.add(lastSpecKeyWord);
        }
        String token;
        while (sc.hasNext()) {
            token = sc.next();
            if (isCurrentLineComment(token)) {
                ignoreComment();
                System.out.println("Comment ignored");
            } else if (isCurrentTokenSpecKeyWord(token)) {
                lastSpecKeyWord = token;
                System.out.println("Spec key word: " + token);
                break;
            } else {
                specs.add(token);
                System.out.println("Spec argument: " + token);
            }
        }
        return specs;
    }
}
