package com.saltsociety.kenyaku;

public class DepositItem {

    private String goalName;
    private String depositDate;
    private String amount;

    public DepositItem(String goalName, String depositDate, String amount) {
        this.goalName = goalName;
        this.depositDate = depositDate;
        this.amount = amount;
    }

    public String getGoalName() {
        return goalName;
    }

    public String getDepositDate() {
        return depositDate;
    }

    public String getAmount() {
        return amount;
    }
}
