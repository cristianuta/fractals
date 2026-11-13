# Fractals

A JPro application (JavaFX in the browser) with a menu that draws fractals:

- **Lindenmayer** – L-systems (Koch, dragon, Hilbert, Gosper, plants…) with editable axiom, rules and angle
- **Mandelbrot & Julia** – zoom/pan the Mandelbrot set, click a point to see its Julia set
- **Sierpinski** – cube (Menger sponge, 3D), carpet, pyramid (tetrahedron, 3D) and triangle

## Architecture

| Package | Role |
|---|---|
| `com.fractals.backend` | Pure Java computations, no JavaFX: L-system rewriting + turtle, escape-time rendering, Sierpinski geometry, a z-buffer software renderer for the 3D fractals |
| `com.fractals.frontend` | JavaFX screens (menu in `FractalsPage.fxml`, one view per fractal). JPro streams them to the browser |
| `com.fractals.FractalsMain` | Entry point (`JProApplication`) |

Heavy computations run on background threads and are coalesced, so the UI stays responsive.
A screen can be opened directly: `http://localhost:9091/index.html?fractal=mandelbrot`
(`lindenmayer`, `mandelbrot`, `cube`, `carpet`, `pyramid`, `triangle`).

Tests: `mvn test`

# How to start #

## Web Browser ##

### Start jpro in foreground (development mode) ###

```shell
mvn jpro:run
```

### Start jpro in background (server mode) ###

```shell
mvn jpro:restart
```

### Open jpro app in Web Browser ###

> http://localhost:9091/index.html

### Show all jpro apps in Browser ####

> http://localhost:9091/test/default

### Open jpro app in fullscreen ####

> http://localhost:9091/test/fullscreen/app-name

# Deployment:

### Step `1`. Prepare your server

To run jpro on linux, the server must be configured correctly.

Checkout the following chapters to configure your server correctly for jpro:

[DEPLOYING JPRO](https://www.jpro.one/?page=docs/current/2.6/DEPLOYING_JPRO)

[PREPARING LINUX FOR JPRO](https://www.jpro.one/?page=docs/current/2.7/PREPARING_LINUX_FOR_JPRO)

### Step `2`. Create the binary

Create a zip which contains the application with the following command:

```shell
mvn jpro:release
```

The path of the zip-file is the following: `build/distributions/HelloJPro-jpro.zip`

Now copy this file to your Server and unzip it.

### Step `3`. Run jpro

In the unzipped folder you can find a start-script: `bin/start.sh`

By running `./bin/start.sh` you start the JPro Server on your server.

The JPro Server is now ready to server your URLs entered in your browser.

```shell
./bin/start.sh
```



