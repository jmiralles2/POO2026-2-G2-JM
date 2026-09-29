# SysEscuelas — Padrón de escuelas públicas (JavaFX)

**Ejercicio 24.** Aplicación JavaFX para gestionar el catálogo de planteles educativos públicos.
Cada escuela tiene: nombre del plantel, nivel educativo (preescolar, primaria, secundaria,
preparatoria), clave de centro de trabajo (única), dirección y matrícula total de alumnos.
El sistema permite **filtrar por nivel educativo**.

## Cómo ejecutar

Requisitos: JDK 21 e internet la primera vez (Maven descarga las dependencias).

```
./mvnw clean javafx:run        # Linux / Mac
mvnw.cmd clean javafx:run      # Windows
```

En IntelliJ: abrir la carpeta `SysEscuelas` como proyecto Maven y ejecutar `App.java`
(o el goal `javafx:run`).

## Funcionalidades

- Registrar, editar y eliminar escuelas (botones de la columna "Acciones").
- Validación con Jakarta Validation (mensajes en tooltip + etiqueta).
- La clave de centro de trabajo es **única** (formato `09DPR0123A`).
- Filtro por nivel educativo (ComboBox) y buscador por nombre o clave.
- Resumen: planteles mostrados y matrícula total de alumnos del filtro actual.
- Los datos viven en memoria (se pierden al cerrar), igual que el sistema base de la clase.

## Arquitectura (misma que SysVentas)

```
pe.edu.upeu.sysescuelas
├── App / SysEscuelas          arranque JavaFX
├── config/AppContext          contenedor de dependencias (repos → servicios → controladores)
├── model/Escuela              entidad + anotaciones de validación
├── enums/NivelEducativo       PREESCOLAR, PRIMARIA, SECUNDARIA, PREPARATORIA
├── dto/ComboBoxOption         par clave/valor para los ComboBox
├── repository/                ICrudGenericoRepository, AbstractJpaRepository, EscuelaRepository
├── service/ (+ impl/)         ICrudGenericoService, CrudGenericoServiceImp, IEscuelaService, EscuelaServiceImp
├── controller/                MainGuiController (menú/pestañas), EscuelaController (pantalla)
├── components/                TableViewHelper, ColumnInfo, Toast, ToltipCustom
└── exception/                 ModelNotFoundException, ClaveDuplicadaException
resources/view/                maingui.fxml, main_escuela.fxml
```

## Código del manual utilizado

| Tema | Dónde |
|------|-------|
| TableView, TableColumn, PropertyValueFactory (cap. 8.2–8.3) | `TableViewHelper`, `EscuelaController.initialize` |
| Filtrado: `FilteredList` + `SortedList` enlazada al comparador (8.8, Ej. 8.4) | `EscuelaController.aplicarFiltro` |
| Botones de acción dentro de la tabla (8.12) | `TableViewHelper.addActionColumn` |
| ComboBox con lista de opciones (5.4) | `cbxNivel`, `cbxFiltroNivel` |
| Alert de confirmación antes de eliminar (11.2, Ej. 11.2) | `EscuelaController.confirmarEliminar` |
| FXML + controlador con `@FXML initialize` (cap. 22–23) | `main_escuela.fxml`, `EscuelaController` |
