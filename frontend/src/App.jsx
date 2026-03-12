import {Routes, Route} from "react-router-dom";
import Login from "./pages/Login";
import Projects from "./pages/projects";
import Documents from "./pages/Documents";
import DocumentEditor from "./pages/DocumentEditor";
import Invitations from "./pages/Invitations";

function App() {
  return (
    <Routes>

      <Route path="/" element={<Login />} />

      <Route path="/projects" element={<Projects />} />

      <Route path="/projects/:projectId/documents" element={<Documents />} />
      
      <Route path="/projects/:projectId/documents/:id" element={<DocumentEditor />} />

      <Route path="/invitations" element={<Invitations />} />

    </Routes>
  );
}


export default App;