package com.xumak.aem.akamai.ccu

import groovy.transform.ToString

/**
 * PurgeStatus -
 *
 * @author Sebastien Bernard
 */
@ToString
public class PurgeResponse {
	public int httpStatus
	public String detail
	public long estimatedSeconds
	String purgeId
	public String progressUri
	long pingAfterSeconds
	String supportId

	public static PurgeResponse noResponse() {
		return new PurgeResponse(httpStatus: -1, detail: "Nothing has been sent because the query was not valid")
	}

	boolean isSuccess() {
		return httpStatus == 201
	}
}
