# GalaxiaMC Fabric Client - Agent Memory

## Project Overview

A Fabric client-side mod for Minecraft 1.21.11 that integrates the GalaxiaNexus social platform directly into the game. Players can browse feeds, view profiles, compose posts, and interact with the Mastodon-compatible API without leaving Minecraft.

## Tech Stack

- **Minecraft:** 1.21.11
- **Fabric Loader:** 0.18.4
- **Fabric API:** 0.141.3+1.21.11
- **Fabric Loom:** 1.13.6
- **Yarn Mappings:** 1.21.11+build.4
- **Java:** 21 (OpenJDK 21.0.10 via Homebrew)
- **HTTP Client:** `java.net.http.HttpClient` (async, non-blocking)
- **JSON Parser:** Gson (bundled with Minecraft)
- **Build System:** Gradle 9.4

## Project Structure

```
src/main/java/com/galaxiamc/client/
├── GalaxiaMCClient.java              # Entry point (ClientModInitializer)
├── api/
│   ├── ApiClient.java                # Async HTTP client for Mastodon API
│   ├── ApiResponse.java              # Generic response wrapper with Optional<T>
│   └── models/                       # API data models (Status, Account, etc.)
├── auth/
│   ├── AuthManager.java              # Token storage and refresh logic
│   └── LinkFlowHandler.java          # In-game link code flow
├── config/
│   └── GalaxiaConfig.java            # JSON config (API URL, tokens)
├── screen/                           # All GUI screens extend Screen
│   ├── GalaxiaHubScreen.java         # Main navigation hub
│   ├── FeedScreen.java               # Home/public timeline with tabs
│   ├── ProfileScreen.java            # User profile + post list
│   ├── PostDetailScreen.java         # Single post + replies
│   ├── NotificationsScreen.java      # Notification list
│   ├── ComposeScreen.java            # Compose new posts
│   └── widget/                       # Custom UI components
│       ├── ScrollableListWidget.java # Paginated list (extends AlwaysSelectedEntryListWidget)
│       ├── StatusEntryWidget.java    # Single post row
│       └── AvatarWidget.java         # Avatar texture renderer
└── keybind/
    └── KeyBindings.java              # Register keybinds (default: G)
```

## Key Conventions

### API Client
- All API methods return `CompletableFuture<ApiResponse<T>>`
- Non-blocking: uses `HttpClient.sendAsync()`
- Always check `response.isSuccess()` and `response.getData().isPresent()`
- Auth: `Authorization: Bearer <token>` header

### UI Screens
- Extend `net.minecraft.client.gui.screen.Screen`
- Use `DrawContext` for rendering (MC 1.21 API)
- All text uses `Text.literal()` for plain strings
- `sendMessage(Text, boolean)` - second param is overlay flag (use `false` for chat)

### Mouse Scroll Handling (MC 1.21.11)
- Signature: `mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)`
- Parent class methods like `getScrollAmount()` and `getMaxPosition()` are NOT available in AlwaysSelectedEntryListWidget
- Scroll-to-bottom pagination needs alternative approach (manual trigger or event-based)

### Texture Rendering (MC 1.21.11)
- **Avatar rendering:** Currently uses placeholder rectangle (`context.fill()`) - proper texture rendering API needs research
- DrawContext methods require `RenderPipeline` parameter (API changed from 1.21.4)
- Avatar cache: in-memory `Map<String, Identifier>`
- Default avatar: `minecraft:textures/entity/steve.png` (not currently used)

### Input Handling (MC 1.21.11)
- Removed `keyPressed()` and `charTyped()` overrides from ComposeScreen - API signatures changed
- TextFieldWidget still handles input internally
- KeyBinding.Category now requires `Identifier` parameter instead of String

### Configuration
- Stored at `config/galaxiamc-client.json`
- Fields: `apiBaseUrl`, `accessToken`, `linkedMinecraftUuid`, `pageSize`
- Loaded on mod init, saved on changes

## Build Commands

```bash
# Clean build
./gradlew clean build

# Compile only
./gradlew compileJava

# Run client (if configured)
./gradlew runClient
```

## Known Issues / TODOs

1. **Avatar rendering**: Currently using placeholder rectangles - need to research proper texture drawing API for MC 1.21.11 (RenderPipeline system)
2. **Scroll-to-bottom pagination**: Currently disabled - need to find correct MC 1.21.11 API for scroll position detection
3. **Avatar caching**: In-memory only - consider disk cache for persistence
4. **Widget rendering**: StatusEntryWidget.render() signature changed - x/entryWidth now hardcoded to 0, needs proper parent list integration
5. **Error handling**: Basic error messages - could add retry logic and better UX
6. **Reply threading**: Limited to depth 3 - could support deeper threads
7. **Image attachments**: Not yet supported - status content is text-only

## API Endpoints (Mastodon-compatible)

Base URL: `https://social.galaxiamc.com`

- `GET /api/v1/timelines/home` - Home feed
- `GET /api/v1/timelines/public` - Public feed
- `GET /api/v1/accounts/:id` - Profile info
- `GET /api/v1/accounts/:id/statuses` - User posts
- `GET /api/v1/statuses/:id` - Single post
- `POST /api/v1/statuses` - Create post
- `POST /api/v1/statuses/:id/favourite` - Like post
- `POST /api/v1/statuses/:id/reblog` - Boost post
- `GET /api/v1/notifications` - Notifications
- `GET /api/v1/auth/minecraft/request_code` - Get link code (custom)
- `POST /api/v1/auth/minecraft/link` - Complete link (custom)

## Testing Notes

- Backend API may not be live yet - API calls will 404 during development
- Use mock responses or run local Mastodon instance for testing
- Config file can be manually edited to test different API URLs

## Future Enhancements

- Bookmark support
- Follow/unfollow from profile screen
- Direct messages (if backend supports)
- Rich text formatting (Markdown support)
- Image attachment upload/display
- Emoji picker
- Search functionality
- Multiple account switching
