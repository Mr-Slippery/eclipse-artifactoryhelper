package ro.aquacola.artifactory.helper.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ro.aquacola.artifactory.helper.ui.messages"; //$NON-NLS-1$
	public static String ArtifactoryPreferencePage_Id;
	public static String ArtifactoryPreferencePage_Location;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
