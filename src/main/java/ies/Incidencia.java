package ies;

 public class Incidencia {
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

