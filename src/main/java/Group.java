public class Group {
    private String groupName;

    public Group(String groupName) {
        setGroupName(groupName);
    }

    private void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public String getGroupName() {
        return groupName;
    }

    public String toString() {
        return this.getGroupName();
    }
}
