package pe.edu.upeu.sysescuelas.service;

import pe.edu.upeu.sysescuelas.dto.ComboBoxOption;
import pe.edu.upeu.sysescuelas.model.Escuela;

import java.util.List;

public interface IEscuelaService extends ICrudGenericoService<Escuela, Long> {
    List<ComboBoxOption> listarNiveles();
}
