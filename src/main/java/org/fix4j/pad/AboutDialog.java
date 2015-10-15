package org.fix4j.pad;

/**
 * User: ben
 * Date: 12/10/15
 * Time: 5:42 PM
 */

import javafx.application.HostServices;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


public class AboutDialog extends Dialog<ButtonType> {

    private final HostServices hostServices;

    public AboutDialog(final HostServices hostServices) {
        super();
        this.hostServices = hostServices;

        final DialogPane dialogPane = getDialogPane();

        final StackPane stackPane = new StackPane();
        stackPane.setMaxWidth(Double.MAX_VALUE);
        stackPane.setAlignment(Pos.CENTER_LEFT);

        setTitle("About");
        dialogPane.getButtonTypes().addAll(ButtonType.OK);

        final TextFlow textFlow = new TextFlow();

        String family = "Arial";
        double size = 12;


        final Text text0 = new Text("FixPad - by Ben Warner\n");
        text0.setFont(Font.font(family, FontWeight.BOLD, FontPosture.REGULAR, 14));

        final Text text1 = new Text("\nA small app to convert snippets of fix into a more readable format. It is covered by the GNU GENERAL PUBLIC LICENSE (free to use commercially and non-commercially).  Find it on github at:\n");
        text1.setFont(Font.font(family, size));

        final Hyperlink text2 = new Hyperlink("https://github.com/fix4j/fix4j-fixpad");
        text2.setFont(Font.font(family, FontWeight.BOLD, size));
        text2.setOnAction(t -> { this.hostServices.showDocument(text2.getText()); });

        final Text text3 = new Text("\n\nFixPad uses the fix4j-assert API to build the readable strings.  fix4j-assert is a Java based framework for testing FIX applications.  Find it at:\n");
        text1.setFont(Font.font(family, size));

        final Hyperlink text4 = new Hyperlink("https://github.com/fix4j/fix4j-assert");
        text4.setFont(Font.font(family, FontWeight.BOLD, size));
        text4.setOnAction(t -> {
            this.hostServices.showDocument(text4.getText());
        });

        final Text text5 = new Text("\n\nCheck out github for some more of our software efforts:\n");
        text1.setFont(Font.font(family, size));

        final Text lineBreak1 = new Text("\n");

        final Hyperlink text6 = new Hyperlink("https://github.com/tools4j/decimal4j");
        text6.setFont(Font.font(family, FontWeight.BOLD, size));
        text6.setOnAction(t -> {
            this.hostServices.showDocument(text6.getText());
        });

        final Text lineBreak2 = new Text("\n");

        final Hyperlink text7 = new Hyperlink("https://github.com/tools4j/unix4j");
        text7.setFont(Font.font(family, FontWeight.BOLD, size));
        text7.setOnAction(t -> {
            this.hostServices.showDocument(text7.getText());
        });

        final Hyperlink text8 = new Hyperlink("https://github.com/tools4j/meanvar");
        text8.setFont(Font.font(family, FontWeight.BOLD, size));
        text8.setOnAction(t -> {
            this.hostServices.showDocument(text8.getText());
        });

        textFlow.getChildren().addAll(text0, text1, text2, text3, text4, text5, text6, lineBreak1, text7, lineBreak2, text8);
        textFlow.setPrefWidth(400);

        stackPane.getChildren().clear();
        stackPane.getChildren().add(textFlow);
        getDialogPane().setContent(stackPane);
    }
}

