package hudson.plugins.memegen;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Asgeir Storesund Nilsen
 *
 */
public class MemeNotifier extends Notifier {

	public final boolean enableFailure;
	public final boolean enableSucceed;
	public final boolean enableAlways;

	private static final Logger LOGGER = Logger.getLogger(MemeNotifier.class.getName());

	@DataBoundConstructor
	public MemeNotifier(boolean enableFailure, boolean enableSucceed, boolean enableAlways) {
		this.enableFailure = enableFailure;
		this.enableSucceed = enableSucceed;
		this.enableAlways = enableAlways;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see hudson.tasks.BuildStep#getRequiredMonitorService()
	 */
	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}

	private void generate(AbstractBuild build, BuildListener listener) {
		listener.getLogger().println("generate() called");
		final String buildId =  build.getProject().getDisplayName()+" " + build.getDisplayName();
		MemegeneratorAPI memegenAPI = new MemegeneratorAPI();
		boolean memeResult;
		try {
			Meme meme = MemeFactory.getMeme(build);
			memeResult = memegenAPI.instanceCreate(meme);

			if (memeResult) {
				listener.getLogger().println("Meme: "+meme.getImageURL());
				build.setDescription("<img class=\"meme\" src=\""+meme.getImageURL()+"\" />");
				AbstractProject proj = build.getProject();
				String desc = proj.getDescription();
				desc = desc.replaceAll("<img class=\"meme\"[^>]+>", "");
				desc += "<img class=\"meme\" src=\""+meme.getImageURL()+"\" />";
				proj.setDescription(desc);
			} else {
				listener.getLogger().println("Sorry, couldn't create a Meme - check the logs for more detail");
			}
		} catch (IOException ie) {
			LOGGER.log(Level.WARNING, "{0}{1}", new Object[]{"Meme generation failed: ", ie.getMessage()});
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * hudson.tasks.BuildStepCompatibilityLayer#perform(hudson.model.AbstractBuild
	 * , hudson.Launcher, hudson.model.BuildListener)
	 */
	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
		BuildListener listener) throws InterruptedException, IOException {
		listener.getLogger().println("perform() called");
		if (enableAlways) {
			listener.getLogger().println("Generating Meme...");
			generate(build,listener);
		} else if (enableSucceed && build.getResult() == Result.SUCCESS) {
			AbstractBuild prevBuild = build.getPreviousBuild();
			if (prevBuild.getResult() == Result.FAILURE) {
				listener.getLogger().println("Build has returned to successful, generating Meme...");
				generate(build,listener);
			}
		} else if (enableFailure && build.getResult() == Result.FAILURE) {
			listener.getLogger().println("Build failure, generating Meme...");
			generate(build,listener);
		}
		return true;
	}

	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		/*
		 * (non-Javadoc)
		 *
		 * @see
		 * hudson.tasks.BuildStepDescriptor#isApplicable(java.lang.Class)
		 */
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			return true;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see hudson.model.Descriptor#getDisplayName()
		 */
		@Override
		public String getDisplayName() {
			return "Meme Generator";
		}
	}
}
