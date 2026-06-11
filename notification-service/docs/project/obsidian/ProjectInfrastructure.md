
---

# InfrastructureMetric[]

metrics:

- label: "Reverse Proxy"
  value: "Not bundled"
  icon: "nginx"
  description: "No nginx/ folder or edge container in this repo — terminate TLS at platform ingress or copy pattern from sibling services"

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

# Infrastructure

> This module does **not** include an Nginx compose service. Use Kubernetes Ingress, a shared gateway, or mirror `*-service/nginx/` + compose snippets from cart/order/product services when you need TLS termination at the edge.