# GalaxiaMC Fabric Client

A client-side Fabric mod for Minecraft 1.21.4 that integrates **GalaxiaNexus** (the GalaxiaMC social platform) directly into the game.

## Features

- 🎮 **In-Game Social Platform** - Browse feeds, profiles, and posts without leaving Minecraft
- 🔐 **Secure Authentication** - Link your Minecraft account to GalaxiaNexus with a simple code
- 📱 **Full UI Integration** - Custom screens with scrollable lists, text input, and avatar rendering
- 🚀 **Async API Client** - Non-blocking HTTP requests to the Mastodon-compatible backend
- ⌨️ **Keybind Support** - Default keybind `G` to open the hub screen
- 💬 **Social Features** - Like, boost, reply, compose posts, and view notifications

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.4
2. Install [Fabric API](https://modrinth.com/mod/fabric-api) (v0.119.0+)
3. Download `galaxiamc-client-0.1.0.jar` from releases
4. Place the JAR in your `.minecraft/mods` folder
5. Launch Minecraft with the Fabric profile

## Usage

### First Time Setup

1. Press `G` in-game (or run `/galaxia` command)
2. Click "Link Account" or run `/galaxia link`
3. Copy the 6-digit code shown in chat
4. Go to `nexus.galaxiamc.com/link` and enter the code
5. Your account is now linked!

### Features

- **Hub Screen** - Press `G` to open the main navigation
- **Feed** - Browse home timeline or public feed
- **Profiles** - View player profiles and their posts
- **Post Detail** - View full posts with replies
- **Compose** - Write new posts (500 character limit)
- **Notifications** - See likes, boosts, and mentions

## Configuration

Configuration is stored in `config/galaxiamc-client.json`:

```json
{
  "apiBaseUrl": "https://social.galaxiamc.com",
  "accessToken": "",
  "linkedMinecraftUuid": "",
  "pageSize": 20
}
```

## Development

### Requirements

- Java 21
- Gradle 9.4+
- Minecraft 1.21.4
- Fabric Loader 0.16.9+
- Fabric API 0.119.0+

### Building

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/galaxiamc-client-0.1.0.jar`

### Project Structure

```
src/main/java/com/galaxiamc/client/
├── GalaxiaMCClient.java         # Mod entry point
├── api/                         # HTTP client + models
├── auth/                        # Authentication flow
├── config/                      # Configuration management
├── screen/                      # GUI screens
│   └── widget/                  # Custom widgets
└── keybind/                     # Keybind registration
```

## API Integration

This mod connects to a Mastodon-compatible API:

- Base URL: `https://social.galaxiamc.com`
- Authentication: OAuth2 Bearer tokens
- Endpoints: `/api/v1/timelines/*`, `/api/v1/statuses/*`, etc.

## License

MIT License - See LICENSE file

## Credits

**Brand:** GalaxiaNexus / Nexus  
**Project:** GalaxiaMC  
**Web:** nexus.galaxiamc.com

Built with [Fabric](https://fabricmc.net/)
