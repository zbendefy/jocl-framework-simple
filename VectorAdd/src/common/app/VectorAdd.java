package common.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jocl.Pointer;
import org.jocl.Sizeof;

import clframework.common.CLContext;
import clframework.common.CLDevice;
import clframework.common.CLKernel;
import clframework.common.CLSourceLoader;
import clframework.common.MemObject;

public class VectorAdd {

	public static void main(String[] args) {
		List<CLDevice> devices = CLDevice.GetAllAvailableDevices();
		
		if (devices.size() == 0)
		{
			System.out.println("No OpenCL devices found!");
			return;
		}
		
		System.out.println("OpenCL devices:");
		for (CLDevice clDevice : devices) {
			System.out.println(clDevice.toString());
		}
		System.out.println("");
		
		CLDevice device = devices.get(0);
		CLContext context = new CLContext(device);
		CLKernel kernel = null;
		
		System.out.println("Using device: " + device.toString());
		
		try {
			kernel = new CLKernel(context, new String[] {CLSourceLoader.getLocalSource("/clsrc/vector.cl")}, new String[] { "vecadd" }, "");
		} catch (Exception e) {
			System.err.println("Failed to create kernel! " + e.getMessage());
			return;
		}
		
		float[] arrayA = new float[5000];
		float[] arrayB = new float[5000];
		float[] output = new float[5000];
		
		Random r = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < arrayB.length; i++) {
			arrayA[i] = r.nextFloat();
			arrayB[i] = r.nextFloat();
		}
		
		List<MemObject> parameters = new ArrayList<MemObject>(3);
		
		MemObject result = null;
		
		try {
			parameters.add(MemObject.createMemObjectReadOnly(context, Sizeof.cl_float * arrayA.length, Pointer.to(arrayA)));
			parameters.add(MemObject.createMemObjectReadOnly(context, Sizeof.cl_float * arrayB.length, Pointer.to(arrayB)));
			result = MemObject.createMemObjectWriteOnly(context, Sizeof.cl_float * arrayB.length);
			parameters.add(result);
		} catch (Exception e) {
			System.err.println("Failed to create memory objects! " + e.getMessage());
			return;
		}
		
		kernel.enqueueNDRangeKernel("vecadd", parameters, new long[] {arrayB.length});
		
		result.ReadBufferWithBlocking(Pointer.to(output));
		
		boolean isCorrect = true;
		
		for (int i = 0; i < output.length; i++) {
			if (Math.abs(output[i] - (arrayA[i]+arrayB[i])) > 0.001f)
			{
				isCorrect = false;
			}
		}
		
		System.out.println("Results are " + (isCorrect ? "correct!" : "incorrect!"));
		
		
		//cleaning up
		for (MemObject memObject : parameters) {
			memObject.delete();
		}
		parameters.clear();
		
		kernel.delete();
		context.delete();
	}

}
