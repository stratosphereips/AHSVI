package ahsvi;

import ahsvi.pomdpproblem.POMDPFileReader;
import java.io.FileNotFoundException;

/**
 *
 * @author dansm
 */
public class AHSVI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String test1 = "resources/1d.POMDP";
        String test2 = "resources/cheese.95.POMDP";
        try {
            POMDPFileReader pomdpFileReader = new POMDPFileReader(test1);
            pomdpFileReader.loadPOMDP();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
}
