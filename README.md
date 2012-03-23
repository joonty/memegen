## Meme Generator for Jenkins CI and Hudson

**Note:** Please use the Jenkins fork of this repository at https://github.com/jenkinsci/memegen-plugin. For the latest stable package go to http://wiki.jenkins-ci.org/display/JENKINS/Meme+Generator+Plugin.

### Post a Meme on the project page, whenever you like

Are your continuous integration processes lacking a certain dimension? Specifically the dimension of purile humour? Fear not. Help is on its way in the form of Memes.

This plugin for Jenkins/Hudson creates a meme (using the [memegenerator.net] API) when the build fails, returns back to normal, or all the time - you choose. The meme is then posted on the project and build page. You can also configure which images are used, and supply the text that goes on them. A random meme is chosen from your collection each time. 

### Installation 

Download [memegen.hpi](/downloads/joonty/memegen/memegen.hpi) from the downloads section. You can then either copy it to `/var/lib/hudson/plugins/` on
your Jenkins/Hudson server or upload it using the advanced tab of the plugin manager. Then give Jenkins a restart to activate the plugin.

### Configuration

Go to the system configuration page (Manage Jenkins -> Configure System), and scroll down to "Global Meme Settings". A [memegenerator.net] username and password is required, but signing up is free. These are the only essential configuration options, and the rest are for configuring possible meme images - see below under the heading "Meme configuration".

Finally, the meme generator needs to be enabled for each project that you want them. Go to a project configuration page, scroll down to "Meme generator" and tick the box. You will then see three options, which will determine when memes are created:

- *Generate when a build fails*
- *Generate when a build succeeds and the previous failed*
- *Generate for every build (regardless of status)*

As long as there are configured memes and one of the three above options are selected, a meme will be generated and posted on the project and build description.

### Meme configuration

On the system configuration page you can manage the Memes that are generated after both successful and failed builds. This involves choosing the image and entering the text that will appear at the top and bottom of the image. You can also use template variables, which look like ${this}. The possible variables are:

- *${project}* - The project display name
- *${build}* - The build display name (this will be something like "#33")
- *${user}* - The user(s) string of the people who contributed between this build and the previous one
- *${day}* - The current day (e.g. "Monday")

This means that you can put in a string like "Oh no, you broke ${project}", and this will be filled in with the project name during generation.

### Building

If you want to build this package you'll need to have JDK 6 and maven2 installed, and the installation process depends on your OS and package manager. For example, Debian + aptitude users can do:

    sudo apt-get install default-jdk maven2

Clone the git repository and build the package

    git clone https://github.com/joonty/memegen.git 
    cd memegen
    mvn package

This will generate an hpi file at `target/memegen.hpi`. This needs to be copied to the Jenkins plugin directory. If a version of this plugin has already been installed, run

    rm -rf /var/lib/jenkins/plugins/meme*

to get rid of it. Then either use the advanced tab of the plugin manager to upload the hpi file or copy it to the plugins directory, e.g. 

    cp target/memegen.hpi /var/lib/jenkins/plugins/

Finally, restart jenkins.

### License

Copyright &copy; 2012, Jonathan Cairns. Licensed under the [MIT license].

[MIT License]: https://github.com/jenkinsci/jenkins/raw/master/LICENSE.txt
[memegenerator.net]: http://memegenerator.net
