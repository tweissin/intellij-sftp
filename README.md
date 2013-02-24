intellij-sftp
=============

This allows you to configure a single path on your system that will save to a 
remote SFTP location whenever you save a file locally.

Example properties file:

ssh.host=<your-host>
ssh.password=<your-password>
ssh.src.root=<the-local-root-path>
ssh.port=<the SSH port, usually 22>
ssh.username=<your-username>
ssh.dest.root=<the-destination-root-path>

This file is stored in your home directory as "intellij-sftp.properties".

Logging goes to your home directory as "intellij-sftp.log".  

Improvements to make:
=====================
* Logging in this release attempts to use log4j, but I haven't worked it out completely yet.  
  So for now, the log is located in your homedir as intellij-sftp.log

* The settings dialog isn't pretty and could be formatted better.

* What is logged needs to be cleaned up.

* It would be nice to be able to sync both directions, or sync the entire directory structure.

