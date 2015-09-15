package ro.aquacola.artifactory.helper.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ro.aquacola.artifactory.helper.Activator;

public class ArtifactoryPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	public ArtifactoryPreferencePage() {
		super(FieldEditorPreferencePage.GRID);

		// Set the preference store for the preference page.
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		setPreferenceStore(store);
	}

	@Override
	public String getTitle() {
		return "Artifactory Helper"; //$NON-NLS-1$
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		StringFieldEditor artifactoryLocation = new StringFieldEditor(
				Messages.ArtifactoryPreferencePage_Location, "Artifactory location: ", //$NON-NLS-2$
				getFieldEditorParent());
		addField(artifactoryLocation);
		StringFieldEditor artifactoryId = new StringFieldEditor(
				Messages.ArtifactoryPreferencePage_Id, "Artifactory repository id: ", //$NON-NLS-2$
				getFieldEditorParent());
		addField(artifactoryId);
	}

}
