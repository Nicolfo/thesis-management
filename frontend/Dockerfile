# Stage 1: Build React App
FROM node:16 AS builder
WORKDIR /app
# Copy package.json and package-lock.json to leverage Docker cache
COPY package*.json ./
# Install dependencies
RUN npm install
# Copy the rest of the application code
COPY . .
# Build the React app
RUN npm run build
# Stage 2: Create Nginx Container
FROM nginx:1.30-alpine
# Pull Alpine security fixes newer than the base image (curl/libcurl 8.23.0: CVE-2026-92392, brotli 1.2.1: CVE-2026-111497)
RUN apk upgrade --no-cache
# Remove default Nginx static files
RUN rm -rf /usr/share/nginx/html/*
# Copy custom Nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copy the built React app from the previous stage
COPY --from=builder /app/build /usr/share/nginx/html
# Expose port
EXPOSE 80
# Command to run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]