package HSVI;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by durkokar on 22.2.17.
 */
public class GeneralSolverSetting {

    //    public List<Long> observations;
//    public int observationStep;
//    public List<Long> actions;
    public Map<Integer, Long> thresholds;
    //    public int maxDepth;
//    public long maxObservation;
    public double discount;
    public Map<Integer, Pair<UserTypeI, Long>> indexToState;
    public Map<Integer, Double> indexedFP = new TreeMap<>();
    public Map<UserTypeI, Map<Long, Integer>> stateToIndex;
    public double[] initBelief;
    private HashMap<Long, Integer> thresholdsInverse;

    public GeneralSolverSetting(Map<Integer, Long> thresholds, int maxDepth, double discount){
        this.thresholds = thresholds;
//        this.maxDepth = maxDepth;
        this.discount = discount;
    }

    public GeneralSolverSetting(Map<Integer, Long> thresholds, int maxDepth, double discount, Prior prior, boolean IS_ADDITIVE, Double FP){
//        this.observations = observations;
//        observationStep = observations.get(1) - observations.get(0);
//        this.actions = actions;
        this.thresholds = thresholds;
//        this.maxDepth = maxDepth;
        this.discount = discount;
//        this.maxObservation = observations.stream().mapToLong(a -> a).max().getAsLong();
    }

    public void prepareStateIndexingOnlySupport() {
        if (indexToState == null) {
            indexToState = new HashMap<Integer, Pair<UserTypeI, Long>>();
            stateToIndex = new HashMap<UserTypeI, Map<Long, Integer>>();
        } else {
            indexToState.clear();
            stateToIndex.clear();
        }

        List<Double> initBelief = new ArrayList<>();

        int index = 0;
        for (Map.Entry<UserTypeI, Double> userTypeIDoubleEntry : prior.getProbabilityDistribution().entrySet()) {
//            for (Long threshold : thresholds) {
            for ( int i=0; i<thresholds.size(); i++ ) {
                int thresholdIndex = i;
                double thresholdPrb = userTypeIDoubleEntry.getKey().getThresholdProbability(thresholdIndex);
                if ( thresholdPrb > 0 ) {
                    Pair<UserTypeI, Long> state = new Pair<UserTypeI, Long>(userTypeIDoubleEntry.getKey(), thresholds.get(thresholdIndex));
                    indexToState.put(index, state);
                    indexedFP.put(index, userTypeIDoubleEntry.getKey().getFalsePositiveForThreshold(thresholdIndex));

                    if (!stateToIndex.containsKey(userTypeIDoubleEntry.getKey())) {
                        stateToIndex.put(userTypeIDoubleEntry.getKey(), new HashMap<Long, Integer>());
                    }
                    stateToIndex.get(userTypeIDoubleEntry.getKey()).put(thresholds.get(thresholdIndex), index);
                    index++;
                    initBelief.add(userTypeIDoubleEntry.getValue() * thresholdPrb);
                }
            }
        }

        for (int i = 0; i < initBelief.size(); ++i) {
            this.initBelief[i] = initBelief.get(i);
        }
    }

    public int getDefendersThresholdActionInverse(long a) {
        if ( thresholdsInverse == null ) {
            this.thresholdsInverse = new HashMap<Long, Integer>();
            for ( int i=0; i<thresholds.size(); i++ ) {
                thresholdsInverse.put(thresholds.get(i), i);
            }
        }
        return thresholdsInverse.get(a);
//        if ( a == 0 ) {
//            return 0;
//        } else {
//            return (int) (Math.log(a) / Math.log(2)) + 1;
//        }
//        return -1;
    }

    public long getAttackerUtilityForAction(int a) {
        return thresholds.get(a);
//        if ( a == 0 ) {
//            return 0;
//        } else {
//            return (long) Math.pow(2, a-1);
//        }
    }

