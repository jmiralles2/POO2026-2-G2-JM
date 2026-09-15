package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.Producto;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.ProductoRepository;
import pe.edu.upeu.sysventas.service.IProductoService;

import java.util.List;

@RequiredArgsConstructor

public class ProductoServiceImp extends CrudGenericoServiceImp<Producto, Long> implements IProductoService {

    private final ProductoRepository productoRepository;

    @Override
    public List<ComboBoxOption> listarComboBox() {
        return List.of();
    }

    @Override
    protected ICrudGenericoRepository<Producto, Long> getRepo() {
        return productoRepository;
    }

}
