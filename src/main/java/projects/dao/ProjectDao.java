package projects.dao;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProjectDao extends DaoBase {

    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";

    public Project insertProject(Project project) {
      //@formatter:off
        String sql = ""
                + "INSERT INTO " + PROJECT_TABLE + " "
                + " (project_name, estimated_hours, actual_hours, difficulty, notes) "
                + "VALUES "
                + "(? ,? ,? ,? ,? )";
        //@formatter:on



        try (Connection conn = DbConnection.getConnection()) {

            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                stmt.executeUpdate();

                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
                commitTransaction(conn);

                project.setProjectId(projectId);
                return project;



            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }



    public List<Project> fetchAllProjects() {

        List<Project> projectsList = new LinkedList<>();

        try (Connection conn = DbConnection.getConnection()) {
            try {
                startTransaction(conn);

                String sqlProjectsGetter =
                        "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_id";


                try (PreparedStatement statement = conn.prepareStatement(sqlProjectsGetter)) {

                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            Project project = new Project();

                            project.setActualHours(resultSet.getBigDecimal("actual_hours"));
                            project.setEstimatedHours(resultSet.getBigDecimal("estimated_hours"));
                            project.setDifficulty(resultSet.getObject("difficulty", Integer.class));
                            project.setNotes(resultSet.getString("notes"));
                            project.setProjectId(resultSet.getObject("project_id", Integer.class));
                            project.setProjectName(resultSet.getString("project_name"));

                            projectsList.add(project);
                        }
                    } catch (SQLException e) {
                        throw new DbException(e);
                    }

                } catch (Exception e) {
                    rollbackTransaction(conn);
                    throw new DbException(e);
                }

            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }

        return projectsList;
    } // end of fetchAllProjects



    public Optional<Project> fetchProjectById(int projectId) {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            try {
                Project project = null;
                try (PreparedStatement statement = conn.prepareStatement(sql)) {

                    setParameter(statement, 1, projectId, Integer.class);
                    try (ResultSet rs = statement.executeQuery()) {

                        if (rs.next()) {
                            project = extract(rs, Project.class);
                        }
                    }
                }

                if (Objects.nonNull(project)) {
                    project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
                    project.getSteps().addAll(fetchStepsForProject(conn, projectId));
                    project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
                }
                commitTransaction(conn);
                return Optional.ofNullable(project);
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }



    private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId)
            throws SQLException {
        //@formatter:off
        String sql = ""
                + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
                + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
                + "WHERE project_id = ?";
        //@formatter:on

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            setParameter(statement, 1, projectId, Integer.class);

            try (ResultSet rs = statement.executeQuery()) {
                List<Category> categories = new LinkedList<>();

                while (rs.next()) {
                    categories.add(extract(rs, Category.class));
                }

                return categories;
            }
        }
    }


    // what are you doing step table uwu
    private List<Step> fetchStepsForProject(Connection conn, int projectId) throws SQLException {
      //@formatter:off
        String sql = ""
                + "SELECT c.* FROM " + STEP_TABLE + " c " + "WHERE project_id = ?";
        //@formatter:on

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            setParameter(statement, 1, projectId, Integer.class);

            try (ResultSet rs = statement.executeQuery()) {
                List<Step> steps = new LinkedList<>();

                while (rs.next()) {
                    steps.add(extract(rs, Step.class));
                }

                return steps;
            }
        }
    }



    private List<Material> fetchMaterialsForProject(Connection conn, int projectId)
            throws SQLException {
      //@formatter:off
        String sql = ""
                + "SELECT c.* FROM " + MATERIAL_TABLE + " c "
                + "WHERE project_id = ?";
        //@formatter:on

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            setParameter(statement, 1, projectId, Integer.class);

            try (ResultSet rs = statement.executeQuery()) {
                List<Material> materials = new LinkedList<>();

                while (rs.next()) {
                    materials.add(extract(rs, Material.class));
                }

                return materials;
            }
        }
    }
}

