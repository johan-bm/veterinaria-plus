package com.veterinariaplus.app.servicio.impl;

import com.veterinariaplus.app.dto.MascotaDTO;
import com.veterinariaplus.app.excepcion.RecursoNoEncontradoException;
import com.veterinariaplus.app.modelo.Especie;
import com.veterinariaplus.app.modelo.Mascota;
import com.veterinariaplus.app.modelo.Propietario;
import com.veterinariaplus.app.repositorio.EspecieRepository;
import com.veterinariaplus.app.repositorio.MascotaRepository;
import com.veterinariaplus.app.repositorio.PropietarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de MascotaServiceImpl, enfocadas en la resolución
 * de relaciones (Especie, Propietario) al guardar desde el DTO y en
 * el flujo de creación frente a actualización.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MascotaServiceImpl")
class MascotaServiceImplTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private EspecieRepository especieRepository;

    @Mock
    private PropietarioRepository propietarioRepository;

    @InjectMocks
    private MascotaServiceImpl mascotaService;

    private Especie especie;
    private Propietario propietario;
    private MascotaDTO dto;

    @BeforeEach
    void setUp() {
        especie = new Especie("Canino", "Perro doméstico");
        especie.setId(1L);

        propietario = new Propietario("Ana", "Pérez", "5551234567", "ana@example.com", "Calle 1");
        propietario.setId(1L);

        dto = new MascotaDTO();
        dto.setEspecieId(1L);
        dto.setPropietarioId(1L);
        dto.setNombre("Firulais");
        dto.setRaza("Labrador");
        dto.setFechaNacimiento(LocalDate.of(2020, 1, 1));
        dto.setSexo(Mascota.Sexo.MACHO);
    }

    @Test
    @DisplayName("crea una nueva mascota cuando el DTO no trae id")
    void creaNuevaMascotaSinId() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));

        Mascota resultado = mascotaService.guardarDesdeDto(dto);

        assertThat(resultado.getNombre()).isEqualTo("Firulais");
        assertThat(resultado.getEspecie()).isEqualTo(especie);
        assertThat(resultado.getPropietario()).isEqualTo(propietario);
        verify(mascotaRepository, never()).findById(any());
    }

    @Test
    @DisplayName("actualiza una mascota existente cuando el DTO trae id")
    void actualizaMascotaExistenteConId() {
        dto.setId(5L);
        Mascota mascotaExistente = new Mascota();
        mascotaExistente.setId(5L);

        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(mascotaRepository.findById(5L)).thenReturn(Optional.of(mascotaExistente));
        when(mascotaRepository.save(any(Mascota.class))).thenAnswer(inv -> inv.getArgument(0));

        Mascota resultado = mascotaService.guardarDesdeDto(dto);

        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getNombre()).isEqualTo("Firulais");
    }

    @Test
    @DisplayName("lanza excepción cuando la especie del DTO no existe")
    void lanzaExcepcionCuandoEspecieNoExiste() {
        when(especieRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.guardarDesdeDto(dto))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(mascotaRepository, never()).save(any());
    }

    @Test
    @DisplayName("lanza excepción cuando el propietario del DTO no existe")
    void lanzaExcepcionCuandoPropietarioNoExiste() {
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(propietarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.guardarDesdeDto(dto))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("lanza excepción al actualizar una mascota que no existe")
    void lanzaExcepcionAlActualizarMascotaInexistente() {
        dto.setId(99L);
        when(especieRepository.findById(1L)).thenReturn(Optional.of(especie));
        when(propietarioRepository.findById(1L)).thenReturn(Optional.of(propietario));
        when(mascotaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mascotaService.guardarDesdeDto(dto))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("buscarTodos delega en el repositorio")
    void buscarTodosDelegaEnRepositorio() {
        when(mascotaRepository.findAll()).thenReturn(List.of(new Mascota()));

        List<Mascota> resultado = mascotaService.buscarTodos();

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("buscar con texto vacío devuelve todas las mascotas")
    void buscarConTextoVacioDevuelveTodas() {
        when(mascotaRepository.findAll()).thenReturn(List.of(new Mascota()));

        List<Mascota> resultado = mascotaService.buscar("   ");

        assertThat(resultado).hasSize(1);
        verify(mascotaRepository, never()).buscar(any());
    }

    @Test
    @DisplayName("buscar con texto delega la búsqueda al repositorio")
    void buscarConTextoDelegaAlRepositorio() {
        when(mascotaRepository.buscar("Firulais")).thenReturn(List.of(new Mascota()));

        List<Mascota> resultado = mascotaService.buscar("Firulais");

        assertThat(resultado).hasSize(1);
    }

    @Test
    @DisplayName("buscarPorId delega en el repositorio")
    void buscarPorIdDelegaEnRepositorio() {
        Mascota mascota = new Mascota();
        mascota.setId(1L);
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(mascota));

        Optional<Mascota> resultado = mascotaService.buscarPorId(1L);

        assertThat(resultado).isPresent();
    }

    @Test
    @DisplayName("elimina correctamente una mascota existente")
    void eliminaMascotaExistente() {
        when(mascotaRepository.existsById(1L)).thenReturn(true);

        mascotaService.eliminar(1L);

        verify(mascotaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("lanza excepción al eliminar una mascota inexistente")
    void lanzaExcepcionAlEliminarMascotaInexistente() {
        when(mascotaRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> mascotaService.eliminar(99L))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(mascotaRepository, never()).deleteById(any());
    }
}
