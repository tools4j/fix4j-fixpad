package org.fix4j.pad;

import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import org.fix4j.spec.fix50sp2.FixSpec;
import org.fix4j.test.fixmodel.FixMessage;
import org.fix4j.test.fixspec.FixSpecification;

import javax.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * User: ben
 * Date: 8/01/15
 * Time: 7:14 AM
 */
public class FixpadPresenter implements Initializable {
    public static final String EXAMPLE_NOS = "35=D|38=1000|59=1|100=N|40=1|11=ORD10001|60=20070123-19:01:17|55=AUD/USD|54=1|";
    public static final String EXAMPLE_MDR = "35=V|262=request123|263=0|264=20|267=2|269=0|269=1|146=1|55=AUD/USD|";
    public static final String EXAMPLE_PRICE = "35=X|262=ABCD|268=4|279=0|269=0|55=AUD/USD|270=1.12345|279=0|269=1|55=AUD/USD|270=1.12355|279=0|269=0|55=AUD/USD|270=1.12335|279=0|269=1|55=AUD/USD|270=1.12365|";
    public static final String EXAMPLE_EXEC_REPORT = "35=8|55=CVS|37=ORD10001/03232009|11=ORD10001|17=12345678|150=3|39=2|150=2|54=1|38=1000|40=1|59=1|31=1.12355|32=1000|14=0|6=0|151=0|60=20070123-19:01:17|58=Fill|30=N|207=N|63=0|";
    @FXML private TextArea textAreaFrom;
    @FXML private TextArea textAreaTo;
    @FXML private MenuBar menuBar;
    @Inject private HostServices hostServices;

    public final static String PROMPT = "Put your FIX here...";
    private FixSpecification fixSpecification;
    private FixpadPreferences preferences = new FixpadPreferences();

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        fixSpecification = FixSpec.INSTANCE;
        textAreaFrom.setWrapText(preferences.getIsWordWrap());
        textAreaFrom.setText(PROMPT);
        textAreaFrom.selectAll();
        textAreaFrom.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                convertInput();
            }
        });
    }

    public String convertInput(final String textFrom) {
        if(textFrom == null || textFrom.trim().length() == 0 || textFrom.trim().equals(PROMPT)){
            return "";
        }
        try {
            final StringBuilder sb = new StringBuilder();
            final String[] lines = textFrom.split("\n\r?");
            for(int i=0; i<lines.length; i++){
                final String line = lines[i];
                if(line.isEmpty()){
                    sb.append("\n");
                } else {
                    try {
                        final FixMessage message = fixSpecification.parseRawFix(line);
                        final String messageStr = message.toPrettyString();
                        sb.append(messageStr);
                    } catch (Exception e) {
                        throw new RuntimeException("Error on line: " + (i + 1), e);
                    }
                }
            }
            return sb.toString();

        } catch(Throwable e){
            String errorMessage = e.getMessage();
            while(e.getCause() != null && e.getCause() != e){
                e = e.getCause();
                errorMessage = errorMessage + "\n" + e.getMessage();
            }
            e.printStackTrace();
            log(errorMessage);
            return errorMessage;
        }
    }

    private void log(final String errorMessage) {
        System.out.println(errorMessage);
    }

    public void handleMenuClick(ActionEvent actionEvent) throws IOException {
        if(actionEvent.getEventType().equals(ActionEvent.ACTION)
            && actionEvent.getTarget() instanceof MenuItem) {

            final MenuItem menuItem = (MenuItem) actionEvent.getTarget();
            final String menuId = menuItem.getId();

            if (menuId.equals("setFixDelim")) {
                handleMenuClick_setFixDelim(actionEvent);

            } else if (menuId.equals("toggleWrap")) {
                toggleWordWrap();

            } else if (menuId.equals("exampleNos")) {
                textAreaFrom.setText(EXAMPLE_NOS);

            } else if (menuId.equals("exampleMdr")) {
                textAreaFrom.setText(EXAMPLE_MDR);

            } else if (menuId.equals("examplePrice")) {
                textAreaFrom.setText(EXAMPLE_PRICE);

            } else if (menuId.equals("exampleExecReport")) {
                textAreaFrom.setText(EXAMPLE_EXEC_REPORT);

            } else if (menuId.equals("about")) {
                AboutDialog aboutDialog = new AboutDialog(hostServices);
                aboutDialog.showAndWait();

            } else if (menuId.equals("exit")) {
                Platform.exit();

            } else {
                throw new UnsupportedOperationException("When clicking on menu with id:" + menuId);
            }
        }
    }

    private void handleMenuClick_setFixDelim(final ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog(preferences.getFixDelimiter());
        dialog.setTitle("Fix Delimiter");
        dialog.setHeaderText("Please enter a fix delimiter");
        dialog.setContentText("Delimiter must be a valid regex:");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("fixpad.css").toExternalForm());

        final ButtonType defaultButtonType = new ButtonType("Reset");
        final ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        final ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(defaultButtonType, okButtonType, cancelButtonType);

        final Node defaultButton = dialog.getDialogPane().lookupButton(defaultButtonType);
        final Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        final Node cancelButton = dialog.getDialogPane().lookupButton(cancelButtonType);

        defaultButton.setId("dialogResetButton");
        okButton.setId("dialogOkButton");
        cancelButton.setId("dialogCancelButton");

        final TextField textField = dialog.getEditor();

        setDialogStateDependingOnRegexValidity(dialog, okButton, textField.getText().trim());

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            setDialogStateDependingOnRegexValidity(dialog, okButton, newValue.trim());
        });

        defaultButton.addEventFilter(EventType.ROOT, e -> {
            if (e.getEventType().equals(ActionEvent.ACTION)) {
                e.consume();
                final String defaultDelim = preferences.getDefaultDelimiter();
                textField.setText(defaultDelim);
                preferences.setFixDelimiter(defaultDelim);
                setDialogStateDependingOnRegexValidity(dialog, okButton, textField.getText().trim());
            }
        });

        okButton.addEventFilter(EventType.ROOT, e -> {
            if (e.getEventType().equals(ActionEvent.ACTION)) {
                preferences.setFixDelimiter(textField.getText().trim());
                convertInput();
            }
        });

        dialog.show();
    }

    private void convertInput() {
        textAreaTo.setText(convertInput(textAreaFrom.getText()));
    }

    private void setDialogStateDependingOnRegexValidity(final TextInputDialog dialog, final Node okButton, final String delim) {
        final String regexCompileError = getRegexCompileError(delim);
        if(regexCompileError != null){
            okButton.setDisable(true);
            dialog.setContentText("Not a valid regex!! [" + regexCompileError + "]");
        } else {
            okButton.setDisable(false);
            dialog.setContentText("Regex good");
        }
    }

    private String getRegexCompileError(final String delim) {
        try {
            Pattern.compile(delim);
            return null;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private void toggleWordWrap(){
        final boolean newWordWrap = !preferences.getIsWordWrap();
        preferences.setWordWrap(newWordWrap);
        textAreaFrom.setWrapText(newWordWrap);
    }
}
