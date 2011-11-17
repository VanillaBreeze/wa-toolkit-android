package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.HttpStatus;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.StorageException;

import android.test.AndroidTestCase;

public class CloudTableClientTests extends AndroidTestCase {

	public void testWhenQueryTablesShouldReturnTableList() throws Exception {
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();

		client.createTableIfNotExist("TableClientTestsList1");
		client.createTableIfNotExist("TableClientTestsList2");
		client.createTableIfNotExist("TableClientTestsList3");

		boolean table1 = false, table2 = false, table3 = false;
		Iterator<String> tables = client.listTables().iterator();
		while (tables.hasNext()) {
			String table = tables.next();
			table1 = table1 || table.equalsIgnoreCase("TableClientTestsList1");
			table2 = table2 || table.equalsIgnoreCase("TableClientTestsList2");
			table3 = table3 || table.equalsIgnoreCase("TableClientTestsList3");
		}

		Assert.assertTrue(table1 && table2 && table3);
		
		client.deleteTable("TableClientTestsList1");
		client.deleteTable("TableClientTestsList2");
		client.deleteTable("TableClientTestsList3");
	}

	public void testWhenCreateTableShouldBeAdded() throws Exception {
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();

		String testTableName = "TableClientTestsAdd";
		
		client.deleteTableIfExist(testTableName);		
		Assert.assertFalse(client.doesTableExist(testTableName));
		
		client.createTable(testTableName);
		Assert.assertTrue(client.doesTableExist(testTableName));
		
		client.deleteTable(testTableName);
	}

	public void testWhenCreateDuplicatedTableShouldThrow() throws Exception {
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();

		String testTableName = "TableClientTestsDupAdd";
		client.deleteTableIfExist(testTableName);
		Assert.assertFalse(client.doesTableExist(testTableName));

		boolean errorThrown = false;
		client.createTable(testTableName);
		Assert.assertTrue(client.doesTableExist(testTableName));
		try {
			client.createTable(testTableName);
		} catch (StorageException e) {
			errorThrown = e.m_HttpStatusCode == HttpStatus.SC_CONFLICT;
		}
		Assert.assertTrue(errorThrown);
		
		client.deleteTable(testTableName);
	}

	public void testWhenDeleteTableShouldBeRemoved() throws Exception {
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();

		String testTableName = "TableClientTestsDelete";
		client.createTable(testTableName);

		Assert.assertTrue(client.doesTableExist(testTableName));

		client.deleteTable(testTableName);

		Assert.assertFalse(client.doesTableExist(testTableName));
	}

	public void testWhenDeleteNonExistentTableShouldThrow() throws Exception {
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();

		String testTableName = "TableClientTestsDeleteNotExist";
		Assert.assertFalse(client.doesTableExist(testTableName));

		boolean errorThrown = false;
		try {
			client.deleteTable(testTableName);
		} catch (StorageException e) {
			errorThrown = e.m_HttpStatusCode == HttpStatus.SC_NOT_FOUND;
		}

		Assert.assertTrue(errorThrown);
	}
	
	public void testWhenFindExistentShouldReturnTrue() throws URISyntaxException, Exception {
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		String testTableName = "TableClientTestsFindExist";
		client.createTableIfNotExist(testTableName);
		
		Assert.assertTrue(client.doesTableExist(testTableName));
		
		client.deleteTable(testTableName);
	}

	public void testWhenFindNotExistentShouldReturnFalse() throws URISyntaxException, Exception {
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		String testTableName = "TableClientTestsFindNotExist";
		
		Assert.assertFalse(client.doesTableExist(testTableName));
	}

}