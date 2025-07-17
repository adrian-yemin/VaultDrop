# PipeDream: Data Pipeline Simulation Platform

PipeDream is a full-stack web application for simulating, testing, and debugging data pipelines using synthetic data. It allows users to define input data schemas, apply custom transformation steps (e.g., map, filter, group-by), and visualize the output and processing logs—all in an interactive browser-based interface.

## 🌐 Live Demo
> Coming soon! Will be hosted on AWS (EC2 + S3 + CloudFront)

---

## 🛠 Features

- 🔧 **Custom Pipeline Builder**  
  Visually define pipeline steps (map, filter, rename, etc.) via a frontend UI.

- 🧪 **Schema-Based Synthetic Data Generation**  
  Users can define input schemas with field types (int, string, float, boolean, timestamp) and generation rules.

- 📊 **Simulated Pipeline Runs**  
  See live execution results with output samples, stage-by-stage metrics, and error tracking.

- 📁 **Run History + Persistence**  
  Save pipelines, schemas, and past simulation results to PostgreSQL.

- ☁️ **Cloud-Ready Deployment**  
  Containerized with Docker and deployable on AWS infrastructure.

---

## ⚙️ Tech Stack

### Frontend
- **React** with Vite
- Tailwind CSS
- React DnD or form-based UI for transformations

### Backend
- **Spring Boot (Java)** – REST API and simulation engine
- Java Streams and Reflection for transformation logic
- In-memory execution with structured logging

### Database
- **PostgreSQL** – user profiles, pipeline definitions, run history

### Infrastructure
- **Docker** – containerization for all services
- **AWS** – EC2 (deployment), S3 (asset storage), CloudWatch (optional logs)

---

## 📦 How It Works

1. **Define Input Schema**  
   Choose field names and types. Data will be randomly generated based on the schema.

2. **Add Transformation Stages**  
   Each stage modifies the dataset via user-defined logic (e.g., filter out nulls, rename fields, reformat data).

3. **Simulate Run**  
   The backend simulates a pipeline run, showing:
   - Live data previews after each stage
   - Performance metrics (records processed, dropped, errors)
   - Debugging logs

4. **View Output**  
   Download the final dataset, or inspect records in-browser.

---

## 🧪 Example Use Case

### Product Data Cleanup Pipeline

- **Input Schema**
  ```json
  {
    "product_id": "int",
    "name": "string",
    "price": "float",
    "in_stock": "boolean"
  }

