
##  🛠️ Run tests Chrome gradle:
```
gradle clean test -Dcontext=chrome -Dwebdriver.driver=chrome
gradle clean test --info --stacktrace --tests "ruta.nameRunner" -Dcontext=chrome -Dwebdriver.driver=chrome
gradle clean test -Dcucumber.options="--tags @someTag" -Dcontext=chrome -Dwebdriver.driver=chrome
gradle clean test -Dcucumber.options="--tags '@someTag or @someTag'" -Dcontext=chrome -Dwebdriver.driver=chrome
```

Nota:

*   Si ejecuta en la consola de gradle no debe usar comillas simples '...' para encerrar '-Dwebdriver.driver=chrome'
*   Si ejecuta en la consola estándar de la máquina quizás si deba utilizar '...' en las porciones del comando que incluyan puntos
*   Con "./gradlew test ..." ejecuta el gradle compilado del proyecto
*   Con "gradle test ..." ejecuta el gradle de su maquina, el configurado en las variables de entorno de su sistema operativo


### ejemplo
```
./gradlew clean test --info --stacktrace --tests "co.com.pragma.runners.CompareImageRunner" -Dcontext=chrome '-Dwebdriver.driver=chrome'
./gradlew clean test --info --stacktrace --tests "co.com.pragma.runners.LoginRunner" -Dcontext=chrome '-Dwebdriver.driver=chrome'
```


##  🛠️ Run tests Firefox gradle:
```
./gradlew clean test -Dcontext=firefox '-Dwebdriver.driver=firefox'
./gradlew test --tests "runners.RunnerTags" '-Dcontext=firefox -Dwebdriver.driver=firefox'
```
### ejemplo
```
./gradlew clean test --info --stacktrace --tests "runners.RunnerTags" '-Dcontext=firefox -Dwebdriver.driver=firefox'
```

## **Run tests in different environments:**
```
gradle command... -Denvironment=defaul
gradle command... -Denvironment=dev
gradle command... -Denvironment=qa
gradle command... -Denvironment=prod
```
### Note: 
    - The default environment will be used if no other value is provided
    - Could modify the environment urls in .../test/resources/serenity.conf


## **Run tests in different browser:**
```
gradle command... -Dwebdriver.driver=chrome
gradle command... -Dwebdriver.driver=firefox
gradle command... -Dwebdriver.driver=edge
```
