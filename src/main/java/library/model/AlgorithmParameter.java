package library.model;

/**
 * Mirrors the `algorithm_parameters` table. Anti-plagiarism traceability:
 * every algorithm parameter derived from a student index number, with the
 * formula spelled out so it is defensible verbally at the oral exam.
 */
public class AlgorithmParameter {
    public int paramId;
    public String memberIndexNumber;
    public String paramName;      // e.g. 'hash_table_size', 'priority_weight', 'route_penalty'
    public double derivedValue;
    public String derivationNote; // e.g. 'sum of last 4 digits mod 97'

    public AlgorithmParameter() { }

    public AlgorithmParameter(int paramId, String memberIndexNumber, String paramName,
                               double derivedValue, String derivationNote) {
        this.paramId = paramId;
        this.memberIndexNumber = memberIndexNumber;
        this.paramName = paramName;
        this.derivedValue = derivedValue;
        this.derivationNote = derivationNote;
    }

    @Override
    public String toString() {
        return "AlgorithmParameter{member=" + memberIndexNumber + ", name=" + paramName +
               ", value=" + derivedValue + ", note='" + derivationNote + "'}";
    }
}
