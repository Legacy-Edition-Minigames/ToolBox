# Guide-for-Emmie: How to publish a LEB-net.lem.ToolBox update
This guide will walk you through the steps required to successfully publish to all LEB users a LEB-net.lem.ToolBox update.


## What is a LEB-net.lem.ToolBox update?
A LEB-net.lem.ToolBox update consist of 4 files: The Source Code (.py), the Windows build (.exe), and the Linux and MacOS (binary)


### Things to know:
LEB-net.lem.ToolBox update number scheme consists of a FLOAT type number. That is, a number with decimals.
Updates __must__ be named as follows:

A.BBB


_A: main revision_

_B: sub revision_

Unlike other name schemes, you cannot, for example, use the scheme A.B.C, as 1.2.3 isn't a decimal number (you can't just have two points in a decimal number bruh).


## GitHub Structure Scheme
LEB-net.lem.ToolBox.py  -----  Required to fetch new versions

LEB-net.lem.ToolBox-vA.B.exe  -----  Windows Builds

LEB-net.lem.ToolBox-vA.B  -----  Linux Builds

LEB-net.lem.ToolBox-vA.B-MacOS  -----  MacOS Builds


## Hypothetical Environment
Current version: 1.2  ||  Target version: 1.3

## Step 1: Build your __new__ binaries
To start, first you need to compile the binaries for your new version.

The values that need to be changed are:

__cnt_program = 1.2__ ----> __cnt_program = 1.3__

__ver_info = "OLD CHANGELOG"__ ----> __ver_info = "NEW CHANGELOG"__

Once it has been patched, build the files.

The names of the files __must__ be:

__LEB-net.lem.ToolBox-vA.B__ _+ OS specific extension_

_A and B are the version number_


## Step 2: Upload files
Now, you must upload all the binaries and the source code (.py) file.
__Be warned that the moment you upload the source code (.py) file, the version will be published instantly!__
If for whatever reason, you don't want to publish the update, change the cnt_program value.

Note that, starting from 1.3, you can skip binaries for some operating systems. A message will popup to the user telling them there isn't a build available at the time.``


# You finished!
You can now test if your update is live by updating an old executable file.
