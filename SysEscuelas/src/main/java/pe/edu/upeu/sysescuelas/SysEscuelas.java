package pe.edu.upeu.sysescuelas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pe.edu.upeu.sysescuelas.config.AppContext;

import java.io.IOException;

public class SysEscuelas extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Rectangle2D pantalla = Screen.getPrimary().getVisualBounds();
        AppContext appContext = AppContext.getInstance();
        FXMLLoader fxmlLoader = new FXMLLoader(SysEscuelas.class.getResource("/view/maingui.fxml"));
        fxmlLoader.setControllerFactory(appContext::getBean);
        Scene scene = new Scene(fxmlLoader.load(), pantalla.getWidth(), pantalla.getHeight() - 50);
        scene.getStylesheets().add(SysEscuelas.class.getResource("/css/style.css").toExternalForm());
        stage.setTitle("Padrón de Escuelas Públicas");
        stage.setScene(scene);
        stage.show();
    }
}
