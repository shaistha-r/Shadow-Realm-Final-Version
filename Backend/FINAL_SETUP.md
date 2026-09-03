# Shadow Realm — Final backend setup

## What this version adds
- JWT authentication remains server-side.
- BCrypt password hashing remains enabled.
- Room creation/joining uses the authenticated JWT user.
- Public player lists no longer expose player roles.
- `/player/me/{roomCode}` exposes only the logged-in player's own role.
- `/game/{roomCode}/secret` returns only that logged-in player's word or Traitor hint.
- Secrets are stored server-side with the Game and hidden from JSON responses.
- New secret word/hint is generated for each round.
- Night actions use the authenticated player; clients cannot impersonate another player by supplying an actor ID.
- Voting uses the authenticated player as voter and waits for all living players before resolving.
- Room deletion is restricted to the host.
- Night resolution moves the game to DAY and checks the winner.

## Required local environment
Set these before running Spring Boot:
- `DB_URL` (default: jdbc:mysql://localhost:3306/shadowrealm)
- `DB_USERNAME` (default: root)
- `DB_PASSWORD` (your local MySQL password)
- `JWT_SECRET` (a long random secret, at least 32 characters)

Do NOT commit real database passwords or JWT secrets to GitHub.

## Run
Windows:
`mvnw.cmd spring-boot:run`

The API is normally available at:
`http://localhost:8080`

For Android Emulator, the app uses:
`http://10.0.2.2:8080/`

For a physical phone, change the Android base URL to your computer's LAN IP and ensure the firewall permits port 8080.

## Important
This is a local-development configuration. HTTPS should be used before exposing the backend to the public internet.
