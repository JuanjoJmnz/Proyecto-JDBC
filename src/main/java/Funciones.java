import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.sql.SQLException;

public class Funciones {
    private Scanner sc = new Scanner(System.in);
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");


    public void mostrarUpdates(ConexionBBDD cb) {
        System.out.println("\n¿Qué quieres hacer?");
        System.out.println("1. Diagnóstico de un paciente");
        System.out.println("2. Especialidad de un doctor");
        System.out.println("3. Número de colegiado de un logopeda");
        System.out.println("4. Dar de alta un paciente");
        System.out.println("0. Volver");

        int opcion = pedirEntero("Elige una opción: ");

        try {
            switch(opcion){
                case 1: actualizarDiagnostico(cb); break;
                case 2: actualizarEspecialidad(cb); break;
                case 3: actualizarColegiado(cb); break;
                case 4: darAltaPaciente(cb); break;
                case 0: break;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) {
            System.out.println("Error al procesar la operación: " + e.getMessage());
        }
    }

    public void mostrarMenuCreacion(ConexionBBDD cb) {
        System.out.println("\n¿Qué quieres crear?");
        System.out.println("1. Nuevo Doctor");
        System.out.println("2. Nuevo Logopeda");
        System.out.println("3. Nuevo Paciente");
        System.out.println("4. Nueva Asignación");
        System.out.println("0. Volver");

        int opcion = pedirEntero("Elige una opción: ");

        try {
            switch(opcion){
                case 1: nuevoDoctor(cb); break;
                case 2: nuevoLogopeda(cb); break;
                case 3: nuevoPaciente(cb); break;
                case 4: nuevaAsignacion(cb); break;
                case 0: break;
                default: System.out.println("Opción no válida.");
            }
        } catch (Exception e) {
            System.out.println("Error al crear el registro: " + e.getMessage());
        }
    }


    private void actualizarDiagnostico(ConexionBBDD cb) {
        try {
            cb.mostrarPacientes();
            int id = pedirEntero("Introduce el ID del paciente: ");
            String diag = pedirCadena("Nuevo diagnóstico: ");
            cb.cambiarDiagnostico(id, diag);
            System.out.println("Diagnóstico actualizado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private void actualizarEspecialidad(ConexionBBDD cb) {
        try {
            cb.mostrarDoctores();
            int id = pedirEntero("Introduce el ID del doctor: ");
            String esp = pedirCadena("Nueva especialidad: ");
            cb.cambiarEspecialidad(id, esp);
            System.out.println("Especialidad actualizada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private void actualizarColegiado(ConexionBBDD cb) {
        try {
            cb.mostrarLogopedas();
            int id = pedirEntero("Introduce el ID del logopeda: ");
            String num = pedirCadena("Nuevo número de colegiado: ");
            cb.cambiarColegiado(id, num);
            System.out.println("Número de colegiado actualizado correctamente.");
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }

    private void darAltaPaciente(ConexionBBDD cb){
        try {
            cb.mostrarPacientes();
            int id = pedirEntero("Introduce el ID del paciente a dar de alta: ");
            cb.darAltaPaciente(id);
            System.out.println("Paciente dado de alta.");
        } catch (SQLException e) {
            System.out.println("Error de base de datos: " + e.getMessage());
        }
    }


    private void nuevaAsignacion(ConexionBBDD cb) {
        try {
            cb.mostrarPacientes();
            int idPac = pedirEntero("ID del paciente: ");

            cb.mostrarDoctores();
            int idDoc = pedirEntero("ID del doctor: ");

            cb.mostrarLogopedas();
            int idLogo = pedirEntero("ID del logopeda: ");

            double tiempo = pedirDouble("Tiempo de sesión (horas): ");

            OffsetDateTime fechaActualUTC = OffsetDateTime.now(ZoneOffset.UTC);
            boolean estadoActivo = true;

            cb.crearAsignacionConTransaccion(idPac, idDoc, idLogo, fechaActualUTC, estadoActivo, tiempo);
            System.out.println("Asignación creada exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error al crear asignación: " + e.getMessage());
        }
    }

    private void nuevoDoctor(ConexionBBDD cb){
        try {
            String email = pedirEmail("Email: ");
            String pass = pedirCadena("Contraseña: ");
            String nombre = pedirCadena("Nombre Completo: ");
            String esp = pedirCadena("Especialidad: ");

            cb.crearDoctorConTransaccion(email, pass, nombre, esp);
            System.out.println("Doctor creado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    private void nuevoLogopeda(ConexionBBDD cb) {
        try {
            String email = pedirEmail("Email: ");
            String pass = pedirCadena("Contraseña: ");
            String nombre = pedirCadena("Nombre Completo: ");
            String num = pedirCadena("Número de Colegiado: ");

            cb.crearLogopedaConTransaccion(email, pass, nombre, num);
            System.out.println("Logopeda creado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    private void nuevoPaciente(ConexionBBDD cb) {
        try {
            String email = pedirEmail("Email: ");
            String pass = pedirCadena("Contraseña: ");
            String nombre = pedirCadena("Nombre Completo: ");

            String fecha = pedirFecha("Fecha Nacimiento (dd-mm-yyyy): ");

            String diag = pedirCadena("Diagnóstico Principal: ");
            String historial = pedirCadena("Historial Médico: ");

            cb.crearPacienteConTransaccion(email, pass, nombre, fecha, diag, historial);
            System.out.println("Paciente creado exitosamente.");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }


    public void eliminarDoctor(ConexionBBDD cb){
        try {
            cb.mostrarDoctores();
            int id = pedirEntero("Introduce el ID del doctor a eliminar: ");
            cb.borrarDoctor(id);
            System.out.println("Doctor eliminado.");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    public void eliminarLogopeda(ConexionBBDD cb) {
        try {
            cb.mostrarLogopedas();
            int id = pedirEntero("Introduce el ID del logopeda a eliminar: ");
            cb.borrarLogopeda(id);
            System.out.println("Logopeda eliminado.");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    public void eliminarPaciente(ConexionBBDD cb) {
        try {
            cb.mostrarPacientes();
            int id = pedirEntero("Introduce el ID del paciente a eliminar: ");
            cb.borrarPaciente(id);
            System.out.println("Paciente eliminado.");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }

    public void eliminarAsignacion(ConexionBBDD cb) {
        try {
            cb.mostrarAsignaciones();
            int id = pedirEntero("Introduce el ID de la asignación a eliminar: ");
            cb.borrarAsignacion(id);
            System.out.println("Asignación eliminada.");
        } catch (SQLException e) {
            System.out.println("Error SQL: " + e.getMessage());
        }
    }


    private String pedirCadena(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("El campo no puede estar vacío.");
        }
    }


    private int pedirEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                int numero = Integer.parseInt(sc.nextLine().trim());
                if (numero < 0) {
                    System.out.println("Introduce un número positivo.");
                } else {
                    return numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debes introducir un número entero válido.");
            }
        }
    }


    private double pedirDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                double numero = Double.parseDouble(sc.nextLine().trim());
                if (numero < 0) {
                    System.out.println("El número no puede ser negativo.");
                } else {
                    return numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debes introducir un número decimal (usa punto . para decimales).");
            }
        }
    }


    private String pedirEmail(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();
            if (EMAIL_PATTERN.matcher(input).matches()) {
                return input;
            }
            System.out.println("Formato de email incorrecto.");
        }
    }


    private String pedirFecha(String mensaje) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (true) {
            System.out.print(mensaje);
            String input = sc.nextLine().trim();
            try {
                LocalDate fecha = LocalDate.parse(input, formatter);
                return input;
            } catch (DateTimeParseException e) {
                System.out.println("Fecha inválida.");
            }
        }
    }
}