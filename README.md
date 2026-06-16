# Veterinaria+

Sistema de gestión integral para clínicas veterinarias. Proyecto académico
desarrollado para la materia de Ingeniería de Software II.

## Descripción

Veterinaria+ permite administrar mascotas, propietarios, veterinarios,
citas médicas, historiales clínicos y medicamentos en una única plataforma
web, con arquitectura monolítica MVC.

## Stack tecnológico

- **Backend:** Java 21, Spring Boot 3.4
- **Persistencia:** Spring Data JPA + Hibernate, PostgreSQL 15
- **Vista:** Thymeleaf + Bootstrap 5
- **Internacionalización:** Español, Inglés y Francés
- **Pruebas:** JUnit 5, Mockito, AssertJ, Spring MockMvc
- **Cobertura:** JaCoCo (mínimo 80%)
- **Contenedores:** Docker + Docker Compose
- **CI/CD:** GitHub Actions

## Cómo ejecutar el proyecto

### Opción 1: Todo en Docker (recomendado para evaluación)

Requiere tener Docker Desktop instalado y corriendo.

```bash
docker compose up -d --build
```

Esto levanta dos contenedores: la base de datos PostgreSQL y la aplicación
Spring Boot. La aplicación queda disponible en `http://localhost:8080`.

Para ver los logs de la aplicación:
```bash
docker compose logs -f app
```

Para detener todo:
```bash
docker compose down
```

Para detener y borrar también los datos de la base de datos:
```bash
docker compose down -v
```

### Opción 2: Aplicación local + base de datos en Docker (desarrollo)

Útil para desarrollar desde NetBeans con recarga rápida.

1. Levantar solo la base de datos:
```bash
docker run -d --name veterinaria-db -p 5432:5432 \
  -e POSTGRES_DB=veterinaria_plus \
  -e POSTGRES_USER=veterinaria_user \
  -e POSTGRES_PASSWORD=veterinaria_pass \
  postgres:15
```

2. Ejecutar la aplicación con el perfil `local`:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

3. Abrir `http://localhost:8080`

## Ejecutar las pruebas unitarias

```bash
mvn test
```

## Generar el reporte de cobertura (JaCoCo)

```bash
mvn clean test jacoco:report
```

El reporte HTML se genera en `target/site/jacoco/index.html`.

## Estructura del proyecto

```
src/main/java/com/veterinariaplus/app/
├── modelo/          Entidades JPA
├── repositorio/      Repositorios Spring Data JPA
├── servicio/          Interfaces de servicio (lógica de negocio)
│   ├── impl/          Implementaciones de servicio
│   └── disponibilidad/ Patrón Strategy (disponibilidad de veterinarios)
├── controlador/       Controladores Spring MVC
├── dto/                Data Transfer Objects
├── evento/             Patrón Observer (eventos de cambio de estado)
├── excepcion/          Excepciones de negocio
└── config/             Configuración (i18n, auditoría)

src/main/resources/
├── templates/          Vistas Thymeleaf
├── i18n/               Archivos de mensajes (ES/EN/FR)
└── application*.yml    Configuración por perfil
```

## Patrones de diseño aplicados

| Patrón | Ubicación |
|---|---|
| Repository | Paquete `repositorio` |
| Service Layer | Paquete `servicio` |
| DTO | Paquete `dto` |
| Builder | `modelo.Tratamiento.Builder` |
| Strategy | `servicio.disponibilidad` |
| Observer (Spring Events) | `evento` |
| Template Method | `modelo.EntidadBase` |

## Documentación adicional

Ver carpeta `docs/` para el manual de usuario y el manual técnico
(planteamiento del problema, requerimientos, casos de uso, diagrama de
base de datos y diccionario de datos).

## Profesor

Ing. Julio César Sarandingua Quintero — Ingeniería de Software II
