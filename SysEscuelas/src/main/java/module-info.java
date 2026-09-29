module pe.edu.upeu.sysescuelas {
    requires javafx.controls;
    requires javafx.fxml;

    requires static lombok;
    requires jakarta.validation;

    opens pe.edu.upeu.sysescuelas to javafx.fxml;
    opens pe.edu.upeu.sysescuelas.controller to javafx.fxml;
    opens pe.edu.upeu.sysescuelas.model;
    exports pe.edu.upeu.sysescuelas;
}
