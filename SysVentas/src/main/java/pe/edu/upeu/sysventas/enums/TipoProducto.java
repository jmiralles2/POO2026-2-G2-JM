package pe.edu.upeu.sysventas.enums;


import lombok.Getter;

@Getter
public enum TipoProducto {
    PRODUCTO("PRODUCTO"),
    PREPARADO("PREPARADO"),
    SERVICIO("SERVICIO");

    String descripcion;
    TipoProducto(String descripcion){
        this.descripcion=descripcion;
    }
}
