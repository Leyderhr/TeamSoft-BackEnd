package com.tesis.teamsoft.metaheuristics.auxiliary;

//import metaheurictics.strategy.Strategy;
//import org.teamsoft.controller.util.CSVUtils;
//import org.teamsoft.entity.Worker;
//import org.teamsoft.metaheuristics.util.test.BelbinCategoryRole;
//import org.teamsoft.metaheuristics.util.test.CompetenceProjectRole;
//import org.teamsoft.metaheuristics.util.test.CompetenceRoleWorker;
//import org.teamsoft.metaheuristics.util.test.CompetentWorker;
//import problem.definition.State;
//
//import javax.faces.context.FacesContext;
//import javax.servlet.ServletContext;
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.text.DecimalFormat;
//import java.time.LocalDate;
//import java.util.*;

/**
 * @author Alejandro Durán & jpinas
 */
public class SaveTxt {

//    private static ServletContext relativePath = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
//    private static String mainPath = relativePath.getRealPath("/reports/");
//
//    /**
//     * salva el proyecto en formato csv, donde hay 2 columnas, una para los roles
//     * y otra para las personas
//     *
//     * @param actualProject proyecto que se quiere salvar
//     * @param fo            string que contiene los valores de las funciones objetivos
//     * @param solutionType  si es final o inicial
//     */
//    public static void salvarProyecto(ProjectRole actualProject, String solutionType, String fo) throws IOException {
//        List<RoleWorker> roleWorker = actualProject.getRoleWorkers();
//        roleWorker.sort(Comparator.comparing(roleWorker1 -> roleWorker1.getRole().getId()));
//        File fileToExport = new File("solucion-" + solutionType + "-proyecto-" +
//                actualProject.getProject().getProjectName() + "-" + LocalDate.now().toString() + "-" +
//                System.currentTimeMillis() + "-" + fo + ".csv"
//        );
//        Writer writer = new OutputStreamWriter(new FileOutputStream(fileToExport), StandardCharsets.UTF_8);
//        List<String> roleWorkerString = new LinkedList<>();
//
////        se escribe el header
//        roleWorkerString.add("Rol");
//        roleWorkerString.add("Trabajador(es)");
//        CSVUtils.writeLine(writer, roleWorkerString, ';', ' ');
//
////        por cada rol, se ponen sus trabajadores asociados
//        roleWorker.forEach(e -> {
//            try {
//                roleWorkerString.clear();
//                roleWorkerString.add(e.getRole().getRoleName());
//
//                StringBuilder workers = new StringBuilder();
//                e.getWorkers().forEach(w -> workers.append(w.getPersonName()).append("-"));
//                roleWorkerString.add(workers.toString());
//
//                CSVUtils.writeLine(writer, roleWorkerString, ';', ' ');
//            } catch (IOException ioException) {
//                ioException.printStackTrace();
//            }
//        });
//        writer.flush();
//        writer.close();
//    }
//
//    public static void writeFileTxt(String fichero, List<State> listRefPoblacFinal, int e) throws IOException {
//        FileWriter fw = new FileWriter(mainPath + "/" + fichero);// Objeto para que establece origen de los datos
//        BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//        PrintWriter writer = new PrintWriter(bw);
//
//        writer.print("Ejecucion");
//        writer.print(";");
//        writer.print("Estado");
//        writer.print(";");
//        writer.print("Cantidad de FO");
//        writer.print(";");
//        writer.print("Evaluaciones en FO");
//        writer.println();
//
//        for (int j = 0; j < listRefPoblacFinal.size(); j++) {
//            writer.write(String.valueOf(e)); //ejecución
//            writer.write(";[");
//            ArrayList<Object> projects = listRefPoblacFinal.get(j).getCode();
//            for (Iterator<Object> iterator = projects.iterator(); iterator.hasNext(); ) {
//                ProjectRole projectRole = (ProjectRole) iterator.next();
//                writer.write(projectRole.getProject().getProjectName() + ":");
//                List<RoleWorker> roles = projectRole.getRoleWorkers();
//                for (Iterator<RoleWorker> iterator1 = roles.iterator(); iterator1.hasNext(); ) {
//                    RoleWorker roleWorker = iterator1.next();
//                    writer.write(roleWorker.getRole().getRoleName() + "-");
//                    List<Worker> workers = roleWorker.getWorkers();
//                    for (Iterator<Worker> iterator2 = workers.iterator(); iterator2.hasNext(); ) {
//                        Worker worker = iterator2.next();
//                        if (iterator2.hasNext())
//                            writer.write(worker.getPersonName() + " " + worker.getSurName() + ",");
//                        else if (iterator.hasNext()) {
//                            writer.write(worker.getPersonName() + " " + worker.getSurName() + ",");
//                        } else {
//                            writer.write(worker.getPersonName() + " " + worker.getSurName());
//                        }
//                    }
//                }
//            }
//            writer.write("];"); //estado
//            writer.write(String.valueOf(listRefPoblacFinal.get(j).getEvaluation().size())); //cantidad de funciones objetivo
//            writer.write(";[");
//            ArrayList<Double> evaluations = listRefPoblacFinal.get(j).getEvaluation();
//            String format = ResourceBundle.getBundle("/algorithmConf").getString("decimalFormat");
//            DecimalFormat df = new DecimalFormat(format);
//            for (Iterator<Double> iterator = evaluations.iterator(); iterator.hasNext(); ) {
//                Double evaluation = iterator.next();
//                if (iterator.hasNext()) {
//                    writer.print(df.format(evaluation) + ","); //Evaluación
//                } else {
//                    writer.print(df.format(evaluation));
//                }
//            }
//            writer.write("]");
//            writer.println();
//        }
//        writer.close();
//    }
//
//    public static void writeCompetenceWorkersList(ArrayList<CompetenceProjectRole> workersMultiList) throws IOException {
//        FileWriter fw = new FileWriter(mainPath + "CompetenceWorkersList " + ".txt");// Objeto para que establece origen de los datos
//        BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//        PrintWriter writer = new PrintWriter(bw);
//
//        for (CompetenceProjectRole competenceProjectRole : workersMultiList) {
//            writer.println();
//            writer.write(competenceProjectRole.getProject().getProjectName());
//            List<CompetenceRoleWorker> competenceRoleWorkers = competenceProjectRole.getRoleWorkers();
//            writer.println();
//
//            for (CompetenceRoleWorker competenceRoleWorker : competenceRoleWorkers) {
//                writer.write(competenceRoleWorker.getRole().getRoleName() + ": ");
//                List<CompetentWorker> competentWorkers = competenceRoleWorker.getWorkers();
//
//                for (CompetentWorker competentWorker : competentWorkers) {
//                    writer.write(competentWorker.getWorker().getPersonName() + " - " + competentWorker.getEvaluation() + "/");
//                }
//            }
//            writer.println();
//        }
//        writer.close();
//    }
//
//    public static void writeBelbinWorkersList(ArrayList<BelbinCategoryRole> workersMultiList) throws IOException {
//        FileWriter fw = new FileWriter(mainPath + "BelbinWorkersList " + ".txt");// Objeto para que establece origen de los datos
//        BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//        PrintWriter writer = new PrintWriter(bw);
//
//        for (int i = 0; i < 3; i++) {
//            if (i == 0) {
//                writer.write("Acción: ");
//            } else if (i == 1) {
//                writer.write("Mental: ");
//            } else {
//                writer.write("Social: ");
//            }
//            List<Worker> belbinWorkers = workersMultiList.get(i).getCategoryWorkers();
//            for (Worker belbinWorker : belbinWorkers) {
//                writer.write(belbinWorker.getPersonName() + "/");
//            }
//            writer.println();
//        }
//        writer.close();
//    }
//
//    public static void writeStatesMono() throws IOException {
//        Date date = new Date();
//        Long time = System.currentTimeMillis() / 1000;
//        FileWriter fw = new FileWriter(mainPath + "ReportMonoObj " + time + ".txt");// Objeto para que establece origen de los datos
//        BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//        PrintWriter writer = new PrintWriter(bw);
//
//        writer.write((String.valueOf(date)));
//        writer.println();
//        writer.write("Método de Solución - " + Strategy.getStrategy().getProblem().getTypeSolutionMethod());
//        writer.println();
//        writer.write("Tipo de Generador - " + Strategy.getStrategy().generator.getType());
//        writer.println();
//        String operator = Strategy.getStrategy().getProblem().getOperator().getClass().getName();
//        writer.write("Operador - " + operator.substring(41));
//        writer.println();
//        writer.println();
//        writer.write("Estado");
//        writer.println();
//
//        for (int i = 0; i < Strategy.getStrategy().listStates.size(); i++) {
//            List<Object> projects = Strategy.getStrategy().listStates.get(i).getCode();
//
//            for (int j = 0; j < projects.size(); j++) {
//                ProjectRole projectRole = (ProjectRole) projects.get(j);
//                writer.write("Proyecto: " + projectRole.getProject().getProjectName() + " - ");
//                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
//
//                for (int k = 0; k < roleWorkers.size(); k++) {
//                    writer.write(roleWorkers.get(k).getRole().getRoleName() + ": ");
//                    List<Worker> workers = roleWorkers.get(k).getWorkers();
//
//                    for (int l = 0; l < workers.size(); l++) {
//                        writer.write(workers.get(l).getPersonName() + ", ");
//                    }
//                }
//                writer.println();
//            }
//            writer.write("Evaluación: " + Strategy.getStrategy().listStates.get(i).getEvaluation().get(0).toString());
//            writer.println();
//            writer.println();
//        }
//
//        writer.println();
//        writer.write("Mejor Estado");
//        writer.println();
//        List<Object> bState = Strategy.getStrategy().getBestState().getCode();
//
//        for (int i = 0; i < bState.size(); i++) {
//            ProjectRole bProjectRole = (ProjectRole) bState.get(i);
//            writer.write("Proyecto: " + bProjectRole.getProject().getProjectName() + " - ");
//            List<RoleWorker> bRoleWorkers = bProjectRole.getRoleWorkers();
//
//            for (int j = 0; j < bRoleWorkers.size(); j++) {
//                writer.write(bRoleWorkers.get(j).getRole().getRoleName() + ": ");
//                List<Worker> bWorkers = bRoleWorkers.get(j).getWorkers();
//
//                for (int k = 0; k < bWorkers.size(); k++) {
//                    writer.write(bWorkers.get(k).getPersonName() + ", ");
//                }
//            }
//            writer.println();
//        }
//        writer.write("Evaluación: " + Strategy.getStrategy().getBestState().getEvaluation().get(0).toString());
//        writer.close();
//    }
//
//    public static void writeStatesMulti() throws IOException {
//        Date date = new Date();
//        Long time = System.currentTimeMillis() / 1000;
//        FileWriter fw = new FileWriter(mainPath + "ReportMultiObj " + time + ".txt");// Objeto para que establece origen de los datos
//        BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//        PrintWriter writer = new PrintWriter(bw);
//
//        writer.write((String.valueOf(date)));
//        writer.println();
//        writer.write("Método de Solución - " + Strategy.getStrategy().getProblem().getTypeSolutionMethod());
//        writer.println();
//        writer.write("Tipo de Generador - " + Strategy.getStrategy().generator.getType());
//        writer.println();
//        String operator = Strategy.getStrategy().getProblem().getOperator().getClass().getName();
//        writer.write("Operador - " + operator.substring(41));
//        writer.println();
//        writer.println();
//        writer.write("Estado");
//        writer.println();
//
//        for (int i = 0; i < Strategy.getStrategy().listStates.size(); i++) {
//            List<Object> projects = Strategy.getStrategy().listStates.get(i).getCode();
//
//            for (int j = 0; j < projects.size(); j++) {
//                ProjectRole projectRole = (ProjectRole) projects.get(j);
//                writer.write("Proyecto: " + projectRole.getProject().getProjectName() + " - ");
//                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
//
//                for (int k = 0; k < roleWorkers.size(); k++) {
//                    writer.write(roleWorkers.get(k).getRole().getRoleName() + ": ");
//                    List<Worker> workers = roleWorkers.get(k).getWorkers();
//
//                    for (int l = 0; l < workers.size(); l++) {
//                        writer.write(workers.get(l).getPersonName() + ", ");
//                    }
//                }
//                writer.println();
//            }
//            writer.write("Evaluación: " + Strategy.getStrategy().listStates.get(i).getEvaluation().get(0).toString());
//            writer.println();
//            writer.println();
//        }
//
//        writer.println();
//        writer.write("Soluciones No Dominadas");
//        writer.println();
//        writer.write("Estado");
//        writer.println();
//
//        for (int i = 0; i < Strategy.getStrategy().listRefPoblacFinal.size(); i++) {
//            List<Object> projects = Strategy.getStrategy().listRefPoblacFinal.get(i).getCode();
//
//            for (int j = 0; j < projects.size(); j++) {
//                ProjectRole projectRole = (ProjectRole) projects.get(j);
//                writer.write("Proyecto: " + projectRole.getProject().getProjectName() + " - ");
//                List<RoleWorker> roleWorkers = projectRole.getRoleWorkers();
//
//                for (int k = 0; k < roleWorkers.size(); k++) {
//                    writer.write(roleWorkers.get(k).getRole().getRoleName() + ": ");
//                    List<Worker> workers = roleWorkers.get(k).getWorkers();
//
//                    for (int l = 0; l < workers.size(); l++) {
//                        writer.write(workers.get(l).getPersonName() + ", ");
//                    }
//                }
//                writer.println();
//            }
//            writer.write("Evaluación: " + Strategy.getStrategy().listRefPoblacFinal.get(i).getEvaluation().get(0).toString());
//            writer.println();
//            writer.println();
//        }
//        writer.close();
//    }
//
//    public static void writeFileTxtMono(String pathFile, String fichero, List<State> listRefPoblacFinal, int e) {
//
//        try {
//            FileWriter fw = new FileWriter(pathFile + fichero);// Objeto para que establece origen de los datos
//            BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//            PrintWriter title = new PrintWriter(bw);
//
//            title.print("Ejecucion");
//            title.print(";");
//            title.print("Iteracion");
//            title.print(";");
//            title.print("Evaluacion en FO");
//            title.println();
//            title.close();
//            bw = new BufferedWriter(new FileWriter(pathFile + fichero, true));// buffer para el manejo de los datos
//            PrintWriter writer = new PrintWriter(bw);
//            for (int j = 0; j < listRefPoblacFinal.size(); j++) {
//
//                writer.print(e);
//                writer.print(";");
//                writer.print(j);
//                writer.print(";");
//                writer.print(listRefPoblacFinal.get(j).getEvaluation());
//                writer.println();
//                writer.close();
//            }
//        } catch (java.io.IOException ignored) {
//        }
//    }
//
//    public static void writeFileTxtTime(String pathFile, String fichero, int e, long time) {
//        try {
//            FileWriter fw = new FileWriter(pathFile + fichero);// Objeto para que establece origen de los datos
//            BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//            PrintWriter title = new PrintWriter(bw);
//
//            title.print("Ejecucion");
//            title.print(";");
//            title.print("Iteracion");
//            title.print(";");
//            title.print("Evaluacion en FO");
//            title.println();
//            title.close();
//            bw = new BufferedWriter(new FileWriter(pathFile + fichero, true));// buffer para el manejo de los datos
//            PrintWriter writer = new PrintWriter(bw);
//
//            writer.print(e);
//            writer.print(";");
//            writer.print(0);
//            writer.print(";");
//            writer.print("[" + time + "]");
//            writer.println();
//            writer.close();
//
//        } catch (IOException ignored) {
//        }
//    }
//
//    public static void writeFileTxtMulti(String pathFile, String fichero, List<State> listRefPoblacFinal, int e) {
//
//        try {
//            FileWriter fw = new FileWriter(pathFile + fichero);// Objeto para que establece origen de los datos
//            BufferedWriter bw = new BufferedWriter(fw);// buffer para el manejo de los datos
//            PrintWriter title = new PrintWriter(bw);
//            title.print("Ejecucion");
//            title.print(";");
//            title.print("Iteracion");
//            title.print(";");
//            title.print("Estado");
//            title.print(";");
//            title.print("Cantidad de FO");
//            title.print(";");
//            title.print("Evaluaciones en FO");
//            title.println();
//            title.close();
//            for (int j = 0; j < listRefPoblacFinal.size(); j++) {
//                bw = new BufferedWriter(new FileWriter(pathFile + fichero, true));// buffer para el manejo de los datos
//                PrintWriter writer = new PrintWriter(bw);
//                writer.print(e);
//                writer.print(";");
//                writer.print(j);
//                writer.print(";");
//                writer.print(listRefPoblacFinal.get(j).getCode());
//                writer.print(";");
//                writer.print(listRefPoblacFinal.get(j).getEvaluation().size());
//                writer.print(";");
//                writer.print(listRefPoblacFinal.get(j).getEvaluation());
//                writer.println();
//                writer.close();
//            }
//        } catch (java.io.IOException ignored) {
//        }
//    }
}
