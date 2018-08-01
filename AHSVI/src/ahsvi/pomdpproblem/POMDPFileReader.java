package ahsvi.pomdpproblem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

/**
 *
 * @author dansm
 */
public class POMDPFileReader {

    private static final HashSet<String> pomdpFileKeyWords = new HashSet<String>(Arrays.asList("discount:",
            "values:", "states:", "actions:", "observations:", "start:", "T:", "O:", "R:"));

    private final POMDPProblem hsviProblem;

    public POMDPFileReader(String fileName) {
        this(new File(fileName));
    }

    public POMDPFileReader(File file) {
        hsviProblem = new POMDPProblem();
        try {
            Scanner sc = new Scanner(new FileReader(file));
            String token;
            while (sc.hasNext()) {
                token = sc.next();
                if (isCurrentLineComment(token)) {
                    // System.out.println("# " + sc.nextLine());
                    continue;
                }
                System.out.println(token);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private boolean isCurrentLineComment(String token) {
        return token.startsWith("#");
    }
}
