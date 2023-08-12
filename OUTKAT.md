# The `OUTKAT` Script Interpreter

The `OUTKAT` script interpreter is a Java class designed to execute scripted commands stored in a file. It reads through a script file line by line, interprets the commands, and performs various actions based on those commands. Let's delve into the details of how this interpreter works and the commands it supports.

## Overview

The `OUTKAT` class is responsible for processing a script file and executing commands specified in the file. It provides functionalities for tasks such as printing output, downloading files from URLs, moving files, extracting ZIP archives, and more.

## Key Components

### Fields and Variables

- `currentLine`: Keeps track of the current line being processed in the script.
- `shouldStop`: A boolean flag that determines whether the script execution should be stopped.

### `executeScript` Method

This method is the heart of the interpreter. It reads the script file, extracts metadata from the first few lines (description, version, and interpreter version), and then iterates through the remaining lines to execute commands. It uses the `executeCommand` method to perform actions based on each command.

### `executeCommand` Method

This method takes a command and its arguments as input and determines the appropriate action to execute. Supported commands include:

- `OUT`: Prints output to the console.
- `DWN`: Downloads a file from a URL.
- `MOV`: Moves a file from one location to another.
- `GTO`: Jumps to a specified line number in the script.
- `EXT`: Extracts files from a ZIP archive.
- `STP`: Stops script execution.
- `WIT`: Waits for a specified amount of time.
- `KAT`: A comment line that does nothing.
- `RUN`: **(Experimental)** Executes a command in the embedded command line.

> **Caution:** The `RUN` command is experimental and may not be suitable for all environments. It's advised not to use it in critical or cross-compatible scenarios.

### `extractZipFile` Method

This method extracts files from a given ZIP archive and saves them to a specified destination directory. It uses the `ZipInputStream` class to efficiently handle ZIP archives.

### `downloadFile` Method

This method downloads a file from a provided URL and saves it to a specified local destination. It utilizes `URL` and file streams for the download process.

### `moveFile` Method

The `moveFile` method moves a file from a source location to a destination location. It employs file renaming to perform the move operation.

### `append` Method

This utility method appends a new item to an existing array and returns the updated array.

## Scripting Example

An example of a script that the `OUTKAT` interpreter can execute:

```
Sample Script
1.0
COMOK100

OUT Hello, world!
DWN http://example.com/sample.txt local.txt
MOV source.txt destination.txt
GTO 5
EXT archive.zip extracted_files/
STP
WIT 3000
KAT This is a comment.
RUN echo "Hello from embedded command line!"
```

In this example, the script starts by printing "Hello, world!" to the console, then proceeds to download a file from a URL, move a file, jump to line 5, extract files from a ZIP archive, stop execution, wait for 3 seconds, ignore a comment line, and finally, experimentally runs a command in the embedded command line.

> **Note:** Use the `RUN` command with caution and only in controlled environments due to its experimental nature.

## Conclusion

The `OUTKAT` script interpreter is a versatile tool for automating tasks and executing predefined actions through simple script files. By supporting a range of commands, it provides users with the ability to perform various file-related operations and control the flow of the script execution.