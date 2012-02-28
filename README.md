## Meme Generator for Jenkins CI and Hudson
### Post a Meme on the project page, whenever you like

Are your continuous integration processes lacking a certain dimension? Specifically the dimension of purile humour? Fear not. Help is on its way in the form of Memes.

This plugin for Jenkins/Hudson allows you to create a meme (using the [memegenerator.net](http://memegenerator.net) API) when the build fails, returns back to normal, or all the time - you choose. You can also configure which images are used, and supply the text that goes on them. A random meme is chosen from your collection each time. 

### Installation 

Download [memegen.hpi](/downloads/joonty/memegen/memegen.hpi) from the downloads section. You can then either copy it to `/var/lib/hudson/plugins/` on
your Jenkins/Hudson server or upload it using the advanced tab of the plugin manager. Then give Jenkins a restart to activate the plugin.

### Configuration

Click on "Manage Jenkins" link on the home page. Scroll down to "Global Meme Settings", where you will see various options.

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
