package com.saltsociety.kenyaku;

public class GoalItem {

    private String goalId;
    private int goalTypeIcon;
    private String goalName;
    private String dateGoalCreated;

    public GoalItem(String goalId, int goalTypeIcon, String goalName, String dateGoalCreated) {
        this.goalId = goalId;
        this.goalTypeIcon = goalTypeIcon;
        this.goalName = goalName;
        this.dateGoalCreated = dateGoalCreated;
    }

    public String getGoalId() {
        return
                goalId;
    }

    public int getGoalTypeIcon() {
        return goalTypeIcon;
    }

    public void setGoalTypeIcon(int goalTypeIcon) {
        this.goalTypeIcon = goalTypeIcon;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getDateGoalCreated() {
        return dateGoalCreated;
    }

    public void setDateGoalCreated(String dateGoalCreated) {
        this.dateGoalCreated = dateGoalCreated;
    }
}
