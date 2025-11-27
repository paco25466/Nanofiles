# NanoFiles

NanoFiles — Lightweight P2P file-sharing and directory service written in Java (educational project).

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Requirements](#requirements)
- [Build & Run (recommended: Eclipse/IDE)](#build--run-recommended-eclipseide)
- [Examples](#examples)


## Overview

NanoFiles is a small, educational Java project that implements a peer-to-peer (P2P) file-sharing system together with a directory service. It was developed as a teaching exercise in network programming and demonstrates core concepts such as TCP/UDP communication, message formats, peer coordination, and simple file metadata management.

## Features
- P2P file sharing between peers
- UDP-based directory server for peer discovery
- TCP connections for file transfer and P2P messaging
- Simple file database and digest utilities
- Command-line shell to interact with the node

## Project Structure
Source code lives under 'src/es/um/redes/nanoFiles'. Main packages and important classes:

- 'es.um.redes.nanoFiles.application'
  - 'NanoFiles.java' — Application entry point / startup wrapper
  - 'Directory.java' — Small helper for directory operations

- 'es.um.redes.nanoFiles.logic'
  - 'NFController.java', 'NFControllerLogicDir.java', 'NFControllerLogicP2P.java' — Core control logic for directory and P2P behaviors

- 'es.um.redes.nanoFiles.shell'
  - 'NFShell.java', 'NFCommands.java' — Interactive shell and command handling

- 'es.um.redes.nanoFiles.udp.server'
  - 'NFDirectoryServer.java' — UDP directory server implementation

- 'es.um.redes.nanoFiles.tcp.server' and '...client'
  - 'NFServer.java', 'NFServerThread.java', `NFConnector.java' — TCP server and client components for peer communication

- 'es.um.redes.nanoFiles.util'
  - 'FileDatabase.java', 'FileDigest.java', 'FileInfo.java' — File metadata and digest utilities

The repository also contains precompiled 'bin/' folders produced by Eclipse; source code is in 'src/'.

## Requirements
- Java JDK 8 or newer
- (Recommended) Eclipse IDE or another Java IDE for easy import and execution

## Build & Run (recommended: Eclipse/IDE)
1. Import the 'NanoFiles' project into Eclipse: 'File -> Import -> Existing Projects into Workspace' and select the project root.
2. Ensure 'src' is configured as the source folder and 'bin' as the output folder (Eclipse typically configures this automatically).
3. Run the main classes using 'Run -> Run As -> Java Application':
   - Run the directory server: 'es.um.redes.nanoFiles.udp.server.NFDirectoryServer'
   - Run a peer node (interactive shell): 'es.um.redes.nanoFiles.application.NanoFiles'



## Examples
- Start directory server in one terminal.
- Start two or more peers in separate terminals (each runs 'NanoFiles' main class).
- Use the interactive shell commands provided by 'NFShell' to share files, list peers, and request file transfers.

(See 'src/es/um/redes/nanoFiles/shell/NFCommands.java' for available commands and usage.)


