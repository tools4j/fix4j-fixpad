package org.fix4j.pad;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.fix4j.spec.fix50sp2.FixSpec;
import org.fix4j.test.fixmodel.FixMessage;
import org.fix4j.test.fixspec.FixSpecification;
import org.fix4j.test.properties.PropertyKeysAndDefaultValues;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

/**
 * User: ben
 * Date: 8/01/15
 * Time: 7:14 AM
 */
public class PadController implements Initializable {
    @FXML private TextArea textAreaFrom;
    @FXML private TextArea textAreaTo;
    @FXML private MenuBar menuBar;

    private FixSpecification fixSpecification;

    @Override
    public void initialize(final URL url, final ResourceBundle resourceBundle) {
        fixSpecification = FixSpec.INSTANCE;
        textAreaFrom.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                textAreaTo.setText(convertInput(newValue));
            }
        });
    }

    public String convertInput(final String textFrom) {
        if(textFrom == null || textFrom.trim().length() == 0){
            return "";
        }
        try {
            final StringBuilder sb = new StringBuilder();
            final String[] lines = textFrom.split("\n\r?");
            for(int i=0; i<lines.length; i++){
                final String line = lines[i];
                try {
                    final FixMessage message = fixSpecification.parse(line);
                    sb.append(message.toPrettyString());
                } catch (Exception e) {
                    throw new RuntimeException("Error on line: " + (i+1), e);
                }
            }
            return sb.toString();

        } catch(Throwable e){
            String errorMessage = e.getMessage();
            while(e.getCause() != null && e.getCause() != e){
                e = e.getCause();
                errorMessage = errorMessage + "\n" + e.getMessage();
            }
            return errorMessage;
        }
    }

    public void handleFixpadMenuClick(ActionEvent actionEvent) throws IOException {
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Text Input Dialog");
        dialog.setHeaderText("Look, a Text Input Dialog");
        dialog.setContentText("Please enter your name:");

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            System.out.println("Your name: " + result.get());
        }

        // The Java 8 way to get the response value (with lambda expression).
        result.ifPresent(name -> System.out.println("Your name: " + name));
    }

    private String getDelimiter(){
        Preferences preferences = Preferences.userRoot().node("java-buddy");

        final String fixDelimiter = preferences.get("fixpad.fixFieldDelimiter", "");//PropertyKeysAndDefaultValues.FAST_FAIL_ON_TRIGGER_OF_INCOMING_MESSAGE_FLAG);
//        int prefInt = preferences.getInt(KeyInt, 0);
//        boolean prefBoolean = preferences.getBoolean(KeyBoolean, false);
//
//        System.out.println("Stored KEY_WEB: " + prefWeb);
//        System.out.println("Stored KEY_INT: " + prefInt);
//        System.out.println("Stored KEY_BOOLEAN: " + prefBoolean);
//
//        System.out.println("Save something to Preferences");
//        preferences.put(KeyWeb, "http://java-buddy.blogspot.com/");
//        preferences.putInt(KeyInt, 1234567890);
//        preferences.putBoolean(KeyBoolean, true);
        return fixDelimiter;
    }
}
