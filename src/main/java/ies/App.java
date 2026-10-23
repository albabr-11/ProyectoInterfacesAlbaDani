package ies;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class App extends Application {

    // Clase interna que representa los datos de cada incidencia (sin getters ni setters)
    public static class Incidencia {
        String nombre;
        String estado;
        String prioridad;
        String ultimaActualizacion;
        String detalles;
        String solicitante;

        // Constructor para inicializar una incidencia
        public Incidencia(String nombre, String estado, String prioridad, String ultimaActualizacion, String detalles, String solicitante) {
            this.nombre = nombre;
            this.estado = estado;
            this.prioridad = prioridad;
            this.ultimaActualizacion = ultimaActualizacion;
            this.detalles = detalles;
            this.solicitante = solicitante;
        }
    }

    // Declaración de variables globales de la interfaz
    private ObservableList<Incidencia> listaIncidencias;
    private FilteredList<Incidencia> listaFiltrada;
    private ListView<Incidencia> listViewIncidencias;
    
    private Label lblTotalNum;
    private Label lblAbiertasNum;
    private Label lblEnProcesoNum;
    private Label lblCerradasNum;
    private Label lblFeedback;

    private Stage primaryStage;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage; // Guardamos la referencia de la ventana principal
        stage.setTitle("Gestión de Incidencias");

        // Inicializamos la lista con datos de ejemplo
        listaIncidencias = FXCollections.observableArrayList(
            new Incidencia("Error en pasarela de pago", "Abierta", "Crítica", "Hace 10 min", "El cliente no puede finalizar la compra con tarjeta.", "Juan Pérez"),
            new Incidencia("Actualización de perfiles", "En proceso", "Media", "Ayer, 14:20", "No se guardan las fotos de perfil en la base de datos.", "María López"),
            new Incidencia("Fallo carga de imágenes", "Cerrada", "Baja", "Anteayer, 12:30", "Las imágenes tardaban más de 5 segundos en cargar.", "Carlos Ruiz")
        );

        // Envolvemos la lista en un FilteredList para que funcione el buscador en tiempo real
        listaFiltrada = new FilteredList<>(listaIncidencias, p -> true);

        // --- 1. CABECERA ---
        Label titulo = new Label("Gestión Incidencias");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // Espacio flexible que empuja el botón a la derecha
        Pane espacioFlexible = new Pane();
        HBox.setHgrow(espacioFlexible, Priority.ALWAYS);

        Button btnAnadir = new Button("+ AÑADIR INCIDENCIA");
        btnAnadir.setStyle(
            "-fx-background-color: #2563eb; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 14;"
        );
        btnAnadir.setOnAction(e -> abrirVentanaCrear());

        HBox topHeader = new HBox(titulo, espacioFlexible, btnAnadir);
        topHeader.setAlignment(Pos.CENTER_LEFT);

        // --- 2. CONTADORES SUPERIORES ---
        lblTotalNum = new Label("0");
        lblAbiertasNum = new Label("0");
        lblEnProcesoNum = new Label("0");
        lblCerradasNum = new Label("0");

        HBox panelContadores = new HBox(12,
            crearTarjetaContador("TOTALES", lblTotalNum, "#475569"),
            crearTarjetaContador("ABIERTAS", lblAbiertasNum, "#e11d48"),
            crearTarjetaContador("EN PROCESO", lblEnProcesoNum, "#d97706"),
            crearTarjetaContador("CERRADAS", lblCerradasNum, "#059669")
        );
        panelContadores.setAlignment(Pos.CENTER_LEFT);

        // --- 3. BARRA DE BÚSQUEDA Y FILTROS ---
        TextField campoBuscar = new TextField();
        campoBuscar.setPromptText(" Buscar incidencia...");
        campoBuscar.setPrefWidth(350);
        campoBuscar.setStyle("-fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-padding: 6;");
        HBox.setHgrow(campoBuscar, Priority.ALWAYS);

        // Buscador 
        campoBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            listaFiltrada.setPredicate(incidencia -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return incidencia.nombre.toLowerCase().contains(lowerCaseFilter) ||
                       incidencia.solicitante.toLowerCase().contains(lowerCaseFilter) ||
                       incidencia.estado.toLowerCase().contains(lowerCaseFilter);
            });
        });

        ComboBox<String> comboFiltroEstado = new ComboBox<>(FXCollections.observableArrayList("Estado", "Abierta", "En proceso", "Cerrada"));
        comboFiltroEstado.setValue("Estado");
        comboFiltroEstado.setStyle("-fx-background-radius: 6;");

        ComboBox<String> comboFiltroPrioridad = new ComboBox<>(FXCollections.observableArrayList("Prioridad", "Crítica", "Urgente", "Media", "Baja"));
        comboFiltroPrioridad.setValue("Prioridad");
        comboFiltroPrioridad.setStyle("-fx-background-radius: 6;");

        HBox barraFiltros = new HBox(10, campoBuscar, comboFiltroEstado, comboFiltroPrioridad);
        barraFiltros.setAlignment(Pos.CENTER_LEFT);

        // --- 4. LISTA DE INCIDENCIAS ---
        listViewIncidencias = new ListView<>(listaFiltrada);
        listViewIncidencias.setPrefHeight(340);
        listViewIncidencias.setStyle("-fx-background-color: transparent; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");
        
        listViewIncidencias.setCellFactory(lv -> new ListCell<Incidencia>() {
            @Override
            protected void updateItem(Incidencia inc, boolean empty) {
                super.updateItem(inc, empty);
                if (empty || inc == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox card = new HBox(15);
                    card.setAlignment(Pos.CENTER_LEFT);
                    card.setPadding(new Insets(10, 15, 10, 15));
                    card.setStyle(
                        "-fx-background-color: white; " +
                        "-fx-border-color: #e2e8f0; " +
                        "-fx-border-radius: 6; " +
                        "-fx-background-radius: 6;"
                    );

                    VBox info = new VBox(4);
                    Label lblNombre = new Label(inc.nombre);
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0f172a;");
                    
                    Label lblSub = new Label("Actualizado: " + inc.ultimaActualizacion + "  •  Solicitante: " + inc.solicitante);
                    lblSub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                    
                    info.getChildren().addAll(lblNombre, lblSub);
                    HBox.setHgrow(info, Priority.ALWAYS);

                    Label lblEstado = new Label(" " + inc.estado + " ");
                    String estiloEstado = "-fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4; -fx-padding: 3 8;";
                    if (inc.estado.equalsIgnoreCase("Abierta")) {
                        estiloEstado += "-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c;";
                    } else if (inc.estado.equalsIgnoreCase("En proceso")) {
                        estiloEstado += "-fx-background-color: #fef3c7; -fx-text-fill: #b45309;";
                    } else {
                        estiloEstado += "-fx-background-color: #d1fae5; -fx-text-fill: #047857;";
                    }
                    lblEstado.setStyle(estiloEstado);

                    Label lblPrioridad = new Label(" Prioridad: " + inc.prioridad + " ");
                    lblPrioridad.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-background-radius: 4; -fx-padding: 3 8;");

                    Button btnDetalles = new Button("Detalles");
                    btnDetalles.setStyle(
                        "-fx-background-color: #eff6ff; " +
                        "-fx-text-fill: #2563eb; " +
                        "-fx-border-color: #bfdbfe; " +
                        "-fx-border-radius: 4; " +
                        "-fx-background-radius: 4; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-weight: bold;"
                    );
                    btnDetalles.setOnAction(e -> abrirVentanaDetalles(inc));

                    card.getChildren().addAll(info, lblEstado, lblPrioridad, btnDetalles);
                    setGraphic(card);
                }
            }
        });

        // --- 5. MENSAJE DE FEEDBACK ---
        lblFeedback = new Label("  ⓘ Mensaje de Feedback");
        lblFeedback.setMaxWidth(Double.MAX_VALUE);
        lblFeedback.setPadding(new Insets(10));
        lblFeedback.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #cbd5e1; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-font-style: italic; " +
            "-fx-text-fill: #334155;"
        );

        VBox raiz = new VBox(15,
            topHeader,
            panelContadores,
            barraFiltros,
            new Label("Listado de Incidencias:"),
            listViewIncidencias,
            lblFeedback
        );
        raiz.setPadding(new Insets(20));
        raiz.setStyle("-fx-background-color: #f8fafc;");

        actualizarContadores();

        Scene scene = new Scene(raiz, 950, 650);
        stage.setScene(scene);
        stage.show();
    }

    private VBox crearTarjetaContador(String titulo, Label lblNum, String colorHex) {
        VBox tarjeta = new VBox(4);
        tarjeta.setPadding(new Insets(10, 18, 10, 18));
        tarjeta.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e2e8f0; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6;"
        );
        
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        
        lblNum.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        
        tarjeta.getChildren().addAll(lblTitulo, lblNum);
        return tarjeta;
    }

    private void actualizarContadores() {
        int total = listaIncidencias.size();
        int abiertas = 0, enProceso = 0, cerradas = 0;

        for (Incidencia i : listaIncidencias) {
            String est = i.estado.toLowerCase();
            if (est.contains("abierta")) abiertas++;
            else if (est.contains("proceso")) enProceso++;
            else if (est.contains("cerrada")) cerradas++;
        }

        lblTotalNum.setText(String.valueOf(total));
        lblAbiertasNum.setText(String.valueOf(abiertas));
        lblEnProcesoNum.setText(String.valueOf(enProceso));
        lblCerradasNum.setText(String.valueOf(cerradas));
    }

    // --- VENTANA EMERGENTE: AÑADIR INCIDENCIA ---
    private void abrirVentanaCrear() {
        Stage ventanaCrear = new Stage();
        ventanaCrear.initModality(Modality.APPLICATION_MODAL);
        ventanaCrear.initOwner(primaryStage);
        ventanaCrear.setTitle("Añadir Incidencia");

        TextField txtNombre = new TextField();
        txtNombre.setStyle("-fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5;");
        
        TextArea txtDetalles = new TextArea();
        txtDetalles.setPrefHeight(80);
        txtDetalles.setWrapText(true);

        TextField txtSolicitante = new TextField();
        txtSolicitante.setStyle("-fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5;");
        
        ComboBox<String> cbEstado = new ComboBox<>(FXCollections.observableArrayList("Abierta", "Cerrada", "En proceso"));
        cbEstado.setValue("Abierta");
        
        ComboBox<String> cbPrioridad = new ComboBox<>(FXCollections.observableArrayList("Crítica", "Urgente", "Media", "Baja"));
        cbPrioridad.setValue("Media");

        Button btnCancelar = new Button("CANCELAR");
        btnCancelar.setStyle("-fx-background-radius: 4; -fx-cursor: hand;");
        btnCancelar.setOnAction(e -> ventanaCrear.close());

        Button btnAnadir = new Button("AÑADIR");
        btnAnadir.setStyle(
            "-fx-background-color: #2563eb; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btnAnadir.setOnAction(e -> {
            if (!txtNombre.getText().trim().isEmpty()) {
                Incidencia nueva = new Incidencia(
                    txtNombre.getText(),
                    cbEstado.getValue(),
                    cbPrioridad.getValue(),
                    "Ahora mismo",
                    txtDetalles.getText(),
                    txtSolicitante.getText()
                );
                listaIncidencias.add(0, nueva);
                actualizarContadores();
                lblFeedback.setText("  Nueva incidencia añadida correctamente.");
                ventanaCrear.close();
            } else {
                lblFeedback.setText("  El nombre de la incidencia no puede estar vacío.");
            }
        });

        HBox botones = new HBox(10, btnCancelar, btnAnadir);
        botones.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(10,
            new Label("Nombre Incidencia"), txtNombre,
            new Label("Detalles"), txtDetalles,
            new Label("Solicitante"), txtSolicitante,
            new Label("Estado"), cbEstado,
            new Label("Prioridad"), cbPrioridad,
            botones
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 440, 500);
        ventanaCrear.setScene(scene);
        ventanaCrear.showAndWait();
    }

    // --- VENTANA EMERGENTE: DETALLES, MODIFICAR Y ELIMINAR ---
    private void abrirVentanaDetalles(Incidencia inc) {
        Stage ventanaDetalles = new Stage();
        ventanaDetalles.initModality(Modality.APPLICATION_MODAL);
        ventanaDetalles.initOwner(primaryStage);
        ventanaDetalles.setTitle("Detalles de la incidencia");

        TextField txtNombre = new TextField(inc.nombre);
        txtNombre.setStyle("-fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5;");
        
        TextArea txtDetalles = new TextArea(inc.detalles);
        txtDetalles.setPrefHeight(80);
        txtDetalles.setWrapText(true);

        ComboBox<String> cbModEstado = new ComboBox<>(FXCollections.observableArrayList("Abierta", "Cerrada", "En proceso"));
        cbModEstado.setValue(inc.estado);

        ComboBox<String> cbModPrioridad = new ComboBox<>(FXCollections.observableArrayList("Crítica", "Urgente", "Media", "Baja"));
        cbModPrioridad.setValue(inc.prioridad);

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle(
            "-fx-background-color: #fee2e2; " +
            "-fx-text-fill: #b91c1c; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btnEliminar.setOnAction(e -> {
            listaIncidencias.remove(inc);
            actualizarContadores();
            lblFeedback.setText("  Incidencia eliminada correctamente.");
            ventanaDetalles.close();
        });

        Button btnModificar = new Button("Modificar");
        btnModificar.setStyle(
            "-fx-background-color: #2563eb; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btnModificar.setOnAction(e -> {
            inc.nombre = txtNombre.getText();
            inc.detalles = txtDetalles.getText();
            inc.estado = cbModEstado.getValue();
            inc.prioridad = cbModPrioridad.getValue();
            inc.ultimaActualizacion = "Hace un momento";
            
            // Forzamos actualización reemplazando el elemento
            int index = listaIncidencias.indexOf(inc);
            if (index >= 0) {
                listaIncidencias.set(index, inc);
            }
            
            actualizarContadores();
            lblFeedback.setText("  Cambios aplicados en '" + inc.nombre + "'");
            ventanaDetalles.close();
        });

        HBox botonesAccion = new HBox(10, btnEliminar, btnModificar);
        botonesAccion.setAlignment(Pos.CENTER_RIGHT);

        VBox layout = new VBox(10,
            new Label("Nombre Incidencia"), txtNombre,
            new Label("Detalles"), txtDetalles,
            new Label("Modificar estado"), cbModEstado,
            new Label("Modificar prioridad"), cbModPrioridad,
            botonesAccion
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        Scene scene = new Scene(layout, 440, 460);
        ventanaDetalles.setScene(scene);
        ventanaDetalles.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}