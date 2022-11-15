package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

    ProjectService projectService = new ProjectService();
    //@formatter:off
    private List<String> operations = List.of(
            "1) Add a project" +
            "\n 2) List all projects" +
            "\n 3) Select a project"        
            );
  //@formatter:on
    Project curProject = null;

    private void processUserSelections() {
        boolean done = false;

        while (!done) {
            try {
                int selection = getUserSelection();

                switch (selection) {
                    case -1:
                        done = true;
                        exitMenu();
                        break;
                    case 1:
                        createProject();
                        break;
                    case 2:
                        listProjects();
                        break;
                    case 3:
                        selectProject();
                        break;
                    default:
                        System.out.println(
                                "\n" + selection + "is not a valid selection. Please try again.");
                        break;
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e + " Try again.");
            }
        }
    }

    private void createProject() {
        String projectName = getStringInput("Enter the project name");
        BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
        BigDecimal actualHours = (getDecimalInput("Enter the actual hours"));
        Integer difficulty = Integer.valueOf(getStringInput("Enter the project difficulty (1-5)"));
        String notes = getStringInput("Enter the project notes");

        Project project = new Project();

        project.setProjectName(projectName);
        project.setEstimatedHours(estimatedHours);
        project.setActualHours(actualHours);
        project.setDifficulty(difficulty);
        project.setNotes(notes);

        Project dbProject = projectService.addProject(project);

        System.out.println("You have successfully created project: " + dbProject);

    }

    private void selectProject() {
        listProjects();
        Integer projectId = getIntInput("Enter the project ID");
        curProject = null;
        curProject = projectService.fetchProjectById(projectId);

    }

    private void listProjects() {
        List<Project> listOfProjects = projectService.fetchAllProjects();

        System.out.println("\n Projects:");
        listOfProjects.forEach(projects -> System.out
                .println(" " + projects.getProjectId() + ": " + projects.getProjectName()));

    }



    private void exitMenu() {
        System.out.println("Exiting the menu.");
    }



    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        new ProjectsApp().processUserSelections();

    } // end of main method *************************



    private int getUserSelection() {

        printOperations();

        Integer input = getIntInput("Enter a menu selection");

        return Objects.isNull(input) ? -1 : input;
    }



    // fundamental input type, most primitive. other inputs should use this then convert as
    // appropriate
    private String getStringInput(String prompt) {

        System.out.print(prompt + ":");
        String input = scanner.nextLine();

        return input.isBlank() ? null : input.trim();
    }



    // calls getStringInput then converts type from string to wrapper class Integer
    private Integer getIntInput(String prompt) {
        String input = getStringInput(prompt);

        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return Integer.valueOf(input);
        } catch (Exception e) {
            throw new DbException(input + " is not a valid number");
        }
    }



    // returns BigDecimal object from console scanner input
    private BigDecimal getDecimalInput(String prompt) {
        String input = getStringInput(prompt);

        if (Objects.isNull(input)) {
            return null;
        }
        try {
            return new BigDecimal(input).setScale(2);
        } catch (Exception e) {
            throw new DbException(input + " is not a valid decimal");
        }
    }



    private void printOperations() {

        System.out.println("\nThese are the available selections. Press enter to quit:");

        operations.forEach(line -> System.out.println(" " + line));
        if(Objects.isNull(curProject)) {
        System.out.println("\nYou are not working with a project currently");
        } else {
            System.out.println("\nYou are currently working within: " + curProject);
        }

    }
}
