package pe.edu.upeu.sysescuelas.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.upeu.sysescuelas.enums.NivelEducativo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Escuela {

    private Long idEscuela;

    @NotBlank(message = "El nombre del plantel es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
    private String nombre;

    @NotNull(message = "El nivel educativo es obligatorio")
    private NivelEducativo nivel;

    // Clave de centro de trabajo (CCT). Ejemplo: 09DPR0123A
    @NotBlank(message = "La clave de centro de trabajo es obligatoria")
    @Pattern(regexp = "^\\d{2}[A-Z]{3}\\d{4}[A-Z]$",
            message = "La clave debe tener el formato 09DPR0123A (2 dígitos, 3 letras, 4 dígitos, 1 letra)")
    private String clave;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "La matrícula total es obligatoria")
    @PositiveOrZero(message = "La matrícula debe ser un número entero mayor o igual a 0")
    private Integer matricula;
}
