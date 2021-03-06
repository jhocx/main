package seedu.address.model.statistics;

import java.util.ArrayList;

import javafx.collections.transformation.FilteredList;
import seedu.address.model.attributes.Category;
import seedu.address.model.attributes.Date;
import seedu.address.model.budget.Budget;
import seedu.address.model.debt.Debt;
import seedu.address.model.expense.Expense;
/**
 * Statistics
 */
public class Statistics {

    public static final int FOOD = 0;
    public static final int TRANSPORT = 1;
    public static final int SHOPPING = 2;
    public static final int WORK = 3;
    public static final int UTILITIES = 4;
    public static final int HEALTHCARE = 5;
    public static final int ENTERTAINMENT = 6;
    public static final int TRAVEL = 7;
    public static final int OTHERS = 8;
    public static final int ALL = 9;
    protected Date startDate;
    protected Date endDate;
    protected Category category;
    protected String tableHtml;

    /**
     * Every field except category must be present and not null.
     */
    public Statistics(Date startDate, Date endDate, Category category) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.category = category;
    }

    public Date getStartDate() {
        return startDate;
    }
    public Date getEndDate() {
        return endDate;
    }
    public String getHtmlTable() {
        return tableHtml;
    }

    /**
     * Calculates Statistics with data from model
     */
    public void calculateStats(FilteredList<Expense> statsExpenses, FilteredList<Debt> statsDebts,
                               FilteredList<Budget> statsBudgets) {

        boolean isCategoryNull = (this.category == null);
        ArrayList<ArrayList<Expense>> data = new ArrayList<>();
        for (int i = 0; i <= ALL; i++) {
            data.add(new ArrayList<Expense>());
        }

        for (Expense expense : statsExpenses) {
            Date date = expense.getDate();
            if (date.compareTo(startDate) != -1 && date.compareTo(endDate) != 1) {
                data.get(ALL).add(expense);
                int categoryInInteger = convertCategoryToInteger(expense.getCategory().toString());
                data.get(categoryInInteger).add(expense);
            }
        }

        for (Expense expense: data.get(TRANSPORT)) {
            System.out.println(expense.toString());
        }
        this.tableHtml = htmlTableBuilder(data);
    }

    /**
     * Converts String Category into Numerical Category
     */
    protected int convertCategoryToInteger(String categoryInString) {
        switch (categoryInString) {
        case "FOOD":
            return FOOD;
        case "TRANSPORT":
            return TRANSPORT;
        case "SHOPPING":
            return SHOPPING;
        case "WORK":
            return WORK;
        case "UTILITIES":
            return UTILITIES;
        case "HEALTHCARE":
            return HEALTHCARE;
        case "ENTERTAINMENT":
            return ENTERTAINMENT;
        case "TRAVEL":
            return TRAVEL;
        case "OTHERS":
            return OTHERS;
        default:
        }
        return -1;
    }

    /**
     * Builds table for display
     */
    private String htmlTableBuilder(ArrayList<ArrayList<Expense>> data) {
        String table = "";
        table = table + "<table style=\"width:100%\">\n"
                + "  <tr>\n"
                + "    <th>Category</th>\n"
                + "    <th>Amount Spent</th> \n"
                + "    <th>Entry Count</th>\n"
                + "    <th>Percentage of Total</th>\n"
                + "  </tr>\n";

        ArrayList<Expense> totalList = data.get(ALL);
        double totalSpent = 0;
        int totalCount = 0;
        for (Expense e : totalList) {
            totalSpent += e.getAmount().value;
            totalCount++;
        }
        for (int i = 0; i < ALL; i++) {
            ArrayList<Expense> list = data.get(i);
            if (!list.isEmpty()) {
                double categoryTotal = 0;
                int categoryCount = 0;
                double categoryPercentage;
                String categoryString = "";
                for (Expense e : list) {
                    categoryTotal += e.getAmount().value;
                    categoryCount++;
                    categoryString = e.getCategory().toString();
                }
                categoryPercentage = categoryTotal / totalSpent;
                table = table
                        + "   <tr>\n"
                        + "    <th>" + categoryString + "</th>\n"
                        + "    <th>$" + String.format("%.2f", categoryTotal / 100) + "</th> \n"
                        + "    <th>" + categoryCount + "</th>\n"
                        + "    <th>" + String.format("%.2f", categoryPercentage * 100) + " %</th>\n"
                        + "  </tr>\n";
            }
        }
        table = table + "   <tr>\n"
                + "    <th>Total</th>\n"
                + "    <th>$" + String.format("%.2f", totalSpent / 100) + "</th> \n"
                + "    <th>" + totalCount + "</th>\n"
                + "    <th>100%</th>\n"
                + "  </tr>\n";
        table = table + "</table>";
        return table;
    }

}
