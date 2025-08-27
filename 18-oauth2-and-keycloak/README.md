
---

# Project Setup Guide

This guide walks you through setting up **Keycloak** with a database using Prisma and configuring Google OAuth integration.

---

## 1. Install & Setup Keycloak

1. Navigate to the Keycloak `bin` directory:

```bash
cd <keycloak-installation-path>/bin
```

2. Set up admin account credentials (replace with your own secure password):

```bash
set KEYCLOAK_ADMIN=keycloak-admin
set KEYCLOAK_ADMIN_PASSWORD=YourSecurePassword
```

3. Start Keycloak in development mode:

```bash
kc.bat start-dev
```

Optional: specify HTTP port and hostname:

```bash
kc.bat start-dev --http-port=8080 --hostname=localhost
```

4. Reset admin password (if needed):

```bash
bin/kc.sh bootstrap-admin user --username new-admin --password "NewStrongPassword!"
```

---

## 2. Database Setup with Prisma

Run the following commands to generate Prisma client, run migrations, and seed the database:

```bash
# Generate Prisma client
npm run prisma:generate

# Apply database migrations
npm run prisma:migrate

# Open Prisma Studio for DB inspection
npm run prisma:studio

# Seed the database
npx prisma db seed
```

---

## 3. Google OAuth Configuration

1. Go to **Google Cloud Console** and create OAuth 2.0 credentials.
2. Save the following info (replace with your actual credentials):

```
Client ID: <your-google-client-id>
Client Secret: <your-google-client-secret>
```

3. Authorized redirect URLs (for local development):

```
http://localhost:8080/realms/master/broker/google/endpoint
```

---

## 4. Keycloak Client Configuration

1. In Keycloak, create a new client for Google OAuth:

```
Client ID: keycloak-idp
Client Secret: <your-keycloak-client-secret>
```

2. Add the same authorized redirect URL as above.

---

## 5. Running the Project

1. Start Keycloak:

```bash
kc.bat start-dev
```

2. Ensure database migrations and seeds have been applied.
3. Open Keycloak Admin Console to verify Google OAuth client.

---

### Notes

* Replace all sensitive credentials (`Client Secret`, passwords) with your own secure values.
* Use `http://localhost:8080` for local development testing.
* For production, update hostname and redirect URLs accordingly.

---