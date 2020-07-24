FROM nginx:stable

RUN chmod g+rwx /var/cache/nginx /var/run /var/log/nginx

COPY default.conf /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/nginx.conf
COPY dist /usr/share/nginx/html
RUN mv /usr/share/nginx/html/smart-campus/* /usr/share/nginx/html/
RUN rm -rf /usr/share/nginx/html/smart-campus
EXPOSE 8081