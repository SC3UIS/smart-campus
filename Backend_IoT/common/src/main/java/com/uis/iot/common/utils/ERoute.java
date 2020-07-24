package com.uis.iot.common.utils;

public enum ERoute {
	ADMIN("admin-service.backend.svc:8090"), 
	DATA("data-service.backend.svc:8091"),
	JOBS("jobs-service.backend.svc:8092");
	
//	ADMIN("localhost:8090"), 
//	DATA("localhost:8091"),
//	JOBS("localgost:8092");

	private String url;

	private ERoute(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "http://" + this.url + "/";
	}
}
