FROM node:24 AS build

ARG BUILD_CONFIG=production

WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npx ng build --configuration ${BUILD_CONFIG}

FROM nginx:alpine
COPY --from=build /app/dist/bookstore-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]