package com.psy.util;

import java.lang.ref.WeakReference;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

public class BitmapUtil {
	 /**
	   * @description ����ͼƬ��ѹ������
	   *
	   * @param options ����
	   * @param reqWidth Ŀ��Ŀ��
	   * @param reqHeight Ŀ��ĸ߶�
	   * @return
	   */
	  private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // ԴͼƬ�ĸ߶ȺͿ��
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    if (height > reqHeight || width > reqWidth) {
	      // �����ʵ�ʿ�ߺ�Ŀ���ߵı���
	      final int halfHeight = height / 2;
	      final int halfWidth = width / 2;
	      while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
	        inSampleSize *= 2;
	      }
	    }
	    return inSampleSize;
	  }
	  /**
	   * @description ͨ�����bitmap������ѹ�����õ���ϱ�׼��bitmap
	   *
	   * @param src
	   * @param dstWidth
	   * @param dstHeight
	   * @return
	   */
	  private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight, int inSampleSize,Context ctx) {
	    //���inSampleSize��2�ı���Ҳ��˵���src�Ѿ���������Ҫ������ͼ�ˣ�ֱ�ӷ��ؼ��ɡ�
	    if (inSampleSize % 2 == 0) {
	      return src;
	    }
	    // ����ǷŴ�ͼƬ��filter�����Ƿ�ƽ�����������СͼƬ��filter��Ӱ�죬������������СͼƬ������ֱ������Ϊfalse
	    Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
	    if (src != dst) { // ���û�����ţ���ô������
	      src.recycle(); // �ͷ�Bitmap��native��������
	    }
	    return dst;
	  }
	  
	  /**
	   * 
	   */
	  private static Uri pathToUri(String picPath,Context ctx){
		  Uri mUri = Uri.parse("content://media/external/images/media");
		  Uri mImageUri = null;
		  ContentResolver cr = ctx.getContentResolver();
		  Cursor cursor = cr.query(
		                                  Images.Media.EXTERNAL_CONTENT_URI, null, null, null,
		                                  Images.Media.DEFAULT_SORT_ORDER);
		  cursor.moveToFirst();

		  while (!cursor.isAfterLast()) {
		                          String data = cursor.getString(cursor
		                                          .getColumnIndex(MediaStore.MediaColumns.DATA));
		                          if (picPath.equals(data)) {
		                                  int ringtoneID = cursor.getInt(cursor
		                                                  .getColumnIndex(MediaStore.MediaColumns._ID));
		                                  mImageUri = Uri.withAppendedPath(mUri, "" + ringtoneID);
		                                  break;
		                          }
		                          cursor.moveToNext();
		  }
		  return mImageUri;
	  }
	  
	  /**
	   * ��ת��Ƭ
	   * @param mImageCaptureUri
	   * @return
	   */
		private static  Bitmap imgRotate(Uri mImageCaptureUri,Bitmap src, int dstWidth, int dstHeight, int inSampleSize,Context ctx) {

			// ���������ջ���ѡ��ͼƬÿ��ͼƬ����������д洢Ҳ�洢�ж�Ӧ��ת�Ƕ�orientationֵ
			// ����������ȡ��ͼƬ�ǰѽǶ�ֵȡ���Ա�����ȷ����ʾͼƬ,û����תʱ��Ч��ۿ�
		
			ContentResolver cr = ctx.getContentResolver();
			Cursor cursor = cr.query(mImageCaptureUri, null, null, null, null);// ���Uri����ݿ�����
			if (cursor != null) {
				cursor.moveToFirst();// ���α��ƶ�����λ����Ϊ�����Uri�ǰ�ID��������Ψһ�Ĳ���Ҫѭ����ָ���һ��������
				String filePath = cursor.getString(cursor.getColumnIndex("_data"));// ��ȡͼƬ·
				String orientation = cursor.getString(cursor
						.getColumnIndex("orientation"));// ��ȡ��ת�ĽǶ�
				cursor.close();
				if (filePath != null) {
					Bitmap bitmap = createScaleBitmap(src, dstWidth, dstHeight, inSampleSize, ctx);//���Path��ȡ��ԴͼƬ
					int angle = 0;
					if (orientation != null && !"".equals(orientation)) {
						angle = Integer.parseInt(orientation);
					}
					if (angle != 0) {
						// ����ķ�����Ҫ�����ǰ�ͼƬתһ���Ƕȣ�Ҳ���ԷŴ���С��
						Matrix m = new Matrix();
						int width = bitmap.getWidth();
						int height = bitmap.getHeight();
						m.setRotate(angle); // ��תangle��
						bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
								m, true);// �������ͼƬ
						
					}
					return bitmap;
				}
			}
			return null;
		}
	  
