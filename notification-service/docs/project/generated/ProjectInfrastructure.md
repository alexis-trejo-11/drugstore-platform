---

# InfrastructureMetric[]

metrics:

- label: "" value: "" icon: "" description: ""

# CloudService[]

cloudServices:

- name: "" purpose: "" icon: "" cost: ""

# DeploymentLayer[]

deploymentLayers:

- name: "" color: ""
    
    # DeploymentComponent[]
    
    components:
    - name: "" icon: "" description: ""

# DockerFile[]

dockerFiles:

- service: "" description: "" content: ""

---

## Reverse proxy (Nginx)

This service repository does **not** ship a bundled Nginx edge container. Terminate TLS at your platform ingress (Kubernetes Ingress, ALB, API Gateway, or a standalone Nginx following the pattern in sibling services).

# Infrastructure

> Notification Service — wire infra ingress separately; see other `*-service/nginx/` folders for a reusable compose pattern.