package systemtests;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.DATE_DESC_EXPENSE;
import static seedu.address.logic.commands.CommandTestUtil.DEADLINE_DESC_DEBT;
import static seedu.address.logic.commands.CommandTestUtil.CATEGORY_DESC_EXPENSE;
import static seedu.address.logic.commands.CommandTestUtil.CATEGORY_DESC_DEBT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_CATEGORY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_AMOUNT_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DEADLINE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_EXPENSE;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_DEBT;
import static seedu.address.logic.commands.CommandTestUtil.AMOUNT_DESC_EXPENSE;
import static seedu.address.logic.commands.CommandTestUtil.AMOUNT_DESC_DEBT;
import static seedu.address.logic.commands.CommandTestUtil.REMARKS_DESC_EXPENSE;
import static seedu.address.logic.commands.CommandTestUtil.REMARKS_DESC_DEBT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_CATEGORY_DEBT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_DEBT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_AMOUNT_DEBT;
import static seedu.address.testutil.TypicalExpenses.DUCK_RICE;
import static seedu.address.testutil.TypicalExpenses.EXPENSE;
import static seedu.address.testutil.TypicalExpenses.BOB;
import static seedu.address.testutil.TypicalExpenses.GROCERIES;
import static seedu.address.testutil.TypicalExpenses.JAPAN;
import static seedu.address.testutil.TypicalExpenses.STOCKS;
import static seedu.address.testutil.TypicalExpenses.KEYWORD_MATCHING_CHICKEN;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.attributes.Address;
import seedu.address.model.attributes.Email;
import seedu.address.model.attributes.Name;
import seedu.address.model.expense.Expense;
import seedu.address.model.attributes.Amount;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.ExpenseBuilder;
import seedu.address.testutil.ExpenseUtil;

public class AddCommandSystemTest extends FinanceTrackerSystemTest {

