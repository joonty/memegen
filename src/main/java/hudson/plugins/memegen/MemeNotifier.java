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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;

import java.io.PrintStream;
import org.kohsuke.stapler.StaplerRequest;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Asgeir Storesund Nilsen
 *
 */
public class MemeNotifier extends Notifier {

	private static final Logger LOGGER = Logger.getLogger(MemeNotifier.class.getName());
	public boolean memeEnabledFailure;
	public boolean memeEnabledSuccess;
	public boolean memeEnabledAlways;

	@DataBoundConstructor
	public MemeNotifier(boolean memeEnabledFailure, boolean memeEnabledSuccess, boolean memeEnabledAlways) {
		this.memeEnabledAlways = memeEnabledAlways;
		this.memeEnabledSuccess = memeEnabledSuccess;
		this.memeEnabledFailure = memeEnabledFailure;
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

		PrintStream output = listener.getLogger();
		output.println("Generating Meme with account " + ((DescriptorImpl) getDescriptor()).getMemeUsername());
		final String buildId = build.getProject().getDisplayName() + " " + build.getDisplayName();
		MemegeneratorAPI memegenAPI = new MemegeneratorAPI(((DescriptorImpl) getDescriptor()).getMemeUsername(), ((DescriptorImpl) getDescriptor()).getMemePassword());
		boolean memeResult;
		try {
			Result res = build.getResult();
			Meme meme = MemeFactory.getMeme((res==Result.FAILURE)?((DescriptorImpl) getDescriptor()).getFailMemes():((DescriptorImpl) getDescriptor()).getSuccessMemes(),build);
			memeResult = memegenAPI.instanceCreate(meme);

			if (memeResult) {
				output.println("Meme: " + meme.getImageURL());
				if (((DescriptorImpl) getDescriptor()).isBuildDescriptionEnabled()) {
					addToBuildDescription(build,meme);
				}
				AbstractProject proj = build.getProject();
				addToProjectDescription(proj,meme);
			} else {
				output.println("Sorry, couldn't create a Meme - check the logs for more detail");
			}
		} catch (NoMemesException nme) {
			LOGGER.log(Level.WARNING, "{0}{1}", new Object[]{"Meme generation failed: ", nme.getMessage()});
			output.println("There are no memes to use! Please add some in the Jenkins configuration page.");
		} catch (IOException ie) {
			LOGGER.log(Level.WARNING, "{0}{1}", new Object[]{"Meme generation failed: ", ie.getMessage()});
			output.println("Sorry, couldn't create a Meme - check the logs for more detail");
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "{0}{1}", new Object[]{"Meme generation failed: ", e.getMessage()});
			output.println("Sorry, couldn't create a Meme - check the logs for more detail");
		}
	}

	private void addToBuildDescription(AbstractBuild build, Meme meme) throws IOException {
			build.setDescription("<img class=\"meme\" src=\"" + meme.getImageURL() + "\" />");
	}

	private void addToProjectDescription(AbstractProject proj, Meme meme) throws IOException {
			String desc = proj.getDescription();
			desc = desc.replaceAll("<img class=\"meme\"[^>]+>", "");
			desc += "<img class=\"meme\" src=\"" + meme.getImageURL() + "\" />";
			proj.setDescription(desc);
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

		if (memeEnabledAlways) {
			listener.getLogger().println("Generating Meme...");
			generate(build, listener);
		} else if (memeEnabledSuccess && build.getResult() == Result.SUCCESS) {
			AbstractBuild prevBuild = build.getPreviousBuild();
			if (prevBuild != null && prevBuild.getResult() == Result.FAILURE) {
				listener.getLogger().println("Build has returned to successful, generating Meme...");
				generate(build, listener);
			}
		} else if (memeEnabledFailure && build.getResult() == Result.FAILURE) {
			listener.getLogger().println("Build failure, generating Meme...");
			generate(build, listener);
		}
		return true;
	}

	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		public String memeUsername;
		public String memePassword;
		public boolean buildDescriptionEnabled;

		public ArrayList<Meme> smemes = new ArrayList<Meme>();
		public ArrayList<Meme> fmemes = new ArrayList<Meme>();

		public DescriptorImpl() {
			super(MemeNotifier.class);
			load();
		}

		public boolean isBuildDescriptionEnabled() {
			return buildDescriptionEnabled;
		}

		public String getMemeUsername() {
			return memeUsername;
		}

		public String getMemePassword() {
			return memePassword;
		}

		public ArrayList<Meme> getFailMemes() {
			if (fmemes.isEmpty()) {
				return getDefaultFailMemes();
			} else {
				return fmemes;
			}
		}

		public ArrayList<Meme> getSuccessMemes() {
			if (smemes.isEmpty()) {
				return getDefaultSuccessMemes();
			} else {
				return smemes;
			}
		}

		private ArrayList<Meme> getDefaultSuccessMemes() {
			String[][] list = getMemeList();
			smemes.add(new Meme(list[0][0],"But when I do, I win","I don't always commit"));
			return smemes;
		}

		private ArrayList<Meme> getDefaultFailMemes() {
			String[][] list = getMemeList();
			fmemes.add(new Meme(list[0][0],"But when I do, I break the build","I don't always commit"));
			return fmemes;
		}

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

		@Override
		public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
			memeUsername = req.getParameter("memeUsername");
			memePassword = req.getParameter("memePassword");
			String bdChecked = req.getParameter("buildDescriptionEnabled");
			System.err.println("Check box value: "+bdChecked);
			if (bdChecked != null && bdChecked.equals("on")) {
				System.err.println("Build description is enabled");
				buildDescriptionEnabled = true;
			} else {
				System.err.println("Build description is NOT enabled");
				buildDescriptionEnabled = false;
			}

			smemes.clear();
			for (Object data : getArray(json.get("smemes"))) {
				Meme m = req.bindJSON(Meme.class, (JSONObject) data);
				smemes.add(m);
			}
			fmemes.clear();
			for (Object data : getArray(json.get("fmemes"))) {
				Meme m = req.bindJSON(Meme.class, (JSONObject) data);
				fmemes.add(m);
			}
			save();
			return super.configure(req, json);
		}

		public static JSONArray getArray(Object data) {
			JSONArray result;
			if (data instanceof JSONArray) {
				result = (JSONArray) data;
			} else {
				result = new JSONArray();
				if (data != null) {
					result.add(data);
				}
			}
			return result;
		}

		public String[][] getMemeList() {
			return new String[][] {
				{"74-2485","The Most Interesting Man In The World"},
				{"2-166088","Y U No"},
				{"17-984","Philosoraptor"},
				{"305-84688","Futurama Fry"},
				{"121-1031","Success Kid"},
				{"116-142442","Forever Alone"},
				{"29-983","Socially Awkward Penguin"},
				{"534-699717","Good Guy Greg"},
				{"3-203","Foul Bachelor Frog"},
				{"45-20","Insanity Wolf"},
				{"54-42","Joseph Ducreux"},
				{"79-108785","Yo Dawg"},
				{"111-1436","High Expectations Asian Father"},
				{"112-288872","Paranoid Parrot"},
				{"308-332591","Business Cat"},
				{"225-32","Advice Dog"},
				{"113750-1591284","iHate"},
				{"96-89714","Bear Grylls"},
				{"629-963","Advice Yoda Gives"},
				{"5588-4944","Chuck Norris"},
				{"5115-1110726","Chemistry Cat"},
			};
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
