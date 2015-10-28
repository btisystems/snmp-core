# Deploying to OSSRH 
Besides an Apache Maven installation, you have to have a GPG client installed and on your command line path 
as required by the Maven GPG plugin. 

From [here](http://blog.ghostinthemachines.com/2015/03/01/how-to-use-gpg-command-line/) assuming homebrew installed 
on OSX, run the following command. 

     brew install gnupg gpg-agent
     
To save you having to type the password out each time you build a jar use gnupg agent described [here](http://sudoers.org/2013/11/05/gpg-agent.html).

At this point (assuming the keys are placed in the gnupg directory and the ossrh credentials are in your setttings.xml) you can sign the jars, build the javadoc and source and deploy by running the following.

     mvn clean deploy -Prelease
     
This will upload the jars to https://oss.sonatype.org and sync with Maven Central.
