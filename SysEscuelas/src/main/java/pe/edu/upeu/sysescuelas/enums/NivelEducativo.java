package pe.edu.upeu.sysescuelas.enums;

import lombok.Getter;

@Getter
public enum NivelEducativo {
    PREESCOLAR("Preescolar"),
    PRIMARIA("Primaria"),
    SECUNDARIA("Secundaria"),
    PREPARATORIA("Preparatoria");

    private final String descripcion;

    NivelEducativo(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
