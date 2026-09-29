package pe.edu.upeu.sysescuelas.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import pe.edu.upeu.sysescuelas.config.AppContext;

import java.io.IOException;
import java.util.Map;

public class MainGuiController {

    @FXML
    TabPane tabPane;

    @FXML
    MenuItem menuItem1, menuItem2;

    @FXML
    public void initialize() {
        MenuItemListener miL = new MenuItemListener();
        menuItem1.setOnAction(miL::handle);
        menuItem2.setOnAction(miL::handle);

        // Al iniciar, se abre directamente el padrón de escuelas
        menuItem1.fire();
    }

    class MenuItemListener {
        // id del menú → {ruta FXML, título de la pestaña, tipo: T = abrir pestaña, C = cerrar app}
        Map<String, String[]> menuConfig = Map.of(
                "menuItem1", new String[]{"/view/main_escuela.fxml", "Padrón de Escuelas", "T"},
                "menuItem2", new String[]{"", "Salir", "C"}
        );

        public void handle(ActionEvent e) {
            String id = ((MenuItem) e.getSource()).getId();
            if (menuConfig.containsKey(id)) {
                String[] items = menuConfig.get(id);
                if (items[2].equals("C")) {
                    Platform.exit();
                    System.exit(0);
                } else {
                    abrirTabPaneFXML(items[0], items[1]);
                }
            }
        }

        private void abrirTabPaneFXML(String fxmlPath, String titulo) {
            try {
                AppContext context = AppContext.getInstance();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                fxmlLoader.setControllerFactory(context::getBean);
                Parent root = fxmlLoader.load();

                ScrollPane scrollPane = new ScrollPane(root);
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
                Tab newTab = new Tab(titulo, scrollPane);
                tabPane.getTabs().clear();
                tabPane.getTabs().add(newTab);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