    public void prepareStateIndexing() {
        if (indexToState == null) {
            indexToState = new HashMap<Integer, Pair<UserTypeI, Long>>();
            stateToIndex = new HashMap<UserTypeI, Map<Long, Integer>>();
        } else {
            indexToState.clear();
            stateToIndex.clear();
        }

        List<Double> initBelief = new ArrayList<>();

        int index = 0;
        for (Map.Entry<UserTypeI, Double> userTypeIDoubleEntry : prior.getProbabilityDistribution().entrySet()) {
//            for (Integer threshold : thresholds) {
//            for (int i=0; i<thresholds.size(); i++ ) {
            for (int i=0; i<thresholds.size(); i++ ) {
                int thresholdIndex = i;
                double thresholdPrb = userTypeIDoubleEntry.getKey().getThresholdProbability(thresholdIndex);
//                if ( thresholdPrb > 0 ) {
//                    Pair<UserTypeI, Long> state = new Pair<UserTypeI, Long>(userTypeIDoubleEntry.getKey(), getDefendersThresholdAction(thresholdIndex));
                Pair<UserTypeI, Long> state = new Pair<UserTypeI, Long>(userTypeIDoubleEntry.getKey(), thresholds.get(thresholdIndex));
                indexToState.put(index, state);
                indexedFP.put(index, userTypeIDoubleEntry.getKey().getFalsePositiveForThreshold(thresholdIndex));

                if ( !stateToIndex.containsKey(userTypeIDoubleEntry.getKey()) ) {
                    stateToIndex.put(userTypeIDoubleEntry.getKey(), new HashMap<Long, Integer>());
                }
                stateToIndex.get(userTypeIDoubleEntry.getKey()).put(thresholds.get(thresholdIndex), index);
                index++;
                initBelief.add(userTypeIDoubleEntry.getValue() * thresholdPrb);
            }
        }
//        initBelief.add(0d); // state for being detected
//        indexToState.put(index, null);

        this.initBelief = ArrayUtils.toPrimitive(initBelief.toArray(new Double[0]));
    }
    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void printStgForMathematica() {
        System.out.println("Print Defender's strategy");
        String str = "{";
        Iterator<Map.Entry<UserTypeI, Double>> iterator1 = prior.getProbabilityDistribution().entrySet().iterator();
        while ( iterator1.hasNext() ) {
            Map.Entry<UserTypeI, Double> entry = iterator1.next();
            str += "{" + entry.getKey().getId() + ", ";
            for ( int thresholdIndex = 0; thresholdIndex < thresholds.size(); thresholdIndex++ ) {
//            Iterator<Long> iterator = thresholds.iterator();
//            while ( iterator.hasNext() ) {
//                long threshold = iterator.next();
                double value = entry.getKey().getThresholdProbability(thresholdIndex);
                str += "{" + thresholdIndex + ", " + value + "}";
                if ( thresholdIndex < thresholds.size()-1 ) {
                    str += ",";
                }
            }
            str += "}";
            if ( iterator1.hasNext() ) {
                str += ",";
            }
        }
        str += "}";
        System.out.println("Defense Strategy for Mathematica:");
        System.out.println(str);
    }

    public String stgForMathematicaCumulativeQuantileString() {
        String str = "{";
        Iterator<Map.Entry<UserTypeI, Double>> iterator1 = prior.getProbabilityDistribution().entrySet().iterator();
        while ( iterator1.hasNext() ) {
            Map.Entry<UserTypeI, Double> entry = iterator1.next();
            str += "{" + entry.getKey().getId() + ", ";
            double cum = 0;
            for ( int thresholdIndex = 0; thresholdIndex < thresholds.size(); thresholdIndex++ ) {
//            Iterator<Integer> iterator = thresholds.iterator();
//            while ( iterator.hasNext() ) {
//                Integer threshold = iterator.next();
                cum += entry.getKey().getThresholdProbability(thresholdIndex);
                str += "{" + entry.getKey().getQuantileOfThreshold(thresholdIndex) + ", " + cum + "}";
                if ( thresholdIndex < thresholds.size() - 1 ) {
                    str += ",";
                }
            }
            str += "}";
            if ( iterator1.hasNext() ) {
                str += ",";
            }
        }
        str += "}";
        return str;
    }

    public String stgValues() {
        String str = "";
        Iterator<Map.Entry<UserTypeI, Double>> iterator1 = prior.getProbabilityDistribution().entrySet().iterator();
        while ( iterator1.hasNext() ) {
            Map.Entry<UserTypeI, Double> entry = iterator1.next();
            double cum = 0;
            for ( int thresholdIndex = 0; thresholdIndex < thresholds.size(); thresholdIndex++ ) {
                cum += entry.getKey().getThresholdProbability(thresholdIndex);
                str += cum;
                if ( thresholdIndex < thresholds.size() - 1 ) {
                    str += ",";
                }
            }
            if ( iterator1.hasNext() ) {
                str += ";";
            }
        }
        return str;
    }

    public String stgForMathematicaCumulativeString() {
        String str = "{";
        Iterator<Map.Entry<UserTypeI, Double>> iterator1 = prior.getProbabilityDistribution().entrySet().iterator();
        while ( iterator1.hasNext() ) {
            Map.Entry<UserTypeI, Double> entry = iterator1.next();
            str += "{" + entry.getKey().getId() + ", ";
            double cum = 0;
            for ( int thresholdIndex = 0; thresholdIndex < thresholds.size(); thresholdIndex++ ) {
//            Iterator<Integer> iterator = thresholds.iterator();
//            while ( iterator.hasNext() ) {
//                Integer threshold = iterator.next();
                cum += entry.getKey().getThresholdProbability(thresholdIndex);
                str += "{" + thresholdIndex + ", " + cum + "}";
                if ( thresholdIndex < thresholds.size() - 1 ) {
                    str += ",";
                }
            }
            str += "}";
            if ( iterator1.hasNext() ) {
                str += ",";
            }
        }
        str += "}";
        return str;
    }

