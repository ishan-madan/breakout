# Breakout Design
## NAME
Ishan Madan

## Design Goals
I designed this project with the following future extandability goals:
* Easy addition of new power-ups through the PowerUpManager class
* Simple creation of new levels through text file configurations
* Flexible collision detection system that can handle different types of objects
* Modular UI components that can be reused across different screens
* Customizable game mechanics through constants and configuration

## High-Level Design
The project is organized into several core classes, each handling specific functionality:

### Main Class
* Serves as the primary controller and JavaFX application entry point
* Manages game initialization and the main game loop
* Handles key input processing and scene transitions

### Core Game Classes
* **Bouncer**: Manages ball behavior including movement, collision detection, and direction changes
* **Pad**: Controls paddle movement and positioning
* **Tile**: Implements block behavior including power-ups and health management

### Manager Classes
* **UIElements**: Handles creation and updates of UI components
* **CollisionManager**: Provides sophisticated collision detection between game objects
* **PowerUpManager**: Controls power-up implementation and timing
* **GameStateManager**: Manages game state transitions and win/loss conditions
* **SplashScreenManager**: Handles different game screens (start, pause, level completion)

## Assumptions or Simplifications
1. **Level Design**
   * Levels are represented in simple text files with fixed dimensions
   * Block sizes are standardized (40x20 pixels)
   * Power-up types are limited to single characters

2. **Game Mechanics**
   * Ball movement follows simplified physics (constant speed, perfect reflection)
   * Paddle movement is restricted to horizontal motion
   * Power-up durations are fixed at 300 frames
   * Maximum of 3 levels

3. **UI Design**
   * Fixed window size (400x400 pixels)
   * Consistent color scheme across levels
   * Simple text-based UI elements

## Changes from the Plan
The final implementation includes several enhancements from the original design:
1. Addition of multiple ball support
2. Implementation of multi-hit blocks
3. Enhanced power-up system with temporary effects
4. Addition of explosion mechanics for special blocks
5. Implementation of pause functionality with visual feedback
6. Score system with high score tracking

## How to Add New Features

### Adding New Power-ups
1. Add a new character identifier in the level text files
2. Create a new method in `PowerUpManager` for the power-up effect
3. Add the power-up logic to `Tile.activatePowerup()`
4. Update the tile color scheme in `Tile.setColor()`
5. Update the start screen instructions

### Adding New Levels
1. Create a new text file in the resources folder named `lvlX.txt` (where X is the level number)
2. Update `MAX_LEVEL` constant in `Main`
3. Configure the level layout using the following characters:
   * '0': Empty space
   * '1': Standard block
   * '2'-'9': Multi-hit blocks
   * 'a': Add ball power-up
   * 'p': Paddle extension power-up
   * 's': Speed up power-up
   * 'e': Explosion power-up

### Adding New Block Types
1. Add new character identifier in level file format
2. Extend `Tile` class to handle new block behavior
3. Add new color scheme in `setColor()`
4. Implement custom damage behavior if needed
5. Update collision handling if required

### Adding New Game Modes
1. Create new scene handling in `SplashScreenManager`
2. Implement mode-specific logic in `GameStateManager`
3. Add new UI elements through `UIElements` class
4. Update key handling for mode-specific controls
5. Create new initialization logic in `loadNewScene()`
