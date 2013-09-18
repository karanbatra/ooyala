<<<<<<< HEAD
Ooyala AdobeCQ Connector
=========================

<dl>
	<dt>Compatibility</dt>
	<dd>Adobe CQ 5.5</dd>

	<dt>Usage</dt>
	<dd>A pre built package is available through github by clicking the download link above. Install the zip file into CQs package manager. Configuration instructions are available in documentation/Ooyala-AdobeCQ-Documentation.pdf</dd>
</dl>


Building from Source
====================

<dl>
	<dt>Requirements</dt>
	<dd>Maven, Adobe CQ5.5</dd>

	<dt>Build</dt>
	<dd>Clone the repository. Execute `mvn install` in the parent directory (Ooyala-AdobeCQ). A full content-package zip can be found under deploy/target.</dd>

	<dt>Deploy</dt>
	<dd>You can automatically deploy to the local CQ instance, using the built in deploy profile. (mvn clean install -Pdeploy,local-author) All CQ connection parameters are exposed clearly within the parent POM for customization.</dd>
</dl>


### Built by [Siteworx](http://www.siteworx.com) in NYC.
=======
ooyala
======
>>>>>>> e04aa1c57d9c2246e7f689ae2b7c47cc35143233
