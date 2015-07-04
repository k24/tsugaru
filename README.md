W.I.P.

# Tsugaru

As Tsugaru Strait - that is small but international -, Tsugaru offers a thin wrapper for an application and libraries.

## Purpose

- For Application Developer
	- Can determine which library to use for a wrapper
- For Library Developer
	- Can use convenient libraries without hesitation

## Lane

Tsugaru can mediate lanes below:

- JSON
- Store
- EventBus
- Network
- Promise
- Logger


## Basic

### Application Usage

Application must configure Tsugaru before using it. There are two ways to configure, via Configurator or Mediation.

#### Configurator

Configuring via Configurator can set simply specific lanes needed.

```java
Tsugaru.Configuration.configurator()
	.json(new JsonLane() {
            @Override
            public <T> T decode(String string, Class<T> clazz) {
                return JSON.decode(string, clazz);
            }

            @Override
            public String encode(Object object) {
                return JSON.encode(object);
            }
        })
	.apply();
```

#### Mediation

Configuring via Mediation is for setting whole lanes.

See tests for codes.

### Library Usage

```java
public class Sample {
	public String string;
	public int integer;
}

// In some method
	Sample sample = Tsugaru.json().decode("{ string: \"s\", integer: 1 }", Class<Sample>);
	// sample.string == "s", sample.integer == 1
```

## Advanced

To be determined.