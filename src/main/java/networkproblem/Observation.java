package networkproblem;

public class Observation {

    public enum ObservationType {
        NOTHING("nothing"),
        REAL("real"),
        HONEYPOT("honeypot");

        private final String text;

        ObservationType(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
