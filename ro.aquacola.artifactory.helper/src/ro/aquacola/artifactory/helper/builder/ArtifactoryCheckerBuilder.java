package ro.aquacola.artifactory.helper.builder;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import ro.aquacola.artifactory.helper.Activator;

public class ArtifactoryCheckerBuilder extends IncrementalProjectBuilder {

	class ArtifactoryDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkTarget(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkTarget(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class ArtifactoryTargetVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			checkTarget(resource);
			//return true to continue visiting children.
			return true;
		}
	}

	class TargetDefinitionHandler extends DefaultHandler {
		
		private IFile file;
		private Locator locator;
		
		public TargetDefinitionHandler(IFile file) {
			this.file = file;
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}
	
		@Override
	    public void startElement (String uri, String localName,
                String qName, Attributes attributes) 
        throws SAXException
		{	
    		checkRepository(uri, localName, qName, attributes);
		}

	    private void checkRepository(String uri, String localName,
				String qName, Attributes attributes) throws SAXException {
	    	if (isRepository(qName)) {
	    		String repositoryLocation = repositoryLocation(attributes);
	    		if (isArtifactoryLocation(repositoryLocation)) {
	    			checkArtifactoryId(repositoryLocation, attributes);
	    		}
	    	}
		}

	    private void checkArtifactoryId(String repositoryLocation, Attributes attributes) throws SAXException {
			String repositoryId = repositoryId(attributes);
			if (null == repositoryId || !artifactoryId.equals(repositoryId)) {
				String message = "Artifactory repository should have id=" + artifactoryId;
				SAXParseException saxParseException = new SAXParseException(message, repositoryLocation, "", 
						locator.getLineNumber(), locator.getColumnNumber());
				warning(saxParseException);
			}
		}

		private boolean isArtifactoryLocation(String location) {
	    	return location.contains(artifactoryLocation);
	    }
	    
	    private String repositoryLocation(Attributes attributes) {
	    	return attributes.getValue("location");
	    }
	    
		private String repositoryId(Attributes attributes) {
    		return attributes.getValue("id");
		}

		private boolean isRepository(String localName) {
			return "repository".equalsIgnoreCase(localName);
		}

		private void addMarker(SAXParseException e, int severity) {
			ArtifactoryCheckerBuilder.this.addMarker(file, e.getMessage(), e
					.getLineNumber(), severity);
		}

		public void error(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warning(SAXParseException exception) throws SAXException {
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}
	}

	public static final String BUILDER_ID = "ro.aquacola.artifactory.helper.artifactoryChecker";

	private static final String MARKER_TYPE = "ro.aquacola.artifactory.helper.artifactoryProblem";

	private SAXParserFactory parserFactory;

	private String artifactoryLocation;

	private String artifactoryId;

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		
		artifactoryLocation = Activator.getDefault().getArtifactoryLocation();
		artifactoryId = Activator.getDefault().getArtifactoryId();

		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		// delete markers set and files created
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	void checkTarget(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".target")) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			TargetDefinitionHandler reporter = new TargetDefinitionHandler(file);
			try {
				SAXParser parser = getParser();
				parser.parse(file.getContents(), reporter);
			} catch (Exception e1) {
				System.out.println(e1.getMessage());
			}
		}
	}
	
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}
	
	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
			getProject().accept(new ArtifactoryTargetVisitor());
		} catch (CoreException e) {
		}
	}
	
	private SAXParser getParser() throws ParserConfigurationException,
			SAXException {
		if (parserFactory == null) {
			parserFactory = SAXParserFactory.newInstance();
		}
		SAXParser saxParser = parserFactory.newSAXParser();
		return saxParser;
	}

	
	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		// the visitor does the work.
		delta.accept(new ArtifactoryDeltaVisitor());
	}
}
