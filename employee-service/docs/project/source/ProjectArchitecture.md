---
# ArchitectureLayer[]
layers:
  - name: "Controllers"
    description: "Inbound REST adapters for command and query operations"
    color: "#4CAF50"
    expanded: true
    components:
      - "EmployeeCommandController"
      - "EmployeeQueryController"
    responsibilities:
      - "Request validation"
      - "Role-based access control"
      - "Command/query dispatch"
    technologies:
      - "Spring Web MVC"
      - "Spring Security"

  - name: "Application Layer"
    description: "Use cases and business orchestration"
    color: "#2196F3"
    expanded: true
    components:
      - "Employee command handlers"
      - "Employee query handlers"
    responsibilities:
      - "Business rule orchestration"
      - "Transaction boundaries"
    technologies:
      - "Spring Service"
      - "CQS pattern"

  - name: "Reverse Proxy Layer"
    description: "Edge TLS and load balancing for containerized deployment"
    color: "#009688"
    expanded: true
    components:
      - "Nginx 1.27"
    responsibilities:
      - "HTTP to HTTPS redirect"
      - "TLS termination"
      - "least_conn routing to replicas"
    technologies:
      - "Nginx"

# DesignPattern[]
designPatterns:
  - title: "Command Query Separation"
    emoji: "⚙️"
    description: "Separate command and query controllers/use-cases improve clarity and scaling options"
    category: "Architectural"
    badge: "CQS"
    githubExampleUrl: ""

# StrategyItem[] - Scalability
scalabilityStrategies:
  - title: "Nginx Horizontal Scaling"
    description: "Nginx upstream with least_conn allows docker-compose scaling without endpoint changes"
  - title: "Stateless Service Nodes"
    description: "Employee instances remain stateless and can be replicated behind Nginx"

# StrategyItem[] - Security
securityStrategies:
  - title: "Edge TLS Termination"
    description: "Nginx provides HTTPS at :443 and redirects HTTP :80 to HTTPS"
  - title: "JWT + Role Authorization"
    description: "Service enforces token and role checks for employee endpoints"

# CacheStrategy[]
cacheStrategies:
  - name: "Redis Backed Rate Limit/Cache"
    description: "Redis supports cross-cutting control and caching behavior"
    ttl: "Config-driven"
    coverage: "Employee API traffic patterns"

# ArchitectureFeature[]
architectureFeatures:
  - title: "Nginx Front Door"
    emoji: "🚪"
    description: "Single ingress for TLS and load balancing"
  - title: "Command/Query Split"
    emoji: "🧩"
    description: "Separated controller responsibilities for write/read paths"

# ArchitectureDiagramModel
architectureDiagram:
  legendItems:
    - type: "client"
      label: "Client"
      color: "#4CAF50"
      icon: "user"
    - type: "gateway"
      label: "Nginx"
      color: "#009688"
      icon: "nginx"
    - type: "service"
      label: "Employee Service"
      color: "#2196F3"
      icon: "spring"
    - type: "database"
      label: "PostgreSQL"
      color: "#9C27B0"
      icon: "database"
  nodes:
    - id: "client"
      label: "Frontend/Admin"
      type: "client"
      x: 100
      y: 100
      connections:
        - "nginx"
      status: "healthy"
      traffic: 100
    - id: "nginx"
      label: "Nginx TLS + LB"
      type: "gateway"
      x: 260
      y: 100
      connections:
        - "employee-service"
      status: "healthy"
      traffic: 100
    - id: "employee-service"
      label: "Employee Service"
      type: "service"
      x: 430
      y: 100
      connections:
        - "postgres"
      status: "healthy"
      traffic: 80
    - id: "postgres"
      label: "PostgreSQL"
      type: "database"
      x: 430
      y: 250
      connections: []
      status: "healthy"
      traffic: 45
  connections:
    - id: "conn1"
      from: "client"
      to: "nginx"
      label: "HTTPS :443"
      protocol: "HTTPS"
      isActive: true
    - id: "conn2"
      from: "nginx"
      to: "employee-service"
      label: "HTTP :8081 (internal)"
      protocol: "HTTP"
      isActive: true
    - id: "conn3"
      from: "employee-service"
      to: "postgres"
      label: "JDBC"
      protocol: "TCP"
      isActive: true

# DataFlowModel
dataFlow:
  requestFlow:
    - number: 1
      title: "Client Request to Nginx"
      description: "External request arrives on HTTPS :443"
      icon: "nginx"
    - number: 2
      title: "Forward to Employee Service"
      description: "Nginx forwards to internal employee-service:8081"
      icon: "service"
  eventFlow:
    - number: 1
      title: "Employee Domain Events"
      description: "Service may emit/consume events via Kafka depending on integration flow"
      icon: "kafka"

# TechDecisionsModel
techDecisions:
  decisions:
    - title: "Adopt Nginx Edge Proxy"
      problem: "Need HTTPS endpoint and scalable routing without changing service internals"
      solution: "Add Nginx in docker-compose with TLS termination and least_conn upstream"
      outcome: "Stable HTTPS ingress and horizontal scaling path for employee-service"
      icon: "nginx"
      alternatives:
        - "Direct service port exposure without load balancing"
---
# Architecture
> Employee-service now runs behind an Nginx ingress layer in Docker deployments. Nginx handles HTTPS and balances traffic to service replicas on internal port 8081.

