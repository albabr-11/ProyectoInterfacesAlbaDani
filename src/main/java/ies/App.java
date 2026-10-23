package ies;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
   
    private Label TotalNum;
    private Label AbiertasNum;
    private Label EnProcesoNum;
    private Label CerradasNum;
    private Label Feedback;


    private Stage escenarioPrincipal;


    @Override
    public void start(Stage stage) {
        this.escenarioPrincipal = stage; // Guardamos la referencia de la ventana principal
        stage.setTitle("Gestión de Incidencias");


        // Inicializamos la lista con datos de ejemplo adaptados al documento
        listaIncidencias = FXCollections.observableArrayList(
            new Incidencia("Error en pasarela de pago", "Abierta", "Crítica", "Hace 10 min", "El cliente no puede finalizar la compra con tarjeta.", "Pepe Flores"),
            new Incidencia("Actualización de perfiles", "En proceso", "Media", "Ayer, 14:20", "No se guardan las fotos de perfil en la base de datos.", "Daniel Pérez"),
            new Incidencia("Fallo carga de imágenes", "Cerrada", "Baja", "Anteayer, 12:30", "Las imágenes tardaban más de 5 segundos en cargar.", "Alba Barrado")
        );


        // Envolvemos la lista en un FilteredList para que funcione el buscador en tiempo real
        listaFiltrada = new FilteredList<>(listaIncidencias, p -> true);


        // --- 1. CABECERA ---
        Label titulo = new Label("Gestión Incidencias");
        titulo.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #080808;");


        // Espacio flexible que empuja el botón a la derecha
        Pane espacioFlexible = new Pane();
        HBox.setHgrow(espacioFlexible, Priority.ALWAYS);


        Button Anadir = new Button("+ NUEVA INCIDENCIA");
        Anadir.setStyle(
            "-fx-background-color: #2563eb; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 14;"
        );
        Anadir.setOnAction(e -> abrirVentanaCrear());


        HBox superior = new HBox(titulo, espacioFlexible, Anadir);
        superior.setAlignment(Pos.CENTER_LEFT);


        // --- 2. CONTADORES SUPERIORES ---
        TotalNum = new Label("0");
        AbiertasNum = new Label("0");
        EnProcesoNum = new Label("0");
        CerradasNum = new Label("0");


        HBox Contadores = new HBox(12,
            crearTarjetaContador("TOTALES", TotalNum, "#475569"),
            crearTarjetaContador("ABIERTAS", AbiertasNum, "#e11d48"),
            crearTarjetaContador("EN PROCESO", EnProcesoNum, "#d97706"),
            crearTarjetaContador("CERRADAS", CerradasNum, "#059669")
        );
        Contadores.setAlignment(Pos.CENTER_LEFT);


        // --- 3. BARRA DE BÚSQUEDA Y FILTROS ---
        TextField Buscar = new TextField();
        Buscar.setPromptText(" Buscar incidencia por nombre...");
        Buscar.setPrefWidth(350);
        Buscar.setStyle("-fx-background-radius: 6; -fx-border-color: #cbd5e1; -fx-border-radius: 6; -fx-padding: 6;");
        HBox.setHgrow(Buscar, Priority.ALWAYS);


        ComboBox<String> FiltroEstado = new ComboBox<>(FXCollections.observableArrayList("Estado", "Abierta", "En proceso", "Cerrada"));
        FiltroEstado.setValue("Estado");
        FiltroEstado.setStyle("-fx-background-radius: 6;");


        ComboBox<String> FiltroPrioridad = new ComboBox<>(FXCollections.observableArrayList("Prioridad", "Crítica", "Urgente", "Media", "Baja"));
        FiltroPrioridad.setValue("Prioridad");
        FiltroPrioridad.setStyle("-fx-background-radius: 6;");


        // --- LÓGICA DE FILTRADO COMBINADO ---
        Runnable aplicarFiltros = () -> {
            String textoBusqueda = Buscar.getText();
            String estadoSeleccionado = FiltroEstado.getValue();
            String prioridadSeleccionada = FiltroPrioridad.getValue();


            listaFiltrada.setPredicate(incidencia -> {
                // Filtro de Texto (Nombre, Solicitante o Estado)
                boolean coincideTexto = true;
                if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                    String lowerCaseFilter = textoBusqueda.toLowerCase().trim();
                    coincideTexto = incidencia.nombre.toLowerCase().contains(lowerCaseFilter) ||
                                    incidencia.solicitante.toLowerCase().contains(lowerCaseFilter) ||
                                    incidencia.estado.toLowerCase().contains(lowerCaseFilter);
                }


                // Filtro de Estado
                boolean coincideEstado = true;
                if (estadoSeleccionado != null && !estadoSeleccionado.equals("Estado")) {
                    coincideEstado = incidencia.estado.equalsIgnoreCase(estadoSeleccionado);
                }


                // Filtro de Prioridad
                boolean coincidePrioridad = true;
                if (prioridadSeleccionada != null && !prioridadSeleccionada.equals("Prioridad")) {
                    coincidePrioridad = incidencia.prioridad.equalsIgnoreCase(prioridadSeleccionada);
                }


                // La incidencia pasa el filtro solo si cumple las tres condiciones simultáneamente
                return coincideTexto && coincideEstado && coincidePrioridad;
            });
        };


        // Conectamos los listeners para activar el filtrado en tiempo real
        Buscar.textProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros.run());
        FiltroEstado.valueProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros.run());
        FiltroPrioridad.valueProperty().addListener((observable, oldValue, newValue) -> aplicarFiltros.run());


        HBox barraFiltros = new HBox(10, Buscar, FiltroEstado, FiltroPrioridad);
        barraFiltros.setAlignment(Pos.CENTER_LEFT);


        // --- 4. LISTA DE INCIDENCIAS ---
        listViewIncidencias = new ListView<>(listaFiltrada);
        listViewIncidencias.setPrefHeight(340);
        listViewIncidencias.setStyle("-fx-background-color: transparent; -fx-border-color: #cbd5e1; -fx-border-radius: 6;");
       
        listViewIncidencias.setCellFactory(lv -> new ListCell<Incidencia>() {
            @Override
            public void updateItem(Incidencia inc, boolean empty) {
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


        // --- BOTONES DE MODIFICAR Y ELIMINAR SELECCIONADA ---
        Button btnModificarSeleccionada = new Button("Modificar Seleccionada");
        btnModificarSeleccionada.setStyle(
            "-fx-background-color: #2563eb; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btnModificarSeleccionada.setDisable(true); // Desactivado por defecto hasta seleccionar


        Button btnEliminarSeleccionada = new Button("Eliminar Seleccionada");
        btnEliminarSeleccionada.setStyle(
            "-fx-background-color: #fee2e2; " +
            "-fx-text-fill: #b91c1c; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btnEliminarSeleccionada.setDisable(true); // Desactivado por defecto hasta seleccionar


        // Listener para habilitar/deshabilitar los botones según la selección y el estado
        listViewIncidencias.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, seleccionada) -> {
            if (seleccionada == null) {
                btnModificarSeleccionada.setDisable(true);
                btnEliminarSeleccionada.setDisable(true);
            } else {
                btnModificarSeleccionada.setDisable(false);
                // Solo permite eliminar si el estado es Cerrada (o Completada)
                boolean estaCerrada = seleccionada.estado.equalsIgnoreCase("Cerrada") || seleccionada.estado.equalsIgnoreCase("Completada");
                btnEliminarSeleccionada.setDisable(!estaCerrada);
            }
        });


        // Acción del botón Modificar
        btnModificarSeleccionada.setOnAction(e -> {
            Incidencia seleccionada = listViewIncidencias.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                abrirVentanaModificar(seleccionada);
            }
        });


        // Acción del botón Eliminar
        btnEliminarSeleccionada.setOnAction(e -> {
            Incidencia seleccionada = listViewIncidencias.getSelectionModel().getSelectedItem();
            if (seleccionada != null) {
                if (seleccionada.estado.equalsIgnoreCase("Cerrada") || seleccionada.estado.equalsIgnoreCase("Completada")) {
                    listaIncidencias.remove(seleccionada);
                    actualizarContadores();
                    Feedback.setText("  Incidencia eliminada correctamente.");
                } else {
                    Feedback.setText("  Solo se pueden eliminar incidencias en estado Cerrada.");
                }
            }
        });


        HBox panelAccionesDerecha = new HBox(10, btnModificarSeleccionada, btnEliminarSeleccionada);
        panelAccionesDerecha.setAlignment(Pos.CENTER_RIGHT);


        // Espacio flexible para separar los contadores de los botones de acción
        Pane espacioInferior = new Pane();
        HBox.setHgrow(espacioInferior, Priority.ALWAYS);


        // Contenedor que une contadores a la izquierda y botones a la derecha en la misma fila
        HBox panelInferiorCombinado = new HBox(15, Contadores, espacioInferior, panelAccionesDerecha);
        panelInferiorCombinado.setAlignment(Pos.CENTER_LEFT);


        // --- 5. MENSAJE DE FEEDBACK ---
        Feedback = new Label("  Mensaje de Feedback");
        Feedback.setMaxWidth(Double.MAX_VALUE);
        Feedback.setPadding(new Insets(10));
        Feedback.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #cbd5e1; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6; " +
            "-fx-font-style: italic; " +
            "-fx-text-fill: #334155;"
        );


        VBox raiz = new VBox(15,
            superior,
            barraFiltros,
            new Label("Listado de Incidencias:"),
            listViewIncidencias,
            panelInferiorCombinado,
            Feedback
        );
        raiz.setPadding(new Insets(20));
        raiz.setStyle("-fx-background-color: #f8fafc;");


        actualizarContadores();


        Scene scene = new Scene(raiz, 950, 680);
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


        TotalNum.setText(String.valueOf(total));
        AbiertasNum.setText(String.valueOf(abiertas));
        EnProcesoNum.setText(String.valueOf(enProceso));
        CerradasNum.setText(String.valueOf(cerradas));
    }


    // --- VENTANA EMERGENTE: AÑADIR INCIDENCIA ---
    private void abrirVentanaCrear() {
        Stage ventanaCrear = new Stage();
        ventanaCrear.initModality(Modality.APPLICATION_MODAL);
        ventanaCrear.initOwner(escenarioPrincipal);
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
                Feedback.setText("  Nueva incidencia añadida correctamente.");
                ventanaCrear.close();
            } else {
                Feedback.setText("  El nombre de la incidencia no puede estar vacío.");
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


    // --- VENTANA EMERGENTE: SÓLO DETALLES (LECTURA) ---
    private void abrirVentanaDetalles(Incidencia inc) {
        Stage ventanaDetalles = new Stage();
        ventanaDetalles.initModality(Modality.APPLICATION_MODAL);
        ventanaDetalles.initOwner(escenarioPrincipal);
        ventanaDetalles.setTitle("Detalles de la incidencia");


        TextField txtNombre = new TextField(inc.nombre);
        txtNombre.setEditable(false);
        txtNombre.setStyle("-fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5; -fx-background-color: #f1f5f9;");
       
        TextArea txtDetalles = new TextArea(inc.detalles);
        txtDetalles.setEditable(false);
        txtDetalles.setPrefHeight(80);
        txtDetalles.setWrapText(true);


        TextField txtEstado = new TextField(inc.estado);
        txtEstado.setEditable(false);
        txtEstado.setStyle("-fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5; -fx-background-color: #f1f5f9;");


        TextField txtPrioridad = new TextField(inc.prioridad);
        txtPrioridad.setEditable(false);
        txtPrioridad.setStyle("-fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5; -fx-background-color: #f1f5f9;");


        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle(
            "-fx-background-color: #475569; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btnCerrar.setOnAction(e -> ventanaDetalles.close());


        HBox botonesAccion = new HBox(10, btnCerrar);
        botonesAccion.setAlignment(Pos.CENTER_RIGHT);


        VBox layout = new VBox(10,
            new Label("Nombre Incidencia"), txtNombre,
            new Label("Detalles"), txtDetalles,
            new Label("Estado"), txtEstado,
            new Label("Prioridad"), txtPrioridad,
            botonesAccion
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");


        Scene scene = new Scene(layout, 440, 420);
        ventanaDetalles.setScene(scene);
        ventanaDetalles.showAndWait();
    }


    // --- VENTANA EMERGENTE: MODIFICAR (SIN ELIMINAR) ---
    private void abrirVentanaModificar(Incidencia inc) {
        Stage ventanaModificar = new Stage();
        ventanaModificar.initModality(Modality.APPLICATION_MODAL);
        ventanaModificar.initOwner(escenarioPrincipal);
        ventanaModificar.setTitle("Modificar incidencia");


        TextField txtNombre = new TextField(inc.nombre);
        txtNombre.setStyle("-fx-background-radius: 4; -fx-border-color: #cbd5e1; -fx-border-radius: 4; -fx-padding: 5;");
       
        TextArea txtDetalles = new TextArea(inc.detalles);
        txtDetalles.setPrefHeight(80);
        txtDetalles.setWrapText(true);


        ComboBox<String> cbModEstado = new ComboBox<>(FXCollections.observableArrayList("Abierta", "Cerrada", "En proceso"));
        cbModEstado.setValue(inc.estado);


        ComboBox<String> cbModPrioridad = new ComboBox<>(FXCollections.observableArrayList("Crítica", "Urgente", "Media", "Baja"));
        cbModPrioridad.setValue(inc.prioridad);


        Button btnCerrar = new Button("Cerrar");
        btnCerrar.setStyle(
            "-fx-background-color: #475569; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 4; " +
            "-fx-cursor: hand;"
        );
        btnCerrar.setOnAction(e -> ventanaModificar.close());


        Button btnModificar = new Button("Guardar Cambios");
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
            Feedback.setText("  Cambios aplicados en '" + inc.nombre + "'");
            ventanaModificar.close();
        });


        HBox botonesAccion = new HBox(10, btnCerrar, btnModificar);
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
        ventanaModificar.setScene(scene);
        ventanaModificar.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

