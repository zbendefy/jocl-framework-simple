package clframework.common;

import static org.jocl.CL.*;

import java.util.ArrayList;
import java.util.List;

import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

public class CLDevice {

	private int platformid, deviceid;
	private cl_platform_id clPlatform;
	private cl_device_id clDevice;
	private String extensions = "";

	@Override
	public String toString()
	{
		String type = "";
		long t = getDeviceLongProperty(CL_DEVICE_TYPE);
		if ((t & CL_DEVICE_TYPE_GPU) != 0) {
			type = "GPU";
		}
		if ((t & CL_DEVICE_TYPE_CPU) != 0) {
			type = "CPU";
		}
		if ((t & CL_DEVICE_TYPE_ACCELERATOR) != 0) {
			type = "ACC";
		}
		
		return "[" + platformid + ":" + deviceid + ", " + type + "] " + getDeviceName() ;
	}
	
	/**
	 * Returns all available OpenCL devices represented by an intance of a CLDevice
	 * @return
	 */
	public static List<CLDevice> GetAllAvailableDevices() {
		List<CLDevice> ret = new ArrayList<CLDevice>();
		try {
			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);

			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			int platformId = numPlatformsArray[0];

			for (int i = 0; i < platformId; i++) {
				int numDevices[] = new int[1];
				clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, 0, null,
						numDevices);
				cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
				clGetDeviceIDs(platforms[i], CL_DEVICE_TYPE_ALL, numDevices[0],
						devicesArray, null);

				for (int j = 0; j < devicesArray.length; j++) {
					ret.add(new CLDevice(i, j));
				}
			}

			return ret;
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * Returns all available platforms in the system
	 * @return
	 */
	public static String[] GetCLPlatformNames() {
		String[] ret = new String[0];
		try {
			int numPlatformsArray[] = new int[1];

			clGetPlatformIDs(0, null, numPlatformsArray);

			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			int deviceCount = numPlatformsArray[0];

			ret = new String[deviceCount];

			for (int i = 0; i < deviceCount; i++) {
				ret[i] = String.valueOf(i) + ": "
						+ getPlatformString(platforms[i], CL_PLATFORM_NAME).trim();
			}

		} catch (Throwable e) {
			ret = new String[0];
		}

		return ret;
	}

	/**
	 * Returns all devices within a platform
	 * @param p_id The platform
	 * @return
	 */
	public static String[] GetCLDeviceNames(int p_id) {
		String[] ret = new String[0];
		try {
			int numDevices[] = new int[1];

			int numPlatformsArray[] = new int[1];
			clGetPlatformIDs(0, null, numPlatformsArray);
			cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
			clGetPlatformIDs(platforms.length, platforms, null);

			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, 0, null,
					numDevices);

			cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
			clGetDeviceIDs(platforms[p_id], CL_DEVICE_TYPE_ALL, numDevices[0],
					devicesArray, null);

			int deviceCount = numDevices[0];

			ret = new String[deviceCount];

			for (int i = 0; i < deviceCount; i++) {
				ret[i] = getDeviceString(devicesArray[i], CL_DEVICE_NAME).trim();
			}

		} catch (Throwable e) {
			ret = new String[0];
		}

