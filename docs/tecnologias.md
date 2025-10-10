## Tecnologías
### This project required:
- [JDK java] version 16
- [Serenity] version 4
- [Gradle] last version

Nota: 
*   Se requiere Selenium posterior a la version 4.11 para la descarga automatica de algunos drivers de los navegadores
    La version de Serenity implementada (4.0.0) ya incluye Selenium 4.12 lo cual soporta los navegadores a Octubre del 2023
    si el proyecto presenta problemas relacionados a las version del driver descargado de forma automatica y la version de su 
    navegador vale la pena revisar que este trabajando con versiones recientes de Serenity y checkear las versiones de Selenium
    incluidas en dicha version de Serenity
*   Con Selenium Manager incluido en Serenity 4.0.0 ya no se requiere WebDriverManager de Boni Garcia, razon por la cual ya
    serenity no lo incluye dentro de sus dependencias
