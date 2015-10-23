package org.fix4j.pad;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.fix4j.test.properties.PropertyKeysAndDefaultValues;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import java.io.IOException;
import java.io.InputStream;

import static org.fix4j.pad.ContainsStringMatcher.containsText;
import static org.fix4j.pad.NotContainsStringMatcher.doesNotContainText;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.loadui.testfx.Assertions.verifyThat;
import static org.loadui.testfx.controls.Commons.hasText;

/**
 * User: ben
 * Date: 20/10/15
 * Time: 5:29 PM
 */

public class FixpadTest extends GuiTest {

    @Override
    protected Parent getRootNode() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        try (InputStream inputStream = getClass().getResourceAsStream("fixpad.fxml")) {
            return wrapInContainer(fxmlLoader.load(inputStream));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * https://github.com/TestFX/TestFX/issues/136
     * This is to push the app away from the top left of the screen so that the
     * test does not interfere with the MacOS system menu.
     */
    protected final Parent wrapInContainer(final Parent root) {
        final BorderPane container = new BorderPane();
        container.setCenter(root);
        container.setPadding(new Insets(50, 0, 0, 50));
        return container;
    }

    private FixpadPreferences preferences = new FixpadPreferences();

    @Before
    public void setup(){
        preferences.resetDefaultFixDelimiter();
    }


    @Test
    public void testThatPromptIsPopulatedOnStartup() {
        verifyThat("#textAreaFrom", hasText(FixpadPresenter.PROMPT));
    }

    @Test
    public void testErrorMessages() {
        final TextArea textAreaFrom = find("#textAreaFrom");
        textAreaFrom.setText("blah");
        verifyThat("#textAreaTo", containsText("Error on line:"));

        textAreaFrom.setText("35=D");
        verifyThat("#textAreaTo", containsText("NewOrderSingle"));

        textAreaFrom.setText("35=D^A38=");
        verifyThat("#textAreaTo", containsText("Error on line:"));

        textAreaFrom.setText("35=D^A38=1000");
        verifyThat("#textAreaTo", containsText("NewOrderSingle"));
    }

    @Test
    public void testFixDelimDialog_resetButton() {
        clickMenu("#setFixDelim");
        final TextField textField = find(".text-field");
        click("Reset");
        assertThat(textField.getText(), is(PropertyKeysAndDefaultValues.FIX_FIELD_DELIM.getDefaultValue()));
        click("OK");
    }

    @Test
    public void testFixDelimDialog_otherValue() {
        //Enter a snippet of fix which has a semicolon as a delimiter (semicolon is not a valid delimiter by default)
        final TextArea textAreaFrom = find("#textAreaFrom");
        textAreaFrom.setText("35=D;38=1000");
        verifyThat("#textAreaTo", containsText("Error on line: 1"));

        //Now set the delimiter to be a semicolon
        clickMenu("#setFixDelim");
        final TextField textField = find(".text-field");
        textField.setText(";");
        click("OK");

        //Verify that the fix has been properly parsed
        verifyThat("#textAreaTo", containsText("NewOrderSingle"));
    }

    @Test
    public void testToggleWordWrap() {
        preferences.setWordWrap(false);

        final TextArea textAreaFrom = find("#textAreaFrom");
        clickMenu("#exampleNos");
        verifyThat("#textAreaFrom", hasText(FixpadPresenter.EXAMPLE_NOS));
        clickMenu("#toggleWrap");
        assertThat(textAreaFrom.isWrapText(), is(true));

        clickMenu("#toggleWrap");
        assertThat(textAreaFrom.isWrapText(), is(false));

        clickMenu("#toggleWrap");
        assertThat(textAreaFrom.isWrapText(), is(true));
    }

    @Test
    public void testExamples() {
        clickMenu("#exampleNos");
        verifyThat("#textAreaFrom", hasText(FixpadPresenter.EXAMPLE_NOS));
        verifyThat("#textAreaTo", doesNotContainText("Error on line:"));

        clickMenu("#exampleMdr");
        verifyThat("#textAreaFrom", hasText(FixpadPresenter.EXAMPLE_MDR));
        verifyThat("#textAreaTo", doesNotContainText("Error on line:"));

        clickMenu("#examplePrice");
        verifyThat("#textAreaFrom", hasText(FixpadPresenter.EXAMPLE_PRICE));
        verifyThat("#textAreaTo", doesNotContainText("Error on line:"));

        clickMenu("#exampleExecReport");
        verifyThat("#textAreaFrom", hasText(FixpadPresenter.EXAMPLE_EXEC_REPORT));
        verifyThat("#textAreaTo", doesNotContainText("Error on line:"));
    }

    private void clickMenu(final String menuItem) {
        click("#menuBar");
        click("#fixpadMenu");
        click(menuItem);
    }
}