package ahsvi;

import ahsvi.pomdpproblem.POMDPFileReader;

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
        POMDPFileReader pomdpFileReader = new POMDPFileReader(test1);
    }
    
}
