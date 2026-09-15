package pe.edu.upeu.sysventas.service;

import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Producto;

import java.util.List;

public interface IProductoService extends ICrudGenericoservice<Producto, Long>{
    List<ComboBoxOption> listarComboBox();

}