    public int getNumberOfStates() {
        return indexToState.size();
    }

    public String strategyToString(double[] minimalFPBelief) {
        String str = "";
        for ( int i=0; i<minimalFPBelief.length; i++ ) {
            if ( minimalFPBelief[i] > 0 ) {
                str += indexToState.get(i).getLeft().getId() + ", " + indexToState.get(i).getRight() + " prb " + (minimalFPBelief[i] / prior.getProbabilityDistribution().get(indexToState.get(i).getLeft())) + "\n";
            }
        }
        return str;
    }
    public void updatePrior(double[] minimalFPBelief) {
        String str = "";
//        prior.getProbabilityDistribution().keySet().stream().forEach(a -> a.getThresholdProbabilities().clear());
//        Map<Integer, Double> map = new HashMap<>();
        Map<UserTypeI, Map<Integer, Double>> map = new HashMap<>();
        for ( int i=0; i<minimalFPBelief.length; i++ ) {
            if ( minimalFPBelief[i] > 0 ) {
                if ( !map.containsKey(indexToState.get(i).getLeft()) ) {
                    map.put(indexToState.get(i).getLeft(), new HashMap<Integer, Double>());
                }
                map.get(indexToState.get(i).getLeft()).put(getDefendersThresholdActionInverse(indexToState.get(i).getRight()), (minimalFPBelief[i] / prior.getProbabilityDistribution().get(indexToState.get(i).getLeft())));
            }
        }
        for (UserTypeI userTypeI : prior.getProbabilityDistribution().keySet()) {
            if ( map.get(userTypeI) != null ) {
                userTypeI.updateThresholdPDF(map.get(userTypeI));
            }
        }
    }

    public String attackersUtilityForEachActionStringEach() {

        NumberFormat formatter = new DecimalFormat("###.#####");
//        String f = formatter.format(d)

        List<String> strings = new ArrayList<>();
        for (Map.Entry<UserTypeI, Double> userTypeEntry : prior.getProbabilityDistribution().entrySet()) {
            List<String> strings2 = new ArrayList<>();
            strings2.add(userTypeEntry.getKey().getId());
            for ( int action = 0; action < thresholds.size(); action++ ) {
                double value = 0;

                for (int threshold = 0; threshold < thresholds.size(); threshold++ ) {
//                for (Integer threshold : thresholds) {
                    double prb = userTypeEntry.getKey().probabilityOfNotDetectingActionForThreshold(action, threshold, IS_ADDITIVE);
                    double util = thresholds.get(action) * userTypeEntry.getValue() * userTypeEntry.getKey().getThresholdProbability(threshold) * prb / ( 1 - prb * discount);
                    value += util;
                }
//                strings2.add("{" + action + ", " + value/1024d/1024d + "}");
                strings2.add("{" + action + ", " + formatter.format(value/1024d/1024d) + "}");
            }
            strings.add("{" + strings2.stream().collect(Collectors.joining(",")) + "}");
        }
        return "{" + strings.stream().collect(Collectors.joining(",")) + "}";
    }

    public String attackersUtilityForEachActionString() {
        List<String> strings = new ArrayList<>();
        for ( int action = 0; action < thresholds.size(); action++ ) {
//        for (Integer action : actions) {
            double value = 0;

            for (Map.Entry<UserTypeI, Double> userTypeEntry : prior.getProbabilityDistribution().entrySet()) {
                for (int threshold = 0; threshold < thresholds.size(); threshold++ ) {
//                for (Integer threshold : thresholds) {
                    double prb = userTypeEntry.getKey().probabilityOfNotDetectingActionForThreshold(action, threshold, IS_ADDITIVE);
                    double util = thresholds.get(action) * userTypeEntry.getValue() * userTypeEntry.getKey().getThresholdProbability(threshold) * prb / ( 1 - prb * discount);
                    value += util;
                }
            }
            strings.add("{" + action + ", " + value + "}");
        }
        return "{" + strings.stream().collect(Collectors.joining(",")) + "}";
    }

