public class Member {
    private int memberId;
    private String indexNumber;
    private String name;
    private String membershipType;
    private String registeredDate;

    public Member(int memberId, String indexNumber, String name, String membershipType, String registeredDate) {
        this.memberId = memberId;
        this.indexNumber = indexNumber;
        this.name = name;
        this.membershipType = membershipType;
        this.registeredDate = registeredDate;
    }

    public int getMemberId() { return memberId; }
    public String getIndexNumber() { return indexNumber; }
    public String getName() { return name; }
    public String getMembershipType() { return membershipType; }
    public String getRegisteredDate() { return registeredDate; }

    @Override
    public String toString() {
        return "Member{id=" + memberId + ", name='" + name + "', type='" + membershipType + "'}";
    }
}
