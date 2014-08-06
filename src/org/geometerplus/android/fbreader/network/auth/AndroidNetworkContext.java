/*
 * Copyright (C) 2010-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader.network.auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import android.content.Context;

import org.geometerplus.zlibrary.core.network.*;

public abstract class AndroidNetworkContext extends ZLNetworkContext {
	@Override
	public Map<String,String> authenticate(URI uri, String realm, Map<String,String> params) {
		if (!"https".equalsIgnoreCase(uri.getScheme())) {
			return Collections.singletonMap("error", "Connection is not secure");
		}
		return authenticateWeb(uri, realm, params);
	}

	protected abstract Context getContext();
	protected abstract Map<String,String> authenticateWeb(URI uri, String realm, Map<String,String> params);

	protected Map<String,String> errorMap(String message) {
		return Collections.singletonMap("error", message);
	}

	protected Map<String,String> errorMap(Throwable exception) {
		final String message = exception.getMessage();
		return errorMap(message != null ? message : exception.getClass().getName());
	}

	protected Map<String,String> verify(final String verificationUrl) {
		final Map<String,String> result = new HashMap<String,String>();
		performQuietly(new JsonRequest(verificationUrl) {
			public void processResponse(Object response) {
				result.putAll((Map)response);
			}
		});
		return result;
	}

	protected String url(URI base, Map<String,String> params, String key) {
		return url(base, params.get(key));
	}

	protected String url(URI base, String path) {
		if (path == null) {
			return null;
		}
		try {
			final URI relative = new URI(path);
			return relative.isAbsolute() ? null : base.resolve(relative).toString();
		} catch (URISyntaxException e) {
			return null;
		}
	}
}
