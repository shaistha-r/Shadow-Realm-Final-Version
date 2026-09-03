# Shadow Realm — Backend Integration

This Android project is designed for the Spring Boot backend in the Shadow Realm GitHub repository.

## Current backend routes used by the app
- `POST /users/register`
- `POST /users/login`
- `POST /room/create?maxPlayers=7`
- `POST /room/join?roomCode=XXXXXX`
- `POST /room/leave?roomCode=XXXXXX`
- `DELETE /room/delete?roomCode=XXXXXX`
- `GET /player/{roomCode}`
- `GET /player/{roomCode}/alive`
- `POST /game/start?roomCode=XXXXXX`
- `GET /game/{roomCode}`
- `PUT /game/next?roomCode=XXXXXX`
- `PUT /game/winner?roomCode=XXXXXX`
- `POST /vote/cast`
- `POST /vote/{roomCode}/resolve`

## Added secret-system routes
Apply `ShadowRealm_Backend_SecretSystem_Patch.zip` to the backend first:
- `GET /player/me/{roomCode}`
- `GET /game/{roomCode}/secret`

The secret endpoint is authenticated. The server returns the normal word to non-Traitors and only the hint to the Traitor. The secret fields are excluded from the normal Game JSON response.

## Emulator
Spring Boot on the same Windows PC is reached from the Android emulator with:
`http://10.0.2.2:8080/`

For a physical Android phone on the same Wi-Fi, replace `10.0.2.2` in `RetrofitClient.java` with the PC's LAN IPv4 address.

## Important
The backend repository currently has BCrypt + JWT authentication, server-side room creation/joining, player role assignment, game phase lifecycle, and voting. The Project Status Report still lists the Secret System and Frontend Integration as unfinished, so the supplied secret patch completes the missing private-secret bridge.
