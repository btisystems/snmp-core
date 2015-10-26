# snmp-core
[![Build Status](https://travis-ci.org/btisystems/snmp-core.svg?branch=master)](https://travis-ci.org/btisystems/snmp-core)
[![Coverage Status](http://img.shields.io/coveralls/btisystems/snmp-core/master.svg)](https://coveralls.io/r/btisystems/snmp-core?branch=master)


A Library providing core SNMP functionality for interacting with SNMP4j and the output of [Mibbler](https://github.com/btisystems/mibbler).

### Requirements

* Java 7 onwards.
* Maven 3.0.0 or newer.

### Features

* com.btisystems.pronx.ems.core.exception
* com.btisystems.pronx.ems.core.model
* com.btisystems.pronx.ems.core.snmp
* com.btisystems.pronx.ems.core.snmp.trapreceiver
* com.btisystems.pronx.ems.core.snmp.trapsender 
* com.btisystems.pronx.ems.schemas.meta.notification


### Usage

Set up the snmp-core maven dependency in the build section of the project pom.xml:

```xml
    <dependency>
        <groupId>com.btisystems</groupId>
        <artifactId>snmp-core</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
```

### Changelog

See [changelog](CHANGELOG.md) for more details.

### License

The project snmp-core is licensed under the APACHE 2.0 license.