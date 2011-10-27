package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;

public class PathUtility {
	public static URI appendPathToUri(URI uri, String path)
			throws URISyntaxException {
		return appendPathToUri(uri, path, "/");
	}

    public static URI addToQuery(URI uri, String s)
            throws URISyntaxException, StorageException
        {
            return addToQuery(uri, parseQueryString(s));
        }

    protected static String getBlobNameFromURI(URI uri, boolean flag)
            throws URISyntaxException
        {
            return Utility.safeRelativize(new URI(getContainerURI(uri, flag).toString().concat("/")), uri);
        }

    public static URI getContainerURI(URI uri, boolean flag)
            throws URISyntaxException
        {
            String s = getContainerNameFromUri(uri, flag);
            URI uri1 = appendPathToUri(new URI(getServiceClientBaseAddress(uri, flag)), s);
            return uri1;
        }

    public static URI addToQuery(URI uri, HashMap hashmap)
            throws URISyntaxException, StorageException
        {
            UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
            for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext();)
            {
                java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
                String as[] = (String[])entry.getValue();
                int i = as.length;
                int j = 0;
                while(j < i) 
                {
                    String s = as[j];
                    uriquerybuilder.add((String)entry.getKey(), s);
                    j++;
                }
            }

            return uriquerybuilder.addToURI(uri);
        }

    public static URI stripURIQueryAndFragment(URI uri) throws StorageException {
		try {
			return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(),
					null, null);
		} catch (URISyntaxException urisyntaxexception) {
			throw Utility
					.generateNewUnexpectedStorageException(urisyntaxexception);
		}
	}

	public static URI appendPathToUri(URI baseUri, String path,
			String pathSeparator) throws URISyntaxException {
		byte authorityStartIndex = -1;
		if (path.length() > 8) {
			String prefixOf8 = path.substring(0, 8).toLowerCase();
			if ("https://".equals(prefixOf8)) {
				authorityStartIndex = 8;
			} else if ("http://".equals(prefixOf8.substring(0, 7))) {
				authorityStartIndex = 7;
			}
		}
		if (authorityStartIndex > 0) {
			int pathSeparatorIndex = path.substring(authorityStartIndex)
					.indexOf(pathSeparator);
			String authority = path.substring(authorityStartIndex,
					authorityStartIndex + pathSeparatorIndex);
			URI pathAsUri = new URI(path);
			if (baseUri.getAuthority().equals(authority)) {
				return pathAsUri;
			} else {
				return new URI(baseUri.getScheme(), baseUri.getAuthority(),
						pathAsUri.getPath(), pathAsUri.getRawQuery(),
						pathAsUri.getRawFragment());
			}
		}
		if (baseUri.getPath().length() == 0 && path.startsWith(pathSeparator)) {
			return new URI(baseUri.getScheme(), baseUri.getAuthority(), path,
					baseUri.getRawQuery(), baseUri.getRawFragment());
		}
		StringBuilder stringbuilder = new StringBuilder(baseUri.getPath());
		if (baseUri.getPath().endsWith(pathSeparator)) {
			stringbuilder.append(path);
		} else {
			stringbuilder.append(pathSeparator);
			stringbuilder.append(path);
		}
		return new URI(baseUri.getScheme(), baseUri.getAuthority(),
				stringbuilder.toString(), baseUri.getQuery(),
				baseUri.getFragment());
	}

	public static String getServiceClientBaseAddress(URI uri, boolean flag)
			throws URISyntaxException {
		if (flag) {
			String as[] = uri.getRawPath().split("/");
			if (as.length < 2) {
				String s = String
						.format("Missing account name information inside path style uri. Path style uris should be of the form http://<IPAddressPlusPort>/<accountName>",
								new Object[0]);
				throw new IllegalArgumentException(s);
			} else {
				StringBuilder stringbuilder = new StringBuilder(
						(new URI(uri.getScheme(), uri.getAuthority(), null,
								null, null)).toString());
				stringbuilder.append("/");
				stringbuilder.append(Utility.trimEnd(as[1], '/'));
				return stringbuilder.toString();
			}
		} else {
			return (new URI(uri.getScheme(), uri.getAuthority(), null, null,
					null)).toString();
		}
	}

	public static String getContainerNameFromUri(URI uri, boolean flag)
			throws IllegalArgumentException {
		Utility.assertNotNull("resourceAddress", uri);
		String as[] = uri.getRawPath().split("/");
		byte byte0 = ((byte) (flag ? 3 : 2));
		if (as.length < byte0) {
			String s = String.format(
					"Invalid blob address '%s', missing container information",
					new Object[] { uri });
			throw new IllegalArgumentException(s);
		} else {
			String s1 = flag ? as[2] : as[1];
			return Utility.trimEnd(s1, '/');
		}
	}

	public static HashMap<String, String[]> parseQueryString(String s) throws StorageException {
		HashMap<String, String[]> hashmap = new HashMap<String, String[]>();
		if (Utility.isNullOrEmpty(s))
			return hashmap;
		int i = s.indexOf("?");
		if (i >= 0 && s.length() > 0)
			s = s.substring(i + 1);
		String as[] = s.contains("&") ? s.split("&") : s.split(";");
		for (int j = 0; j < as.length; j++) {
			int k = as[j].indexOf("=");
			if (k < 0 || k == as[j].length() - 1)
				continue;
			String s1 = as[j].substring(0, k);
			String s2 = as[j].substring(k + 1);
			s1 = Utility.safeDecode(s1);
			s2 = Utility.safeDecode(s2);
			String as1[] = (String[]) hashmap.get(s1);
			if (as1 == null) {
				as1 = (new String[] { s2 });
				if (!s2.equals(""))
					hashmap.put(s1, as1);
				continue;
			}
			if (s2.equals(""))
				continue;
			String as2[] = new String[as1.length + 1];
			for (int l = 0; l < as1.length; l++)
				as2[l] = as1[l];

			as2[as2.length] = s2;
		}

		return hashmap;
	}
}
