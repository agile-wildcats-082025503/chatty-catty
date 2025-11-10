# Build stage
FROM node:18-alpine AS build
ARG REACT_APP_HOST_ADDRESS

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
ENV REACT_APP_HOST_ADDRESS=${REACT_APP_HOST_ADDRESS}
RUN npm run build

# Production stage - serve static files using nginx
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
