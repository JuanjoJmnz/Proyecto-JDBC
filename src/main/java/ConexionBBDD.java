import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class ConexionBBDD implements AutoCloseable {
    private static final String url = "jdbc:mysql://pjisr8.h.filess.io:61002/baseDatos_JDBC_borntimeif";
    private static final String usuario = "baseDatos_JDBC_borntimeif";
    private static final String contra = "13bbe7e9e98a50dc78fec66eac9fd5e7800daf4f";
    private Connection con;

    public ConexionBBDD() {
        try {
            this.con = DriverManager.getConnection(url, usuario, contra);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la BBDD", e);
        }
    }

    public Connection getConexion() {
        return con;
    }

    public void cerrarConexion() throws SQLException {
        con.close();
    }


// -----------------------------------------------------------------------------------------------------------------


    // CREATE TABLES
    public void crearTablas() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            String tableUsers = "CREATE TABLE IF NOT EXISTS users (userId int PRIMARY KEY AUTO_INCREMENT, email varchar(255), passwordHash varchar(255), nombreCompleto varchar(255), rol enum('Doctor', 'Logopeda', 'Paciente'))";
            stmt.execute(tableUsers);

            String tableDoctores = "CREATE TABLE IF NOT EXISTS doctores (doctorId int PRIMARY KEY AUTO_INCREMENT, userId int, especialidad varchar(255), FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE)";
            stmt.execute(tableDoctores);

            String tableLogopedas = "CREATE TABLE IF NOT EXISTS logopedas (logopedaId int PRIMARY KEY AUTO_INCREMENT, userId int, numeroColegiado varchar(10), FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE)";
            stmt.execute(tableLogopedas);

            String tablePacientes = "CREATE TABLE IF NOT EXISTS pacientes ( pacienteId int PRIMARY KEY AUTO_INCREMENT, userId int, fechaNacimiento date, diagnosticoPrincipal text, historialMedico text, horaConexion datetime, FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE)";
            stmt.execute(tablePacientes);

            String tableAsignacion = "CREATE TABLE IF NOT EXISTS pacienteAsignacion (asignacionId int PRIMARY KEY AUTO_INCREMENT, pacienteId int, doctorId int, logopedaId int, fechaAsignacion datetime, pacienteActivo tinyint, tiempoSesion DECIMAL(4,2), FOREIGN KEY (pacienteId) REFERENCES pacientes(pacienteId) ON DELETE CASCADE, FOREIGN KEY (doctorId) REFERENCES doctores(doctorId) ON DELETE CASCADE, FOREIGN KEY (logopedaId) REFERENCES logopedas(logopedaId) ON DELETE CASCADE)";
            stmt.execute(tableAsignacion);
        }
    }


// -----------------------------------------------------------------------------------------------------------------


    // DROP TABLESS
    public void borrarTodo() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS pacienteAsignacion");
            stmt.execute("DROP TABLE IF EXISTS pacientes");
            stmt.execute("DROP TABLE IF EXISTS logopedas");
            stmt.execute("DROP TABLE IF EXISTS doctores");
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("DROP PROCEDURE IF EXISTS actualizarDiagnosticoPacientes");
            stmt.execute("DROP PROCEDURE IF EXISTS actualizarEspecialidadDoctores");
            stmt.execute("DROP PROCEDURE IF EXISTS actualizarColegiadoLogopedas");
            stmt.execute("DROP PROCEDURE IF EXISTS darAltaPaciente");
        }
    }

    public void borrarAsignaciones() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS pacienteAsignacion");
        }
    }

    public void borrarPacientes() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS pacientes");
        }
    }

    public void borrarDoctores() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS doctores");
        }
    }

    public void borrarLogopedas() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS logopedas");
        }
    }

    public void borrarUsuarios() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
        }
    }


