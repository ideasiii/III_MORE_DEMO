
/**  _ ***** ** _
***  Project :: Cyberon Android Customize Demo ( Module: AssetsDataManager )
*C*  Author  :: C.C.LIN @ 2013 Cyberon Corp.
*2*   [ v3.12 ] GIT/CS/E/ d? s a- C+++ UL++ P++ L++
*0*   E---- W N o+ K? w++ O-- M V-- PS++ PE++ Y? PGP?
***   t? 5? X+ R+++ tv- b+ DI D++ G e++ h- r- y
**/

package com.cyberon.utility ;

import android.content.res.AssetManager ;

import java.io.File             ;
import java.io.FileOutputStream ;
import java.io.InputStream      ;
import java.io.OutputStream     ;
import java.io.IOException      ;

public class AssetsRepository
{
	private static final class MSG
	{
		private static final String LOG_TAG = "AssetsDataManager" ;
	}

	//
	// MKDIR FUNCTION NOT YET
	//

	//
	// * Notice : Assets have 1MB limit , use CopyAndMergeFile(...) if over-size .
	//
	public static final boolean CopyFileFromList( AssetManager assetManager , String strDestFolderPath , String[] strFileList )
	{
		File         file                 ;
		InputStream  assetsFileInput      ;
		OutputStream dataFolderFileOutput ;

		if( assetManager == null || strDestFolderPath == null || strFileList == null ) { return false ; }

		for( String strFile : strFileList )
		{
			String strOutputFile = strDestFolderPath + strFile ;
			//String strInputFile  = strFile                     ;
			try
			{
		/*
				if( CheckFile( assetManager , strOutputFile , strInputFile ) == true ) { continue ; }

				file = new File( strOutputFile ) ;
				if( file.exists() )
				{
					if( file.delete() == false )
					{
						continue ;
					}
				}
		*/

				file = new File( strOutputFile ) ;
				if( file.exists() )
					continue;
				assetsFileInput = assetManager.open( strFile ) ;               // Input  Stream
				dataFolderFileOutput = new FileOutputStream( strOutputFile ) ; // Output Stream

				byte[] buffer = new byte[1024] ;
				int nLength ;
				while( ( nLength = assetsFileInput.read( buffer ) ) > 0 )
				{
					dataFolderFileOutput.write( buffer , 0 , nLength ) ;
				}

				assetsFileInput.close()      ;
				dataFolderFileOutput.flush() ;
				dataFolderFileOutput.close() ;
			}
			catch( IOException e )
			{
				return false ;
			}
		}

		return true ;
	}

	//
	// To merge assets/strMergeList[] to strDestFolderPath/strFile
	//
	public static final boolean CopyAndMergeFile( AssetManager assetManager , String strDestFolderPath , String[] strMergeList , String strFile )
	{
		//File         file                 ;
		InputStream  assetsFileInput      ;
		OutputStream dataFolderFileOutput ;

		String strOutputFile = strDestFolderPath + strFile ;

		if( assetManager == null || strDestFolderPath == null || strFile == null || strMergeList == null ) { return false ; }

		try
		{
/*
			file = new File( strOutputFile ) ;
			if( file.exists() )
			{
				if( file.delete() == false )
				{
					return true ;
				}
			}
*/

			dataFolderFileOutput = new FileOutputStream( strOutputFile ) ;

			for( String strFilePackage : strMergeList )
			{
				assetsFileInput = assetManager.open( strFilePackage ) ;

				byte[] buffer = new byte[1024] ;
				int nLength ;
				while( ( nLength = assetsFileInput.read( buffer ) ) > 0 )
				{
					dataFolderFileOutput.write( buffer , 0 , nLength ) ;
				}

				assetsFileInput.close() ;
			}

			dataFolderFileOutput.flush() ;
			dataFolderFileOutput.close() ;
		}
		catch( IOException e )
		{
			return false ;
		}

		return true ;
	}

	//
	// Change CheckFile function here , or you can override this function .
	//
	protected static boolean CheckFile( AssetManager assetManager , String strExistFile , String strAssetFile )
	{
		return false ; // Delete all .
		//return CheckFile_Size( assetManager , strExistFile , strAssetFile ) ;
		//return CheckFile_MD5( assetManager , strExistFile , strAssetFile ) ;
		//return CheckFile_SHA1( assetManager , strExistFile , strAssetFile ) ;
	}

	//
	// * Notice : This function only check file size , some modify like CSpotter model ,
	//            if you only adjust threshold , the file size will not change ,
	//            so this function will return true .
	//
	protected static final boolean CheckFile_Size( AssetManager assetManager , String strExistFile , String strAssetFile )
	{
		long nSizeExist ;
		int  nSizeAsset ;

		try
		{
			File file = new File( strExistFile ) ;
			nSizeExist = ( file.exists() )? file.length() : 0 ;

			InputStream assetsFileInput = assetManager.open( strAssetFile ) ;
			nSizeAsset = assetsFileInput.available() ;
			assetsFileInput.close() ;
		}
		catch( IOException e )
		{
			return false ;
		}

		return ( nSizeExist == nSizeAsset )? true : false ;
	}
}