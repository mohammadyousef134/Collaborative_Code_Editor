import { useEffect, useState } from "react";
import api from "../api/api";
import { useNavigate } from "react-router-dom";

function Projects() {

  const [projects, setProjects] = useState([]);
  const [newProjectName, setNewProjectName] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    const res = await api.get("/projects");
    setProjects(res.data);
  };

  const createProject = async () => {
    if (!newProjectName) return;
    await api.post("/projects", {
      name: newProjectName
    });

    setNewProjectName("");
    loadProjects();

  };

  const deleteProject = async (id) => {

    await api.delete(`/projects/${id}`);
    loadProjects();

  };

  return (
    <div>

      <h2>Projects</h2>

      <input
        placeholder="New project name"
        value={newProjectName}
        onChange={(e) => setNewProjectName(e.target.value)}
      />

      <button onClick={createProject}>
        Create Project
      </button>

      <hr />

      {projects.map(project => (
        <div key={project.id}>

          <span
            onClick={() => navigate(`/projects/${project.id}/documents`)}
            style={{ cursor: "pointer", marginRight: "10px" }}
          >
            {project.name}
          </span>

          <button onClick={() => deleteProject(project.id)}>
            Delete
          </button>

        </div>
      ))}

      <button onClick={() => navigate("/invitations")}>
        Invitations
      </button>

    </div>
  );
}

export default Projects;