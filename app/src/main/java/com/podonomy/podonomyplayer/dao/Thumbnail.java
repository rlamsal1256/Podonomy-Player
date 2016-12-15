package com.podonomy.podonomyplayer.dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 *
 */
public class Thumbnail extends RealmObject{
  private byte[] bitmapContent = null;
  @Ignore
  private Ex ex;

  public byte[] getBitmapContent() {
    return bitmapContent;
  }
  public void setBitmapContent(byte[] bitmapContent) {
    this.bitmapContent = bitmapContent;
  }

  /* ====================
        Extension Methods
     ==================== */
  public class Ex {
    Bitmap bitmap = null;

    /**
     * Returns a @{Bitmap} object for this thumbnail.  Returns null if the bitmap is not decodable.
     */
    public Bitmap getBitmap(){
      if (bitmap == null && bitmapContent != null){
        bitmap = BitmapFactory.decodeByteArray(bitmapContent, 0, bitmapContent.length);
      }
      return bitmap;
    }
  }
}
