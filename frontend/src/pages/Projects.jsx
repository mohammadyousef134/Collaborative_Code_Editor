import { useEffect, useState } from "react";
import api from "../api/api";

function Projects() {

  const [projects, setProjects] = useState([]);
  const [newProjectName, setNewProjectName] = useState("");

  useEffect(() => {
    loadProjects();
  }, []);

  const loadProjects = async () => {
    const res = await api.get("/projects");
    setProjects(res.data);
  };

  const createProject = async () => {

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

      {projects.map((project) => (
        <div key={project.id}>

          {project.name}

          <button
            onClick={() => deleteProject(project.id)}
          >
            Delete
          </button>

        </div>
      ))}

    </div>
  );
}

export default Projects;