// -----------------------------------------------------------------------------------------------------------------


    // SELECTS
    public void mostrarDoctores() throws SQLException {
        System.out.println("\n--- LISTA DE DOCTORES ---");
        String sql = "SELECT d.doctorId, u.nombreCompleto, d.especialidad FROM doctores d JOIN users u ON d.userId = u.userId";
        try(Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                System.out.println("ID: " + rs.getInt("doctorId") + " | Nombre: " + rs.getString("nombreCompleto") + " | Especialidad: " + rs.getString("especialidad"));
            }
        }
    }

    public void mostrarLogopedas() throws SQLException {
        System.out.println("\n--- LISTA DE LOGOPEDAS ---");
        String sql = "SELECT l.logopedaId, u.nombreCompleto, l.numeroColegiado FROM logopedas l JOIN users u ON l.userId = u.userId";
        try(Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("logopedaId") + " | Nombre: " + rs.getString("nombreCompleto") + " | Colegiado: " + rs.getString("numeroColegiado"));
            }
        }
    }

    public void mostrarPacientes() throws SQLException {
        System.out.println("\n--- LISTA DE PACIENTES ---");
        String sql = "SELECT p.pacienteId, u.nombreCompleto, p.fechaNacimiento, p.diagnosticoPrincipal, p.historialMedico, p.horaConexion FROM pacientes p JOIN users u ON p.userId = u.userId";        try(Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                System.out.println("ID: " + rs.getInt("pacienteId") + " | Nombre: " + rs.getString("nombreCompleto") + " | Fecha de nacimiento: " + rs.getString("fechaNacimiento") + " | Diagnóstico: " + rs.getString("diagnosticoPrincipal") + " | Historial médico: " + rs.getString("historialMedico") + " | Hora de conexión: " + rs.getString("horaConexion"));
            }
        }
    }

    public void mostrarAsignaciones() throws SQLException {
        System.out.println("\n--- LISTA DE ASIGNACIONES ---");
        String sql = "SELECT * FROM pacienteAsignacion";
        try(Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                System.out.println("ID: " + rs.getInt("asignacionId") + " | Paciente: " + rs.getInt("pacienteId") + " | Doctor: " + rs.getInt("doctorId") + " | Logopeda: " + rs.getInt("logopedaId") + " | Fecha: " + rs.getString("fechaAsignacion") + " | Activo: " + rs.getBoolean("pacienteActivo") + " | Tiempo medio por sesión (horas): " + rs.getDouble("tiempoSesion"));
            }
        }
    }

    public void mostrarDoctoresSegunEspecialidad(String especialidad) throws SQLException {
        System.out.println("\n--- LISTA DE DOCTORES EN " + especialidad.toUpperCase() + " ---");
        String sql = "SELECT d.doctorId, u.nombreCompleto, d.especialidad FROM doctores d JOIN users u ON d.userId = u.userId WHERE d.especialidad = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, especialidad);
            try (ResultSet rs = stmt.executeQuery()) {
                boolean hayResultados = false;
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("doctorId") +
                            " | Nombre: " + rs.getString("nombreCompleto") +
                            " | Especialidad: " + rs.getString("especialidad"));
                    hayResultados = true;
                }
                if (!hayResultados){
                    System.out.println("No hay doctores con esa especialidad exacta.");
                }
            }
        }
    }

    public void mostrarDoctoresSegunEspecialidadParcial(String especialidad) throws SQLException {
        System.out.println("\n--- LISTA DE DOCTORES (BÚSQUEDA PARCIAL): " + especialidad.toUpperCase() + " ---");
        String sql = "SELECT d.doctorId, u.nombreCompleto, d.especialidad FROM doctores d JOIN users u ON d.userId = u.userId WHERE d.especialidad LIKE ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, "%" + especialidad + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("doctorId") +
                            " | Nombre: " + rs.getString("nombreCompleto") +
                            " | Especialidad: " + rs.getString("especialidad"));
                }
            }
        }
    }

    public void mostrarPacientesSegunDiagnostico(String diagnostico) throws SQLException {
        System.out.println("\n--- LISTA DE PACIENTES CON " + diagnostico.toUpperCase() + " ---");
        String sql = "SELECT p.pacienteId, u.nombreCompleto, p.fechaNacimiento, p.diagnosticoPrincipal, p.historialMedico, p.horaConexion FROM pacientes p JOIN users u ON p.userId = u.userId WHERE p.diagnosticoPrincipal = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, diagnostico);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("pacienteId") +
                            " | Nombre: " + rs.getString("nombreCompleto"));
                }
            }
        }
    }

    public void mostrarPacientesConHoraDistinta(String zonaHorariaDestino) throws SQLException {
        System.out.println("\n--- HORA EN ZONA: " + zonaHorariaDestino.toUpperCase() + " ---");
        String sql = "SELECT p.pacienteId, u.nombreCompleto, p.horaConexion FROM pacientes p JOIN users u ON p.userId = u.userId";
        DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        try (PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("pacienteId");
                String nombre = rs.getString("nombreCompleto");
                LocalDateTime fechaCruda = rs.getObject("horaConexion", LocalDateTime.class);
                if (fechaCruda != null) {
                    ZonedDateTime fechaUTC = fechaCruda.atZone(ZoneOffset.UTC);
                    try {
                        ZoneId zonaDestino = ZoneId.of(zonaHorariaDestino);
                        ZonedDateTime fechaTransformada = fechaUTC.withZoneSameInstant(zonaDestino);
                        System.out.println("ID: " + id + " | Nombre: " + nombre +
                                " | UTC: " + fechaUTC.format(formateador) +
                                " | " + zonaDestino + ": " + fechaTransformada.format(formateador));
                    } catch (Exception e) {
                        System.out.println("   Error: La zona horaria '" + zonaHorariaDestino + "' no existe.");
                    }
                }
            }
        }
    }


