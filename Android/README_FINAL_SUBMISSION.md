# Shadow Realm Android Frontend — Final Build

A polished Android frontend for Shadow Realm using the supplied purple-moon visual theme.

## Included
- Aesthetic login screen using the supplied Shadow Realm artwork.
- Matching registration screen with backend-supported fields.
- Real Spring Boot login + registration using JWT.
- JWT stored with Android Keystore-backed AES-GCM protection.
- Back arrows on secondary screens.
- Server-backed room create/join lobby flow.
- Private role screen and private secret-word/hint screen.
- Full local Quick Play fallback for demonstrating the complete night/day/voting/result UI.
- Rules, profile, logout, chat and gameplay screens.

## Before demo
1. Apply `ShadowRealm_Backend_SecretSystem_Patch.zip` to the backend repository.
2. Start MySQL and the Spring Boot backend on port 8080.
3. Open this project in Android Studio and run the app on an emulator.
4. Register a new account or use an existing backend account.
5. For a backend demo: Create Realm / Join Realm and use multiple accounts/devices.
6. For a guaranteed UI/gameplay demonstration without the server, use LOCAL TEST ROUND.

The LOCAL TEST ROUND is intentionally a fallback/demo engine; it is not a substitute for the server-backed multiplayer flow.
