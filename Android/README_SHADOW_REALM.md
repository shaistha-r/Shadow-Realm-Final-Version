# Shadow Realm — Android Frontend v2.0

A polished Java/XML Android frontend for the Shadow Realm social-deduction game.

## What is included

- Splash / session gate
- Login with the verified backend contract: `POST /users/login`
- JWT storage protected by Android Keystore (AES/GCM)
- Automatic Bearer token interceptor for future protected APIs
- Registration + MFA/OTP demo flow
- Home dashboard
- Create / Join room flow with demo room-code generation
- Lobby with player gathering
- Secret role reveal: Villager / Seer / Healer / Traitor
- Day discussion screen
- Night action screen
- Local discussion chat
- Voting screen
- Verdict / results screen
- Rules screen
- Identity / security profile
- Explicit back buttons on secondary screens
- Supplied Shadow Realm night artwork used as the app background

## Important backend note

The supplied Android project only had one verified backend contract:

`POST /users/login`

with:

```json
{"email":"...","password":"..."}
```

and a response containing:

```json
{"token":"..."}
```

The public project description also says the overall system uses Spring Boot, JWT, private rooms, live chat, voting and WebSockets, but exact room/game endpoint JSON was not available in the supplied Android code. Therefore this frontend deliberately does **not** invent room/game API contracts.

The room/game screens are complete, playable frontend/demo flows. When the backend team gives the exact endpoint/request/response shapes, add those calls to `ApiService.java` and replace the corresponding demo actions.

## Run

1. Open the `LoginApp2` folder in Android Studio.
2. Let Gradle sync/download dependencies.
3. Start an Android emulator.
4. Press Run.
5. For the complete frontend demo without starting the backend, use **PLAY A DEMO ROUND** on the login screen.

### Real backend login

Start the Spring Boot backend on port 8080. The emulator uses:

`http://10.0.2.2:8080/`

For a physical phone, change `BASE_URL` in `RetrofitClient.java` to the computer's LAN address, for example `http://192.168.x.x:8080/`.

## Demo gameplay

Login → **PLAY A DEMO ROUND** → Home → **Create a Private Realm** → **Generate Room** → **Add 3 Demo Players** → **Open the Gates** → Lobby → **Start the Ritual** → Secret Role → Game → Chat / Night Action / Vote → Verdict.

## Suggested manual test checklist

- [ ] Login validation rejects invalid email / short password.
- [ ] Demo login opens Home.
- [ ] Registration validates fields and OTP accepts six digits.
- [ ] Back buttons return to the previous screen.
- [ ] Room generation creates a six-character code.
- [ ] Lobby reaches 4 players and starts.
- [ ] Secret role is displayed.
- [ ] Chat sends a message.
- [ ] Night action requires a target for special roles.
- [ ] Vote requires a selection.
- [ ] Results returns home.
- [ ] Logout returns to login and clears the local session.
- [ ] Real backend login works when Spring Boot is running.

## Project ownership

This is the Android Frontend / Person 4 layer. It is designed to consume the backend team's APIs rather than duplicate backend gameplay logic.
