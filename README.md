# SuperServer

A crude little automation helper for home purposes. Uses AWT Robot to click, type, etc on a host machine, interpreting commands received over TCP ServerSocket and using a custom HTTP-like protocol

By default runs on port **44556**

Talk to the SuperServer from any Web browser, just hit the appropriate *IP:port/command*


## Command line arguments

- *kill* - application will briefly start with the only intention to send the *kill* command to any running instance on localhost
- *restart* - sends a *kill* command to any running SuperServer instance prior to proceeding with start-up. This is to avoid double-binding the ServerSocket to the same TCP/IP port

## Commands

### House-keeping

- *kill* - any SuperServer instance will kill itself upon receiving this command
- *hello* - SuperServer responds to *hello* commands by sending an HTTP 200 response containing (main) screen WIDTH and HEIGHT in the response body

### Mouse control

| **example command** | **description**                                                                   |
|-------------------|-------------------------------------------------------------------------------------|
| *mm:500,600      *| Mouse Move to position x=500, y=600. (0,0) is in the top left corner of the screen. |
| *click           *| left-click (main mouse button press and release)                                    |
| *rclick          *| right-click (secondary button press and release)                                    |

### Keyboard control


### Miscellaneous


