package com.cyberon.engine;

public class CReaderVersionInfo {
	public String szSDKVersion;
	public String szSDKName;
	public String szReleaseTo;
	public String szReleaseDate;
	public boolean bTrialVersion;
	public boolean bLicense;

	public CReaderVersionInfo()
	{
		szSDKVersion = null;
		szSDKName = null;
		szReleaseTo = null;
		szReleaseDate = null;
		bTrialVersion = false;
		bLicense = false;
	}
}