// -----------------------------------------------------------------------------------------------------------------


    // UPDATES
        // CREAR PROCEDIMIENTOS
    public void crearProcedimiento() throws SQLException {
        String sqlProcedimientoDiagnostico = """
            CREATE PROCEDURE IF NOT EXISTS actualizarDiagnosticoPacientes(
                IN id_paciente_entrada INT,
                IN nuevo_diagnostico TEXT
            )
            BEGIN
                UPDATE pacientes
                SET diagnosticoPrincipal = nuevo_diagnostico
                WHERE pacienteId = id_paciente_entrada;
            END
            """;

        String sqlProcedimientoEspecialidad = """
            CREATE PROCEDURE IF NOT EXISTS actualizarEspecialidadDoctores(
                IN id_doctor_entrada INT,
                IN nueva_especialidad TEXT
            )
            BEGIN
                UPDATE doctores
                SET especialidad = nueva_especialidad
                WHERE doctorId = id_doctor_entrada;
            END
            """;

        String sqlProcedimientoColegiado = """
            CREATE PROCEDURE IF NOT EXISTS actualizarColegiadoLogopedas(
                            IN id_logopeda_entrada INT,
                            IN nuevo_colegiado TEXT
            )
            BEGIN
                UPDATE logopedas
                SET numeroColegiado = nuevo_colegiado
                WHERE logopedaId = id_logopeda_entrada;
            END
            """;

        String sqlProcedimientoAlta = """
            CREATE PROCEDURE IF NOT EXISTS darAltaPaciente(
                IN id_paciente_entrada INT
            )
            BEGIN
                UPDATE pacienteAsignacion
                SET pacienteActivo = 0
                WHERE pacienteId = id_paciente_entrada;
            END
            """;

        try (Statement stmt = con.createStatement()) {
            stmt.execute(sqlProcedimientoDiagnostico);
            stmt.execute(sqlProcedimientoEspecialidad);
            stmt.execute(sqlProcedimientoColegiado);
            stmt.execute(sqlProcedimientoAlta);
        }
    }


        // USAR PROCEDIMIENTOS
    public void cambiarDiagnostico(int idPaciente, String nuevoTexto) throws SQLException {
        String llamada = "{ call actualizarDiagnosticoPacientes(?, ?) }";

        try (CallableStatement cstmt = con.prepareCall(llamada)) {
            cstmt.setInt(1, idPaciente);
            cstmt.setString(2, nuevoTexto);
            cstmt.execute();
            System.out.println("Procedimiento ejecutado: Diagnóstico actualizado.");
        }
    }

    public void cambiarEspecialidad(int idDoctor, String nuevaEspecialidad) throws SQLException {
        String llamada = "{ call actualizarEspecialidadDoctores(?, ?) }";
        try (CallableStatement cstmt = con.prepareCall(llamada)) {
            cstmt.setInt(1, idDoctor);
            cstmt.setString(2, nuevaEspecialidad);
            cstmt.execute();
            System.out.println("Procedimiento ejecutado: Especialidad actualizada.");
        }
    }

    public void cambiarColegiado(int idLogopeda, String nuevoColegiado) throws SQLException {
        String llamada = "{ call actualizarColegiadoLogopedas(?, ?) }";
        try (CallableStatement cstmt = con.prepareCall(llamada)) {
            cstmt.setInt(1, idLogopeda);
            cstmt.setString(2, nuevoColegiado);
            cstmt.execute();
            System.out.println("Procedimiento ejecutado: Colegiado actualizado.");
        }
    }

    public void darAltaPaciente(int idPaciente) throws SQLException {
        String llamada = "{ call darAltaPaciente(?) }";
        try (CallableStatement cstmt = con.prepareCall(llamada)) {
            cstmt.setInt(1, idPaciente);
            cstmt.execute();
            System.out.println("Procedimiento ejecutado: Paciente dado de alta.");
        }
    }


