package com.saltsociety.kenyaku;

public class DepositComputation {

    public boolean checkIfDepositIsEnough(double requiredPerDay, double amountToDeposit) {
        return amountToDeposit > requiredPerDay;
    }

    public int daysToFinish(double amountDeposit, double requiredPerDay) {
        return (int) (amountDeposit / requiredPerDay);
    }

    public double recalculateRequiredPerDay(double targetMoney, int daysToFinish) {
        return (targetMoney / daysToFinish);
    }

    public boolean isGoalFinished(double targetMoney, double currentSavings) {
        return (targetMoney <= currentSavings);
    }
}
