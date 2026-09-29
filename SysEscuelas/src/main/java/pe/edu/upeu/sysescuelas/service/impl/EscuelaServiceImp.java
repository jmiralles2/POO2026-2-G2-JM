package pe.edu.upeu.sysescuelas.service.impl;

import lombok.RequiredArgsConstructor;
import pe.edu.upeu.sysescuelas.dto.ComboBoxOption;
import pe.edu.upeu.sysescuelas.enums.NivelEducativo;
import pe.edu.upeu.sysescuelas.exception.ClaveDuplicadaException;
import pe.edu.upeu.sysescuelas.model.Escuela;
import pe.edu.upeu.sysescuelas.repository.EscuelaRepository;
import pe.edu.upeu.sysescuelas.repository.ICrudGenericoRepository;
import pe.edu.upeu.sysescuelas.service.IEscuelaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class EscuelaServiceImp extends CrudGenericoServiceImp<Escuela, Long> implements IEscuelaService {

    private final EscuelaRepository escuelaRepository;

    @Override
    protected ICrudGenericoRepository<Escuela, Long> getRepo() {
        return escuelaRepository;
    }

    @Override
    public Escuela save(Escuela escuela) {
        validarClaveUnica(escuela);
        return super.save(escuela);
    }

    @Override
    public Escuela update(Long id, Escuela escuela) {
        validarClaveUnica(escuela);
        return super.update(id, escuela);
    }

    @Override
    public List<Escuela> findAll() {
        escuelaRepository.seedData();
        return super.findAll();
    }

    @Override
    public List<ComboBoxOption> listarNiveles() {
        List<ComboBoxOption> listar = new ArrayList<>();
        for (NivelEducativo n : NivelEducativo.values()) {
            ComboBoxOption cb = new ComboBoxOption();
            cb.setKey(n.name());
            cb.setValue(n.getDescripcion());
            listar.add(cb);
        }
        return listar;
    }

    /** La clave de centro de trabajo es única: no puede repetirse en otra escuela. */
    private void validarClaveUnica(Escuela escuela) {
        escuelaRepository.findByClave(escuela.getClave())
                .filter(existente -> !Objects.equals(existente.getIdEscuela(), escuela.getIdEscuela()))
                .ifPresent(existente -> {
                    throw new ClaveDuplicadaException(
                            "Ya existe una escuela con la clave " + escuela.getClave()
                                    + " (" + existente.getNombre() + ")");
                });
    }
}
