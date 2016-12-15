package com.podonomy.podonomyplayer.service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * The OKHttpClient does not like having too many clients created so this class allows for the
 * creation of one client statically.
 */
public class HttpClient {
  private static OkHttpClient httpClient = null;

  public static OkHttpClient getClient(){
    if (httpClient == null) {
      httpClient = new OkHttpClient().newBuilder()
              .readTimeout(50, TimeUnit.SECONDS)
              .build();
    }
    return httpClient;
  }
}
