# SuperServer

A crude little automation helper for home purposes. Uses AWT Robot to click, type, etc on a host machine, interpreting commands received over TCP ServerSocket and using a custom HTTP-like protocol

By default SuperServer binds to port **44556**

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
| *mm:500,600*      | Mouse Move to position x=500, y=600. (0,0) is in the top left corner of the screen. |
| *click*           | left-click (main mouse button press and release)                                    |
| *rclick*          | right-click (secondary button press and release)                                    |

### Keyboard control

| example command | description |
|-----------------|-------------|
| *app*:1 | sends the Windows+1 keys in order to switch to the application pinned to position number 1 |
| *close* | sends the Alt+F4 keys in order to close active window|
| *ctrl*:s | sends the Ctrl+s command. Note, any other single letter key can be pressed in combination with Ctrl using this command |
| *desktop* | sends the Windows+D keys in order to activate (bring to foreground) the desktop |
| *enter* | Press the Return key |
| *escape* | the Esc button |
| *space* | space bar |
| *bspace* | backspace |
| *type*:something | This can be used to send a series of characters to be typed. Must not include spaces (send separate space command for that).  Only a-zA-Z characters recognized |

### Miscellaneous

| example command | description |
|-----------------|-------------|
| *vol+* | will attempt to increase the sound volume by sending the Windows+. (dot) keys |
| *vol-* | will attempt to reduce the sound volume by sending the Windows+, (comma) keys |
| *wait*:5000 | will cause SuperServer to wait for 5000ms before executing the next command in the chain |

## Usage

Let's say you have a number of computers on your home network. One or more of these may be running SuperServer. For example computer A is running an instance of SuperServer, compute B is on the same network as computer A, and can contact A directy (is not blocked by a firewall, etc.). If you know the IP address, (it will be something like 192.168.0.3) then you can send commands to that computer's SuperServer from any browser by navigating to a URL like this:

http://192.168.0.3:44556/app:4;wait:2000;mm:600,500;click;type:hello;space;type:world

the above example will tell the SuperServer on .3 computer to switch to app pinned to number 4, wait a couple seconds for app to load, mouve the mouse to somewhere in the middle of the screen, then click and start typing 'hello' space 'world'.

This means that you can command your SuperServer-enabled computer from any device, mobile, tablet, pc, as long as all are tuned into the same WIFI network. Just fire up a browser and start commanding!

### Syntax

`ip.add.res.s:port/command[:arg1[,arg2]][;command[:arg]]*`

as you may have noticed, anything after the first forward slash is interpreted by SuperServer in a sequence. Individual commands are separated by a semicolon, commands are separated from their arguments by colons, individual arguments (as in the case of mm:100,200) are separated by a comma (note, not spaces around it to be URL-friendly).

## Notes

### Sound volume control
Sound volume adjustments are not normally possible via the AWT Robot, so special steps need to be taken to make these two commands work as expected. For Windows, there is an AutoHotkey.exe binary included (download and execute at your own risk). You can grab AutoHotkey from the official site, if you feel like it. The script volume_control.ahk needs to be run on system startup in order to map the Win+. / Win+, combinations to the multimedia +/- keys
If anyone knows of a better solution to control sound volume from a Java app, please do shout, but I struggled hard to come up with this workaround, and it's admittedly a major painpoint of the application.