    @Test
    public void add() {
        Model model = getModel();

        /* ------------------------ Perform add operations on the shown unfiltered list ----------------------------- */

        /* Case: add a expense without tags to a non-empty address book, command with leading spaces and trailing spaces
         * -> added
         */
        Expense toAdd = EXPENSE;
        String command = "   " + AddCommand.COMMAND_WORD + "  " + NAME_DESC_EXPENSE + "  " + AMOUNT_DESC_EXPENSE + " "
                + CATEGORY_DESC_EXPENSE + "   " + DATE_DESC_EXPENSE + "   " + REMARKS_DESC_EXPENSE + " ";
        assertCommandSuccess(command, toAdd);

        /* Case: undo adding Amy to the list -> Amy deleted */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo adding Amy to the list -> Amy added again */
        command = RedoCommand.COMMAND_WORD;
        model.addExpense(toAdd);
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: add a expense with all fields same as another expense in the address book except name -> added */
        toAdd = new ExpenseBuilder(EXPENSE).withName(VALID_NAME_DEBT).build();
        command = AddCommand.COMMAND_WORD + NAME_DESC_DEBT + AMOUNT_DESC_EXPENSE + CATEGORY_DESC_EXPENSE + DATE_DESC_EXPENSE
                + REMARKS_DESC_EXPENSE;
        assertCommandSuccess(command, toAdd);

        /* Case: add a expense with all fields same as another expense in the address book except phone and email
         * -> added
         */
        toAdd = new ExpenseBuilder(EXPENSE).withAmount(VALID_AMOUNT_DEBT).withDate(VALID_CATEGORY_DEBT).build();
        command = ExpenseUtil.getAddCommand(toAdd);
        assertCommandSuccess(command, toAdd);

        /* Case: add to empty address book -> added */
        deleteAllPersons();
        assertCommandSuccess(DUCK_RICE);

        /* Case: add a expense with tags, command with parameters in random order -> added */
        toAdd = BOB;
        command = AddCommand.COMMAND_WORD + REMARKS_DESC_EXPENSE + AMOUNT_DESC_DEBT + DEADLINE_DESC_DEBT + NAME_DESC_DEBT
                + REMARKS_DESC_DEBT + CATEGORY_DESC_DEBT;
        assertCommandSuccess(command, toAdd);

        /* Case: add a expense, missing tags -> added */
        assertCommandSuccess(JAPAN);

        /* -------------------------- Perform add operation on the shown filtered list ------------------------------ */

        /* Case: filters the expense list before adding -> added */
        showPersonsWithName(KEYWORD_MATCHING_CHICKEN);
        assertCommandSuccess(STOCKS);

        /* ------------------------ Perform add operation while a expense card is selected --------------------------- */

        /* Case: selects first card in the expense list, add a expense -> added, card selection remains unchanged */
        selectPerson(Index.fromOneBased(1));
        assertCommandSuccess(GROCERIES);

        /* ----------------------------------- Perform invalid add operations --------------------------------------- */

        /* Case: missing name -> rejected */
        command = AddCommand.COMMAND_WORD + AMOUNT_DESC_EXPENSE + CATEGORY_DESC_EXPENSE + DATE_DESC_EXPENSE;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing phone -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_EXPENSE + CATEGORY_DESC_EXPENSE + DATE_DESC_EXPENSE;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing email -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_EXPENSE + AMOUNT_DESC_EXPENSE + DATE_DESC_EXPENSE;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: missing address -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_EXPENSE + AMOUNT_DESC_EXPENSE + CATEGORY_DESC_EXPENSE;
        assertCommandFailure(command, String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

        /* Case: invalid keyword -> rejected */
        command = "adds " + ExpenseUtil.getExpenseDetails(toAdd);
        assertCommandFailure(command, Messages.MESSAGE_UNKNOWN_COMMAND);

        /* Case: invalid name -> rejected */
        command = AddCommand.COMMAND_WORD + INVALID_NAME_DESC + AMOUNT_DESC_EXPENSE + CATEGORY_DESC_EXPENSE + DATE_DESC_EXPENSE;
        assertCommandFailure(command, Name.MESSAGE_CONSTRAINTS);

        /* Case: invalid phone -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_EXPENSE + INVALID_AMOUNT_DESC + CATEGORY_DESC_EXPENSE + DATE_DESC_EXPENSE;
        assertCommandFailure(command, Amount.MESSAGE_CONSTRAINTS);

        /* Case: invalid email -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_EXPENSE + AMOUNT_DESC_EXPENSE + INVALID_CATEGORY_DESC + DATE_DESC_EXPENSE;
        assertCommandFailure(command, Email.MESSAGE_CONSTRAINTS);

        /* Case: invalid address -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_EXPENSE + AMOUNT_DESC_EXPENSE + CATEGORY_DESC_EXPENSE + INVALID_DATE_DESC;
        assertCommandFailure(command, Address.MESSAGE_CONSTRAINTS);

        /* Case: invalid tag -> rejected */
        command = AddCommand.COMMAND_WORD + NAME_DESC_EXPENSE + AMOUNT_DESC_EXPENSE + CATEGORY_DESC_EXPENSE + DATE_DESC_EXPENSE
                + INVALID_DEADLINE_DESC;
        assertCommandFailure(command, Tag.MESSAGE_CONSTRAINTS);
    }

    /**
     * Executes the {@code AddCommand} that adds {@code toAdd} to the model and asserts that the,<br>
     * 1. Command box displays an empty string.<br>
     * 2. Command box has the default style class.<br>
     * 3. Result display box displays the success message of executing {@code AddCommand} with the details of
     * {@code toAdd}.<br>
     * 4. {@code Storage} and {@code PersonListPanel} equal to the corresponding components in
     * the current model added with {@code toAdd}.<br>
     * 5. Browser url and selected card remain unchanged.<br>
     * 6. Status bar's sync status changes.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code FinanceTrackerSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see FinanceTrackerSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandSuccess(Expense toAdd) {
        assertCommandSuccess(ExpenseUtil.getAddCommand(toAdd), toAdd);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(Expense)}. Executes {@code command}
     * instead.
     * @see AddCommandSystemTest#assertCommandSuccess(Expense)
     */
    private void assertCommandSuccess(String command, Expense toAdd) {
        Model expectedModel = getModel();
        expectedModel.addExpense(toAdd);
        String expectedResultMessage = String.format(AddCommand.MESSAGE_SUCCESS, toAdd);

        assertCommandSuccess(command, expectedModel, expectedResultMessage);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Expense)} except asserts that
     * the,<br>
     * 1. Result display box displays {@code expectedResultMessage}.<br>
     * 2. {@code Storage} and {@code PersonListPanel} equal to the corresponding components in
     * {@code expectedModel}.<br>
     * @see AddCommandSystemTest#assertCommandSuccess(String, Expense)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and asserts that the,<br>
     * 1. Command box displays {@code command}.<br>
     * 2. Command box has the error style class.<br>
     * 3. Result display box displays {@code expectedResultMessage}.<br>
     * 4. {@code Storage} and {@code PersonListPanel} remain unchanged.<br>
     * 5. Browser url, selected card and status bar remain unchanged.<br>
     * Verifications 1, 3 and 4 are performed by
     * {@code FinanceTrackerSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see FinanceTrackerSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }
}
