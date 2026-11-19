import java.util.Scanner;

public class SemaforoAcademico {

    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        teclado.useLocale(java.util.Locale.US); // Las notas se escriben con punto: 3.5

        // Se pueden registrar hasta 5 materias con 5 evaluaciones cada una.
        String[] materias = new String[5];
        double[] metas = new double[5];
        int[] cantidades = new int[5];
        String[][] actividades = new String[5][5];
        int[][] porcentajes = new int[5][5];
        double[][] notas = new double[5][5];

        int totalMaterias = 0;
        int opcion = 0;

        while (opcion != 6) {
            System.out.println("\n====== SEMAFORO ACADEMICO ======");
            System.out.println("1. Registrar materia");
            System.out.println("2. Registrar o cambiar una nota");
            System.out.println("3. Consultar riesgo de una materia");
            System.out.println("4. Simular notas pendientes");
            System.out.println("5. Ver reporte de todas las materias");
            System.out.println("6. Salir");
            opcion = leerEntero(teclado, "Elija una opcion: ", 1, 6);

            if (opcion == 1) {
                if (totalMaterias == 5) {
                    System.out.println("Ya se registraron las 5 materias permitidas.");
                } else {
                    String nombre = "";
                    boolean repetida = true;

                    // No se permiten nombres vacios ni repetidos.
                    while (nombre.length() == 0 || repetida) {
                        System.out.print("Nombre de la materia: ");
                        nombre = teclado.nextLine().trim();
                        repetida = false;
                        for (int i = 0; i < totalMaterias; i++) {
                            if (materias[i].equalsIgnoreCase(nombre)) {
                                repetida = true;
                            }
                        }
                        if (nombre.length() == 0 || repetida) {
                            System.out.println("Nombre vacio o materia repetida. Intente de nuevo.");
                        }
                    }

                    int m = totalMaterias;
                    materias[m] = nombre;
                    metas[m] = leerDecimal(teclado, "Nota meta (0.1 a 5.0): ", 0.1, 5.0);
                    cantidades[m] = leerEntero(teclado, "Cuantas evaluaciones (1 a 5): ", 1, 5);
                    int restante = 100;

                    for (int j = 0; j < cantidades[m]; j++) {
                        System.out.println("\nEvaluacion " + (j + 1));
                        System.out.print("Nombre de la evaluacion: ");
                        actividades[m][j] = teclado.nextLine().trim();
                        while (actividades[m][j].length() == 0) {
                            System.out.print("Escriba un nombre: ");
                            actividades[m][j] = teclado.nextLine().trim();
                        }

                        if (j == cantidades[m] - 1) {
                            porcentajes[m][j] = restante;
                            System.out.println("Porcentaje asignado: " + restante + "%");
                        } else {
                            int maximo = restante - (cantidades[m] - j - 1);
                            porcentajes[m][j] = leerEntero(teclado,
                                    "Porcentaje (1 a " + maximo + "): ", 1, maximo);
                        }
                        restante = restante - porcentajes[m][j];

                        int estado = leerEntero(teclado,
                                "1. Ya tiene nota / 2. Pendiente: ", 1, 2);
                        if (estado == 1) {
                            notas[m][j] = leerDecimal(teclado, "Nota (0.0 a 5.0): ", 0.0, 5.0);
                        } else {
                            notas[m][j] = -1; // -1 significa pendiente; 0.0 si es una nota real.
                        }
                    }

                    totalMaterias++;
                    System.out.println("Materia registrada. Porcentajes: 100%.");
                }

            } else if (opcion == 2 || opcion == 3 || opcion == 4) {
                if (totalMaterias == 0) {
                    System.out.println("Primero debe registrar una materia.");
                } else {
                    for (int i = 0; i < totalMaterias; i++) {
                        System.out.println((i + 1) + ". " + materias[i]);
                    }
                    int m = leerEntero(teclado, "Seleccione la materia: ", 1, totalMaterias) - 1;

                    if (opcion == 2) {
                        for (int j = 0; j < cantidades[m]; j++) {
                            System.out.print((j + 1) + ". " + actividades[m][j] + " (" + porcentajes[m][j] + "%)");
                            if (notas[m][j] == -1) {
                                System.out.println(" - PENDIENTE");
                            } else {
                                System.out.println(" - Nota: " + notas[m][j]);
                            }
                        }
                        int j = leerEntero(teclado, "Numero de evaluacion: ", 1, cantidades[m]) - 1;
                        notas[m][j] = leerDecimal(teclado, "Nueva nota (0.0 a 5.0): ", 0.0, 5.0);
                        System.out.println("Nota guardada correctamente.");

                    } else if (opcion == 3) {
                        mostrarRiesgo(materias[m], metas[m], notas[m], porcentajes[m], cantidades[m]);

                    } else {
                        double resultado = 0;
                        boolean hayPendientes = false;
                        System.out.println("\nSIMULACION (no cambia las notas guardadas)");
                        for (int j = 0; j < cantidades[m]; j++) {
                            double notaSimulada = notas[m][j];
                            if (notaSimulada == -1) {
                                hayPendientes = true;
                                notaSimulada = leerDecimal(teclado,
                                        "Nota supuesta en " + actividades[m][j] + ": ", 0.0, 5.0);
                            }
                            resultado = resultado + notaSimulada * porcentajes[m][j] / 100.0;
                        }
                        if (!hayPendientes) {
                            System.out.println("No quedan evaluaciones pendientes.");
                        }
                        System.out.printf("Nota final simulada: %.2f%n", resultado);
                        if (resultado + 0.000001 >= metas[m]) {
                            System.out.println("Con esas notas se cumple la meta.");
                        } else {
                            System.out.println("Con esas notas NO se cumple la meta.");
                        }
                    }
                }

            } else if (opcion == 5) {
                if (totalMaterias == 0) {
                    System.out.println("Todavia no hay materias registradas.");
                } else {
                    for (int i = 0; i < totalMaterias; i++) {
                        mostrarRiesgo(materias[i], metas[i], notas[i], porcentajes[i], cantidades[i]);
                    }
                }
            } else if (opcion == 6) {
                System.out.println("Hasta luego. Los datos no se guardan al salir.");
            }
        }
        teclado.close();
    }

    // Metodo 1: leer una opcion o porcentaje entero dentro de un rango.
    public static int leerEntero(Scanner teclado, String mensaje, int minimo, int maximo) {
        int numero;
        while (true) {
            System.out.print(mensaje);
            if (teclado.hasNextInt()) {
                numero = teclado.nextInt();
                teclado.nextLine();
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } else {
                teclado.nextLine(); // Descarta una entrada que no sea un numero entero.
            }
            System.out.println("Error. Escriba un entero entre " + minimo + " y " + maximo + ".");
        }
    }

    // Metodo 2: leer una nota decimal y comprobar que este en el rango.
    public static double leerDecimal(Scanner teclado, String mensaje, double minimo, double maximo) {
        double numero;
        while (true) {
            System.out.print(mensaje);
            if (teclado.hasNextDouble()) {
                numero = teclado.nextDouble();
                teclado.nextLine();
                if (numero >= minimo && numero <= maximo) {
                    return numero;
                }
            } else {
                teclado.nextLine();
            }
            System.out.println("Error. Escriba una nota entre " + minimo + " y " + maximo + " (con punto).");
        }
    }

    // Metodo 3: calcular el estado de una materia y mostrar el resultado.
    public static void mostrarRiesgo(String materia, double meta, double[] notas,
                                     int[] porcentajes, int cantidad) {
        double acumulado = 0;
        int pendiente = 0;

        for (int j = 0; j < cantidad; j++) {
            if (notas[j] == -1) {
                pendiente = pendiente + porcentajes[j];
            } else {
                acumulado = acumulado + notas[j] * porcentajes[j] / 100.0;
            }
        }

        double notaMaxima = acumulado + 5.0 * pendiente / 100.0;
        System.out.println("\n----- " + materia + " -----");
        System.out.printf("Meta: %.2f | Acumulado: %.2f | Pendiente: %d%%%n",
                          meta, acumulado, pendiente);
        System.out.printf("Maxima nota final posible: %.2f%n", notaMaxima);

        if (pendiente == 0) {
            if (acumulado + 0.000001 >= meta) {
                System.out.println("Resultado: materia finalizada, meta cumplida.");
            } else {
                System.out.println("Resultado: materia finalizada, meta NO cumplida.");
            }
        } else if (acumulado + 0.000001 >= meta) {
            System.out.println("Resultado: ya tiene asegurada su meta.");
        } else if (notaMaxima + 0.000001 < meta) {
            System.out.println("Resultado: IMPOSIBLE alcanzar la meta, incluso sacando 5.0.");
        } else {
            double necesaria = (meta - acumulado) * 100.0 / pendiente;
            System.out.printf("Promedio necesario en lo pendiente: %.2f%n", necesaria);

            if (necesaria <= 3.0 + 0.000001) {
                System.out.println("Riesgo: BAJO");
            } else if (necesaria <= 4.0 + 0.000001) {
                System.out.println("Riesgo: MEDIO");
            } else {
                System.out.println("Riesgo: ALTO");
            }
        }
    }
}
