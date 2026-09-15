package pe.edu.upeu.sysventas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysventas.dto.ComboBoxOption;
import pe.edu.upeu.sysventas.model.UnidMedida;
import pe.edu.upeu.sysventas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysventas.repository.UnidadMedidaRepository;
import pe.edu.upeu.sysventas.service.IUnidadMedidaService;

import java.util.List;

@RequiredArgsConstructor

public class UnidadMedidaServiceImp extends CrudGenericoServiceImp<UnidMedida, Long>
        implements IUnidadMedidaService {
    @Override
    public List<ComboBoxOption> listarComboBox() {
        return List.of();
    }
private final UnidadMedidaRepository unidadMedidaRepository;
    @Override
    protected ICrudGenericoRepository<UnidMedida, Long> getRepo() {
        return unidadMedidaRepository;
    }
}
