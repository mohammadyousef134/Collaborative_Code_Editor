import {Routes, Route} from "react-router-dom";
import Login from "./pages/Login";
import Projects from "./pages/projects";

function App() {
  return (
    <Routes>

      <Route path="/" element={<Login />} />

      <Route path="/projects" element={<Projects />} />

    </Routes>
  );
}


export default App;