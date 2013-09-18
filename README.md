# Hammernet

Here's a quick example:

    // 'this' is an instance of the current android.content.Context.
    final String ipAddress = AndroidNetInfo.wifiIp4Address(this);

If you want to try and get the wifi address first, then default to another interface, say when
testing on an *Android* emulator, if the wifi IP address is not found:

    // 'this' is an instance of the current android.content.Context.
    final String defaultInterfaceName = BuildConstants.DEV_BUILD ? "eth0" : null;
    final String ipAddress
            = AndroidNetInfo.wifiOrDefaultIp4Address(this, defaultInterfaceName);

`wifiOrDefaultIp4Address()` is particularly handy for running your application on an *Android*
emulator.


## How to Use Hammernet

    git clone https://github.com/digitalrounin/hammernet.git
    cd hammernet
    mvn clean install

If you get *PMD*, *Checkstyle*, *Findbugs*, or other related errors run the following instead:

    mvn -P skip-analysis clean install

At this point, *Maven* should have installed *Hammernet* in your local repository, `~/.m2/`.

### Using with Maven

Assuming that you are using *Maven*, add the following to your pom.xml:

    <dependency>
        <groupId>in.droun</groupId>
        <artifactId>hammernet</artifactId>
        <version>0.1-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>

### Using with Other Build Tools

If you are using something other than *Maven*, copy the JAR out of `./target/` and put it where it
should go.  There are other libraries that *Hammernet* depends on, that you will need to hunt down
and copy as well though.

***TODO***: Add dependency list.


## The Idea Behind Hammernet

The original intention behind *Hammernet* is to modularize *Android* network related calls.  To help
simplify things like getting a device's (through the AndroidNetInfo class):

- Wifi IPv4 address.
- Wifi MAC address.
- Wifi interface name.

Also in *Hammernet* (through the NetworkInterfaceInfo class):

- Converting text string MAC addresses to BigIntegers.
- Getting an IPv4 address by interface name.
- Getting an interface name by MAC address,

*Hammernet* tries to hide a lot of the little details behind these tasks.  It also tries to catch
common errors like *NullPointerException*, services not available, empty strings and so on.  Aside
from the occasional *SocketException*, if anything bad happens behind the scenes, *Hammernet* will
just return a null.  This behaviour might change over time though.

*Hammernet* also tries to ease a lot of the unit testing pains around integrating with
*java.net.NetworkInterface*.  *NetworkInterface* is a *final* class with a whole lot of *static*
methods.  This makes it particularly difficult to unit test calls utilize *NetworkInterface*.


## Want to Contribute?

Just send over a pull request!  Or e-mail to discuss?


## Open for Comments and Suggestions

If you have any questions, comments, suggestions, pertaining to *Hammernet*, please feel free to
write.

Thank you!
