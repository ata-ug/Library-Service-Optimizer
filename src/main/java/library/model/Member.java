package library.model;

/** Mirrors the `members` table. indexNumber feeds the anti-plagiarism parameter derivation. */
public class Member {
    public int memberId;
    public String indexNumber;
    public String name;
    public String membershipType; // STUDENT | STAFF | FACULTY
    public String registeredDate;

    public Member() { }

    public Member(int memberId, String indexNumber, String name,
                  String membershipType, String registeredDate) {
        this.memberId = memberId;
        this.indexNumber = indexNumber;
        this.name = name;
        this.membershipType = membershipType;
        this.registeredDate = registeredDate;
    }

    @Override
    public String toString() {
        return "Member{id=" + memberId + ", index='" + indexNumber + "', name='" + name + "'}";
    }
}
