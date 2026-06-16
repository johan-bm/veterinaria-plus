package com.veterinariaplus.app.repositorio;

import com.veterinariaplus.app.config.AuditingConfig;
import com.veterinariaplus.app.modelo.Cita;
import com.veterinariaplus.app.modelo.Especialidad;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.modelo.Veterinario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de integración de CitaRepository contra una base de datos
 * H2 en memoria (configurada en src/test/resources/application.yml).
 *
 * Estas pruebas validan que la consulta JPQL personalizada
 * existeSolapamiento() funcione correctamente contra un motor de
 * base de datos real, complementando las pruebas unitarias con
 * mocks de CitaServiceImplTest.
 *
 * Se importa AuditingConfig explícitamente porque @DataJpaTest solo
 * escanea entidades y repositorios por defecto: sin esta importación,
 * los campos created_at/updated_at de EntidadBase quedarían nulos al
 * guardar, violando la restricción NOT NULL de la base de datos.
 */
@DataJpaTest
@Import(AuditingConfig.class)
@DisplayName("CitaRepository (integración con H2)")
class CitaRepositoryTest {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private EspecieRepository especieRepository;

    @Autowired
    private EspecialidadRepository especialidadRepository;

    @Autowired
    private PropietarioRepository propietarioRepository;

    private Veterinario veterinario;
    private Mascota mascota;
    private final LocalDate fecha = LocalDate.now().plusDays(1);

    @BeforeEach
    void setUp() {
        Especie especie = especieRepository.save(new Especie("Felino", "Gato doméstico"));
        Propietario propietario = propietarioRepository.save(
                new Propietario("Marta", "Ruiz", "5550001111", "marta@example.com", "Av. Siempre Viva 123"));
        mascota = mascotaRepository.save(
                new Mascota("Michi", especie, "Siamés", LocalDate.of(2019, 5, 10), Mascota.Sexo.HEMBRA, propietario));

        Especialidad especialidad = especialidadRepository.save(new Especialidad("Dermatología", "Piel y pelaje"));
        veterinario = veterinarioRepository.save(new Veterinario("Pedro", "Salas", "VET-100", especialidad));

        // Cita existente: 10:00 - 10:30
        Cita citaExistente = new Cita(mascota, veterinario, fecha,
                LocalTime.of(10, 0), LocalTime.of(10, 30), "Consulta de control");
        citaExistente.setEstado(Cita.EstadoCita.AGENDADA);
        citaRepository.save(citaExistente);
    }

    @Test
    @DisplayName("detecta solapamiento cuando el nuevo horario se cruza exactamente con uno existente")
    void detectaSolapamientoExacto() {
        boolean haySolapamiento = citaRepository.existeSolapamiento(
                veterinario.getId(), fecha, LocalTime.of(10, 0), LocalTime.of(10, 30), null);

        assertThat(haySolapamiento).isTrue();
    }

    @Test
    @DisplayName("detecta solapamiento cuando el nuevo horario se cruza parcialmente")
    void detectaSolapamientoParcial() {
        // Nueva cita propuesta: 10:15 - 10:45 (se cruza con la existente 10:00-10:30)
        boolean haySolapamiento = citaRepository.existeSolapamiento(
                veterinario.getId(), fecha, LocalTime.of(10, 15), LocalTime.of(10, 45), null);

        assertThat(haySolapamiento).isTrue();
    }

    @Test
    @DisplayName("NO detecta solapamiento cuando el horario propuesto es completamente distinto")
    void noDetectaSolapamientoEnHorarioLibre() {
        // Horario propuesto: 14:00 - 14:30, sin relación con la cita existente
        boolean haySolapamiento = citaRepository.existeSolapamiento(
                veterinario.getId(), fecha, LocalTime.of(14, 0), LocalTime.of(14, 30), null);

        assertThat(haySolapamiento).isFalse();
    }

    @Test
    @DisplayName("NO detecta solapamiento contra sí misma cuando se excluye el id de la cita")
    void noDetectaSolapamientoAlExcluirLaPropiaCita() {
        Cita citaExistente = citaRepository.findAll().get(0);

        boolean haySolapamiento = citaRepository.existeSolapamiento(
                veterinario.getId(), fecha, LocalTime.of(10, 0), LocalTime.of(10, 30), citaExistente.getId());

        assertThat(haySolapamiento).isFalse();
    }

    @Test
    @DisplayName("NO detecta solapamiento cuando los horarios son consecutivos (uno termina cuando el otro empieza)")
    void noDetectaSolapamientoEnHorariosConsecutivos() {
        // La cita existente termina a las 10:30; una nueva que empieza exactamente
        // a las 10:30 no debería considerarse solapada.
        boolean haySolapamiento = citaRepository.existeSolapamiento(
                veterinario.getId(), fecha, LocalTime.of(10, 30), LocalTime.of(11, 0), null);

        assertThat(haySolapamiento).isFalse();
    }
}
