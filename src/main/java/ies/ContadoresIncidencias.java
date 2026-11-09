package ies;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;


// se ha creado la clase para mejorar el ra 1.e en el cual se nos aconsejaba extraer las tarjetas
//  a una clase propia como mejora de legibilidad. 

public class ContadoresIncidencias {
     private final ObservableList<Incidencia> lista;
    private final Label total, abiertas, enProceso, cerradas;


    public ContadoresIncidencias(ObservableList<Incidencia> lista,
                                 Label total, Label abiertas,
                                 Label enProceso, Label cerradas) {
        this.lista = lista;
        this.total = total;
        this.abiertas = abiertas;
        this.enProceso = enProceso;
        this.cerradas = cerradas;


        // Se actualiza solo cuando la lista cambia (add, remove, set)
        lista.addListener((ListChangeListener<Incidencia>) c -> actualizar());
        actualizar();
    }


    public void actualizar() {
        int a = 0, p = 0, c = 0;
        for (Incidencia i : lista) {
            String est = i.estado.toLowerCase();
            if (est.contains("abierta")) a++;
            else if (est.contains("proceso")) p++;
            else if (est.contains("cerrada")) c++;
        }
        total.setText(String.valueOf(lista.size()));
        abiertas.setText(String.valueOf(a));
        enProceso.setText(String.valueOf(p));
        cerradas.setText(String.valueOf(c));
    }

}

