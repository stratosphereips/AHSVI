package ahsvi.pomdpproblem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author dansm
 */
public class POMDPFileReader {
    
    private POMDPProblem hsviProblem = null;

    public POMDPFileReader(String fileName) {
        this(new File(fileName));
    }
    
    public POMDPFileReader(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    if (line.startsWith("#") || line.isEmpty()) {
                        continue;
                    }
                    
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}