//	  /**
//	   * @description ��Resources�м���ͼƬ
//	   *
//	   * @param res
//	   * @param resId
//	   * @param reqWidth
//	   * @param reqHeight
//	   * @return
//	   */
//	  public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
//	    final BitmapFactory.Options options = new BitmapFactory.Options();
//	    options.inJustDecodeBounds = true; // ���ó���true,��ռ���ڴ棬ֻ��ȡbitmap���
//	    BitmapFactory.decodeResource(res, resId, options); // ��ȡͼƬ����
//	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // �������涨��ķ�������inSampleSizeֵ
//	    // ʹ�û�ȡ����inSampleSizeֵ�ٴν���ͼƬ
//	    options.inJustDecodeBounds = false;
//	    Bitmap src = BitmapFactory.decodeResource(res, resId, options); // ����һ���Դ������ͼ
//	    return createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize); // ��һ���õ�Ŀ���С������ͼ
//	  }
	  /**
	   * @description ��SD���ϼ���ͼƬ
	   *
	   * @param pathName
	   * @param reqWidth
	   * @param reqHeight
	   * @return
	   */
	  public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight,Context ctx) {
		  if (pathName.substring(pathName.lastIndexOf(".") + 1).equals("jpeg")
					|| pathName.substring(pathName.lastIndexOf(".") + 1).equals("jpg")) {
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(pathName, options);
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    Bitmap src = BitmapFactory.decodeFile(pathName, options);
	    return imgRotate( pathToUri(pathName, ctx),src, reqWidth, reqHeight, options.inSampleSize, ctx);
	    		//createScaleBitmap(src, reqWidth, reqHeight, options.inSampleSize,ctx);
		  }
		  else {
		return null;
		}
	  }
	  
//	  /**
//		 * pathתbmp
//		 * @param path
//		 * @param w
//		 * @param h
//		 * @return
//		 */
//		public static Bitmap convertToBitmap(String path, int w, int h) {
//			if (path.substring(path.lastIndexOf(".")+1).equals("jpeg") ||
//				path.substring(path.lastIndexOf(".")+1).equals("jpg") ) { //ֻҪjpegͼƬ
//			BitmapFactory.Options opts = new BitmapFactory.Options();
//			// ����Ϊtureֻ��ȡͼƬ��С
//			opts.inJustDecodeBounds = true;
//			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
//			// ����Ϊ��
//			BitmapFactory.decodeFile(path, opts);
//			int width = opts.outWidth;
//			int height = opts.outHeight;
//			float scaleWidth = 0.f, scaleHeight = 0.f;
//			if (width > w || height > h) {
//				// ����
//				scaleWidth = ((float) width) / w;
//				scaleHeight = ((float) height) / h;
//			}
//			opts.inJustDecodeBounds = false;
//			float scale = Math.max(scaleWidth, scaleHeight);
//			opts.inSampleSize = (int) scale;
//			WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
//					BitmapFactory.decodeFile(path, opts));
//			return Bitmap.createScaledBitmap(weak.get(), w, h, true);
//		}
//			return null;
//			}

}
