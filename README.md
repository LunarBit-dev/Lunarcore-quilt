# LunarCore Quilt

Foundational SDK for Quilt mod development, implementing the LunarCore specification.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.20+-green.svg)](https://www.minecraft.net/)
[![Quilt](https://img.shields.io/badge/Quilt-0.19.1+-purple.svg)](https://quiltmc.org/)

## Overview

LunarCore Quilt provides essential utilities for Quilt mods:
- Lifecycle management with clean initialization and shutdown
- Structured logging with automatic sensitive data redaction
- Configuration system with environment variable support
- Helpers for accessing mod metadata

This is a Quilt-native implementation that follows the same specification as LunarCore Fabric but doesn't share any binary code. It uses Quilt-specific features like `ModContainer` and integrates with QSL.

## Features

### ✅ Lifecycle Helpers
Clean abstraction over Quilt's initialization system:
```java
public class MyMod extends ModLifecycleAdapter {
    @Override
    public void initialize(ModContainer container) {
        // Your initialization code
    }
    
    @Override
    public void shutdown() {
        // Cleanup code
    }
}
```

### ✅ Logging with LunarLogger
Structured logging following LC-LOG specification:
```java
LunarLogger logger = LunarLogger.getLogger("MyComponent");
logger.info("Server started on port 8080");
logger.debug(() -> "Expensive: " + computeData()); // Lazy evaluation
logger.error("Connection failed", exception);
```

**Features:**
- ISO 8601 timestamps
- Automatic sensitive data redaction (passwords, API keys, tokens)
- Component-based naming (`LunarCore.*`)
- Lazy evaluation for performance
- Multiple log levels (ERROR, WARN, INFO, DEBUG, TRACE)

### ✅ Configuration System
Environment-aware configuration with precedence:
```java
Config.ConfigBuilder builder = new Config.ConfigBuilder("my-mod");
builder.setDefault("timeout", 30000);
builder.fromEnvironment(); // Loads LUNARCORE_MY_MOD_* variables
Config config = builder.build(); // Immutable

int timeout = config.getInt("timeout");
boolean debug = config.getBoolean("debug.enabled", false);
```

**Configuration Precedence:**
1. Programmatic (`.set()`) - Highest
2. Environment Variables
3. Configuration File
4. Defaults (`.setDefault()`) - Lowest

### ✅ Metadata Helpers
Type-safe access to Quilt mod metadata:
```java
String modId = MetadataHelper.getModId(container);
Version version = MetadataHelper.getModVersion(container);
Optional<String> homepage = MetadataHelper.getHomepage(container);

if (MetadataHelper.isModLoaded("quilted_fabric_api")) {
    // Do something
}
```

## Getting Started

### Installation

**From GitHub Packages (Recommended):**

Add to your `pom.xml`:
```xml
<repositories>
  <repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Lunarbit-dev/lunarcore-maven</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>dev.lunarbit.lunarcore</groupId>
    <artifactId>lunarcore-quilt</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```

Authentication in `~/.m2/settings.xml`:
```xml
<servers>
  <server>
    <id>github</id>
    <username>YOUR_GITHUB_USERNAME</username>
    <password>YOUR_GITHUB_TOKEN</password>
  </server>
</servers>
```

**For Gradle projects:**
```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/Lunarbit-dev/lunarcore-maven")
        credentials {
            username = project.findProperty("gpr.user") ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.token") ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    modImplementation "dev.lunarbit.lunarcore:lunarcore-quilt:1.0.0-SNAPSHOT"
}
```

### 2. Update Your Mod Class

Change from:
```java
public class MyMod implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        // ...
    }
}
```

To:
```java
public class MyMod extends ModLifecycleAdapter {
    private static final LunarLogger LOGGER = LunarLogger.getLogger("MyMod");
    
    @Override
    public void initialize(ModContainer container) {
        LOGGER.info("Initializing mod");
        
        // Use configuration
        Config.ConfigBuilder builder = new Config.ConfigBuilder("my-mod");
        builder.setDefault("feature.enabled", true);
        builder.fromEnvironment();
        Config config = builder.build();
        
        if (config.getBoolean("feature.enabled", true)) {
            LOGGER.info("Feature enabled");
        }
    }
}
```

### 3. Run Your Mod
```bash
./gradlew runClient
```

## 📚 Documentation

- **[QUILT_NOTES.md](QUILT_NOTES.md)** - Detailed Quilt-specific implementation notes
- **[LunarCore Spec](https://github.com/LunarBit-dev/LunarCore-spec)** - Full specification
- **Examples** - See `src/main/java/dev/lunarbit/lunarcore/LunarCoreQuilt.java`

## 🎯 Quilt-Specific Features

## Quilt-Specific Notes

This implementation is built specifically for Quilt and uses:
- `ModContainer` for richer metadata access
- SLF4J (Quilt's standard logging)
- Quilt's improved mod isolation
- Extended metadata fields from Quilt
- QSL and Quilted Fabric API

See [QUILT_NOTES.md](QUILT_NOTES.md) for detailed differences from the Fabric version.

## Requirements

- Minecraft 1.20+
- Quilt Loader 0.19.1+
- Quilted Fabric API 7.0.2+
- Java 21 (required for MC 1.20.5+)

## Building

```bash
# Build the mod
./gradlew build

# Run client
./gradlew runClient

# Run server
./gradlew runServer
```

## Examples

## Examples

### Basic Mod with Configuration
```java
public class MyMod extends ModLifecycleAdapter {
    private static final LunarLogger LOGGER = LunarLogger.getLogger("MyMod");
    private Config config;
    
    @Override
    public void initialize(ModContainer container) {
        Config.ConfigBuilder builder = new Config.ConfigBuilder(
            MetadataHelper.getModId(container)
        );
        builder.setDefault("max.connections", 10);
        builder.setDefault("timeout.seconds", 30);
        builder.fromEnvironment();
        config = builder.build();
        
        LOGGER.info("Max connections: " + config.getInt("max.connections"));
        LOGGER.info("Timeout: " + config.getInt("timeout.seconds") + "s");
    }
}
```

### Conditional Features Based on Other Mods
```java
@Override
public void initialize(ModContainer container) {
    LunarLogger logger = LunarLogger.getLogger("Features");
    
    if (MetadataHelper.isModLoaded("create")) {
        logger.info("Create mod detected, enabling integration");
        enableCreateIntegration();
    }
    
    if (MetadataHelper.isModLoaded("sodium")) {
        logger.info("Sodium detected, using optimized renderer");
        useOptimizedRenderer();
    }
}
```

### Lazy Logging
```java
LunarLogger logger = LunarLogger.getLogger("Debug");

// Only evaluated if debug level is enabled
logger.debug(() -> {
    String data = serializeComplexObject(largeObject);
    return "Object state: " + data;
});

// Automatic sensitive data redaction
logger.debug("API Key: sk-1234567890abcdef");
// Output: "API Key: sk-12...[REDACTED]"
```

## Contributing

Contributions welcome. When contributing:
1. Follow Quilt patterns, don't port from Fabric
2. Adhere to the LunarCore specification
3. Update QUILT_NOTES.md with implementation details
4. Add tests for new features

## License

MIT License - see LICENSE file

## Links

- Specification: https://github.com/LunarBit-dev/LunarCore-spec
- Fabric Version: https://github.com/LunarBit-dev/LunarCore-fabric
- Issues: https://github.com/LunarBit-dev/LunarCore-quilt/issues

## Notes

- No binary compatibility with Fabric (this is intentional)
- Follows the same specification as Fabric version
- Uses Quilt APIs and patterns throughout
- Maintained separately from LunarCore Fabric

