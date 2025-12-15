import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        try (ConexionBBDD cb = new ConexionBBDD()) {
            Scanner sc = new Scanner(System.in);
            Funciones funciones = new Funciones();

             cb.borrarTodo();
             cb.crearTablas();
             cb.crearProcedimiento();
             cb.insertsDePrueba();

            boolean salir = false;
            while (!salir) {
                System.out.println("\n======= GESTIÓN HOSPITAL =======");
                System.out.println("1. Ver datos (Consultas)");
                System.out.println("2. Insertar datos (Transacciones)");
                System.out.println("3. Modificar datos (Procedimientos)");
                System.out.println("-----------------------------------");
                System.out.println("4. Eliminar un Doctor (Registro)");
                System.out.println("5. Eliminar un Logopeda (Registro)");
                System.out.println("6. Eliminar un Paciente (Registro)");
                System.out.println("7. Eliminar una Asignación (Registro)");
                System.out.println("-----------------------------------");
                System.out.println("8. Eliminar tabla ASIGNACIONES (DROP)");
                System.out.println("9. Eliminar tabla DOCTORES (DROP)");
                System.out.println("10. Eliminar tabla LOGOPEDAS (DROP)");
                System.out.println("11. Eliminar tabla PACIENTES (DROP)");
                System.out.println("0. Salir");
                System.out.print("Elige una opción: ");

                String entrada = sc.nextLine();

                try {
                    switch (entrada) {
                        case "1":
                            mostrarMenuConsultas(cb);
                            break;
                        case "2":
                            funciones.mostrarMenuCreacion(cb);
                            break;
                        case "3":
                            funciones.mostrarUpdates(cb);
                            break;
                        case "4":
                            funciones.eliminarDoctor(cb);
                            break;
                        case "5":
                            funciones.eliminarLogopeda(cb);
                            break;
                        case "6":
                            funciones.eliminarPaciente(cb);
                            break;
                        case "7":
                            funciones.eliminarAsignacion(cb);
                            break;
                        case "8":
                            cb.borrarAsignaciones();
                            System.out.println("Tabla Asignaciones eliminada.");
                            break;
                        case "9":
                            cb.borrarDoctores();
                            System.out.println("Tabla Doctores eliminada.");
                            break;
                        case "10":
                            cb.borrarLogopedas();
                            System.out.println("Tabla Logopedas eliminada.");
                            break;
                        case "11":
                            cb.borrarPacientes();
                            System.out.println("Tabla Pacientes eliminada.");
                            break;
                        case "0":
                            salir = true;
                            System.out.println("Cerrando");
                            break;
                        default:
                            System.out.println("Opción no válida.");
                    }
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error de conexión");
        } catch (Exception e) {
            System.err.println("Error general");
        }
    }

    private static void mostrarMenuConsultas(ConexionBBDD cb) throws SQLException {
        Scanner sc = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n--- SUB-MENÚ DE CONSULTAS ---");
            System.out.println("1. Mostrar todos los doctores");
            System.out.println("2. Mostrar todos los logopedas");
            System.out.println("3. Mostrar todos los pacientes");
            System.out.println("4. Mostrar todas las asignaciones");
            System.out.println("5. Buscar doctores por especialidad (Exacta)");
            System.out.println("6. Buscar doctores por especialidad (Parcial)");
            System.out.println("7. Buscar pacientes por diagnóstico");
            System.out.println("8. Ver hora de pacientes en otra zona horaria");
            System.out.println("0. Volver al menú principal");
            System.out.print("Elige una opción: ");

            String entrada = sc.nextLine();

            switch (entrada) {
                case "1":
                    cb.mostrarDoctores();
                    break;
                case "2":
                    cb.mostrarLogopedas();
                    break;
                case "3":
                    cb.mostrarPacientes();
                    break;
                case "4":
                    cb.mostrarAsignaciones();
                    break;
                case "5":
                    System.out.println("Introduce la especialidad exacta:");
                    String espExacta = sc.nextLine();
                    cb.mostrarDoctoresSegunEspecialidad(espExacta);
                    break;
                case "6":
                    System.out.println("Introduce parte del nombre de la especialidad:");
                    String espParcial = sc.nextLine();
                    cb.mostrarDoctoresSegunEspecialidadParcial(espParcial);
                    break;
                case "7":
                    System.out.println("Introduce el diagnóstico:");
                    String diag = sc.nextLine();
                    cb.mostrarPacientesSegunDiagnostico(diag);
                    break;
                case "8":
                    System.out.println("Introduce la zona horaria (Ej: Asia/Tokyo, America/New_York):");
                    String zona = sc.nextLine();
                    cb.mostrarPacientesConHoraDistinta(zona);
                    break;
                case "0":
                    salir = true;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}