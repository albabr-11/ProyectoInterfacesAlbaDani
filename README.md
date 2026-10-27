El proyecto sigue una estructura limpia basada en el patrón MVC (Modelo-Vista-Controlador) dentro del paquete raíz ies. A continuación, se detalla la responsabilidad y el funcionamiento interno de cada clase principal:

### **1\. App.java** 

Esta clase es el corazón del ciclo de vida de la aplicación JavaFX.

Extiende la clase javafx.application.Application y actúa como el arrancador del programa.

Contiene el método estandar main(String\[\] args) que invoca a launch(), el cual inicializa el toolkit de JavaFX.

Sobrescribe el método start(Stage primaryStage), que es obligatorio en aplicaciones JavaFX. Este método recibe el escenario principal (Stage) que representa la ventana del sistema operativo.

Utiliza un FXMLLoader para cargar el archivo de diseño visual (dashboard.fxml).

Crea una instancia de la clase Scene pasándole el contenedor raíz cargado desde el FXML y le adjunta la hoja de estilos CSS (style.css).

Finalmente, configura el título de la ventana, asigna la escena al Stage y llama a primaryStage.show() para hacerla visible al usuario.

### **2\. Incidencia.java** 

Representa la entidad de negocio del sistema

Modela la estructura de los datos que se van a manipular, almacenar y mostrar en la interfaz (por ejemplo, en las tablas del dashboard).


Define los atributos privados de una incidencia (como ID, título, descripción, estado, fecha, etc.).

Implementa los constructores (vacío y parametrizados)

Al estar pensado para enlazarse dinámicamente con componentes visuales como un TableView, es muy común que en proyectos de este tipo se utilicen Propiedades observables de JavaFX (StringProperty, IntegerProperty, etc.). Esto permite que, si un dato cambia en el modelo, la interfaz gráfica se entere y se refresque automáticamente sin necesidad de recargar la vista a mano (*Two-way Data Binding*).

### **3\. MainController.java** 

Es la clase encargada de gestionar toda la lógica de interacción entre el usuario, la interfaz visual (dashboard.fxml) y los datos (Incidencia).

Escucha los eventos de los botones, gestiona las acciones de la tabla y actualiza los componentes gráficos.

Utiliza esta anotación para enlazar directamente los componentes definidos en el archivo FXML con variables de Java. Por ejemplo, referencias a tablas (TableView), columnas (TableColumn), botones (Button) o campos de texto (TextField).

Implementa la interfaz Initializable (o usa la anotación @FXML public void initialize()). Este método se ejecuta automáticamente justo después de que se ha cargado el archivo FXML. 

Se usa para:

Configurar las columnas de la tabla vinculandose con las propiedades del modelo Incidencia.

Cargar datos iniciales .

Añadir listeners o escuchas a los elementos (por ejemplo, detectar cuándo el usuario selecciona una fila de la tabla).

Manejadores de eventos : Contiene los métodos asociados a los clics de los botones (ej. agregarIncidencia(), eliminarIncidencia(), modificarEstado()), donde se captura la información introducida por el usuario, se valida y se actualiza la colección de datos de la aplicación.

### **5\. Recursos de la Interfaz (dashboard.fxml y style.css)**

dashboard.fxml: Define mediante etiquetas XML la jerarquía y disposición de los elementos visuales (paneles, tablas, menús, campos de formulario) utilizando Scene Builder. El archivo se vincula directamente a MainController.java mediante el atributo fx:controller="ies.MainController".

style.css: Aplica reglas de estilo CSS adaptadas a JavaFX para personalizar colores, tipografías, bordes y estados (como hover o focus en los botones), dándole un aspecto moderno a la aplicación de escritorio.