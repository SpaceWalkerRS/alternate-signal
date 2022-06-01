# Alternate Current Plus

Alternate Current Plus is built upon [Alternate Current](https://github.com/SpaceWalkerRS/alternate-current/blob/main/README.md), using its optimized algorithm to allow for complex but fast interactions between different wire types.

## Information For Mod Developers

If you wish to take advantage of Alternate Current's optimizations for your own custom wire types, you can add Alternate Current Plus as a dependency to your mod.

- Download the latest release of Alternate Current Plus and place it in `/vendor/alternate-current-plus/` in your project folder.

- In `build.gradle` add the following to the `repositories { }` section:

```
repositories {
	...
	flatDir {
		dirs "./vendor/alternate-current-plus/"
	}
}
```

- In `build.gradle` add the following to the `dependencies { }` section:

```
dependencies {
	...
	modImplementation ":alternate-current-plus:${project.alternate_current_plus_version}"
}
```

- Define the version of Alternate Current Plus you wish to use in `gradle.properties`, for example:

```
alternate_current_plus_version=mc1.19-1.3.0
```

- If you wish to create your own custom wire types, you must define an entrypoint in your `fabric.mod.json`:

```
	"entrypoints": {
		...
		"alternate-current-plus": [
			"com.example.ExampleInitializer"
		]
	}
```

- Create an initializer to register your custom wire types and wire connection behaviors:

```java
package com.example;

import alternate.current.plus.WireInitializer;

public class ExampleInitializer implements WireInitializer {

	@Override
	public void initializeWireTypes(WireTypeRegistry registry) {
		// register your custom wire types here
	}

	@Override
	public void initializeWireConnectionBehaviors(WireConnectionBehaviorRegistry registry) {
		// register your custom wire connection behaviors here
	}
}
```

- Custom wire blocks should implement the `WireBlock` interface (this interface defines methods that should be called when a wire is placed, removed, or updated):

```java
...

import alternate.current.plus.wire.WireBlock;

public class MyCoolWireBlock extends Block implements WireBlock {
	...
}
```
