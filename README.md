# eclipse-artifactoryhelper
Small build helper that checks target definition files for proper id for Artifactory repos

The role of this helper builder is to prevent errors due to Maven not resolving the artifacts in Artifactory repos.

Usually these are given a certain id, such as
&lt;repository id="artifactory" location="http://your.artifactory.com/repo"&gt;

The problem is, due to editing with different tools or mistakes, the id can be deleted, and then your artifacts might no longer be resolved by Maven.

This builder produces warnings regarding repos that seem to be in this situation.

It also has preferences for the location marker (to be able to infer it's an Artifactory repo) and the expected id of the repository.

That's it, good luck!