		return ret;
	}
	
	/**
	 * Creates a CLDevice.
	 * @param platformid
	 * @param deviceid
	 */
	public CLDevice(int platformid, int deviceid) {
		super();

		if (platformid < 0 || deviceid < 0)
			throw new IllegalArgumentException("platformid and deviceid must be greater than 0");
		
		int numDevices[] = new int[1];
		int numPlatformsArray[] = new int[1];
		clGetPlatformIDs(0, null, numPlatformsArray);
		cl_platform_id platforms[] = new cl_platform_id[numPlatformsArray[0]];
		clGetPlatformIDs(platforms.length, platforms, null);

		if (platformid >= platforms.length)
			throw new IllegalArgumentException("Invalid platform: " + platformid);
		
		clGetDeviceIDs(platforms[platformid], CL_DEVICE_TYPE_ALL, 0, null,
				numDevices);

		cl_device_id devicesArray[] = new cl_device_id[numDevices[0]];
		clGetDeviceIDs(platforms[platformid], CL_DEVICE_TYPE_ALL,
				numDevices[0], devicesArray, null);

		if (deviceid >= devicesArray.length)
			throw new IllegalArgumentException("Invalid device: " + deviceid);
		
		init(platformid, deviceid, platforms[platformid],
				devicesArray[deviceid]);
	}

	public CLDevice(int platformid, int deviceid, cl_platform_id clplatform,
			cl_device_id cldevice) {
		super();

		if (platformid < 0 || deviceid < 0)
			throw new IllegalArgumentException("platformid and deviceid must be greater than 0");
		
		if (clplatform == null || cldevice == null)
			throw new IllegalArgumentException("The input platform or device is null");
		
		init(platformid, deviceid, clplatform, cldevice);
	}

	private void init(int platformid, int deviceid, cl_platform_id clplatform,
			cl_device_id cldevice) {
		this.platformid = platformid;
		this.deviceid = deviceid;
		this.clPlatform = clplatform;
		this.clDevice = cldevice;
	}

	public cl_platform_id getClPlatform() {
		return clPlatform;
	}

	public cl_device_id getClDevice() {
		return clDevice;
	}

	public int getPlatformid() {
		return platformid;
	}

	public int getDeviceid() {
		return deviceid;
	}

	public String getPlatformName() {
		return getPlatformStringProperty(CL_PLATFORM_NAME);
	}

	public String getDeviceName() {
		return getDeviceStringProperty(CL_DEVICE_NAME);
	}

	public String getDeviceVendor() {
		return getDeviceStringProperty(CL_DEVICE_VENDOR);
	}

	public String getOpenCLVersion() {
		return getDeviceStringProperty(CL_DEVICE_VERSION);
	}

	public long getGlobalMemory() {
		return getDeviceLongProperty(CL_DEVICE_GLOBAL_MEM_SIZE);
	}

	public long getComputeUnitCount() {
		return getDeviceLongProperty(CL_DEVICE_MAX_COMPUTE_UNITS);
	}

	public long getClockFrequencyMhz() {
		return getDeviceLongProperty(CL_DEVICE_MAX_CLOCK_FREQUENCY);
	}

	public long[] getWorkItemDimensions() {
		long workItemDimensions = getDeviceLongProperty(CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS);
		return getDeviceLongArrayProperty(CL_DEVICE_MAX_WORK_ITEM_SIZES,
				(int) workItemDimensions);
	}
	
	public long getMaxWorkgroupSize()
	{
		return getDeviceLongProperty(CL_DEVICE_MAX_WORK_GROUP_SIZE);
		
	}

	public String getDeviceStringProperty(int Property) {
		return getDeviceString(clDevice, Property);
	}

	public long getDeviceLongProperty(int Property) {
		return getDeviceLongs(clDevice, Property, 1)[0];
	}

	public long[] getDeviceLongArrayProperty(int Property, int size) {
		return getDeviceLongs(clDevice, Property, size);
	}

	public String getPlatformStringProperty(int Property) {
		return getPlatformString(clPlatform, Property);
	}

	public long getPlatformLongProperty(int Property) {
		return getPlatformLongs(clPlatform, Property, 1)[0];
	}

	public long[] getPlatformLongArrayProperty(int Property, int size) {
		return getPlatformLongs(clPlatform, Property, size);
	}

	public boolean isExtSupported(String ext) {
		if (ext == null)
			return false;
		
		if (extensions.length() == 0) {
			extensions = getDeviceString(clDevice, CL_DEVICE_EXTENSIONS)
					.concat(" ").toLowerCase();
		}

		return extensions.contains(ext.toLowerCase() + " ");
	}

	private static long[] getDeviceLongs(cl_device_id clDevice, int paramName, int numValues) {
		long values[] = new long[numValues];
		clGetDeviceInfo(clDevice, paramName, Sizeof.cl_long * numValues,
				Pointer.to(values), null);
		return values;
	}

	private static String getDeviceString(cl_device_id clDevice, int paramName) {
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		clGetDeviceInfo(clDevice, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int) size[0]];
		clGetDeviceInfo(clDevice, paramName, buffer.length, Pointer.to(buffer),
				null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length - 1);
	}

	private static long[] getPlatformLongs(cl_platform_id clPlatform, int paramName, int numValues) {
		long values[] = new long[numValues];
		clGetPlatformInfo(clPlatform, paramName, Sizeof.cl_long * numValues,
				Pointer.to(values), null);
		return values;
	}

	private static String getPlatformString(cl_platform_id clPlatform, int paramName) {
		// Obtain the length of the string that will be queried
		long size[] = new long[1];
		clGetPlatformInfo(clPlatform, paramName, 0, null, size);

		// Create a buffer of the appropriate size and fill it with the info
		byte buffer[] = new byte[(int) size[0]];
		clGetPlatformInfo(clPlatform, paramName, buffer.length,
				Pointer.to(buffer), null);

		// Create a string from the buffer (excluding the trailing \0 byte)
		return new String(buffer, 0, buffer.length - 1);
	}
}
