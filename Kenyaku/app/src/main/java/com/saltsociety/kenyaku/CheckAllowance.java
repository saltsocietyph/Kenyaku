package com.saltsociety.kenyaku;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;

public class CheckAllowance {

    private double allowancePerDay, foodExp, transpoExp, othersExp;
    private long daysWithAllowance;

    private double reqAllowancePerDay;
    private int daysToFinishGoal;

    public CheckAllowance(double allowancePerDay, long daysWithAllowance, double foodExp, double transpoExp, double othersExp) {
        this.allowancePerDay = allowancePerDay;
        this.daysWithAllowance = daysWithAllowance;
        this.foodExp = foodExp;
        this.transpoExp = transpoExp;
        this.othersExp = othersExp;
    }

    public boolean checkAllowance() {
        double expensesPerDay = foodExp + transpoExp + othersExp;

        boolean isCorrect = false;
        if(expensesPerDay < allowancePerDay)
            isCorrect = true;

        return isCorrect;
    }

    public double totalAllowancePerWeek() {
        return allowancePerDay * daysWithAllowance;
    }

    public double expensePerDay() {
        return foodExp + transpoExp + othersExp;
    }

    public double savePerDay() {
        return allowancePerDay - expensePerDay();
    }

    public double expensePerWeek() {
        return expensePerDay() * daysWithAllowance;
    }

    public double savePerWeek() {
        return (savePerDay() * daysWithAllowance);
    }

    public int daysToSave(DateTime startDate, DateTime endDate) {
        return Days.daysBetween(startDate, endDate).getDays();
    }

    public double requiredAllowancePerDay() {
        return reqAllowancePerDay;
    }

    public int daysToFinishGoal() {
        return daysToFinishGoal;
    }

    public boolean checkIfGoalIsFeasible(String goalType, double targetMoney, int daysToSave) {
        boolean isGoalFeasible = false;

        if(goalType.equals("Just Cash Goal")) {
            daysToFinishGoal = (int) (targetMoney / savePerDay()) + 1;
            Log.d("DEBUG", "" + daysToFinishGoal);
            isGoalFeasible = true;
        } else if(goalType.equals("Material Goal")) {
            reqAllowancePerDay = targetMoney / daysToSave;
            if(savePerDay() > reqAllowancePerDay)
                isGoalFeasible = true;
        } else if(goalType.equals("Event Goal")) {
            reqAllowancePerDay = targetMoney / daysToSave;
            if(savePerDay() > reqAllowancePerDay)
                isGoalFeasible = true;
        }

        return isGoalFeasible;
    }
}
