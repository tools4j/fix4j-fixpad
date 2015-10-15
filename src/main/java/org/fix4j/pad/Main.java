package org.fix4j.pad;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Injector.setModelOrService(HostServices.class, getHostServices());
        final FixpadView mainView = new FixpadView();
        final Scene scene = new Scene( mainView.getView() );
        primaryStage.setTitle( "FixPad" );
        primaryStage.setScene( scene );
        primaryStage.show();
    }
}
