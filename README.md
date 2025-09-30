Proyecto-Group-2
Este proyecto es una aplicación web de backend desarrollada con Spring Boot y Java, diseñada para gestionar una base de datos de servicios de transporte. La aplicación expone una serie de endpoints RESTful que permiten la creación, lectura, actualización y eliminación de datos (CRUD) para entidades como usuarios, vehículos, ciudades, viajes y más.

🤖 El Papel de la Inteligencia Artificial Generativa
La creación de este archivo README fue asistida por inteligencia artificial generativa. Esta tecnología se empleó para estructurar la información de manera clara y lógica, generar la redacción de cada sección y asegurar un formato coherente, lo que optimizó el tiempo de documentación y permitió presentar la información de forma profesional y ordenada.

🚀 Tecnologías Utilizadas
Java 17: El lenguaje de programación principal.

Spring Boot: Framework para la creación de aplicaciones de Java, que facilita la configuración y el desarrollo de servicios RESTful.

Maven: Herramienta de gestión y automatización de proyectos para Java.

H2 Database: Base de datos relacional en memoria, ideal para el desarrollo y las pruebas.

Lombok: Librería que reduce el código boilerplate en las clases del modelo.

Postman: Herramienta utilizada para probar los endpoints de la API.

🛠️ Requisitos del Sistema
Java Development Kit (JDK) 17 o superior.

Maven.

Un IDE como IntelliJ IDEA o Visual Studio Code con el soporte de Java.

⚙️ Configuración y Ejecución
Clonar el repositorio:

Bash

git clone https://github.com/Juzou4/proyecto-group-2.git
Abrir el proyecto:
Importa el proyecto en tu IDE como un proyecto Maven.

Ejecutar la aplicación:
Ejecuta la clase principal ProyectoGroup2Application que se encuentra en src/main/java/com/juzou/proyectogroup2.

La aplicación se iniciará en http://localhost:8080 y la API estará disponible en la ruta /api.

📚 Endpoints de la API
La API del proyecto expone los siguientes endpoints para las operaciones CRUD:

http://localhost:8080/api/ciudades: Para la gestión de ciudades.

http://localhost:8080/api/disponibilidades: Para la gestión de la disponibilidad de vehículos.

http://localhost:8080/api/puntos-geograficos: Para la gestión de ubicaciones geográficas.

http://localhost:8080/api/revisiones: Para la gestión de revisiones de vehículos.

http://localhost:8080/api/servicios: Para la gestión de los tipos de servicio.

http://localhost:8080/api/usuarios: Para la gestión de usuarios.

http://localhost:8080/api/usuarios-conductores: Para la gestión de los conductores.

http://localhost:8080/api/usuarios-servicios: Para la gestión de la relación entre usuarios y servicios.

http://localhost:8080/api/vehiculos: Para la gestión de vehículos.

http://localhost:8080/api/viajes: Para la gestión de los viajes.

🤝 Contribución
Las contribuciones son bienvenidas. Siéntete libre de abrir un issue o enviar un pull request.
