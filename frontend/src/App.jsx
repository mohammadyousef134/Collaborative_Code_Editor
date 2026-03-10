import {Routes, Route} from "react-router-dom";
import Login from "./pages/Login";
import Projects from "./pages/projects";
import Documents from "./pages/Documents";
import DocumentEditor from "./pages/DocumentEditor";

function App() {
  return (
    <Routes>

      <Route path="/" element={<Login />} />

      <Route path="/projects" element={<Projects />} />

      <Route path="/projects/:projectId/documents" element={<Documents />} />
      
      <Route path="/projects/:projectId/documents/:id" element={<DocumentEditor />} />

    </Routes>
  );
}


export default App;