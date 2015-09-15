package ro.aquacola.artifactory.helper;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ro.aquacola.artifactory.helper.ui.Messages;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ro.aquacola.artifactory.helper"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private String artifactoryLocation;

	private String artifactoryId;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public String getArtifactoryLocation() {
		return artifactoryLocation;
	}
	
	public String getArtifactoryId() {
		return artifactoryId;
	}
	
	private void initPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(Messages.ArtifactoryPreferencePage_Location, "artifactory");
		store.setDefault(Messages.ArtifactoryPreferencePage_Id, "artifactory");
		artifactoryLocation = store.getString(Messages.ArtifactoryPreferencePage_Location);
		artifactoryId = store.getString(Messages.ArtifactoryPreferencePage_Id);				
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		initPreferences();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
