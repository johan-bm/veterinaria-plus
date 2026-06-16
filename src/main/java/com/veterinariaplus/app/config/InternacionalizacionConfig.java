package com.veterinariaplus.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.time.Duration;
import java.util.Locale;

/**
 * Configuracion de internacionalizacion (i18n) de la aplicacion.
 *
 * Soporta tres idiomas: espanol (por defecto), ingles y frances.
 * El idioma seleccionado se persiste en una cookie para que se
 * mantenga entre sesiones del navegador.
 *
 * El cambio de idioma se realiza agregando el parametro ?lang=xx
 * a cualquier URL de la aplicacion, por ejemplo: /citas?lang=en
 */
@Configuration
public class InternacionalizacionConfig implements WebMvcConfigurer {

    private static final String PARAMETRO_IDIOMA = "lang";
    private static final String NOMBRE_COOKIE_IDIOMA = "VETERINARIA_PLUS_LANG";
    private static final int DURACION_COOKIE_SEGUNDOS = (int) Duration.ofDays(30).toSeconds();

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver(NOMBRE_COOKIE_IDIOMA);
        resolver.setDefaultLocale(Locale.of("es"));
        resolver.setCookieMaxAge(Duration.ofSeconds(DURACION_COOKIE_SEGUNDOS));
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(PARAMETRO_IDIOMA);
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
