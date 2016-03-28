package com.sd.utils.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class FileTools {

	/*
	 * File I/O methods
	 */
	
	/**
	 * Saves a byte array to a specified file and directory on the SD card.
	 * @param dir The directory to save to
	 * @param fn The filename
	 * @param data A byte array of bytes to write
	 */
	public static final void savesBytesToFile(final Context c, final String dir, String fn, final byte[] data) {
		savesBytesToFile(c, dir, fn, data, false);
	}
	
	/**
	 * Saves a byte array to a specified file and directory on the SD card.
	 * @param dir The directory to save to
	 * @param fn The filename
	 * @param data A byte array of bytes to write
	 * @param compress If true then the output file is gzip compressed
	 */	
	public static final void savesBytesToFile(final Context c, final String dir, String fn, final byte[] data, boolean compress) {
		try {
			if (data == null) return;
			
			String d = dir.trim();
			String f = fn.trim();
		
			if (f.startsWith("/")) {
				f = f.substring(1);
			}
			
			if (d.startsWith("/")) d = d.substring(1);
			if (d.endsWith("/")) d = d.substring(0, d.length()-2);			
						
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	final File sdCard = Environment.getExternalStorageDirectory();
		    	final File extDir = new File(sdCard.getAbsolutePath() + "/" + d);
		    	if (!extDir.exists()) extDir.mkdirs();
		    	
		    	FileOutputStream fos = null;
		    	GZIPOutputStream gzos = null;
		    	
				try {	
					fos = new FileOutputStream(new File(extDir, f) );					
					if (fos != null) {
						
						if (compress) {
							gzos = new GZIPOutputStream(fos);
							gzos.write(data);						
							gzos.flush();
							
						} else {
							fos.write(data);
							fos.flush();
						}						
					}
					
				} catch (IOException e) {
					e.printStackTrace();
					
				} finally {
					try {
						if (gzos != null) gzos.close();
					} catch (Exception e) {}
					
					try {
						if (fos != null) fos.close();
					} catch (Exception e) {}
									
				}								
		    }			
			
		} catch (Exception e) {
			MetricellTools.logException("FileTools.saveBytesToFile", e);			
		}
		
		registerFile(c, dir, fn);
	}
		
	/**
	 * Registers a file with the media scanner so that it appears on the SD card.
	 * @param c Application context
	 * @param dir	File directory (no leading or trailing /)
	 * @param fn 	Filename
	 */
	public static final void registerFile(Context c, String dir, String fn) {
		try {
			String d = dir.trim();
			String f = fn.trim();
		
			if (f.startsWith("/")) {
				f = f.substring(1);
			}
			
			if (d.startsWith("/")) d = d.substring(1);
			if (d.endsWith("/")) d = d.substring(0, d.length()-2);			
						
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		    	final File sdCard = Environment.getExternalStorageDirectory();
		    	final File extDir = new File(sdCard.getAbsolutePath() + "/" + d);			
			
		    	File file = new File(extDir, f);
			
		    	c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
			}
			
		} catch (Exception e) {
			MetricellTools.logException("FileTools.registerFile", e);
		}
	}
	
	/**
	 * Deletes a private file.
	 * @param c Application context
	 * @param fn The filename to delete
	 * @return True if the file was deleted successfully, false otherwise
	 */
	public static final boolean deletePrivateFile(final Context c, String fn) {
		try {
			return c.deleteFile(fn);
		} catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * Checks to see if a specified file exists in the application private storage.
	 * @param c Application context
	 * @param fn The filename
	 * @return True if the file exists, false otherwise
	 */
	public static final boolean privateFileExists(final Context c, String fn) {
		try {
			File f = new File(c.getFilesDir(), fn);
			return f.exists();

		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Returns the file size of a specified file in the application private storage.
	 * @param c Application context
	 * @param fn The filename
	 * @return The file size of the specified file or -1 if the file cannot be opened.
	 */
	public static final long privateFileSize(final Context c, String fn) {
		try {		
			File f = new File(c.getFilesDir(), fn);
			if (f.exists()) {
				return f.length();
			}
			
			return -1;
			
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * Saves a byte array to the specified filename in the application's private storage area. This method employs file locking so other threads 
	 * shouldn't be able to use the file simultaneously.
	 * @param c Application context
	 * @param fn Filename
	 * @param data Data array
	 * @param deleteIfEmpty If true then if data is null or empty then any existing file will be deleted
	 * @throws FileNotFoundException If the file cannot be created
	 * @throws IOException If an IO error occurs
	 */
	public static final void saveBytesToPrivateFile(final Context c, final String fn, final byte[] data, final boolean deleteIfEmpty)
			throws FileNotFoundException, IOException {
		
		saveBytesToPrivateFile(c, fn, data, deleteIfEmpty, false);		
	}
	
	
	/**
	 * Saves a byte array to the specified filename in the application's private storage area. This method employs file locking so other threads 
	 * shouldn't be able to use the file simultaneously.
	 * @param c Application context
	 * @param fn Filename
	 * @param data Data array
	 * @param deleteIfEmpty If true then if data is null or empty then any existing file will be deleted
	 * @param compress If true then the output file is gzip compressed
	 * @throws FileNotFoundException If the file cannot be created
	 * @throws IOException If an IO error occurs
	 */	
	public static final void saveBytesToPrivateFile(final Context c, String fn, final byte[] data, final boolean deleteIfEmpty, boolean compress)
		throws FileNotFoundException, IOException {

		FileOutputStream fos = null;
		GZIPOutputStream gzos = null;
		
		UUID uuid = UUID.randomUUID();
		String tmpFilename = uuid.toString();
		
		try {
			// Delete the file if required
			if (data == null || data.length == 0) {
				if (deleteIfEmpty) {
					c.deleteFile(fn);
				}
				return;
			}
		
			fos = c.openFileOutput(tmpFilename, Context.MODE_PRIVATE);
						
			if (fos != null) {
				if (compress) {
					gzos = new GZIPOutputStream(fos);
					gzos.write(data);
					gzos.flush();
				} else {
/*					
					int len = data.length;
					int bytesWritten = 0;
					while (bytesWritten < len) {
						
						int bytesToWrite = len - bytesWritten;
						if (bytesToWrite > 1024) {
							bytesToWrite = 1024;
						}
						
						fos.write(data, bytesWritten, bytesToWrite);
						fos.flush();
						
						bytesWritten += bytesToWrite;
					}
*/

					fos.write(data);
					fos.flush();

				}				
			}		
					
		} catch (FileNotFoundException fnfe) {
			throw(fnfe);
			
		} catch (IOException ioe) {
			
			// Delete the temporary file
			try {
				File f = new File(c.getFilesDir(), tmpFilename);
				if (f.exists()) {
					c.deleteFile(tmpFilename);
				}
			} catch (Exception e) {}
			
			throw(ioe);
			
		} finally {
			try {
				if (gzos != null) fos.close();
			} catch (IOException e) {
			}			
			
			try {
				if (fos != null) fos.close();
			} catch (IOException e) {
			}
		}
		
		// Written file, now rename
		try {
			File f = new File(c.getFilesDir(), tmpFilename);
			if (f.exists() && f.length() > 0) {
				c.deleteFile(fn);
				f.renameTo(new File(c.getFilesDir(), fn));				
			}
			
		} catch (Exception e) {}
		
	}	
	
	/**
	 * Loads an array of bytes from a specified private file. This method employs file locking so other threads 
	 * shouldn't be able to use the file simultaneously. If 
	 * @param c Application context
	 * @param fn Filename
	 * @return A byte array containing the bytes read
	 * @throws FileNotFoundException If the file could not be found
	 * @throws IOException If an IO error occurred while loading the data.
	 */
	public static final byte[] loadBytesFromPrivateFile(final Context c, final String fn) 
		throws FileNotFoundException, IOException {

		return loadBytesFromPrivateFile(c, fn, false);
	}
	
	/**
	 * Loads an array of bytes from a specified private file. This method employs file locking so other threads 
	 * shouldn't be able to use the file simultaneously. If 
	 * @param c Application context
	 * @param fn Filename
	 * @param isCompressed If true then the data is treated as gzip compressed
	 * @return A byte array containing the bytes read
	 * @throws FileNotFoundException If the file could not be found
	 * @throws IOException If an IO error occurred while loading the data.
	 */	
	public static final byte[] loadBytesFromPrivateFile(final Context c, String fn, boolean isCompressed)
		throws FileNotFoundException, IOException {
		
		FileInputStream fis = null;
		GZIPInputStream gzis = null;
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
				
		try {
			fis = c.openFileInput(fn);
			
			if (fis != null) {

				final byte[] buffer = new byte[1024];
				int len = -1;

				if (isCompressed) {
					gzis = new GZIPInputStream(fis);
					while ((len = gzis.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
					
				} else {				
					while ((len = fis.read(buffer)) != -1) {
						os.write(buffer, 0, len);
					}
				}
			}						
			
		} catch (FileNotFoundException fnfe) {
			throw(fnfe);
			
		} catch (IOException ioe) {
			throw(ioe);
			
		} finally {
			try {
				if (gzis != null) gzis.close();
			} catch (Exception e) {}			
			
			try {
				if (fis != null) fis.close();
			} catch (Exception e) {}
		}
		
		return os.toByteArray();
	}

	/**
	 * Writes an Object to a private file on the filesystem.
	 * @param c Application context
	 * @param fn Filename
	 * @param obj Object to save
	 * @param deleteIfNull If true then any existing file will be deleted if obj is null.
	 * @throws FileNotFoundException If the file cannot be opened for writing.
	 * @throws IOException If an IO exception occurs
	 */
	public static void saveObjectToPrivateFile(final Context c, String fn, Object obj, boolean deleteIfNull) 
			throws FileNotFoundException, IOException {
		
		saveObjectToPrivateFile(c, fn, obj, deleteIfNull, false);
	}
	
	/**
	 * Writes an Object to a private file on the filesystem.
	 * @param c Application context
	 * @param fn Filename
	 * @param obj Object to save
	 * @param deleteIfNull If true then any existing file will be deleted if obj is null.
	 * @param compress If true then the data is saved gzip compressed
	 * @throws FileNotFoundException If the file cannot be opened for writing.
	 * @throws IOException If an IO exception occurs
	 */	
	public static void saveObjectToPrivateFile(final Context c, String fn, Object obj, boolean deleteIfNull, boolean compress) 
		throws FileNotFoundException, IOException {

		FileOutputStream fos = null;
		GZIPOutputStream gzos = null;
		ObjectOutputStream objos = null;
		
		UUID uuid = UUID.randomUUID();
		String tmpFilename = uuid.toString();		
		
		try {
			
			// Delete the file if required
			if (obj == null) {
				if (deleteIfNull) {
					c.deleteFile(fn);
				}
				return;
			}

			fos = c.openFileOutput(tmpFilename, Context.MODE_PRIVATE);
						
			if (fos != null) {
				if (compress) {
					gzos = new GZIPOutputStream(fos);
					
					objos = new ObjectOutputStream(gzos);
					objos.writeObject(obj);
					
				} else {
					objos = new ObjectOutputStream(fos);
					objos.writeObject(obj);
				}				
			}		
			
		} catch (FileNotFoundException fnfe) {
			throw(fnfe);
			
		} catch (IOException ioe) {
			
			// Delete the temporary file
			try {
				File f = new File(c.getFilesDir(), tmpFilename);
				if (f.exists()) {
					c.deleteFile(tmpFilename);
				}
			} catch (Exception e) {}			
			
			throw(ioe);
			
		} finally {
			try {
				if (objos != null) {
					objos.reset();
					objos.close();
				}
			} catch (IOException e) {
			}			
			
			try {
				if (gzos != null) gzos.close();
			} catch (IOException e) {
			}			
			
			try {
				if (fos != null) fos.close();
			} catch (IOException e) {
			}
		}
		  
		// Written file, now rename
		try {
			File f = new File(c.getFilesDir(), tmpFilename);
			if (f.exists() && f.length() > 0) {
				c.deleteFile(fn);
				f.renameTo(new File(c.getFilesDir(), fn));				
			}
			
		} catch (Exception e) {}		
		
	}
	
	/**
	 * Loads an object previously saved with saveObjectToPrivateFile from a specified file.
	 * @param c Application context
	 * @param fn The file to load
	 * @return The object loaded from the file, or null if no object available.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static final Object loadObjectFromPrivateFile(final Context c, final String fn)
		throws FileNotFoundException, IOException, ClassNotFoundException {
	
		return loadObjectFromPrivateFile(c, fn, false);		
	}	
	
	/**
	 * Loads an object previously saved with saveObjectToPrivateFile from a specified file.
	 * @param c Application context
	 * @param fn The file to load
	 * @param isCompressed If true then the data is treated as being gzip compressed
	 * @return The object loaded from the file, or null if no object available.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */	
	public static final Object loadObjectFromPrivateFile(final Context c, String fn, boolean isCompressed)
		throws FileNotFoundException, IOException, ClassNotFoundException {
			
		FileInputStream fis = null;
		GZIPInputStream gzis = null;
		ObjectInputStream objis = null;
		
		Object obj = null;
		
		try {
			fis = c.openFileInput(fn);
			
			if (fis != null) {
				if (isCompressed) {
					gzis = new GZIPInputStream(fis);
					objis = new ObjectInputStream(gzis);
					obj = objis.readObject();			
					
				} else {
					objis = new ObjectInputStream(fis);
					obj = objis.readObject();					
				}
			}						
			
		} catch (FileNotFoundException fnfe) {
			throw(fnfe);
			
		} catch (IOException ioe) {
			throw(ioe);
			
		} catch (ClassNotFoundException cnfe) {
			throw(cnfe);
			
		} finally {
			
			try {
				if (objis != null) {
					objis.reset();
					objis.close();
				}
			} catch (Exception e) {}
			
			try {
				if (gzis != null) gzis.close();
			} catch (Exception e) {}				
			
			try {
				if (fis != null) fis.close();
			} catch (Exception e) {}

		}
		
		return obj;
	}

	/**
	 * Saves a MetricellStorable object to a private file on the filesystem.
	 * @param c Application context
	 * @param fn Filename
	 * @param storable The MetricellStorable object to save
	 * @param deleteIfNull If true then any existing file will be deleted if obj is null.
	 * @throws FileNotFoundException If the file cannot be opened for writing.
	 * @throws IOException If an IO exception occurs
	 */
	public static final void saveStorableToPrivateFile(final Context c, String fn, MetricellStorable storable, boolean deleteIfNull) 
			throws FileNotFoundException, IOException {
		
		if (storable != null) {
			byte[] data = storable.toByteArray();
			saveBytesToPrivateFile(c, fn, data, deleteIfNull);
		}
	}	

}

