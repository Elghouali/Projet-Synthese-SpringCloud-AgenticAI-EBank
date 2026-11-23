FROM node:22-alpine AS build
WORKDIR /app
COPY package.json angular.json tsconfig.json tsconfig.app.json proxy.conf.json ./
COPY src ./src
RUN npm install --no-audit --no-fund && npm run build
FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/ebank-ui/browser/ /usr/share/nginx/html/
EXPOSE 80
