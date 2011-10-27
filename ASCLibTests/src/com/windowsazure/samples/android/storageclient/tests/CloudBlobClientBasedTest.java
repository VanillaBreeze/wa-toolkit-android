package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;

public abstract class CloudBlobClientBasedTest<T extends WAZServiceAccountProvider> extends TestCaseWithManagedResources { 
	protected void setUp()
	{
		try {
			super.setUp();
			T accountProvider = SuperClassTypeParameterCreator.create(this, 0);
			cloudBlobClient = accountProvider.getCloudBlobClient();
			otherCloudBlobClient = accountProvider.getCloudBlobClientWithDifferentAccount();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	protected CloudBlobContainer createContainer(String containerName) throws StorageException, NotImplementedException, URISyntaxException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = new CloudBlobContainer(containerName, cloudBlobClient);
		container.create();
		this.addCleanUp(new ResourceCleaner()
		{
			public void clean() throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException
			{
				try
				{
					container.delete();
				} catch (Exception e) {
				}
			}
		});
		return container; 
	}

	protected ArrayList<String> getContainerNames(Iterable<CloudBlobContainer> containers) throws NotImplementedException 
	{
		ArrayList<String> names = new ArrayList<String>();
		for(CloudBlobContainer container : containers)
		{
			names.add(container.getName());
		}
		return names;
	}

	protected ArrayList<String> getBlobNames(Iterable<CloudBlob> blobs) throws NotImplementedException, URISyntaxException 
	{
		ArrayList<String> names = new ArrayList<String>();
		for(CloudBlob blob : blobs)
		{
			names.add(blob.getName());
		}
		return names;
	}

	protected CloudBlobClient cloudBlobClient;

	protected CloudBlobClient otherCloudBlobClient;

}
