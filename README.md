# SPRING BOOT APP - MURPHY SERVER

## Pre-Setup Localy

### Define Envrionment Variables 
>You can set the variables in a .env file in the root directory or can set on your docker environment the variables that the app, postgres and pgadmin needs are:

        1. POSTGRES_PORT: Port of Postgres Database.
        2. POSTGRES_PORT_INT: Port internally where Postgres will run.
        3. POSTGRES_USER: User of postgres database.
        4. POSTGRES_PASSWORD: Password of postgres user.
        5. POSTGRES_DB: name of the database of the app.
        6. PGADMIN_PORT: pgadmin port.
        7. PGADMIN_DEFAULT_EMAIL: pgadmin default email to access.
        8. PGADMIN_DEFAULT_PASSWORD: pgadmin default password to access.
        9. JWT_SECRET: Secret that will be use to encrypt data as password an tokens.
        10. AWS_ACCESS_KEY_ID: This variable can be set or provided by amazon if the envrionment if not locally.
        11. AWS_SECRET_ACCESS_KEY: This variable can be set or provided by amazon if the envrionment if not locally.
        12. AWS_REGION:This variable can be set or provided by amazon if the envrionment if not locally.
        13. AWS_BUCKET_NAME: This variable can be set or provided by amazon if the envrionment if not locally.
        14. APP_ADMIN_USER: User of admin of the server.
        15. APP_ADMIN_PASSWORD: Password of admin user of the server. 
        16. APP_ADMIN_EMAIL:  Mail of admin user of the server.
        17. MAIL_SMTP_HOST: Mail host of mail service to use.
        18. MAIL_SMTP_PORT: Mail port of mail service to use.
        19. MAIL_SMTP_USERNAME: Username of mail service to use.
        20. MAIL_SMTP_PASSWORD: Password of mail service to use.

>Note: These variables must be secrets or encrypted protected in order to reduce security issues forward. 


## Setup Localy

### Check image and build containers the Docker-Compose
>The actual version of  Docker-Compose pulls the latest image of the app, that is store in public repository diegopevi05/murphy-server: 

				diegopevi05/murphy-server:<version>

>After check that the desire version of the app is set, we can run docker-compose to build the images in the environment and run the containers:
				
				docker-compose up --build



