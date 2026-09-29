package pe.edu.upeu.sysescuelas.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysescuelas.components.ColumnInfo;
import pe.edu.upeu.sysescuelas.components.TableViewHelper;
import pe.edu.upeu.sysescuelas.components.Toast;
import pe.edu.upeu.sysescuelas.components.ToltipCustom;
import pe.edu.upeu.sysescuelas.dto.ComboBoxOption;
import pe.edu.upeu.sysescuelas.enums.NivelEducativo;
import pe.edu.upeu.sysescuelas.exception.ClaveDuplicadaException;
import pe.edu.upeu.sysescuelas.model.Escuela;
import pe.edu.upeu.sysescuelas.service.IEscuelaService;

import java.util.*;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class EscuelaController {

    private static final String TODOS = "TODOS";

    private final IEscuelaService es;

    @FXML
    TextField txtNombre, txtClave, txtDireccion, txtMatricula, txtFiltroDato;
    @FXML
    ComboBox<ComboBoxOption> cbxNivel;
    @FXML
    ComboBox<ComboBoxOption> cbxFiltroNivel;

    @FXML
    private TableView<Escuela> tableView;

    @FXML
    Label lbnMsg, lblResumen;
    @FXML
    private AnchorPane miContenedor;
    Stage stage;

    private Validator validator;

    // Lista maestra → FilteredList (filtro por nivel/texto) → SortedList (orden por columnas)
    // Patrón del manual, sección 8.8 (Ejemplo 8.4)
    private final ObservableList<Escuela> listaMaestra = FXCollections.observableArrayList();
    private FilteredList<Escuela> filtradas;

    Escuela formulario;
    Long idEscuelaCE = 0L;   // 0 = registro nuevo; >0 = editando

    private final ToltipCustom ttc = new ToltipCustom();

    @FXML
    public void initialize() {
        cbxNivel.getItems().addAll(es.listarNiveles());

        // El filtro incluye la opción "Todos los niveles" al inicio
        cbxFiltroNivel.getItems().add(new ComboBoxOption(TODOS, "Todos los niveles"));
        cbxFiltroNivel.getItems().addAll(es.listarNiveles());
        cbxFiltroNivel.getSelectionModel().selectFirst();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        TableViewHelper<Escuela> tableViewHelper = new TableViewHelper<>();

        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID", new ColumnInfo("idEscuela", 50.0));
        columns.put("Clave CT", new ColumnInfo("clave", 110.0));
        columns.put("Nombre del plantel", new ColumnInfo("nombre", 250.0));
        columns.put("Nivel educativo", new ColumnInfo("nivel", 120.0));
        columns.put("Dirección", new ColumnInfo("direccion", 260.0));
        columns.put("Matrícula", new ColumnInfo("matricula", 90.0));

        Consumer<Escuela> updateAction = this::editForm;
        Consumer<Escuela> deleteAction = this::confirmarEliminar;

        tableViewHelper.addColumnsInOrderWithSize(tableView, columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);

        // Cadena FilteredList + SortedList (manual, 8.8)
        filtradas = new FilteredList<>(listaMaestra, e -> true);
        SortedList<Escuela> ordenadas = new SortedList<>(filtradas);
        ordenadas.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(ordenadas);

        // El filtro reacciona al cambiar el nivel o al escribir en el buscador
        cbxFiltroNivel.valueProperty().addListener((obs, o, n) -> aplicarFiltro());
        txtFiltroDato.textProperty().addListener((obs, o, n) -> aplicarFiltro());

        lbnMsg.setText("");
        idEscuelaCE = 0L;
        listar();
    }

    public void listar() {
        try {
            listaMaestra.setAll(es.findAll());
            aplicarFiltro();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /** Filtra por nivel educativo (ComboBox) y, opcionalmente, por nombre o clave (TextField). */
    private void aplicarFiltro() {
        ComboBoxOption sel = cbxFiltroNivel.getSelectionModel().getSelectedItem();
        String nivelKey = sel == null ? TODOS : sel.getKey();
        String texto = txtFiltroDato.getText() == null ? "" : txtFiltroDato.getText().trim().toLowerCase();

        filtradas.setPredicate(e -> {
            boolean coincideNivel = nivelKey.equals(TODOS) || e.getNivel().name().equals(nivelKey);
            boolean coincideTexto = texto.isEmpty()
                    || e.getNombre().toLowerCase().contains(texto)
                    || e.getClave().toLowerCase().contains(texto);
            return coincideNivel && coincideTexto;
        });
        actualizarResumen();
    }

    private void actualizarResumen() {
        int total = filtradas.stream().mapToInt(Escuela::getMatricula).sum();
        lblResumen.setText("Planteles mostrados: " + filtradas.size()
                + "   |   Matrícula total de alumnos: " + total);
    }

    @FXML
    public void limpiarFiltros() {
        cbxFiltroNivel.getSelectionModel().selectFirst();
        txtFiltroDato.clear();
    }

    @FXML
    public void validarFormulario() {
        formulario = new Escuela();
        formulario.setNombre(txtNombre.getText() == null ? "" : txtNombre.getText().trim());
        formulario.setClave(txtClave.getText() == null ? "" : txtClave.getText().trim().toUpperCase());
        formulario.setDireccion(txtDireccion.getText() == null ? "" : txtDireccion.getText().trim());
        formulario.setMatricula(parseEnteroSafe(txtMatricula.getText()));

        String idxN = cbxNivel.getSelectionModel().getSelectedItem() == null ? ""
                : cbxNivel.getSelectionModel().getSelectedItem().getKey();
        formulario.setNivel(idxN.isEmpty() ? null : NivelEducativo.valueOf(idxN));

        Set<ConstraintViolation<Escuela>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<Escuela>> violacionesOrdenadas = violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString())).toList();

        if (violacionesOrdenadas.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violacionesOrdenadas);
        }
    }

    /**
     * Texto vacío → null (dispara @NotNull).
     * Texto que no es un entero → -1 (dispara @PositiveOrZero con su mensaje).
     */
    private Integer parseEnteroSafe(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void clearForm() {
        txtNombre.clear();
        txtClave.clear();
        txtDireccion.clear();
        txtMatricula.clear();
        cbxNivel.getSelectionModel().clearSelection();
        idEscuelaCE = 0L;
        limpiarError();
        lbnMsg.setText("");
    }

    public void editForm(Escuela escuela) {
        txtNombre.setText(escuela.getNombre());
        txtClave.setText(escuela.getClave());
        txtDireccion.setText(escuela.getDireccion());
        txtMatricula.setText(String.valueOf(escuela.getMatricula()));

        cbxNivel.getSelectionModel().select(
                cbxNivel.getItems().stream()
                        .filter(n -> n.getKey().equals(escuela.getNivel().name()))
                        .findFirst().orElse(null));

        idEscuelaCE = escuela.getIdEscuela();
        limpiarError();
        lbnMsg.setText("Editando: " + escuela.getNombre());
        lbnMsg.setStyle("-fx-text-fill: #1f3397; -fx-font-size: 13px;");
    }

    /** Confirmación antes de eliminar (manual, Ejemplo 11.2). */
    private void confirmarEliminar(Escuela escuela) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de eliminar la escuela \"" + escuela.getNombre() + "\"?",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                stage = (Stage) miContenedor.getScene().getWindow();
                es.delete(escuela.getIdEscuela());
                if (Objects.equals(idEscuelaCE, escuela.getIdEscuela())) clearForm();
                double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
                Toast.showToast(stage, "Se eliminó correctamente!!", 2000, w, h);
                listar();
            }
        });
    }

    private void mostrarErroresValidacion(List<ConstraintViolation<Escuela>> violaciones) {
        limpiarError();
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombre);
        campos.put("nivel", cbxNivel);
        campos.put("clave", txtClave);
        campos.put("direccion", txtDireccion);
        campos.put("matricula", txtMatricula);

        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        final Control[] primerCtrl = {null};
        for (String campo : campos.keySet()) {
            violaciones.stream()
                    .filter(v -> v.getPropertyPath().toString().equals(campo))
                    .findFirst().ifPresent(v -> {
                        erroresOrdenados.put(campo, v.getMessage());
                        Control c = campos.get(campo);
                        if (c != null) ttc.marcarError(c, v.getMessage().trim());
                        if (primerCtrl[0] == null) primerCtrl[0] = c;
                    });
        }
        if (!erroresOrdenados.isEmpty()) {
            lbnMsg.setText(erroresOrdenados.entrySet().iterator().next().getValue());
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");
            if (primerCtrl[0] != null) Platform.runLater(primerCtrl[0]::requestFocus);
        }
    }

    private void procesarFormulario() {
        stage = (Stage) miContenedor.getScene().getWindow();
        limpiarError();
        double w = stage.getWidth() / 1.5, h = stage.getHeight() / 2;
        try {
            if (idEscuelaCE > 0L) {
                formulario.setIdEscuela(idEscuelaCE);
                es.update(idEscuelaCE, formulario);
                Toast.showToast(stage, "Se actualizó correctamente!!", 2000, w, h);
            } else {
                es.save(formulario);
                Toast.showToast(stage, "Se guardó correctamente!!", 2000, w, h);
            }
        } catch (ClaveDuplicadaException ex) {
            // La clave de centro de trabajo debe ser única
            ttc.marcarError(txtClave, ex.getMessage());
            lbnMsg.setText(ex.getMessage());
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 13px;");
            Platform.runLater(txtClave::requestFocus);
            return;
        }
        clearForm();
        listar();
    }

    public void limpiarError() {
        List.of(txtNombre, cbxNivel, txtClave, txtDireccion, txtMatricula)
                .forEach(c -> {
                    c.getStyleClass().remove("text-field-error");
                    ttc.limpiarCampo(c);
                });
    }
}
