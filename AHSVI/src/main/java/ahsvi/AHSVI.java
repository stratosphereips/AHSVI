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
        POMDPFileReader pomdpFileReader = new POMDPFileReader("resources/1d.POMDP");
    }
    
}
