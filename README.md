# BanItems Mod

A Minecraft Forge mod designed for server administrators to flexibly manage and restrict in-game items, supporting both soft bans and hard bans to maintain server balance and gameplay rules.


## Core Features
BanItems provides targeted item control with user-friendly management tools, ensuring server rules are enforced without disrupting player experience:
- Dual Ban Modes:
  - Soft Ban: Restrict item functionality (e.g., prevent placing blocks, using tools) while allowing players to keep the item in their inventory.
  - Hard Ban: Fully block players from obtaining, holding, or using the item (automatically removes the item if detected).
- NBT-Aware Restrictions: Ban specific item variants (e.g., enchanted swords, custom-named potions) by recognizing NBT data, not just base item IDs.
- Intuitive GUI Management:
  - In-game inventory-style GUI for adding/removing banned items (no manual config editing required).
  - Visual previews of banned items with details (e.g., ban type, NBT tags).
- Comprehensive Command Set: Quick access to ban management, list viewing, and config reloading via in-game commands.
- Persistent Configuration: Banned item lists are saved to a JSON file, ensuring rules persist across server restarts.


## Commands
All commands require the `banitems.admin` permission (granted to server operators by default). Use `/banitem help` in-game for a quick reference:

| Command | Description |
|---------|-------------|
| `/banitem soft` | Open GUI to add an item to the soft ban list |
| `/banitem hard` | Open GUI to add an item to the hard ban list |
| `/banitem list soft [page]` | View all soft-banned items (supports pagination, e.g., `/banitem list soft 2`) |
| `/banitem list hard [page]` | View all hard-banned items (supports pagination) |
| `/banitem reload` | Reload the configuration file (applies changes without restarting the server) |
| `/banitem help` | Show all available commands and usage instructions |


## Installation
BanItems is a Forge mod—ensure your server/client uses a compatible Minecraft Forge version (check the Releases page for version details):

1. Install Forge: Download and install Minecraft Forge for your target Minecraft version (e.g., 1.20.1-forge-47.1.0).
2. Download Mod: Get the latest BanItems JAR from the GitHub Releases or your preferred mod platform.
3. Add to Mods Folder:
   - For servers: Place the JAR in your server’s `mods/` directory.
   - For clients (single-player or server access): Place the JAR in your local `.minecraft/mods/` directory.
4. Start Server/Client: Launch Minecraft—BanItems will auto-initialize and create default config files.


## Configuration
BanItems uses a human-readable JSON config file to store ban rules, making manual edits easy if needed:

### Config Path
- Server/Client: `config/banitems/banitems.json`

### Config Structure
The file includes three key sections (auto-generated on first launch):
```json
{
  "softBannedItems": [],  // Items with restricted functionality (e.g., ["minecraft:ender_pearl"])
  "hardBannedItems": [],  // Items blocked from use/ownership (e.g., ["minecraft:creative_only_item"])
  "excludedPlayers": []   // Players exempt from ban rules (e.g., ["AdminPlayer123"])
}
```
- **Edit Tips**: Use the in-game GUI/commands for safer management—manual edits require a `/banitem reload` to take effect.


## Development
BanItems follows Minecraft Forge best practices, with code structure inspired by community standards and references from [itembarriers](https://github.com/linstarowo/itembarriers) (replace with actual reference if provided).

### Local Build Setup
1. **Clone Repository**:
   ```bash
   git clone https://github.com/YuWan-030/BanItems.git
   cd BanItems
2. **Build with Gradle**:
   - Windows: `gradlew.bat build`
   - Linux/macOS: `./gradlew build`
**Retrieve Built JAR**: The compiled mod JAR will be in `build/libs/` (look for the file without `-sources` or `-dev` suffix).

### Contribution Guidelines
- Fork the repository and create a feature branch (e.g., `feature/add-whitelist`).
- Follow the existing code style (consistent with Forge modding conventions).
- Test changes locally before submitting a pull request.

## License
BanItems is licensed under the MIT License—see the LICENSE file for full terms. You are free to:
- Use the mod on public/private servers.
- Modify the code for personal use.
- Distribute modified versions with attribution.

## Credits
- Framework: Built with Minecraft Forge, the de facto modding framework for Minecraft.
- Inspiration: Mod design and functionality draw from community feedback and modding concepts referenced at [itembarriers](https://github.com/linstarowo/itembarriers).
- Maintenance: The project is actively maintained by YuWan-030 (check the GitHub repository for latest updates).

For issues, feature requests, or support, please open an issue on GitHub!
