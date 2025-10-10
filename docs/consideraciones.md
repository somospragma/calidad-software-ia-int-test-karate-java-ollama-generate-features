## Consideraciones
- Para hacer uso de la la utilidad de Base de Datos es importante 
        que se instacie una Base de datos y se configura en el archivo de configuración ubicado en:

            ./src/main/resources/configs/congig.properties

        En las dependencias del proyecto esta agregada la dependencia del driver de MySQL, si no 
        desea realizar mayores ajustes respecto al motor de BD use MySQL. Si desea usar otro motor, 
        adiciones la dependencia del driver al build.gradle y configure este driver como observa 
        se realizo para MySQL en: 
    
            ./src/main/java/utils/ConectionBD.java
        
        Nota: Algunos motores de BD no requieren agregar la dependencia del driver como Oracle o MSserver