    public double insiderBR(){
        double totalUtility = 0;
        for (Map.Entry<UserTypeI, Double> userTypeEntry : prior.getProbabilityDistribution().entrySet()) {
            double bestUtilityAgainstThisType = -1;
            Integer bestAction = -1;
            System.out.println("Type " + userTypeEntry.getKey().getId());

            for ( int action = 0; action < thresholds.size(); action++ ) {
//            for (Integer action : actions) {
                double expUtilityForAction = 0 ;
                for ( int threshold = 0; threshold < thresholds.size(); threshold++ ) {
//                for (Integer threshold : thresholds) {
                    double prb = userTypeEntry.getKey().probabilityOfNotDetectingActionForThreshold(action, threshold, IS_ADDITIVE);
                    double util = thresholds.get(action) * userTypeEntry.getKey().getThresholdProbability(threshold) * prb / (1 - prb * discount);
                    expUtilityForAction += util;
                }
                if ( bestUtilityAgainstThisType < expUtilityForAction ) {
                    bestUtilityAgainstThisType = expUtilityForAction;
                    bestAction = action;
                }
                System.out.println("{"+action+","+expUtilityForAction+"},");
            }

            System.out.println(userTypeEntry.getKey().getId() + " action " + bestAction + " util " + (bestUtilityAgainstThisType * prior.getProbabilityDistribution().get(userTypeEntry.getKey())));
            totalUtility += bestUtilityAgainstThisType * userTypeEntry.getValue();
        }
        return totalUtility;
    }

    public String insiderBRForMath(){
        Map<String, Integer> stg = new HashMap<>();
        for (Map.Entry<UserTypeI, Double> userTypeEntry : prior.getProbabilityDistribution().entrySet()) {
            double bestUtilityAgainstThisType = 0;

            int bestAttack = 0;
            for ( int actionIndex = 0; actionIndex < thresholds.size(); actionIndex++ ) {
//            for (Integer action : actions) {
                double expUtilityForAction = 0 ;
                for ( int thresholdIndex = 0; thresholdIndex < thresholds.size(); thresholdIndex++ ) {
//                for (Integer threshold : thresholds) {
                    double prb = userTypeEntry.getKey().probabilityOfNotDetectingActionForThreshold(actionIndex, thresholdIndex, IS_ADDITIVE);
                    double util = actionIndex * userTypeEntry.getKey().getThresholdProbability(thresholdIndex) * prb / (1 - prb * discount);
                    expUtilityForAction += util;
                }
                if ( bestUtilityAgainstThisType < expUtilityForAction ) {
                    bestUtilityAgainstThisType = expUtilityForAction;
                    bestAttack = actionIndex;
                }
            }
            stg.put(userTypeEntry.getKey().getId(), bestAttack);
        }
        String collect = prior.getProbabilityDistribution().keySet().stream()
                .map(def -> "{" + def.getId() + "," + IntStream.range(0, thresholds.size())
                        .mapToObj(t -> "{" + t + ", " + (stg.get(def.getId()) == t ? "1.0" : "0.0") + "}")
                        .collect(Collectors.joining(",")) + "}")
                .collect(Collectors.joining(","));
        return "{" + collect + "}";
    }

    public void setUniformThreshold() {
        for ( int i=0; i<thresholds.size(); i++ ) {
            double v = this.computeFPForThreshold(i);
            if ( v < FP ) {
                this.setAllUsersThresholdTo(i);
                break;
            }
        }
    }

    private void setAllUsersThresholdTo(int threshold) {
        for (UserTypeI userTypeI : prior.getProbabilityDistribution().keySet()) {
            userTypeI.setThresholdTo(threshold);
        }
    }


    private double computeFPForThreshold(int thresh) {
        double fp = 0;
        for (UserTypeI userTypeI : prior.getProbabilityDistribution().keySet()) {
            fp += userTypeI.getFalsePositiveForThreshold(thresh) * prior.getProbabilityDistribution().get(userTypeI);
        }
        return fp;
    }

    public void setUniformQuantileProbabilistic() {
        nextUser: for (UserTypeI userTypeI : prior.getProbabilityDistribution().keySet()) {
            for (int i = 0; i < thresholds.size(); i++) {
                double fp = userTypeI.getFalsePositiveForThreshold(i);
                if (fp < FP) {
                    double fp1 = userTypeI.getFalsePositiveForThreshold(i-1);
                    double p = (FP - fp1) / (fp - fp1);
//                    userTypeI.setThresholdTo(i);
                    Map<Integer, Double> map = new HashMap<>();
                    map.put(i-1, 1-p);
                    map.put(i, p);

                    userTypeI.setThresholdTo(map);

                    // remaining set to the previous threshold

                    continue nextUser;
                }
            }
        }
    }

    public void setUniformThresholdProbabilistic() {
        for ( int i=0; i<thresholds.size(); i++ ) {
            double fp = this.computeFPForThreshold(i);
            if ( fp < FP ) {
                double fp1 = this.computeFPForThreshold(i-1);
                double p = (FP - fp1) / (fp - fp1);
                Map<Integer, Double> map = new HashMap<>();
                map.put(i-1, 1-p);
                map.put(i, p);

                nextUser: for (UserTypeI userTypeI : prior.getProbabilityDistribution().keySet()) {
                    userTypeI.setThresholdTo(map);
                }
                return;
            }
        }

    }

}
