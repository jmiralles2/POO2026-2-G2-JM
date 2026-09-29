package pe.edu.upeu.sysescuelas.repository;

import pe.edu.upeu.sysescuelas.enums.NivelEducativo;
import pe.edu.upeu.sysescuelas.model.Escuela;

import java.util.Optional;

public class EscuelaRepository extends AbstractJpaRepository<Escuela, Long> {
    private long sequence = 1;
    private boolean sembrado = false;

    @Override
    protected Long getId(Escuela entity) {
        return entity.getIdEscuela();
    }

    @Override
    protected void setId(Escuela entity, Long id) {
        entity.setIdEscuela(id);
    }

    @Override
    protected Long generateId() {
        return sequence++;
    }

    /** Busca una escuela por su clave de centro de trabajo (sin distinguir mayúsculas). */
    public Optional<Escuela> findByClave(String clave) {
        return data.stream()
                .filter(e -> e.getClave().equalsIgnoreCase(clave))
                .findFirst();
    }

    /** Datos de ejemplo (ficticios). Se cargan una sola vez. */
    public void seedData() {
        if (sembrado) return;
        sembrado = true;
        save(Escuela.builder().nombre("Jardín de Niños Rosaura Zapata")
                .nivel(NivelEducativo.PREESCOLAR).clave("09DJN0101A")
                .direccion("Calle Hidalgo 45, Col. Centro").matricula(120).build());
        save(Escuela.builder().nombre("Escuela Primaria Benito Juárez")
                .nivel(NivelEducativo.PRIMARIA).clave("09DPR0202B")
                .direccion("Av. Reforma 120, Col. Juárez").matricula(340).build());
        save(Escuela.builder().nombre("Escuela Secundaria Técnica No. 5")
                .nivel(NivelEducativo.SECUNDARIA).clave("09DST0005C")
                .direccion("Calle Morelos 310, Col. Obrera").matricula(520).build());
        save(Escuela.builder().nombre("Preparatoria Oficial No. 12")
                .nivel(NivelEducativo.PREPARATORIA).clave("09DCT0012D")
                .direccion("Av. Universidad 800, Col. Copilco").matricula(610).build());
    }
}