// -----------------------------------------------------------------------------------------------------------------


    // DELETES
    public void borrarPaciente(int idPaciente) throws SQLException {
        String sqlSelectUser = "SELECT userId FROM pacientes WHERE pacienteId = ?";
        String sqlDeleteUser = "DELETE FROM users WHERE userId = ?";

        try {
            con.setAutoCommit(false);
            int userId = -1;
            try(PreparedStatement stmt = con.prepareStatement(sqlSelectUser)){
                stmt.setInt(1, idPaciente);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) userId = rs.getInt("userId");
            }
            if (userId != -1) {
                try(PreparedStatement stmt = con.prepareStatement(sqlDeleteUser)){
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }
                System.out.println("Paciente y su usuario asociados eliminados.");
            } else {
                System.out.println("No existe ese paciente.");
            }
            con.commit();
        } catch (SQLException e) {
            if(con!=null) con.rollback();
            throw new SQLException("No se puede borrar. Tiene asignaciones activas. " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void borrarDoctor(int idDoctor) throws SQLException {
        String sqlSelectUser = "SELECT userId FROM doctores WHERE doctorId = ?";
        String sqlDeleteUser = "DELETE FROM users WHERE userId = ?";
        try {
            con.setAutoCommit(false);
            int userId = -1;
            try(PreparedStatement stmt = con.prepareStatement(sqlSelectUser)){
                stmt.setInt(1, idDoctor);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) userId = rs.getInt("userId");
            }

            if (userId != -1) {
                try(PreparedStatement stmt = con.prepareStatement(sqlDeleteUser)){
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }
                System.out.println("Doctor (y su usuario y asignaciones) eliminados por cascada.");
            } else {
                System.out.println("No existe ese doctor.");
            }
            con.commit();
        } catch (SQLException e) {
            if(con!=null) con.rollback();
            throw new SQLException("Error inesperado al borrar doctor: " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void borrarLogopeda(int idLogopeda) throws SQLException {
        String sqlSelectUser = "SELECT userId FROM logopedas WHERE logopedaId = ?";
        String sqlDeleteUser = "DELETE FROM users WHERE userId = ?";

        try {
            con.setAutoCommit(false);
            int userId = -1;
            try(PreparedStatement stmt = con.prepareStatement(sqlSelectUser)){
                stmt.setInt(1, idLogopeda);
                ResultSet rs = stmt.executeQuery();
                if(rs.next()) userId = rs.getInt("userId");
            }
            if (userId != -1) {
                try(PreparedStatement stmt = con.prepareStatement(sqlDeleteUser)){
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }
                System.out.println("Logopeda y su usuario asociados eliminados.");
            } else {
                System.out.println("No existe ese logopeda.");
            }
            con.commit();
        } catch (SQLException e) {
            if(con!=null) con.rollback();
            throw new SQLException("No se puede borrar. Tiene asignaciones activas. " + e.getMessage());
        } finally {
            con.setAutoCommit(true);
        }
    }

    public void borrarAsignacion(int idAsignacion) throws SQLException {
        String sql = "DELETE FROM pacienteAsignacion WHERE asignacionId = ?";
        try(PreparedStatement stmt = con.prepareStatement(sql)){
            stmt.setInt(1, idAsignacion);
            stmt.executeUpdate();
            System.out.println("Asignación eliminada.");
        }
    }


// -----------------------------------------------------------------------------------------------------------------


    // INSERTS
        // MANUALES
    public void insertsDePrueba() throws SQLException {
        String sqlUser = "INSERT INTO users(email, passwordHash, nombreCompleto, rol) VALUES (?, ?, ?, ?)";
        String sqlDoctor = "INSERT INTO doctores(userId, especialidad) VALUES (?, ?)";
        String sqlLogopeda = "INSERT INTO logopedas(userId, numeroColegiado) VALUES (?, ?)";
        String sqlPaciente = "INSERT INTO pacientes(userId, fechaNacimiento, diagnosticoPrincipal, historialMedico, horaConexion) VALUES(?, STR_TO_DATE(?, '%d-%m-%Y'), ?, ?, ?)";
        String sqlAsignacion = "INSERT INTO pacienteAsignacion(pacienteId, doctorId, logopedaId, fechaAsignacion, pacienteActivo, tiempoSesion) VALUES (?, ?, ?, ?, ?, ?)";


        // DOCTOR DE PRUEBA
        int idGenerado = -1;
        try(PreparedStatement stmt = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "doctor1@doctores.com");
            stmt.setString(2, "1234");
            stmt.setString(3, "Perico Palotes");
            stmt.setString(4, "Doctor");
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) idGenerado = rs.getInt(1);
        }

        if (idGenerado != -1) {
            try(PreparedStatement stmt = con.prepareStatement(sqlDoctor)) {
                stmt.setInt(1, idGenerado);
                stmt.setString(2, "Medicina Física y Rehabilitación");
                stmt.executeUpdate();
            }
        }


        // LOGOPEDA DE PRUEBA
        idGenerado = -1;
        try(PreparedStatement stmt = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "logopeda1@logopedas.com");
            stmt.setString(2, "1234");
            stmt.setString(3, "Ignacio Cuñado");
            stmt.setString(4, "Logopeda");
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) idGenerado = rs.getInt(1);
        }

        if (idGenerado != -1) {
            try(PreparedStatement stmt = con.prepareStatement(sqlLogopeda)) {
                stmt.setInt(1, idGenerado);
                stmt.setString(2, "ABC1234567");
                stmt.executeUpdate();
            }
        }


        // PACIENTE DE PRUEBA
        idGenerado = -1;
        try(PreparedStatement stmt = con.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, "paciente1@paciente.com");
            stmt.setString(2, "1234");
            stmt.setString(3, "Pepe Botella Verde");
            stmt.setString(4, "Paciente");
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if(rs.next()) idGenerado = rs.getInt(1);
        }

        if (idGenerado != -1) {
            try(PreparedStatement stmt = con.prepareStatement(sqlPaciente)) {
                stmt.setInt(1, idGenerado);
                stmt.setString(2, "01-01-1990");
                stmt.setString(3, "Disfagia moderada");
                stmt.setString(4, "Familiares cercanos con misma dolencia");
                stmt.setObject(5, OffsetDateTime.now(ZoneOffset.UTC));
                stmt.executeUpdate();
            }
        }


        // ASIGNACIÓN DE PRUEBA
        try (PreparedStatement stmt = con.prepareStatement(sqlAsignacion)) {
            stmt.setInt(1, 1);
            stmt.setInt(2, 1);
            stmt.setInt(3, 1);
            stmt.setObject(4, OffsetDateTime.now(ZoneOffset.UTC));
            stmt.setBoolean(5, true);
            stmt.setDouble(6, 1.5);
            stmt.executeUpdate();
        }
    }

    private int insertarUsuario(String email, String pass, String nombre, String rol) throws SQLException {
        String sqlUsuario = "INSERT INTO users(email, passwordHash, nombreCompleto, rol) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, email);
            stmt.setString(2, pass);
            stmt.setString(3, nombre);
            stmt.setString(4, rol);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            else throw new SQLException("Error al crear usuario.");
        }
    }

    public void crearDoctorConTransaccion(String email, String pass, String nombre, String especialidad) throws SQLException {
        String sqlDoctor = "INSERT INTO doctores(userId, especialidad) VALUES (?, ?)";

        PreparedStatement stmtDoctor = null;
        try {
            con.setAutoCommit(false);

            int idUsuario = insertarUsuario(email, pass, nombre, "Doctor");

            stmtDoctor = con.prepareStatement(sqlDoctor);
            stmtDoctor.setInt(1, idUsuario);
            stmtDoctor.setString(2, especialidad);
            stmtDoctor.executeUpdate();

            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (stmtDoctor != null) stmtDoctor.close();
            con.setAutoCommit(true);
        }
    }

    public void crearLogopedaConTransaccion(String email, String pass, String nombre, String numeroColegiado) throws SQLException {
        String sqlLogopeda = "INSERT INTO logopedas(userId, numeroColegiado) VALUES (?, ?)";
        PreparedStatement stmtLogopeda = null;
        try {
            con.setAutoCommit(false);
            int idUsuario = insertarUsuario(email, pass, nombre, "Logopeda");
            stmtLogopeda = con.prepareStatement(sqlLogopeda);
            stmtLogopeda.setInt(1, idUsuario);
            stmtLogopeda.setString(2, numeroColegiado);
            stmtLogopeda.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (stmtLogopeda != null) stmtLogopeda.close();
            con.setAutoCommit(true);
        }
    }

    public void crearPacienteConTransaccion(String email, String pass, String nombre, String fechaNacimiento, String diagnostico, String historial) throws SQLException {
        String sqlPaciente = "INSERT INTO pacientes(userId, fechaNacimiento, diagnosticoPrincipal, historialMedico, horaConexion) VALUES(?, STR_TO_DATE(?, '%d-%m-%Y'), ?, ?, ?)";
        PreparedStatement stmtPaciente = null;
        try {
            con.setAutoCommit(false);
            int idUsuario = insertarUsuario(email, pass, nombre, "Paciente");
            stmtPaciente = con.prepareStatement(sqlPaciente);
            stmtPaciente.setInt(1, idUsuario);
            stmtPaciente.setString(2, fechaNacimiento);
            stmtPaciente.setString(3, diagnostico);
            stmtPaciente.setString(4, historial);
            stmtPaciente.setObject(5, LocalDateTime.now(ZoneOffset.UTC));
            stmtPaciente.executeUpdate();
            con.commit();
        } catch (SQLException e) {
            if (con != null) con.rollback();
            throw e;
        } finally {
            if (stmtPaciente != null) stmtPaciente.close();
            con.setAutoCommit(true);
        }
    }

    public void crearAsignacionConTransaccion(int pacienteId, int doctorId, int logopedaId, OffsetDateTime fechaAsignacion, boolean estado, double tiempo) throws SQLException {

        String sqlAsignacion = "INSERT INTO pacienteAsignacion(pacienteId, doctorId, logopedaId, fechaAsignacion, pacienteActivo, tiempoSesion) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement stmtAsignacion = null;

        try {
            con.setAutoCommit(false);

            stmtAsignacion = con.prepareStatement(sqlAsignacion);
            stmtAsignacion.setInt(1, pacienteId);
            stmtAsignacion.setInt(2, doctorId);
            stmtAsignacion.setInt(3, logopedaId);
            stmtAsignacion.setObject(4, fechaAsignacion);
            stmtAsignacion.setBoolean(5, estado);
            stmtAsignacion.setDouble(6, tiempo);

            stmtAsignacion.executeUpdate();

            con.commit();
            System.out.println("Transacción ÉXITO: Asignación creada");

        } catch (SQLException e) {
            if (con != null) {
                System.out.println("FALLO EN LA TRANSACCIÓN. VUELVO ATRÁS (Rollback).");
                con.rollback();
            }
            throw new SQLException("Error al crear asignación. Verifica IDs. " + e.getMessage());
        } finally {
            if (stmtAsignacion != null) stmtAsignacion.close();
            con.setAutoCommit(true);
        }
    }


// -----------------------------------------------------------------------------------------------------------------


    @Override
    public void close() throws SQLException {
        if (con != null && !con.isClosed()) {
            con.close();
        }
    }
}