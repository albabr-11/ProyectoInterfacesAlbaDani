package ies;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class MainController {
    @FXML private TextField Buscar;
    @FXML private ComboBox<String> FiltroEstado;
    @FXML private ComboBox<String> FiltroPrioridad;
    @FXML private ListView<Incidencia> listViewIncidencias;
   
    @FXML private Label TotalNum;
    @FXML private Label AbiertasNum;
    @FXML private Label EnProcesoNum;
    @FXML private Label CerradasNum;
    @FXML private Label Feedback;
   
    @FXML private Button ModificarSeleccionada;
    @FXML private Button EliminarSeleccionada;


    private ObservableList<Incidencia> listaIncidencias;
    private FilteredList<Incidencia> listaFiltrada;


    @FXML
    public void initialize() {
        FiltroEstado.getItems().addAll("Estado", "Abierta", "En proceso", "Cerrada");
        FiltroEstado.setValue("Estado");


        FiltroPrioridad.getItems().addAll("Prioridad", "Crítica", "Urgente", "Media", "Baja");
        FiltroPrioridad.setValue("Prioridad");


        listaIncidencias = FXCollections.observableArrayList(
            new Incidencia("Error en pasarela de pago", "Abierta", "Crítica", "Hace 10 min", "El cliente no puede finalizar la compra con tarjeta.", "Pepe Flores"),
            new Incidencia("Actualización de perfiles", "En proceso", "Media", "Ayer, 14:20", "No se guardan las fotos de perfil en la base de datos.", "Daniel Pérez"),
            new Incidencia("Fallo carga de imágenes", "Cerrada", "Baja", "Anteayer, 12:30", "Las imágenes tardaban más de 5 segundos en cargar.", "Alba Barrado")
        );


        listaFiltrada = new FilteredList<>(listaIncidencias, p -> true);
        listViewIncidencias.setItems(listaFiltrada);


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
                    card.setStyle("-fx-background-color: white; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-background-radius: 6;");


                    VBox info = new VBox(4);
                    Label Nombre = new Label(inc.nombre);
                  Nombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #0f172a;");
                   
                    Label Sub = new Label("Actualizado: " + inc.ultimaActualizacion + "  •  Solicitante: " + inc.solicitante);
                    Sub.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11px;");
                   
                    info.getChildren().addAll(Nombre, Sub);
                    HBox.setHgrow(info, Priority.ALWAYS);


                    Label Estado = new Label(" " + inc.estado + " ");
                    String estiloEstado = "-fx-font-weight: bold; -fx-font-size: 11px; -fx-background-radius: 4; -fx-padding: 3 8;";
                    if (inc.estado.equalsIgnoreCase("Abierta")) {
                        estiloEstado += "-fx-background-color: #fee2e2; -fx-text-fill: #b91c1c;";
                    } else if (inc.estado.equalsIgnoreCase("En proceso")) {
                        estiloEstado += "-fx-background-color: #fef3c7; -fx-text-fill: #b45309;";
                    } else {
                        estiloEstado += "-fx-background-color: #d1fae5; -fx-text-fill: #047857;";
                    }
                    Estado.setStyle(estiloEstado);


                    Label Prioridad = new Label(" Prioridad: " + inc.prioridad + " ");
                    Prioridad.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-background-radius: 4; -fx-padding: 3 8;");


                    Button Detalles = new Button("Ver Detalles");
                    Detalles.setStyle("-fx-background-color: #eff6ff; -fx-text-fill: #2563eb; -fx-border-color: #bfdbfe; -fx-border-radius: 4; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-weight: bold;");
                    Detalles.setOnAction(e -> abrirVentanaDetalles(inc));


                    card.getChildren().addAll(info, Estado, Prioridad, Detalles);
                    setGraphic(card);
                }
            }
        });


        Runnable aplicarFiltros = () -> {
            String textoBusqueda = Buscar.getText();
            String estadoSeleccionado = FiltroEstado.getValue();
            String prioridadSeleccionada = FiltroPrioridad.getValue();


            listaFiltrada.setPredicate(incidencia -> {
                boolean coincideTexto = true;
                if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
                    String lowerCaseFilter = textoBusqueda.toLowerCase().trim();
                    coincideTexto = incidencia.nombre.toLowerCase().contains(lowerCaseFilter) ||
                                    incidencia.solicitante.toLowerCase().contains(lowerCaseFilter) ||
                                    incidencia.estado.toLowerCase().contains(lowerCaseFilter);
                }


                boolean coincideEstado = true;
                if (estadoSeleccionado != null && !estadoSeleccionado.equals("Estado")) {
                    coincideEstado = incidencia.estado.equalsIgnoreCase(estadoSeleccionado);
                }


                boolean coincidePrioridad = true;
                if (prioridadSeleccionada != null && !prioridadSeleccionada.equals("Prioridad")) {
                    coincidePrioridad = incidencia.prioridad.equalsIgnoreCase(prioridadSeleccionada);
                }


                return coincideTexto && coincideEstado && coincidePrioridad;
            });
        };


        Buscar.textProperty().addListener((obs, oldV, newV) -> aplicarFiltros.run());
        FiltroEstado.valueProperty().addListener((obs, oldV, newV) -> aplicarFiltros.run());
        FiltroPrioridad.valueProperty().addListener((obs, oldV, newV) -> aplicarFiltros.run());


        ModificarSeleccionada.setDisable(true);
        EliminarSeleccionada.setDisable(true);


        listViewIncidencias.getSelectionModel().selectedItemProperty().addListener((obs, oldV, seleccionada) -> {
            if (seleccionada == null) {
                ModificarSeleccionada.setDisable(true);
                EliminarSeleccionada.setDisable(true);
            } else {
                ModificarSeleccionada.setDisable(false);
                boolean estaCerrada = seleccionada.estado.equalsIgnoreCase("Cerrada") || seleccionada.estado.equalsIgnoreCase("Completada");
                EliminarSeleccionada.setDisable(!estaCerrada);
            }
        });


        actualizarContadores();
    }


    @FXML
    private void handleNuevaIncidencia() {
        Stage ventanaCrear = new Stage();
        ventanaCrear.initModality(Modality.APPLICATION_MODAL);
        ventanaCrear.setTitle("Crear Nueva Incidencia");


        TextField Nombre = new TextField();
        TextArea textoDetalles = new TextArea();
        textoDetalles.setPrefHeight(80);
        textoDetalles.setWrapText(true);
        TextField Solicitante = new TextField();
       
        ComboBox<String> Estado = new ComboBox<>(FXCollections.observableArrayList("Abierta", "Cerrada", "En proceso"));
        Estado.setValue("Abierta");
       
        ComboBox<String> Prioridad = new ComboBox<>(FXCollections.observableArrayList("Crítica", "Urgente", "Media", "Baja"));
        Prioridad.setValue("Media");


        Button Cancelar = new Button("Cancelar");
        Cancelar.setOnAction(e -> ventanaCrear.close());


        Button Anadir = new Button("Guardar Incidencia");
        Anadir.setStyle("-fx-background-color: #2563eb; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
       Anadir.setOnAction(e -> {
            if (!Nombre.getText().trim().isEmpty()) {
                Incidencia nueva = new Incidencia(
                    Nombre.getText(),
                    Estado.getValue(),
                    Prioridad.getValue(),
                    "Hace un momento",
                    textoDetalles.getText(),
                    Solicitante.getText()
                );
                listaIncidencias.add(0, nueva);
                actualizarContadores();
                Feedback.setText("  Incidencia creada correctamente.");
                ventanaCrear.close();
            } else {
                Feedback.setText("  Error: El nombre de la incidencia no puede estar vacío.");
            }
        });


        HBox botones = new HBox(10, Cancelar, Anadir);
        botones.setAlignment(Pos.CENTER_RIGHT);


        VBox layout = new VBox(10,
            new Label("Nombre de la incidencia:"), Nombre,
            new Label("Detalles / Descripción:"), textoDetalles,
            new Label("Solicitante:"), Solicitante,
            new Label("Estado inicial:"), Estado,
            new Label("Prioridad:"), Prioridad,
            botones
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");


        ventanaCrear.setScene(new Scene(layout, 440, 520));
        ventanaCrear.showAndWait();
    }


    @FXML
    private void handleModificar() {
        Incidencia seleccionada = listViewIncidencias.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            abrirVentanaModificar(seleccionada);
        }
    }


    @FXML
    private void handleEliminar() {
        Incidencia seleccionada = listViewIncidencias.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            if (seleccionada.estado.equalsIgnoreCase("Cerrada") || seleccionada.estado.equalsIgnoreCase("Completada")) {
                listaIncidencias.remove(seleccionada);
                actualizarContadores();
                Feedback.setText("  Incidencia eliminada del registro.");
            } else {
                Feedback.setText("  Aviso: Solo se pueden eliminar incidencias en estado Cerrada.");
            }
        }
    }


    private void abrirVentanaDetalles(Incidencia inc) {
        Stage ventanaDetalles = new Stage();
        ventanaDetalles.initModality(Modality.APPLICATION_MODAL);
        ventanaDetalles.setTitle("Detalles de la Incidencia");


        TextField txtNombre = new TextField(inc.nombre);
        txtNombre.setEditable(false);
        TextArea textoDetalles = new TextArea(inc.detalles);
        textoDetalles.setEditable(false);
        TextField txtEstado = new TextField(inc.estado);
        txtEstado.setEditable(false);
        TextField Prioridad = new TextField(inc.prioridad);
        Prioridad.setEditable(false);


        Button Cerrar = new Button("Cerrar Ventana");
        Cerrar.setOnAction(e -> ventanaDetalles.close());


        VBox layout = new VBox(10,
            new Label("Nombre Incidencia"), txtNombre,
            new Label("Detalles"), textoDetalles,
            new Label("Estado"), txtEstado,
            new Label("Prioridad"), Prioridad,
            Cerrar
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");


        ventanaDetalles.setScene(new Scene(layout, 440, 420));
        ventanaDetalles.showAndWait();
    }


    private void abrirVentanaModificar(Incidencia inc) {
        Stage ventanaModificar = new Stage();
        ventanaModificar.initModality(Modality.APPLICATION_MODAL);
        ventanaModificar.setTitle("Modificar Incidencia");


        TextField Nombre = new TextField(inc.nombre);
        TextArea textoDetalles = new TextArea(inc.detalles);
        ComboBox<String> Estado = new ComboBox<>(FXCollections.observableArrayList("Abierta", "Cerrada", "En proceso"));
        Estado.setValue(inc.estado);
        ComboBox<String> Prioridad = new ComboBox<>(FXCollections.observableArrayList("Crítica", "Urgente", "Media", "Baja"));
        Prioridad.setValue(inc.prioridad);


        Button Guardar = new Button("Guardar Cambios");
        Guardar.setOnAction(e -> {
            inc.nombre = Nombre.getText();
            inc.detalles = textoDetalles.getText();
            inc.estado = Estado.getValue();
            inc.prioridad = Prioridad.getValue();
            inc.ultimaActualizacion = "Hace un momento";
           
            int index = listaIncidencias.indexOf(inc);
            if (index >= 0) {
                listaIncidencias.set(index, inc);
            }
           
            actualizarContadores();
            Feedback.setText("  Cambios guardados para '" + inc.nombre + "'");
            ventanaModificar.close();
        });


        VBox layout = new VBox(10,
            new Label("Nombre Incidencia"), Nombre,
            new Label("Detalles"), textoDetalles,
            new Label("Modificar estado"), Estado,
            new Label("Modificar prioridad"), Prioridad,
            Guardar
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");


        ventanaModificar.setScene(new Scene(layout, 440, 460));
        ventanaModificar.showAndWait();
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
}
