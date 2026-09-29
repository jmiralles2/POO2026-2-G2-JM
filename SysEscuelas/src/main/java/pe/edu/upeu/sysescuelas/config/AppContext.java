package pe.edu.upeu.sysescuelas.config;

import pe.edu.upeu.sysescuelas.controller.EscuelaController;
import pe.edu.upeu.sysescuelas.controller.MainGuiController;
import pe.edu.upeu.sysescuelas.repository.EscuelaRepository;
import pe.edu.upeu.sysescuelas.service.IEscuelaService;
import pe.edu.upeu.sysescuelas.service.impl.EscuelaServiceImp;

import java.util.HashMap;
import java.util.Map;

public class AppContext {

    // Singleton: una sola instancia en toda la app
    private static AppContext instance;

    public static synchronized AppContext getInstance() {
        if (instance == null) instance = new AppContext();
        return instance;
    }

    // El "directorio": Clase → Objeto
    private final Map<Class<?>, Object> contenedor = new HashMap<>();

    private AppContext() {
        registrarRepositorios();
        registrarServicios();
        registrarControladores();
    }

    // CAPA 1 — REPOSITORIOS
    private void registrarRepositorios() {
        registrar(EscuelaRepository.class, new EscuelaRepository());
    }

    // CAPA 2 — SERVICIOS (reciben su repositorio por constructor)
    private void registrarServicios() {
        registrar(IEscuelaService.class, new EscuelaServiceImp(getBean(EscuelaRepository.class)));
    }

    // CAPA 3 — CONTROLADORES JavaFX (reciben sus servicios por constructor)
    private void registrarControladores() {
        registrar(MainGuiController.class, new MainGuiController());
        registrar(EscuelaController.class, new EscuelaController(getBean(IEscuelaService.class)));
    }

    private void registrar(Class<?> tipo, Object bean) {
        contenedor.put(tipo, bean);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tipo) {
        Object bean = contenedor.get(tipo);
        if (bean == null) {
            bean = contenedor.values().stream()
                    .filter(b -> tipo.isAssignableFrom(b.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(
                            "Bean no encontrado: " + tipo.getName() +
                                    "\n→ ¿Lo registraste en AppContext?"));
        }
        return (T) bean;
    }
}
