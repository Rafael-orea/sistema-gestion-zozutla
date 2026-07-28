# Sistema de Gestión Artesanías Zozutla

Sistema de escritorio desarrollado en JavaFX para la gestión integral de Artesanías Zozutla, empresa dedicada a la fabricación de alcancías de cerámica ubicada en México.

## Equipo de desarrollo - Team F.E.V.E.R

- Víctor Manuel Bello Ponce - Product Owner
- Erick Gabriel Salinas Miranda - Scrum Master
- Rafael Orea Carrera - Desarrollador
- Emiliano Reyes Aparicio - Desarrollador
- Fernanda Fernández Rodríguez - Desarrolladora

## Tecnologías utilizadas

- Java 21
- JavaFX 21
- MariaDB / MySQL
- XAMPP
- Maven
- IntelliJ IDEA

## Módulos del sistema

- Panel Principal
- Gestión de Moldes
- Inventario de Alcancías
- Registro de Ventas
- Historial de Ventas
- Clientes
- Insumos y Costos
- Envíos
- Empleados
- Reportes

## Requisitos previos

- JDK 21
- XAMPP con MariaDB corriendo
- Maven instalado

## Configuración de base de datos

1. Iniciar XAMPP y activar MySQL
2. Abrir phpMyAdmin en http://localhost/phpmyadmin
3. Crear la base de datos `zosutla`
4. Ejecutar los scripts SQL del proyecto

## Configuración del proyecto

Clonar el repositorio:

```bash
git clone https://github.com/Rafael-orea/sistema-gestion-zozutla.git
```

Configurar la conexión en `src/main/java/Model/ConexionBD.java`:

```java
private static final String URL = "jdbc:mysql://localhost:3306/zosutla";
private static final String USER = "root";
private static final String PASSWORD = "";
```

Compilar y ejecutar:

```bash
mvn clean compile
mvn javafx:run
```

## Metodología

Proyecto desarrollado bajo la metodología Scrum con 8 sprints.
